package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.ProductsDescriptionRowMapper;
import com.example.productmanagementservice.database.mappers.ProductsRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void addDebitCardToApplication(long idApplication) {
        jdbcTemplate.update("UPDATE applications SET product = 'debit-card', " +
                "limit_on_card = NULL, amount = null, time_in_month = null " +
                "WHERE id = ?", idApplication);
    }

    public void addCreditCardToApplication(long idApplication, int limit) {
        jdbcTemplate.update("UPDATE applications SET product = 'credit-card',amount = null, time_in_month = null," +
                " limit_on_card = ? WHERE id = ?", limit, idApplication);
    }

    public void addCreditCashToApplication(long idApplication, int amount, int timeInMonth) {
        jdbcTemplate.update("UPDATE applications SET product = 'credit-cash',limit_on_card = null,  amount = ?," +
                "time_in_month = ? WHERE id = ?", amount, timeInMonth, idApplication);
    }

    public Product getProductOfDataBase(long id) {
        String query = "select * from products where id = ?";
        return (Product) jdbcTemplate.queryForObject(query, new Object[]{id}, new ProductsDescriptionRowMapper());
    }

    public List<Product> getProductsForClient(long userId) {
        String query = "SELECT products.id, products.name FROM products " +
                "INNER JOIN applications ON applications.product = products.name " +
                "INNER JOIN users ON users.id = applications.id " +
                "WHERE applications.status = ? AND users.id = ? ";

        List<Product> products = jdbcTemplate.query(query, new Object[]{Application.status.APPROVED.ordinal(),
                userId}, new ProductsRowMapper());

        return products;
    }

    public String getProductOfApplication(long idApplication) {
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication}, new ApplicationsRowMapper());

        return applications.get(0).getProduct();
    }

}
