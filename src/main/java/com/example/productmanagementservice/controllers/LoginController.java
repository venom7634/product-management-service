package com.example.productmanagementservice.controllers;

import com.example.productmanagementservice.entity.data.Account;
import com.example.productmanagementservice.entity.data.Token;
import com.example.productmanagementservice.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final LoginService loginHandler;

    @Autowired
    public LoginController(LoginService loginHandler) {
        this.loginHandler = loginHandler;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Token signUp(@RequestBody Account account) {
        return loginHandler.login(account.getLogin(), account.getPassword());
    }
}
