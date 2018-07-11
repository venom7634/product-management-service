package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductHandler {

    final
    DatabaseHandler databaseHandler;

    @Autowired
    public ProductHandler(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
    }

    public Product getDescriptionDebitCard(){
        return databaseHandler.getProduct("debit-card");
    }

    public Product getDescriptionCreditCard(){
        return databaseHandler.getProduct("credit-card");
    }

    public Product getDescriptionCreditCash(){
        return databaseHandler.getProduct("credit-cash");
    }
}
