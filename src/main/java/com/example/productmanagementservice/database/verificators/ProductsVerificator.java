package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.entity.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsVerificator {

    private final JdbcTemplate jdbcTemplate;
    private final ApplicationVerificator applicationVerificator;

    @Autowired
    public ProductsVerificator(JdbcTemplate jdbcTemplate, ApplicationVerificator applicationVerificator) {
        this.jdbcTemplate = jdbcTemplate;
        this.applicationVerificator = applicationVerificator;
    }

    public boolean checkProductInApplicationsClient(long idApplication) {
        Application application = applicationVerificator.getApplicationOfId(idApplication);

        String query = "select * from applications where product = ? and id = ? and status = ?";
        List<Application> applications = jdbcTemplate.query(query, new Object[]{application.getProduct(),
                        idApplication, Application.status.APPROVED.ordinal()},
                new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

}
