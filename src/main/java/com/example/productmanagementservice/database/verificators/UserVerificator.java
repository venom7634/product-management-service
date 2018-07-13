package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserVerificator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserVerificator(JdbcTemplate jdbcTemplate) {
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

    public boolean authenticationOfBankEmployee(String token) {
        User user = getUserOfToken(token);
        return user.getSecurity_id() == User.access.EMPLOYEE_BANK.ordinal();
    }

    public boolean checkTokenInDatabase(String token) {
        User user = getUserOfToken(token);
        return !(user == null);
    }

    public User getUserOfToken(String token) {
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[]{token}, new UsersRowMapper());

        return users.get(0);
    }
}
