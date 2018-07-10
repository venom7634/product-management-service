package com.example.productmanagementservice.database;

import com.example.productmanagementservice.entity.Application;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

    public class ApplicationsRowMapper implements  RowMapper{

    public Application mapRow(ResultSet resultSet, int i) throws SQLException {
        Application app = new Application(resultSet.getInt("id"));

        return app;
    }

}
