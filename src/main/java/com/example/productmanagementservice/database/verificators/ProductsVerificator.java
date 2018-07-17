package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.ApplicationsRepository;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductsVerificator {


    private final ApplicationsRepository applicationsRepository;
    private final ApplicationVerificator applicationVerificator;

    @Autowired
    public ProductsVerificator(ApplicationVerificator applicationVerificator, ApplicationsRepository applicationsRepository) {
        this.applicationVerificator = applicationVerificator;
        this.applicationsRepository = applicationsRepository;
    }

    public boolean checkProductInApplicationsClient(long idApplication) {
        Application application = applicationVerificator.getApplicationOfId(idApplication);

        List<Application> applications = applicationsRepository.getApplicationsByValues
                (application.getProduct(), Application.status.APPROVED.ordinal());

        return !applications.isEmpty();
    }

}
