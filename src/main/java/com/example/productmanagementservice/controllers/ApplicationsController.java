package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.data.Reason;
import com.example.productmanagementservice.entity.products.CreditCard;
import com.example.productmanagementservice.entity.products.CreditCash;
import com.example.productmanagementservice.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Application createApplications(@RequestHeader("token") String token) {
        return applicationService.createApplication(token);
    }

    @RequestMapping(value = "/applications/{id}/debit-card", method = RequestMethod.POST)
    public void addDebitCard(@PathVariable("id") long idApplication,
                             @RequestHeader("token") String token) {
        applicationService.addDebitCardToApplication(token, idApplication);
    }

    @RequestMapping(value = "/applications/{id}/credit-card", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                              @RequestBody CreditCard creditCard) {
        applicationService.addCreditCardToApplication(token, idApplication, creditCard.getLimit());
    }

    @RequestMapping(value = "/applications/{id}/credit-cash", method = RequestMethod.POST)
    public void addCreditCard(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                              @RequestBody CreditCash creditCash) {
        applicationService.addCreditCashToApplication(token, idApplication, creditCash.getAmount(),
                creditCash.getTimeInMonth());
    }

    @RequestMapping(value = "/applications/{id}", method = RequestMethod.POST)
    public void sentApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token) {
        applicationService.sendApplicationForApproval(token, idApplication);
    }

    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public List<Application> getListApplicationsClientForApproval(@RequestParam("userId") long userId,
                                                                  @RequestHeader("token") String token) {
        return applicationService.getApplicationsClientForApproval(userId, token);
    }

    @RequestMapping(value = "/applications/my", method = RequestMethod.GET)
    public List<Application> getMyListApplicationsForApproval(@RequestHeader("token") String token) {
        return applicationService.getApplicationsForApproval(token);
    }

    @RequestMapping(value = "/applications/{id}/approve", method = RequestMethod.POST)
    public void approveApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token) {
        applicationService.approveApplication(idApplication, token);
    }

    @RequestMapping(value = "/applications/{id}/negative", method = RequestMethod.POST)
    public void negativeApplication(@PathVariable("id") long idApplication, @RequestHeader("token") String token,
                                    @RequestBody Reason reason) {
        applicationService.negativeApplication(idApplication, token, reason.getReason());
    }

}
