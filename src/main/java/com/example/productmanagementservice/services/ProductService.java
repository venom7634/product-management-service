package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final DatabaseHandler databaseHandler;

    private final LoginService loginService;

    private final VerificationDatabase verificationDatabase;
    @Autowired
    public ProductService(DatabaseHandler databaseHandler, LoginService loginService,
                          VerificationDatabase verificationDatabase) {
        this.databaseHandler = databaseHandler;
        this.loginService = loginService;
        this.verificationDatabase = verificationDatabase;
    }

    public Product getDescriptionDebitCard(){
        return databaseHandler.getProductOfDataBase("debit-card");
    }

    public Product getDescriptionCreditCard(){
        return databaseHandler.getProductOfDataBase("credit-card");
    }

    public Product getDescriptionCreditCash(){
        return databaseHandler.getProductOfDataBase("credit-cash");
    }

    public ResponseEntity<List<Product>> getProductsForClient(String token, long id){
        ResponseEntity<List<Product>>responseEntity;

        if(!loginService.checkTokenOnValidation(token)){
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if(verificationDatabase.authenticationOfBankEmployee(token)){
            responseEntity = new ResponseEntity<>(databaseHandler.getProductsForClient(id),HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
