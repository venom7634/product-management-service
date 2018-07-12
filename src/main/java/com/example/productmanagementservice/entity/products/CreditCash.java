package com.example.productmanagementservice.entity.products;

public class CreditCash extends Product {

    int timeInMonth;
    int amount;

    public CreditCash(int timeInMonth, int amount) {
        this.timeInMonth = timeInMonth;
        this.amount = amount;
    }

    public CreditCash() {

    }

    public int getTimeInMonth() {
        return timeInMonth;
    }

    public void setTimeInMonth(int limit) {
        this.timeInMonth = limit;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
