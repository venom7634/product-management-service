package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.data.Reason;
import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductsController {

    final
    ProductService productService;

    @Autowired
    public ProductsController(ProductService productService) {
        this.productService = productService;
    }

    @RequestMapping(value = "/products/debit-card", method = RequestMethod.GET)
    public Product getDescriptionDebitCard() {
        return productService.getDescriptionDebitCard();
    }

    @RequestMapping(value = "/products/credit-card", method = RequestMethod.GET)
    public Product getDescriptionCreditCard() {
        return productService.getDescriptionCreditCard();
    }

    @RequestMapping(value = "/products/credit-cash", method = RequestMethod.GET)
    public Product getDescriptionCreditCash() {
        return productService.getDescriptionCreditCash();
    }

    @RequestMapping(value = "/clients/{id}/products", method = RequestMethod.GET)
    public ResponseEntity<List<Product>> getClientProducts(@PathVariable long userId, @RequestHeader("token") String token) {
        return productService.getProductsForClient(token, userId);
    }
}
