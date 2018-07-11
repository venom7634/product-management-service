package com.example.productmanagementservice.database;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class CreatorInDatabase {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Application createNewApplication(String token){
        String query = "select * from clients where token = ?";
        int status = 0;
        Application result = new Application();
        result.setId(-1);

        List<User> users = jdbcTemplate.query(query, new Object[] { token }, new UsersRowMapper());

        jdbcTemplate.update("INSERT INTO applications (client_id,status) " +
                "VALUES ('"+ users.get(0).getId()+"',0)");

        query = "select * from applications where client_id = ? AND status = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { users.get(0).getId(),status }, new ApplicationsRowMapper());

        for (Application app: applications){
            if(app.getId() > result.getId()){
                result = app;
            }
        }
        return result;
    }

    public String createToken(String login){
        long time = new Date().getTime()/1000+1800;
        Key key = MacProvider.generateKey();

        String token = Jwts.builder()
                .setSubject(login)
                .signWith(SignatureAlgorithm.HS512, key).setExpiration(new Date(time))
                .compact();

        jdbcTemplate.update("UPDATE clients SET token = '" + token + "' WHERE login = '" + login + "'");

        return token;
    }

    @PostConstruct
    public void createTables(){
        jdbcTemplate.execute("DROP TABLE IF EXISTS clients ");
        jdbcTemplate.execute("DROP TABLE IF EXISTS applications ");
        jdbcTemplate.execute("DROP TABLE IF EXISTS products ");
        jdbcTemplate.execute("CREATE TABLE clients(" +
                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login NCHAR(20) UNIQUE," +
                "password NCHAR(20) NOT NULL," +
                "token NCHAR(255)," +
                "security INTEGER NOT NULL,"+
                "name NCHAR(20)  NOT NULL," +
                "description NCHAR(100))");

        jdbcTemplate.execute("CREATE TABLE applications(" +
                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "client_id INTEGER NOT NULL," +
                "status INTEGER NOT NULL," +
                "product NCHAR(20)," +
                "limitOnCard INTEGER," +
                "amount INTEGER,"+
                "timeInMonth INTEGER," +
                "FOREIGN KEY(client_id) REFERENCES clients(id))");

        jdbcTemplate.execute("CREATE TABLE products(" +
                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name NCHAR(30) NOT NULL," +
                "description NCHAR(255) NOT NULL)");

        jdbcTemplate.update("INSERT INTO clients (login,password,token,security,name,description) " +
                "VALUES ('katya','0502','','0','Katya','student'), " +
                "('viktor','1234','','1','Viktor','3 years experience')");
        jdbcTemplate.update("INSERT INTO products (name,description) VALUES ('debit-card','Regular client card')," +
                    "('credit-card','This card have something amount money that you can spend')," +
                "('credit-cash','You are given the amount of money that you will return in a period of time')");
    }
}
