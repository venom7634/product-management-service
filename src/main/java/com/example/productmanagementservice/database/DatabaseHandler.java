package com.example.productmanagementservice.database;

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
public class DatabaseHandler {

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

    @PostConstruct
    public void createTables(){
        jdbcTemplate.execute("DROP TABLE IF EXISTS clients ");
        jdbcTemplate.execute("DROP TABLE IF EXISTS applications ");
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

        jdbcTemplate.update("INSERT INTO clients (login,password,token,security,name,description) " +
                "VALUES ('katya','0502','','0','Katya','student')");
        jdbcTemplate.update("INSERT INTO clients (login,password,token,security,name,description) " +
                "VALUES ('viktor','1234','','1','Viktor','3 years experience')");
    }

}
