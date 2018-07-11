package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.data.Reason;
import com.example.productmanagementservice.entity.products.CreditCard;
import com.example.productmanagementservice.entity.products.CreditCash;
import com.example.productmanagementservice.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationsController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationsController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }


    @RequestMapping(value = "/applications", method = RequestMethod.POST)
    public ResponseEntity<Application> createApplications(@RequestHeader("token") String token){
        return applicationService.createApplication(token);
    }

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public ResponseEntity<String> addDebitCard(@PathVariable("id") long id, @RequestHeader("token") String token){
        return applicationService.addDebitCardToApplication(token,id);
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public ResponseEntity<String> addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                                                @RequestBody CreditCard creditCard){
        return applicationService.addCreditCardToApplication(token,id,creditCard.getLimit());
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public ResponseEntity<String> addCreditCard(@PathVariable("id") long id, @RequestHeader("token") String token,
                                                @RequestBody CreditCash creditCash){
        return applicationService.addCreditCashToApplication(token, id, creditCash.getAmount(),
                creditCash.getTimeInMonth());
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public ResponseEntity<String> sentApplication(@PathVariable("id") long id, @RequestHeader("token") String token){
        return applicationService.sendApplicationForApproval(token,id);
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> getListApplicationsClientForApproval(@RequestParam("userId") long id,
                                                                  @RequestHeader("token") String token){
        return applicationService.getApplicationsClientForApproval(id,token);
    }

    @RequestMapping(value = "/applications/my", method = RequestMethod.GET)
    public ResponseEntity<List<Application>> getMyListApplicationsForApproval(@RequestHeader("token") String token){
        return applicationService.getApplicationsForApproval(token);
    }

    @RequestMapping(value = "/applications/{id}/approve", method = RequestMethod.POST)//ДОБАВИТЬ ВАЛИДАЦИЮ
    public ResponseEntity<String> approveApplication(@PathVariable long id, @RequestHeader("token") String token){
        return applicationService.approveApplication(id, token);
    }

    @RequestMapping(value = "/applications/{id}/negative", method = RequestMethod.GET)
    public ResponseEntity<String> negativeApplication(@PathVariable long id, @RequestHeader("token") String token,
                                                      @RequestBody Reason reason){
        return applicationService.negativeApplication(id,token,reason.getReason());
    }

}
