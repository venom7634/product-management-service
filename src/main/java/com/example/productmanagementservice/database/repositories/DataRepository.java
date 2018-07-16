package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addTokenInDatabase(String token, String login) {
        jdbcTemplate.update("UPDATE users SET token = ? WHERE login = ?", token, login);
    }

    public List<User> getUsersByLogin(String login) {
        String query = "select * from users where login = ?";

        return jdbcTemplate.query(query, new Object[]{login}, new UsersRowMapper());
    }

    public List<User> getUsersByToken(String token) {
        String query = "select * from users where token = ?";

        return jdbcTemplate.query(query, new Object[]{token}, new UsersRowMapper());
    }
}
