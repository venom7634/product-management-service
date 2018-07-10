package com.example.productmanagementservice.database;

import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.Client;
import com.example.productmanagementservice.entity.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseHandler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Token returnTokenForLogin(String login){
        String query = "select token from clients where login = ?";

        return new Token(jdbcTemplate.queryForObject(query, new Object[] { login }, String.class));
    }

    public Application createNewApplication(String token){
        String query = "select * from clients where token = ?";
        int status = 0;
        Application result = new Application();
        result.setId(-1);
        List<Client> clients = jdbcTemplate.query(query, new Object[] { token }, new ClientsRowMapper());

        jdbcTemplate.update("INSERT INTO applications (client_id,status) " +
                "VALUES ('"+clients.get(0).getId()+"',0)");

        query = "select * from applications where client_id = ? AND status = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { clients.get(0).getId(),status }, new ApplicationsRowMapper());

        for (Application app: applications){
            if(app.getId() > result.getId()){
                result = app;
            }
        }

        return result;
    }

    public void addDebitCardToApplication(long idApplication, String token){
        String query = "select * from clients where token = ?";

        List<Client> clients = jdbcTemplate.query(query, new Object[] { token }, new ClientsRowMapper());

        jdbcTemplate.update("UPDATE applications SET product = 'debit-card' WHERE client_id ='"
                + clients.get(0).getId() + "' AND id ='" + idApplication + "'");
    }

    public void addCreditCardToApplication(long idApplication, String token,int limit){
        String query = "select * from clients where token = ?";

        List<Client> clients = jdbcTemplate.query(query, new Object[] { token }, new ClientsRowMapper());

        jdbcTemplate.update("UPDATE applications SET product = 'credit-card', limitOnCard = '"
                + limit + "' WHERE client_id ='" + clients.get(0).getId() + "' AND id ='" + idApplication + "'");
    }

    public void addCreditCashToApplication(long idApplication, String token,int amount, int timeInMonth){
        String query = "select * from clients where token = ?";

        List<Client> clients = jdbcTemplate.query(query, new Object[] { token }, new ClientsRowMapper());

        jdbcTemplate.update("UPDATE applications SET product = 'credit-cash', amount = '"
                + amount + "', timeInMonth = '" +timeInMonth + "' WHERE client_id ='"
                + clients.get(0).getId() + "' AND id ='" + idApplication + "'");
    }

    public void createTables(){
        jdbcTemplate.execute("DROP TABLE IF EXISTS clients ");
        jdbcTemplate.execute("DROP TABLE IF EXISTS applications ");
        jdbcTemplate.execute("CREATE TABLE clients(" +
                "id  INTEGER PRIMARY KEY AUTOINCREMENT," +
                "login NCHAR(20) UNIQUE," +
                "password NCHAR(20) NOT NULL," +
                "token NCHAR(255) NOT NULL," +
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
                "VALUES ('katya','0502','AB830','0','Katya','student')");
        jdbcTemplate.update("INSERT INTO clients (login,password,token,security,name,description) " +
                "VALUES ('viktor','1234','iamvitya031','1','Viktor','3 years experience')");
    }

}
