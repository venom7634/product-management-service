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
public class CreatorInDatabase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Application createNewApplication(String token) {
        String query = "select * from users where token = ?";
        Application result = new Application();
        result.setId(0);
        int status = 0;

        List<User> users = jdbcTemplate.query(query, new Object[]{token}, new UsersRowMapper());

        jdbcTemplate.update("INSERT INTO applications " +
                "(client_id, status, product, limit_on_card, amount, time_in_month, description) " +
                "VALUES (?, 0, null, null, null, null, null)", users.get(0).getId());

        query = "select * from applications where client_id = ? AND status = ?";

        List<Application> applications = jdbcTemplate.query(query,
                new Object[]{users.get(0).getId(), status}, new ApplicationsRowMapper());

        for (Application app : applications) {
            if (app.getId() > result.getId()) {
                result = app;
            }
        }
        return result;
    }

    public void addTokenInDatabase(String token, String login) {
        jdbcTemplate.update("UPDATE users SET token = ? WHERE login = ?", token, login);
    }
}
