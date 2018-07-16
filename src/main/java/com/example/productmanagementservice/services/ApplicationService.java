package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.*;
import org.omg.CosNaming.NamingContextPackage.NotFound;
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
        return userVerificator.checkTokenInDatabase(token) && loginService.checkTokenOnValidation(token);
    }

    public Application createApplication(String token) {
        if (!checkToken(token)) {
            throw new NoAccessException();
        }
        return createNewApplication(token);
    }

    public void addDebitCardToApplication(String token, long idApplication) {
        checkForAddProduct(token, idApplication);

        if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addDebitCardToApplication(idApplication);
        } else {
            throw new ApplicationNoExistsException();
        }
    }

    public void addCreditCardToApplication(String token, long idApplication, int limit) {
        checkForAddProduct(token, idApplication);

        if (limit < 0 && limit > 1000) {
            throw new IncorrectValueException();
        }

        if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new ApplicationNoExistsException();
        }
    }

    public void addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        checkForAddProduct(token, idApplication);

        if ((amount <= 0 && amount > 1000) || timeInMonth <= 0) {
            throw new IncorrectValueException();
        }

        if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            productsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
        } else {
            throw new ApplicationNoExistsException();
        }
    }

    public void checkForAddProduct(String token, long idApplication) {
        if (!checkToken(token)) {
            throw new ApplicationNoExistsException();
        }

        if (!applicationVerificator.verificationOfBelongingApplicationToClient(idApplication, token)) {
            throw new NotMatchUserException();
        }
    }

    public List<Application> getApplicationsForApproval(String token) {
        if (!checkToken(token)) {
            throw new NoAccessException();
        } else {
            return applicationsRepository.getListSentApplicationsOfDataBase
                    (userVerificator.getUserOfToken(token).getId());
        }
    }

    public void sendApplicationForApproval(String token, long idApplication) {
        if (!checkToken(token) || productsVerificator.checkProductInApplicationsClient(idApplication)) {
            throw new NoAccessException();
        }

        if (!applicationVerificator.verificationOfBelongingApplicationToClient(idApplication, token)) {
            throw new NotMatchUserException();
        }

        if (applicationVerificator.checkIsEmptyOfApplication(idApplication)) {
            throw new IncorrectValueException();
        }

        if (checkTotalAmountMoneyHasReachedMax(idApplication)) {
            throw new MaxAmountCreditReachedException();
        }

        if (applicationVerificator.isExistsApplication(idApplication, 0)) {
            applicationsRepository.sendApplicationToConfirmation(idApplication);
        } else {
            throw new ApplicationNoExistsException();
        }
    }

    public List<Application> getApplicationsClientForApproval(long userId, String token) {
        if (userVerificator.authenticationOfBankEmployee(token) || !checkToken(token)) {
            return applicationsRepository.getListSentApplicationsOfDataBase(userId);
        } else {
            throw new NoAccessException();
        }
    }

    public void approveApplication(long idApplication, String token) {
        if (!checkToken(token) || !applicationVerificator.checkForChangeStatusApplication(idApplication) ||
                productsVerificator.checkProductInApplicationsClient(idApplication)) {
            throw new NoAccessException();
        }

        if (!applicationVerificator.isExistsApplication(idApplication)) {
            throw new ApplicationNoExistsException();
        }

        if (checkTotalAmountMoneyHasReachedMax(idApplication)) {
            throw new IncorrectValueException();
        }

        if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsRepository.setNegativeOfAllIdenticalProducts
                    (applicationsRepository.getApplicationsById(idApplication).get(0).getProduct());

            applicationsRepository.approveApplication(idApplication);
        } else {
            throw new PageNotFoundException();
        }
    }

    public void negativeApplication(long idApplication, String token, String reason) {
        if (!applicationVerificator.isExistsApplication(idApplication)) {
            throw new ApplicationNoExistsException();
        }
        if (userVerificator.authenticationOfBankEmployee(token) || !checkToken(token)
                || applicationVerificator.checkForChangeStatusApplication(idApplication)) {
            applicationsRepository.negativeApplication(idApplication, reason);
        } else {
            throw new NoAccessException();
        }
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
