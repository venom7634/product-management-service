package com.example.productmanagementservice.database.handlers;

import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DataHandler {

    private final JdbcTemplate jdbcTemplate;
    private final UserVerificator userVerificator;

    @Autowired
    public DataHandler(JdbcTemplate jdbcTemplate, UserVerificator userVerificator) {
        this.jdbcTemplate = jdbcTemplate;
        this.userVerificator = userVerificator;
    }

    public void addTokenInDatabase(String token, String login) {
        jdbcTemplate.update("UPDATE users SET token = ? WHERE login = ?", token, login);
    }

    public String getLoginByToken(String token) {
        return userVerificator.getUserOfToken(token).getLogin();
    }

    public long getIdByLogin(String login) {
        String query = "select * from users where login = ?";
        List<User> users = jdbcTemplate.query(query, new Object[]{login}, new UsersRowMapper());
        return users.get(0).getId();
    }

    public String getStatusByLogin(String login) {
        String query = "select * from users where login = ?";
        List<User> users = jdbcTemplate.query(query, new Object[]{login}, new UsersRowMapper());
        return users.get(0).getSecurity_id() + "";
    }

}
