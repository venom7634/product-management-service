package com.example.productmanagementservice.database.rowmappers;

import com.example.productmanagementservice.entity.Application;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ApplicationsRowMapper implements RowMapper {

    public Application mapRow(ResultSet resultSet, int i) throws SQLException {
        String[] columns = {"product", "limit_on_card", "time_in_month", "amount", "description"};
        Application app = new Application();

        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

        app.setId(resultSet.getInt("id"));
        app.setClient_id(resultSet.getInt("client_id"));


        for (String element : columns) {
            for (int x = 1; x <= resultSetMetaData.getColumnCount(); x++) {
                if (element.equals(resultSetMetaData.getColumnName(x))) {
                    switch (element) {
                        case "product":
                            app.setProduct(resultSet.getString("product"));
                            break;
                        case "limit_on_card":
                            app.setLimit(resultSet.getString("limit_on_card"));
                            break;
                        case "time_in_month":
                            app.setTimeInMonth(resultSet.getString("time_in_month"));
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
