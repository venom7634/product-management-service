package com.example.productmanagementservice.database;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.ProductsRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseHandler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void sendApplicationToConfirmation(long id){
        jdbcTemplate.update("UPDATE applications SET status = '1' WHERE id = '" + id + "'");
    }

    public void addDebitCardToApplication(long idApplication){
        jdbcTemplate.update("UPDATE applications SET product = 'debit-card', " +
                "limitOnCard = null, amount = null, timeInMonth = null WHERE id ='" + idApplication + "'");
    }

    public void addCreditCardToApplication(long idApplication, int limit){
        jdbcTemplate.update("UPDATE applications SET product = 'credit-card',amount = null, timeInMonth = null," +
                " limitOnCard = '" + limit + "' WHERE id = " + idApplication);
    }

    public void addCreditCashToApplication(long idApplication,int amount, int timeInMonth){
        jdbcTemplate.update("UPDATE applications SET product = 'credit-cash',limitOnCard = null,  amount = '"
                + amount + "', timeInMonth = '" + timeInMonth + "' WHERE id ='" + idApplication + "'");
    }

    public List<Application> getListApplicationsOfDataBase(String token){
        String query = "select applications.id, status, client_id, product, limitOnCard, amount, timeInMonth " +
                "from applications " +
                "INNER JOIN clients ON client_id = clients.id " +
                "where token = ? AND status = 1";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { token }, new ApplicationsRowMapper());
        return applications;
    }

    public List<Application> getListApplicationsOfDataBase(long id){
        String query = "select applications.id, status, client_id, product, limitOnCard, amount, timeInMonth " +
                "from applications " +
                "INNER JOIN clients ON client_id = clients.id " +
                "where client_id = ? AND status = 1";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());
        return applications;
    }

    public Product getProduct(String product){
        int id = 0;
        switch (product){
            case "debit-card":
                id = 1;
                break;
            case "credit-card":
                id = 2;
                break;
            case "credit-cash":
                id = 3;
                break;
        }

        String query = "select * from products where id = ?";

        return  (Product) jdbcTemplate.queryForObject(query, new Object[] { id }, new ProductsRowMapper());
    }
}
