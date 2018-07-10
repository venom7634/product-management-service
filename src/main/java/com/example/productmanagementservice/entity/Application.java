package com.example.productmanagementservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Application {

    int id;

    @JsonIgnore
    private int status;
    @JsonIgnore
    private int client_id;

    private String product;
    private Integer limit;
    private Integer amount;
    private Integer timeInMonth;

    public Application(){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient_id() {
        return client_id;
    }

    public void setClient_id(int client_id) {
        this.client_id = client_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getTimeInMonth() {
        return timeInMonth;
    }

    public void setTimeInMonth(Integer timeInMonth) {
        this.timeInMonth = timeInMonth;
    }
}
