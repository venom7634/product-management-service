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

    public boolean checkingUser(String login, String password){
        String query = "select * from users where login = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { login }, new UsersRowMapper());

        if(!users.isEmpty()){
            return users.get(0).getPassword().equals(password);
        }
        return false;
    }

    public boolean verificationOnExistsApplication(String token, long id){
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { token }, new UsersRowMapper());

        if(users.isEmpty()){
            return false;
        }

        query = "select * from applications where id = ? AND status = 0";
        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());
        return !applications.isEmpty();
    }

    public boolean authenticationOfBankEmployee(String token){
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { token }, new UsersRowMapper());

        if(!users.isEmpty()){
            return users.get(0).getSecurity_id() == 0;
        }
        return false;
    }

    public boolean checkProductInApplicationsClient(long id){
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id }, new ApplicationsRowMapper());

        query = "select * from applications where product = ? and id = ? and status = 2";
        applications = jdbcTemplate.query(query,new Object[] { applications.get(0).getProduct(), id },
                new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean verificationOfBelongingApplicationToClient(long id, String token){
        String query = "select * from users where token = ?";
        List<User> users = jdbcTemplate.query(query,new Object[] { token }, new UsersRowMapper());

        query = "select * from applications where id = ? and client_id = ?";
        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id, users.get(0).getId()}, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkExistenceOfApplication(long id){
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkIsEmptyOfApplication(long id){
        String query = "select * from applications where id = ?";
        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id }, new ApplicationsRowMapper());

        return applications.get(0).getProduct() == null;
    }

    public boolean checkTokenInDatabase(String token){
        String query = "select * from users where token = ?";
        List<User> users = jdbcTemplate.query(query,
                new Object[] { token }, new UsersRowMapper());

        return !users.isEmpty();
    }
}
