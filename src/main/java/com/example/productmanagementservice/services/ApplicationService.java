package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.ApplicationVerificator;
import com.example.productmanagementservice.database.verificators.ProductsVerificator;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final UsersRepository usersRepository;

    @Autowired
    public ApplicationService(LoginService loginService, ApplicationsRepository applicationsRepository,
                              ProductsRepository productsRepository, ApplicationVerificator applicationVerificator,
                              ProductsVerificator productsVerificator, UserVerificator userVerificator, UsersRepository usersRepository) {
        this.loginService = loginService;
        this.applicationsRepository = applicationsRepository;
        this.productsRepository = productsRepository;
        this.applicationVerificator = applicationVerificator;
        this.productsVerificator = productsVerificator;
        this.userVerificator = userVerificator;
        this.usersRepository = usersRepository;
    }

    public boolean checkToken(String token) {
        if (userVerificator.checkTokenInDatabase(token)) {
            return loginService.checkTokenOnValidation(token);
        }
        return false;
    }

    public Application createApplication(String token) {
        if (!checkToken(token)) {
            throw new NoAccessException();
        }
        return createNewApplication(token);
    }

    public void addDebitCardToApplication(String token, long idApplication) {
        if (applicationVerificator.isExistsApplication(idApplication, Application.status.CREATED.ordinal())) {
            checkForAddProduct(token, idApplication);
            productsRepository.addDebitCardToApplication(idApplication);
        } else {
            throw new ApplicationNoExistsException();
        }
    }

    public void addCreditCardToApplication(String token, long idApplication, int limit) {
        if (!applicationVerificator.isExistsApplication(idApplication, Application.status.CREATED.ordinal())) {
            throw new ApplicationNoExistsException();
        }

        checkForAddProduct(token, idApplication);

        if (limit > 0 || limit <= 1000) {
            productsRepository.addCreditCardToApplication(idApplication, limit);
        } else {
            throw new IncorrectValueException();
        }
    }

    public void addCreditCashToApplication(String token, long idApplication, int amount, int timeInMonth) {
        if (!applicationVerificator.isExistsApplication(idApplication, Application.status.CREATED.ordinal())) {
            throw new ApplicationNoExistsException();
        }

        checkForAddProduct(token, idApplication);

        if ((amount > 0 || amount <= 1000) || timeInMonth > 0) {
            productsRepository.addCreditCashToApplication(idApplication, amount, timeInMonth);
        } else {
            throw new IncorrectValueException();
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
                    (userVerificator.getUserOfToken(token).getId(), Application.status.SENT.ordinal());
        }
    }

    public void sendApplicationForApproval(String token, long idApplication) {
        if (!applicationVerificator.isExistsApplication(idApplication, 0)) {
            throw new ApplicationNoExistsException();
        }

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

        applicationsRepository.sendApplicationToConfirmation(idApplication, Application.status.SENT.ordinal());
    }

    public List<Application> getApplicationsClientForApproval(long userId, String token) {
        if (userVerificator.authenticationOfBankEmployee(token) || !checkToken(token)) {
            return applicationsRepository.getListSentApplicationsOfDataBase(userId, Application.status.SENT.ordinal());
        } else {
            throw new NoAccessException();
        }
    }

    public void approveApplication(long idApplication, String token) {
        if (!checkToken(token) || !applicationVerificator.checkForChangeStatusApplication(idApplication) ||
                productsVerificator.checkProductInApplicationsClient(idApplication)) {
            throw new NoAccessException();
        }

        if (!applicationVerificator.isExistsApplication(idApplication, Application.status.SENT.ordinal())) {
            throw new ApplicationNoExistsException();
        }

        if (checkTotalAmountMoneyHasReachedMax(idApplication)) {
            throw new IncorrectValueException();
        }

        if (userVerificator.authenticationOfBankEmployee(token)) {
            applicationsRepository.setNegativeOfAllIdenticalProducts
                    (applicationsRepository.getApplicationsById(idApplication).get(0).getProduct(),
                            Application.status.NEGATIVE.ordinal());

            applicationsRepository.approveApplication(idApplication, Application.status.APPROVED.ordinal());
        } else {
            throw new PageNotFoundException();
        }
    }

    public void negativeApplication(long idApplication, String token, String reason) {
        if (!applicationVerificator.isExistsApplication(idApplication)) {
            throw new ApplicationNoExistsException();
        }
        if (!applicationVerificator.checkForChangeStatusApplication(idApplication)) {
            throw new NoAccessException();
        }

        if (userVerificator.authenticationOfBankEmployee(token) || !checkToken(token)) {
            applicationsRepository.negativeApplication(idApplication, reason, Application.status.NEGATIVE.ordinal());
        } else {
            throw new NoAccessException();
        }
    }

    public Application createNewApplication(String token) {
        Application result = new Application();
        result.setId(0);

        applicationsRepository.createNewApplicationInDatabase(loginService.getIdByToken(token), Application.status.CREATED.ordinal());
        List<Application> applications = applicationsRepository.getAllClientApplications
                (loginService.getIdByToken(token), Application.status.CREATED.ordinal());

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
                (usersRepository.getUsersByIdApplication(idApplication).get(0).getId(), Application.status.APPROVED.ordinal());

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
