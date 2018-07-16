package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.ProductsRepository;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductsVerificator {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationVerificator applicationVerificator;

    @Autowired
    private ProductsRepository productsRepository;
    @Autowired
    public ProductsVerificator(JdbcTemplate jdbcTemplate, ApplicationVerificator applicationVerificator) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicationVerificator = applicationVerificator;
    }

    public boolean checkProductInApplicationsClient(long idApplication) {
        Application application = applicationVerificator.getApplicationOfId(idApplication);

        List<Application> applications = productsRepository.getApplicationsByValues
                (application.getProduct(),idApplication,Application.status.APPROVED.ordinal());

        return !applications.isEmpty();
    }

}
