package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.services.ProductHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductsController {

    final
    ProductHandler productHandler;

    @Autowired
    public ProductsController(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    @RequestMapping(value = "/products/debit-card", method = RequestMethod.GET)
    public Product getDescriptionDebitCard(){
        return productHandler.getDescriptionDebitCard();
    }

    @RequestMapping(value = "/products/credit-card", method = RequestMethod.GET)
    public Product getDescriptionCreditCard(){
        return productHandler.getDescriptionCreditCard();

    }

    @RequestMapping(value = "/products/credit-cash", method = RequestMethod.GET)
    public Product getDescriptionCreditCash(){
        return productHandler.getDescriptionCreditCash();
    }
}
