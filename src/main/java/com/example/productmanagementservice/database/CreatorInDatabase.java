package com.example.productmanagementservice.database;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreatorInDatabase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Application createNewApplication(String token) {
        String query = "select * from users where token = ?";
        Application result = new Application();
        result.setId(0);
        int status = 0;

        List<User> users = jdbcTemplate.query(query, new Object[]{token}, new UsersRowMapper());

        jdbcTemplate.update("INSERT INTO applications (client_id, status) " +
                "VALUES (?,0)", users.get(0).getId());

        query = "select * from applications where client_id = ? AND status = ?";

        List<Application> applications = jdbcTemplate.query(query,
                new Object[]{users.get(0).getId(), status}, new ApplicationsRowMapper());

        for (Application app : applications) {
            if (app.getId() > result.getId()) {
                result = app;
            }
        }
        return result;
    }

    public void addTokenInDatabase(String token, String login) {
        jdbcTemplate.update("UPDATE users SET token = ? WHERE login = ?", token, login);
    }

//    @PostConstruct
//    public void createTables() {
//        jdbcTemplate.execute("DROP TABLE IF EXISTS users ");
//        jdbcTemplate.execute("DROP TABLE IF EXISTS applications ");
//        jdbcTemplate.execute("DROP TABLE IF EXISTS products ");
//        jdbcTemplate.execute("CREATE TABLE users(" +
//                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "login NCHAR(20) UNIQUE," +
//                "password NCHAR(20) NOT NULL," +
//                "token NCHAR(255)," +
//                "security INTEGER NOT NULL," +
//                "name NCHAR(20)  NOT NULL," +
//                "description NCHAR(100))");
//
//        jdbcTemplate.execute("CREATE TABLE applications(" +
//                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "client_id INTEGER NOT NULL," +
//                "status INTEGER NOT NULL," +
//                "product NCHAR(20)," +
//                "limitOnCard INTEGER," +
//                "amount INTEGER," +
//                "timeInMonth INTEGER," +
//                "description NCHAR(300)," +
//                "FOREIGN KEY(client_id) REFERENCES users(id))");
//
//        jdbcTemplate.execute("CREATE TABLE products(" +
//                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "name NCHAR(30) NOT NULL," +
//                "description NCHAR(255) NOT NULL)");
//
//        jdbcTemplate.update("INSERT INTO users (login, password, token, security, name, description) " +
//                "VALUES ('katya', '0502', '', '0', 'Katya', '3 years experience'), " +
//                "('victor', '1234', '', '1', 'Victor', 'student')");
//        jdbcTemplate.update("INSERT INTO products (name, description) VALUES ('debit-card', 'Regular client card')," +
//                "('credit-card', 'This card have something amount money that you can spend')," +
//                "('credit-cash', 'You are given the amount of money that you will return in a period of time')");
//    }
}
