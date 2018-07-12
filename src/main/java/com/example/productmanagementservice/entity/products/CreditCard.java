package com.example.productmanagementservice.entity.products;

public class CreditCard extends Product {

    int limit;

    public CreditCard() {

    }

    public CreditCard(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
