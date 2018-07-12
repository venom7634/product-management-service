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

    public ResponseEntity<Application> createApplication(String token){
        ResponseEntity<Application> responseEntity;
        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!loginService.checkTokenOnValidation(token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(creatorInDatabase.createNewApplication(token),HttpStatus.OK);
        }
        return responseEntity;
    }

    public ResponseEntity<String> addDebitCardToApplication(String token, long id) {
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!loginService.checkTokenOnValidation(token) ||
                !verificationDatabase.verificationOfBelongingApplicationToClient(id, token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.addDebitCardToApplication(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCardToApplication(String token, long id, int limit) {
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!loginService.checkTokenOnValidation(token)||
                !verificationDatabase.verificationOfBelongingApplicationToClient(id, token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.addCreditCardToApplication(id,limit);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else if (limit < 0) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCashToApplication(String token, long id, int amount, int timeInMonth) {
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!loginService.checkTokenOnValidation(token)||
                !verificationDatabase.verificationOfBelongingApplicationToClient(id, token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.addCreditCashToApplication(id,amount,timeInMonth);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else if (amount <= 0 || timeInMonth <= 0) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    public ResponseEntity<String> sendApplicationForApproval(String token, long id){
        ResponseEntity<String> responseEntity;

        if(!verificationDatabase.checkExistenceOfApplication(id)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if(!loginService.checkTokenOnValidation(token) ||
                verificationDatabase.checkProductInApplicationsClient(id) ||
                !verificationDatabase.verificationOfBelongingApplicationToClient(id,token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (verificationDatabase.checkIsEmptyOfApplication(id)){
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.sendApplicationToConfirmation(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public ResponseEntity<List<Application>> getApplicationsForApproval(String token){
        ResponseEntity<List<Application>> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!loginService.checkTokenOnValidation(token)){
                responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else{
                responseEntity = new ResponseEntity<>(databaseHandler.getListApplicationsOfDataBase(token),HttpStatus.OK);
            }

        return responseEntity;
    }

    public ResponseEntity<List<Application>> getApplicationsClientForApproval(long id, String token){
        ResponseEntity<List<Application>> responseEntity;

        if(!loginService.checkTokenOnValidation(token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.authenticationOfBankEmployee(token)){
            responseEntity = new ResponseEntity<>(databaseHandler.getListApplicationsOfDataBase(id),HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }

    public ResponseEntity<String> approveApplication(long id, String token){
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!verificationDatabase.checkExistenceOfApplication(id)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if(!loginService.checkTokenOnValidation(token) || verificationDatabase.checkProductInApplicationsClient(id)){
                responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
            } else if(verificationDatabase.authenticationOfBankEmployee(token)){
                databaseHandler.approveApplication(id);
                responseEntity = new ResponseEntity<>(HttpStatus.OK);
            } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public ResponseEntity<String> negativeApplication(long id, String token, String reason){
        ResponseEntity<String> responseEntity;

        if (!verificationDatabase.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(!verificationDatabase.checkExistenceOfApplication(id)) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if(!loginService.checkTokenOnValidation(token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.authenticationOfBankEmployee(token)){
            databaseHandler.negativeApplication(id,reason);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
