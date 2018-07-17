package com.example.productmanagementservice.entity;

public class User {

    public enum access {
        EMPLOYEE_BANK,
        CLIENT
    }

    int id;
    String login;
    String password;
    int security;
    String token;
    String name;
    String description;

    public User() {

    }

    public User(int id, String login, String password, String token, int security, String name, String description) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.security = security;
        this.description = description;
        this.token = token;
    }

    public int getSecurity() {
        return security;
    }

    public void setSecurity(int security) {
        this.security = security;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
