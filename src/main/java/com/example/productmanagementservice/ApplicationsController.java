package com.example.productmanagementservice;

import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.Token;
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
    public Token createToken(HttpServletResponse response, @RequestParam("login") String login,
                             @RequestParam("password") String password){

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

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public void addDebitCard(HttpServletResponse response, @PathVariable("id") long id,
                             @RequestHeader("token") String token){

        if(verificationDatabase.verificatrionOnExistsApplication(token, id)){
            databaseHandler.addDebitCardToApplication(id);
        } else {
            response.setStatus(404);
        }
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public void addCreditCard(HttpServletResponse response,@PathVariable("id") long id,
                              @RequestHeader("token") String token, @RequestParam("limit") int limit){

        if(verificationDatabase.verificatrionOnExistsApplication(token, id)){
            databaseHandler.addCreditCardToApplication(id,limit);
        } else {
            response.setStatus(404);
        }
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public void addCreditCard(HttpServletResponse response,
                              @PathVariable("id") long id, @RequestHeader("token") String token,
                              @RequestParam("amount") int amount, @RequestParam("timeInMonth") int timeInMonth ){

        if(verificationDatabase.verificatrionOnExistsApplication(token, id)){
            databaseHandler.addCreditCashToApplication(id,amount,timeInMonth);
        } else {
            response.setStatus(404);
        }
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public void sentApplication(HttpServletResponse response, @PathVariable("id") long id,
                                @RequestHeader("token") String token){

        if(verificationDatabase.verificatrionOnExistsApplication(token, id)){
            databaseHandler.sentApplicationToConfirmation(id);
        } else {
            response.setStatus(404);
        }
    }
}
