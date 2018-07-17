package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.entity.products.Statistic;
import com.example.productmanagementservice.exceptions.NoAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductsRepository productsRepository;
    private final LoginService loginService;
    private final UserVerificator userVerificator;

    @Autowired
    public ProductService(LoginService loginService, ProductsRepository productsRepository,
                          UserVerificator userVerificator) {
        this.loginService = loginService;
        this.userVerificator = userVerificator;
        this.productsRepository = productsRepository;
    }

    public Product getDescriptionDebitCard() {
        return getProductOfName("debit-card");
    }

    public Product getDescriptionCreditCard() {
        return getProductOfName("credit-card");
    }

    public Product getDescriptionCreditCash() {
        return getProductOfName("credit-cash");
    }

    public List<Product> getProductsForClient(String token, long userId) {

        if (userVerificator.checkTokenInDatabase(token)) {
            if (!loginService.checkTokenOnValidation(token)
                    || !userVerificator.authenticationOfBankEmployee(token)) {
                throw new NoAccessException();
            }
        } else {
            throw new NoAccessException();
        }

        return productsRepository.getProductsForClient(Application.status.APPROVED.ordinal(), userId);
    }

    public Product getProductOfName(String product) {
        int id = 0;
        switch (product) {
            case "debit-card":
                id = Product.type.DEBIT_CARD.ordinal() + 1;
                break;
            case "credit-card":
                id = Product.type.CREDIT_CARD.ordinal() + 1;
                break;
            case "credit-cash":
                id = Product.type.CREDIT_CASH.ordinal() + 1;
                break;
        }

        return productsRepository.getProductOfDataBase(id);
    }

    public List<Statistic> getStatisticUsesProducts(String token) {
        if (userVerificator.checkTokenInDatabase(token)) {
            if (!loginService.checkTokenOnValidation(token)
                    || !userVerificator.authenticationOfBankEmployee(token)) {
                throw new NoAccessException();
            }
        } else {
            throw new NoAccessException();
        }

        return calculatePercent(productsRepository.getApprovedStatistics(Application.status.APPROVED.ordinal()));
    }

    public List<Statistic> getStatisticsNegativeApplications(String token) {
        if (userVerificator.checkTokenInDatabase(token)) {
            if (!loginService.checkTokenOnValidation(token)
                    || !userVerificator.authenticationOfBankEmployee(token)) {
                throw new NoAccessException();
            }
        } else {
            throw new NoAccessException();
        }
        return calculatePercent(productsRepository.getNegativeStatistics(Application.status.NEGATIVE.ordinal()));
    }


    public List<Statistic> calculatePercent(List<Statistic> statistics) {
        double count = 0;

        for (Statistic statistic : statistics) {
            count += statistic.getCount();
        }

        for (Statistic statistic : statistics) {
            statistic.setPercent(Math.round(statistic.getCount() / count * 10000) / 100.0);
        }

        return statistics;
    }
}
