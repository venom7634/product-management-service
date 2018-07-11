package com.example.productmanagementservice.entity.products;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Product {

    String description;
    @JsonIgnore
    Integer id;

    public Product(){

    }

    public Product(String description){
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
