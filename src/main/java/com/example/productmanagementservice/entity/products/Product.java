package com.example.productmanagementservice.entity.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

public class Product {

    public enum type {
        DEBIT_CARD("debit-card"),
        CREDIT_CARD("credit-card"),
        CREDIT_CASH("credit-cash");

        private String name;

        type(String name){
            this.name = name;
        }
    }

    String description;

    @JsonIgnore
    Integer id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String name;

    public Product() {

    }

    public Product(String description, Integer id, String name) {
        this.description = description;
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
