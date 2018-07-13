package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplicationVerificator {

    private final JdbcTemplate jdbcTemplate;
    private final UserVerificator userVerificator;

    @Autowired
    public ApplicationVerificator(JdbcTemplate jdbcTemplate, UserVerificator userVerificator) {
        this.jdbcTemplate = jdbcTemplate;
        this.userVerificator = userVerificator;
    }

    public boolean verificationOnExistsApplication(long idApplication) {
        Application application = getApplicationOfIdAndStatus(idApplication, 0);
        return !(application == null);
    }

    public boolean verificationOfBelongingApplicationToClient(long idApplication, String token) {
        User user = userVerificator.getUserOfToken(token);

        String query = "select * from applications where id = ? and client_id = ?";
        List<Application> applications = jdbcTemplate.query(query,
                new Object[]{idApplication, user.getId()}, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkExistenceOfApplication(long idApplication) {
        Application application = getApplicationOfId(idApplication);
        return !(application == null);
    }

    public boolean checkForChangeStatusApplication(long idApplication) {
        Application application = getApplicationOfIdAndStatus(idApplication, 1);
        return !(application == null);
    }

    public boolean checkIsEmptyOfApplication(long idApplication) {
        Application application = getApplicationOfId(idApplication);
        String product = application.getProduct();
        return (product == null);
    }

    public Application getApplicationOfId(long idApplication) {
        String query = "select * from applications where id = ?";
        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication},
                new ApplicationsRowMapper());

        return applications.get(0);
    }

    public Application getApplicationOfIdAndStatus(long idApplication, int status) {
        String query = "select * from applications where id = ? and status = ?";
        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication, status},
                new ApplicationsRowMapper());
        return applications.get(0);
    }
}
