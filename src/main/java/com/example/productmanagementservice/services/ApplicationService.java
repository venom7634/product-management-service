package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.handlers.ApplicationsHandler;
import com.example.productmanagementservice.database.handlers.DataHandler;
import com.example.productmanagementservice.database.handlers.ProductsHandler;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
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
    private final ApplicationsHandler applicationsHandler;
    private final DataHandler dataHandler;
    private final ProductsHandler productsHandler;
    private final LoginService loginService;

    @Autowired
    public ApplicationService(LoginService loginService, ApplicationsHandler applicationsHandler, DataHandler dataHandler,
                              ProductsHandler productsHandler, ApplicationVerificator applicationVerificator,
                              ProductsVerificator productsVerificator, UserVerificator userVerificator) {
        this.loginService = loginService;
        this.applicationsHandler = applicationsHandler;
        this.dataHandler = dataHandler;
        this.productsHandler = productsHandler;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
        this.userVerificator = userVerificator;
    }

    public boolean checkToken(String token) {
        return !userVerificator.checkTokenInDatabase(token) || !loginService.checkTokenOnValidation(token);
    }

    public ResponseEntity<Application> createApplication(String token) {
        ResponseEntity<Application> responseEntity;

        if (checkToken(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(createNewApplication(token), HttpStatus.OK);
        }
        return responseEntity;
    }

    public ResponseEntity<String> addDebitCardToApplication(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (applicationVerificator.verificationOnExistsApplication(idApplication)) {
            productsHandler.addDebitCardToApplication(idApplication);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCardToApplication(String token, long idApplication, int limit) {
        ResponseEntity<String> responseEntity;

        if (limit < 0 && limit > 1000) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (applicationVerificator.verificationOnExistsApplication(idApplication)) {
            productsHandler.addCreditCardToApplication(idApplication, limit);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        ResponseEntity<String> responseEntity;

        if ((amount <= 0 && amount > 1000) || timeInMonth <= 0) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (applicationVerificator.verificationOnExistsApplication(idApplication)) {
            productsHandler.addCreditCashToApplication(idApplication, amount, timeInMonth);
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
            responseEntity = new ResponseEntity<>(applicationsHandler.getListSentApplicationsOfDataBase
                    (userVerificator.getUserOfToken(token).getId()), HttpStatus.OK);
        }

        return responseEntity;
    }

    public ResponseEntity<String> sendApplicationForApproval(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (!applicationVerificator.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (checkToken(token) || productsVerificator.checkProductInApplicationsClient(idApplication) ||
                !applicationVerificator.verificationOfBelongingApplicationToClient(idApplication, token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (applicationVerificator.checkIsEmptyOfApplication(idApplication) ||
                checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (applicationVerificator.verificationOnExistsApplication(idApplication)) {
            applicationsHandler.sendApplicationToConfirmation(idApplication);
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
            responseEntity = new ResponseEntity<>(applicationsHandler.getListSentApplicationsOfDataBase(userId), HttpStatus.OK);
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
        } else if (!applicationVerificator.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsHandler.setNegativeOfAllIdenticalProducts
                    (productsHandler.getProductOfApplication(idApplication));
            applicationsHandler.approveApplication(idApplication);
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
        } else if (!applicationVerificator.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsHandler.negativeApplication(idApplication, reason);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public Application createNewApplication(String token) {
        Application result = new Application();
        result.setId(0);

        applicationsHandler.createNewApplicationInDatabase(token);
        List<Application> applications = applicationsHandler.getAllClientApplications(token);

        for (Application app : applications) {
            if (app.getId() > result.getId()) {
                result = app;
            }
        }
        return result;
    }

    public boolean checkTotalAmountMoneyHasReachedMax(long idApplication) {
        int totalAmount = 0;

        List<Application> applications = applicationsHandler.getListApprovedApplicationsOfDatabase
                (applicationsHandler.getUserByIdApplication(idApplication).getId());

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
