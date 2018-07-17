package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.entity.products.Statistic;
import com.example.productmanagementservice.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Product> getClientProducts(@PathVariable("id") long userId, @RequestHeader("token") String token) {
        return productService.getProductsForClient(token, userId);
    }

    @RequestMapping(value = "/products/statistics/approvedApplications", method = RequestMethod.GET)
    public List<Statistic> getStatisticsApprovedApplications(@RequestHeader("token") String token) {
        return productService.getStatisticUsesProducts(token);
    }

    @RequestMapping(value = "/products/statistics/negativeApplications", method = RequestMethod.GET)
    public List<Statistic> getStatisticsNegativeApplications(@RequestHeader("token") String token) {
        return productService.getStatisticsNegativeApplications(token);
    }
}
