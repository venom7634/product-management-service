package com.example.productmanagementservice.entity;

public class User {

    int id;
    String login;
    String password;
    String token;
    int security_id;
    String name;
    String description;

    public User(){

    }

    public User(int id, String login, String password, String token, int security_id, String name, String description) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
        this.security_id = security_id;
        this.description = description;
        this.token = token;
    }

    public int getSecurity_id() {
        return security_id;
    }

    public void setSecurity_id(int security_id) {
        this.security_id = security_id;
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
