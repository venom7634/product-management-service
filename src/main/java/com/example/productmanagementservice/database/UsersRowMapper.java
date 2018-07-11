package com.example.productmanagementservice.database;

import com.example.productmanagementservice.entity.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRowMapper implements RowMapper {


    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();

        user.setLogin(resultSet.getString("login"));
        user.setPassword(resultSet.getString("password"));
        user.setSecurity_id(resultSet.getInt("security"));
        user.setName(resultSet.getString("name"));
        user.setDescription(resultSet.getString("description"));
        user.setId(resultSet.getInt("id"));
        user.setToken(resultSet.getString("token"));

        return user;
    }
}
