package com.example.productmanagementservice.database;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.ProductsDescriptionRowMapper;
import com.example.productmanagementservice.database.mappers.ProductsRowMapper;
import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.products.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseHandler {

    private final JdbcTemplate jdbcTemplate;
    private final VerificationDatabase verificationDatabase;

    @Autowired
    public DatabaseHandler(JdbcTemplate jdbcTemplate, VerificationDatabase verificationDatabase) {
        this.jdbcTemplate = jdbcTemplate;
        this.verificationDatabase = verificationDatabase;
    }



    public void sendApplicationToConfirmation(long idApplication) {
        jdbcTemplate.update("UPDATE applications SET status = '1' WHERE id = ?", idApplication);
    }

    public void addDebitCardToApplication(long idApplication) {
        jdbcTemplate.update("UPDATE applications SET product = 'debit-card', " +
                "limitOnCard = null, amount = null, timeInMonth = null " +
                "WHERE id = ?", idApplication);
    }

    public void addCreditCardToApplication(long idApplication, int limit) {
        jdbcTemplate.update("UPDATE applications SET product = 'credit-card',amount = null, timeInMonth = null," +
                " limitOnCard = ? WHERE id = ?", limit, idApplication);
    }

    public void addCreditCashToApplication(long idApplication, int amount, int timeInMonth) {
        jdbcTemplate.update("UPDATE applications SET product = 'credit-cash',limitOnCard = null,  amount = ?," +
                "timeInMonth = ? WHERE id = ?", amount, timeInMonth, idApplication);
    }

    public List<Application> getListApplicationsOfDataBase(long userId) {
        String query = "select applications.id, status, client_id, product, limitOnCard, amount, timeInMonth " +
                "from applications " +
                "INNER JOIN users ON client_id = users.id " +
                "where client_id = ? AND status = 1";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{userId}, new ApplicationsRowMapper());
        return applications;
    }

    public Product getProductOfDataBase(String product) {
        int id = 0;
        switch (product) {
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

        return (Product) jdbcTemplate.queryForObject(query, new Object[]{ id }, new ProductsDescriptionRowMapper());
    }

    public String getLoginByToken(String token) {
        return verificationDatabase.getUserOfToken(token).getLogin();
    }

    public long getIdByLogin(String login){
        String query = "select * from users where login = ?";
        List<User> users = jdbcTemplate.query(query, new Object[] {login}, new UsersRowMapper());
        return users.get(0).getId();
    }

    public String getStatusByLogin(String login){
        String query = "select * from users where login = ?";
        List<User> users = jdbcTemplate.query(query, new Object[] {login}, new UsersRowMapper());
        return users.get(0).getSecurity_id()+"";
    }
    public void approveApplication(long idApplication) {
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{idApplication}, new ApplicationsRowMapper());

        String product = applications.get(0).getProduct();
        jdbcTemplate.update("UPDATE applications SET status = 3, " +
                "description = 'One user can have only one product of the same type' WHERE product = ?", product);

        jdbcTemplate.update("UPDATE applications SET status = 2 WHERE id = ?", idApplication);
    }

    public void negativeApplication(long idApplication, String reason) {
        jdbcTemplate.update("UPDATE applications SET status = 3, description = ? WHERE id = ?", reason, idApplication);
    }

    public List<Product> getProductsForClient(long userId) {
        String query = "SELECT products.id, products.name FROM products " +
                "INNER JOIN applications ON applications.product = products.name " +
                "INNER JOIN users ON users.id = applications.id " +
                "WHERE applications.status = 2 AND users.id = ? ";

        List<Product> products = jdbcTemplate.query(query, new Object[]{userId}, new ProductsRowMapper());

        return products;
    }
}
