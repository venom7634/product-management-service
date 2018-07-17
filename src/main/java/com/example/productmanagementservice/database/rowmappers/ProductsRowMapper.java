package com.example.productmanagementservice.database.rowmappers;

import com.example.productmanagementservice.entity.products.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductsRowMapper implements RowMapper {

    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        Product product = new Product();

        product.setDescription(resultSet.getString("description"));
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));

        return product;
    }
}