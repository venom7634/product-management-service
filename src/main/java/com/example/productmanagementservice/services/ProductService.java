package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    final
    DatabaseHandler databaseHandler;

    @Autowired
    public ProductService(DatabaseHandler databaseHandler) {
        this.databaseHandler = databaseHandler;
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
}
