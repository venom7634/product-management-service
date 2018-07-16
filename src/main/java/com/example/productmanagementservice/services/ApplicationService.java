package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.ApplicationNoExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationVerificator applicationVerificator;
    private final ProductsVerificator productsVerificator;
    private final UserVerificator userVerificator;
    private final ApplicationsRepository applicationsRepository;
    private final ProductsRepository productsRepository;
    private final LoginService loginService;

    @Autowired
    public ApplicationService(LoginService loginService, ApplicationsRepository applicationsRepository,
                              ProductsRepository productsRepository, ApplicationVerificator applicationVerificator,
                              ProductsVerificator productsVerificator, UserVerificator userVerificator) {
        this.loginService = loginService;
        this.applicationsRepository = applicationsRepository;
        this.productsRepository = productsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
        this.userVerificator = userVerificator;
    }

    public boolean checkToken(String token) {
        return userVerificator.checkTokenInDatabase(token) || loginService.checkTokenOnValidation(token);
    }

    public Application createApplication(String token) {

        if (!checkToken(token)) {
            throw new ApplicationNoExistsException();
        }

        return createNewApplication(token);
    }

    public ResponseEntity<String> addDebitCardToApplication(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addDebitCardToApplication(idApplication);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCardToApplication(String token, long idApplication, int limit) {
        ResponseEntity<String> responseEntity;

        if (limit < 0 && limit > 1000) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addCreditCardToApplication(idApplication, limit);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        ResponseEntity<String> responseEntity;

        if ((amount <= 0 && amount > 1000) || timeInMonth <= 0) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> checkForAddProduct(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !applicationVerificator.verificationOfBelongingApplicationToClient(idApplication, token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    public ResponseEntity<List<Application>> getApplicationsForApproval(String token) {
        ResponseEntity<List<Application>> responseEntity;

        if (checkToken(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(applicationsRepository.getListSentApplicationsOfDataBase
                    (userVerificator.getUserOfToken(token).getId()), HttpStatus.OK);
        }

        return responseEntity;
    }

    public ResponseEntity<String> sendApplicationForApproval(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (!applicationVerificator.isExistsApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (checkToken(token) || productsVerificator.checkProductInApplicationsClient(idApplication) ||
                !applicationVerificator.verificationOfBelongingApplicationToClient(idApplication, token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (applicationVerificator.checkIsEmptyOfApplication(idApplication) ||
                checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            applicationsRepository.sendApplicationToConfirmation(idApplication);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public ResponseEntity<List<Application>> getApplicationsClientForApproval(long userId, String token) {
        ResponseEntity<List<Application>> responseEntity;

        if (checkToken(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            responseEntity = new ResponseEntity<>(applicationsRepository.getListSentApplicationsOfDataBase(userId), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    public ResponseEntity<String> approveApplication(long idApplication, String token) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !applicationVerificator.checkForChangeStatusApplication(idApplication) ||
                productsVerificator.checkProductInApplicationsClient(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (!applicationVerificator.isExistsApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsRepository.setNegativeOfAllIdenticalProducts
                    (applicationsRepository.getApplicationsById(idApplication).get(0).getProduct());
            applicationsRepository.approveApplication(idApplication);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public ResponseEntity<String> negativeApplication(long idApplication, String token, String reason) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !applicationVerificator.checkForChangeStatusApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (!applicationVerificator.isExistsApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsRepository.negativeApplication(idApplication, reason);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public Application createNewApplication(String token) {
        Application result = new Application();
        result.setId(0);

        applicationsRepository.createNewApplicationInDatabase(token);
        List<Application> applications = applicationsRepository.getAllClientApplications(token);

        for (Application app : applications) {
            if (app.getId() > result.getId()) {
                result = app;
            }
        }
        return result;
    }

    public boolean checkTotalAmountMoneyHasReachedMax(long idApplication) {
        int totalAmount = 0;

        List<Application> applications = applicationsRepository.getListApprovedApplicationsOfDatabase
                (applicationsRepository.getUsersByIdApplication(idApplication).get(0).getId());

        for (Application app : applications) {
            if (app.getAmount() != null) {
                totalAmount += Integer.parseInt(app.getAmount());
            }
            if (app.getLimit() != null) {
                totalAmount += Integer.parseInt(app.getLimit());
            }
        }

        Application application = applicationVerificator.getApplicationOfId(idApplication);

        if (application.getLimit() != null) {
            if ((Integer.parseInt(application.getLimit()) + totalAmount) <= 1000) {
                return false;
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) <= 1000) {
                return false;
            }
        }

        if (application.getAmount() == null && application.getLimit() == null) {
            return false;
        }
        return true;
    }
}
