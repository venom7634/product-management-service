package com.example.productmanagementservice.database;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationDatabase {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public VerificationDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkingUser(String login, String password) {
        String query = "select * from users where login = ?";

        List<User> users = jdbcTemplate.query(query, new Object[]{login}, new UsersRowMapper());

        if (!users.isEmpty()) {
            return users.get(0).getPassword().equals(password);
        }
        return false;
    }

    public boolean verificationOnExistsApplication(long idApplication) {
        Application application = getApplicationOfIdAndStatus(idApplication, 0);
        return !(application == null);
    }

    public boolean authenticationOfBankEmployee(String token) {
        User user = getUserOfToken(token);
        return user.getSecurity_id() == 0;
    }

    public boolean checkProductInApplicationsClient(long idApplication) {
        Application application = getApplicationOfId(idApplication);

        String query = "select * from applications where product = ? and id = ? and status = 2";
        List<Application> applications = jdbcTemplate.query(query, new Object[]{application.getProduct(), idApplication},
                new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean verificationOfBelongingApplicationToClient(long idApplication, String token) {
        User user = getUserOfToken(token);

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

    public boolean checkTokenInDatabase(String token) {
        User user = getUserOfToken(token);
        return !(user == null);
    }

    public boolean checkTotalAmountMoneyHasReachedMax(long idApplication) {
        int totalAmount = 0;
        String query = "select users.id, login, password, token, security, users.name, users.description " +
                "from users JOIN applications ON users.id = client_id where applications.id = ?";

        List<User> users = jdbcTemplate.query(query, new Object[]{idApplication}, new UsersRowMapper());

        query = "select * from applications where client_id = ? and status = 2";
        List<Application> applications = jdbcTemplate.query(query, new Object[]{users.get(0).getId()}, new ApplicationsRowMapper());

        for (Application app : applications) {
            if (app.getAmount() != null) {
                totalAmount += Integer.parseInt(app.getAmount());
            }
            if (app.getLimit() != null) {
                totalAmount += Integer.parseInt(app.getLimit());
            }
        }

        Application application = getApplicationOfId(idApplication);

        if (application.getLimit() != null) {
            if ((Integer.parseInt(application.getLimit()) + totalAmount) <= 1000) {
                return false;
            }
        }
        if (application.getAmount() != null) {
            if ((Integer.parseInt(application.getAmount()) + totalAmount) <= 1000) {
                return false;
            }
        }

        if (application.getAmount() == null && application.getLimit() == null) {
            return false;
        }
        return true;
    }

    public User getUserOfToken(String token) {
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[]{token}, new UsersRowMapper());

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public Application getApplicationOfId(long idApplication) {
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication},
                new ApplicationsRowMapper());

        if (applications.isEmpty()) {
            return null;
        }

        return applications.get(0);
    }

    public Application getApplicationOfIdAndStatus(long idApplication, int status) {
        String query = "select * from applications where id = ? and status = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication, status},
                new ApplicationsRowMapper());

        if (applications.isEmpty()) {
            return null;
        }

        return applications.get(0);
    }
}
