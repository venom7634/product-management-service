package com.example.productmanagementservice.database.mappers;

import com.example.productmanagementservice.entity.Application;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ApplicationsRowMapper implements RowMapper {

    public Application mapRow(ResultSet resultSet, int i) throws SQLException {
        String[] columns = {"product", "limitOnCard", "timeInMonth", "amount", "description"};
        Application app = new Application();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        app.setId(resultSet.getInt("id"));
        app.setStatus(resultSet.getInt("status"));
        app.setClient_id(resultSet.getInt("client_id"));


        for (String element : columns) {
            for (int x = 1; x <= resultSetMetaData.getColumnCount(); x++) {
                if (element.equals(resultSetMetaData.getColumnName(x))) {
                    switch (element) {
                        case "product":
                            app.setProduct(resultSet.getString("product"));
                            break;
                        case "limitOnCard":
                            app.setLimit(resultSet.getString("limitOnCard"));
                            break;
                        case "timeInMonth":
                            app.setTimeInMonth(resultSet.getString("timeInMonth"));
                            break;
                        case "amount":
                            app.setAmount(resultSet.getString("amount"));
                            break;
                        case "description":
                            app.setDescription(resultSet.getString("description"));
                            break;
                    }
                }
            }
        }
        return app;
    }

}
