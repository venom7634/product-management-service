package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ApplicationHandler {

    private final DatabaseHandler databaseHandler;

    private final VerificationDatabase verificationDatabase;

    @Autowired
    public ApplicationHandler(DatabaseHandler databaseHandler, VerificationDatabase verificationDatabase) {
        this.databaseHandler = databaseHandler;
        this.verificationDatabase = verificationDatabase;
    }

    public Application createApplication(String token){
        return databaseHandler.createNewApplication(token);
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
            databaseHandler.sentApplicationToConfirmation(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
