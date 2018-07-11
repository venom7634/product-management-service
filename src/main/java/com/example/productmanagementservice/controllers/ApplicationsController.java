package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.products.CreditCard;
import com.example.productmanagementservice.entity.products.CreditCash;
import com.example.productmanagementservice.services.ApplicationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApplicationsController {

    private final DatabaseHandler databaseHandler;

    private final ApplicationHandler applicationHandler;

    @Autowired
    public ApplicationsController(ApplicationHandler applicationHandler, DatabaseHandler databaseHandler) {
        this.applicationHandler = applicationHandler;
        this.databaseHandler = databaseHandler;
    }


    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public Application createApplications(@RequestHeader("token") String token){
        return applicationHandler.createApplication(token);
    }

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public ResponseEntity<String> addDebitCard(@PathVariable("id") long id, @RequestHeader("token") String token){
        return applicationHandler.addDebitCardToApplication(token,id);
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public ResponseEntity<String> addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                                                @RequestBody CreditCard creditCard){
        return applicationHandler.addCreditCardToApplication(token,id,creditCard.getLimit());
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public ResponseEntity<String> addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                                                @RequestBody CreditCash creditCash){
        return applicationHandler.addCreditCashToApplication(token, id, creditCash.getAmount(),
                creditCash.getTimeInMonth());
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> sentApplication(@PathVariable("id") long id, @RequestHeader("token") String token){
        return applicationHandler.sendApplicationForApproval(token,id);
    }

}
