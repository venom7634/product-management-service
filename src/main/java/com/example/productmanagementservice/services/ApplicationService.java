package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.CreatorInDatabase;
import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final CreatorInDatabase creatorInDatabase;

    private final DatabaseHandler databaseHandler;

    private final VerificationDatabase verificationDatabase;

    private final LoginService loginService;

    @Autowired
    public ApplicationService(DatabaseHandler databaseHandler, VerificationDatabase verificationDatabase,
                              CreatorInDatabase creatorInDatabase, LoginService loginService) {
        this.databaseHandler = databaseHandler;
        this.verificationDatabase = verificationDatabase;
        this.creatorInDatabase = creatorInDatabase;
        this.loginService = loginService;
    }

    public boolean checkToken(String token) {
        return !verificationDatabase.checkTokenInDatabase(token) || !loginService.checkTokenOnValidation(token);
    }

    public ResponseEntity<Application> createApplication(String token) {
        ResponseEntity<Application> responseEntity;

        if (checkToken(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(creatorInDatabase.createNewApplication(token), HttpStatus.OK);
        }
        return responseEntity;
    }

    public ResponseEntity<String> addDebitCardToApplication(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (verificationDatabase.verificationOnExistsApplication(idApplication)) {
            databaseHandler.addDebitCardToApplication(idApplication);
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
        } else if (verificationDatabase.verificationOnExistsApplication(idApplication)) {
            databaseHandler.addCreditCardToApplication(idApplication, limit);
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
        } else if (verificationDatabase.verificationOnExistsApplication(idApplication)) {
            databaseHandler.addCreditCashToApplication(idApplication, amount, timeInMonth);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = checkForAddProduct(token, idApplication);
        }

        return responseEntity;
    }

    public ResponseEntity<String> checkForAddProduct(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !verificationDatabase.verificationOfBelongingApplicationToClient(idApplication, token)) {
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
            responseEntity = new ResponseEntity<>(databaseHandler.getListApplicationsOfDataBase
                    (verificationDatabase.getUserOfToken(token).getId()), HttpStatus.OK);
        }

        return responseEntity;
    }

    public ResponseEntity<String> sendApplicationForApproval(String token, long idApplication) {
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (checkToken(token) || verificationDatabase.checkProductInApplicationsClient(idApplication) ||
                !verificationDatabase.verificationOfBelongingApplicationToClient(idApplication, token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (verificationDatabase.checkIsEmptyOfApplication(idApplication) ||
                verificationDatabase.checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (verificationDatabase.verificationOnExistsApplication(idApplication)) {
            databaseHandler.sendApplicationToConfirmation(idApplication);
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
        } else if (verificationDatabase.authenticationOfBankEmployee(token)) {
            responseEntity = new ResponseEntity<>(databaseHandler.getListApplicationsOfDataBase(userId), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    public ResponseEntity<String> approveApplication(long idApplication, String token) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !verificationDatabase.checkForChangeStatusApplication(idApplication) ||
                verificationDatabase.checkProductInApplicationsClient(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (!verificationDatabase.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (verificationDatabase.checkTotalAmountMoneyHasReachedMax(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if (verificationDatabase.authenticationOfBankEmployee(token)) {
            databaseHandler.approveApplication(idApplication);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public ResponseEntity<String> negativeApplication(long idApplication, String token, String reason) {
        ResponseEntity<String> responseEntity;

        if (checkToken(token) || !verificationDatabase.checkForChangeStatusApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (!verificationDatabase.checkExistenceOfApplication(idApplication)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (verificationDatabase.authenticationOfBankEmployee(token)) {
            databaseHandler.negativeApplication(idApplication, reason);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
