package com.example.productmanagementservice.database;

import com.example.productmanagementservice.entity.Client;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientsRowMapper implements RowMapper {


    public Client mapRow(ResultSet resultSet, int i) throws SQLException {
        Client client = new Client();
        client.setLogin(resultSet.getString("login"));
        client.setPassword(resultSet.getString("password"));
        client.setSecurity_id(resultSet.getInt("security"));
        client.setName(resultSet.getString("name"));
        client.setDescription(resultSet.getString("description"));
        client.setId(resultSet.getInt("id"));
        client.setToken(resultSet.getString("token"));

        return client;
    }
}
