package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductsVerificator {

    private final ApplicationVerificator applicationVerificator;

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsVerificator(ApplicationVerificator applicationVerificator, ProductsRepository productsRepository) {
        this.applicationVerificator = applicationVerificator;
        this.productsRepository = productsRepository;
    }

    public boolean checkProductInApplicationsClient(long idApplication) {
        Application application = applicationVerificator.getApplicationOfId(idApplication);

        List<Application> applications = productsRepository.getApplicationsByValues
                (application.getProduct(), Application.status.APPROVED.ordinal());

        return !applications.isEmpty();
    }

}
