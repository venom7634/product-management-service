package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {


    private final ProductsRepository productsRepository;
    private final LoginService loginService;
    private final UserVerificator userVerificator;

    @Autowired
    public ProductService(LoginService loginService, UserVerificator userVerificator,
                          ProductsRepository productsRepository) {
        this.loginService = loginService;
        this.userVerificator = userVerificator;
        this.productsRepository = productsRepository;
    }

    public Product getDescriptionDebitCard() {
        return getProductOfName("debit-card");
    }

    public Product getDescriptionCreditCard() {
        return getProductOfName("credit-card");
    }

    public Product getDescriptionCreditCash() {
        return getProductOfName("credit-cash");
    }

    public ResponseEntity<List<Product>> getProductsForClient(String token, long userId) {
        ResponseEntity<List<Product>> responseEntity;

        if (!userVerificator.checkTokenInDatabase(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (!loginService.checkTokenOnValidation(token)) {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else if (userVerificator.authenticationOfBankEmployee(token)) {
            responseEntity = new ResponseEntity<>(productsRepository.getProductsForClient(userId), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }

    public Product getProductOfName(String product) {
        int id = 0;
        switch (product) {
            case "debit-card":
                id = Product.type.DEBIT_CARD.ordinal() + 1;
                break;
            case "credit-card":
                id = Product.type.CREDIT_CARD.ordinal() + 1;
                break;
            case "credit-cash":
                id = Product.type.CREDIT_CASH.ordinal() + 1;
                break;
        }

        return productsRepository.getProductOfDataBase(id);
    }

}
