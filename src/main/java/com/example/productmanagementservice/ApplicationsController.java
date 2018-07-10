package com.example.productmanagementservice;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.Token;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

@RestController
public class ApplicationsController {

    @Autowired
    private DatabaseHandler databaseHandler;

    @Autowired
    private VerificationDatabase verificationDatabase;

    @PostConstruct
    public void init(){
        databaseHandler.createTables();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Token createToken(HttpServletResponse response, @RequestParam("login") String login, @RequestParam("password") String password){

        if(verificationDatabase.checkingUser(login,password)) {
            Token token = databaseHandler.returnTokenForLogin(login);
            return token;
        }

        response.setStatus(403);
        return new Token("");
    }

    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public Application createApplications(@RequestHeader("token") String token){
        return databaseHandler.createNewApplication(token);
    }

    @RequestMapping(value="/applications/{id}/debit-card", method = RequestMethod.POST)
    public void addDebitCard(@PathVariable("id") long id, @RequestHeader("token") String token){

        databaseHandler.addDebitCardToApplication(id, token);
    }

    @RequestMapping(value="/applications/{id}/credit-card", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                              @RequestParam("limit") int limit){

        databaseHandler.addCreditCardToApplication(id, token,limit);
    }
    @RequestMapping(value="/applications/{id}/credit-cash", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                              @RequestParam("amount") int amount,@RequestParam("timeInMonth") int timeInMonth ){

        databaseHandler.addCreditCashToApplication(id, token,amount,timeInMonth);
    }

}
