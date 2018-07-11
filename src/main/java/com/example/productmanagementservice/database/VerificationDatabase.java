package com.example.productmanagementservice.database;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VerificationDatabase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean checkingUser(String login, String password){
        String query = "select * from clients where login = ?";

        List<Client> clients = jdbcTemplate.query(query, new Object[] { login }, new ClientsRowMapper());

        if(!clients.isEmpty()){
            if(clients.get(0).getPassword().equals(password)){
                return true;
            }
        }
        return false;
    }

    public boolean verificationOnExistsApplication(String token, long id){
        String query = "select * from clients where token = ?";

        List<Client> clients = jdbcTemplate.query(query, new Object[] { token }, new ClientsRowMapper());

        if(clients.isEmpty()){
            return false;
        }

        query = "select * from applications where id = ? AND status = 0";
        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());
        if(applications.isEmpty()){
            return false;
        }
        return true;
    }
}
