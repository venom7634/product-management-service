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
public class ApplicationHandler {

    private final CreatorInDatabase creatorInDatabase;

    private final DatabaseHandler databaseHandler;

    private final VerificationDatabase verificationDatabase;

    @Autowired
    public ApplicationHandler(DatabaseHandler databaseHandler,
                              VerificationDatabase verificationDatabase, CreatorInDatabase creatorInDatabase) {
        this.databaseHandler = databaseHandler;
        this.verificationDatabase = verificationDatabase;
        this.creatorInDatabase = creatorInDatabase;
    }

    public Application createApplication(String token){
        return creatorInDatabase.createNewApplication(token);
    }

    public ResponseEntity<String> addDebitCardToApplication(String token, long id) {

        ResponseEntity<String> responseEntity;
        if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.addDebitCardToApplication(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return responseEntity;
    }

    public ResponseEntity<String> addCreditCardToApplication(String token, long id, int limit) {
        ResponseEntity<String> responseEntity;

        if(verificationDatabase.verificationOnExistsApplication(token, id)){
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

        if(verificationDatabase.verificationOnExistsApplication(token, id)){
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

        if(verificationDatabase.verificationOnExistsApplication(token, id)){
            databaseHandler.sendApplicationToConfirmation(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public List<Application> getApplicationsForApproval(String token){
        return databaseHandler.getListApplicationsOfDataBase(token);
    }

    public ResponseEntity<List<Application>> getApplicationsClientForApproval(long id, String token){
        ResponseEntity<List<Application>> responseEntity;

        if(verificationDatabase.authenticationOfBankEmployee(token)){

            responseEntity = new ResponseEntity<>(databaseHandler.getListApplicationsOfDataBase(id),HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;

    }
}
