package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.data.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LoginHandler {
    @Autowired
    private DatabaseHandler databaseHandler;

    @Autowired
    private VerificationDatabase verificationDatabase;

    public ResponseEntity<Token> login(String login, String password){
        ResponseEntity<Token> responseEntity;

        if(verificationDatabase.checkingUser(login,password)) {
            responseEntity = new ResponseEntity<>
                    (new Token(databaseHandler.createToken(login)),HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return responseEntity;
    }
}
