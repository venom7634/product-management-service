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
public class VerificationDatabase {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public VerificationDatabase(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkingUser(String login, String password){
        String query = "select * from users where login = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { login }, new UsersRowMapper());

        if(!users.isEmpty()){
            return users.get(0).getPassword().equals(password);
        }
        return false;
    }

    public boolean verificationOnExistsApplication(String token, long id){
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { token }, new UsersRowMapper());

        if(users.isEmpty()){
            return false;
        }

        query = "select * from applications where id = ? AND status = 0";
        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());
        return !applications.isEmpty();
    }

    public boolean authenticationOfBankEmployee(String token){
        String query = "select * from users where token = ?";

        List<User> users = jdbcTemplate.query(query, new Object[] { token }, new UsersRowMapper());

        if(!users.isEmpty()){
            return users.get(0).getSecurity_id() == 0;
        }
        return false;
    }

    public boolean checkProductInApplicationsClient(long id){
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id }, new ApplicationsRowMapper());

        query = "select * from applications where product = ? and id = ? and status = 2";
        applications = jdbcTemplate.query(query,new Object[] { applications.get(0).getProduct(), id },
                new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkTotalAmountMoneyHasReachedMax(long id){
        int totalAmount = 0;
        String query = "select users.id, login, password, token, security, users.name, users.description " +
                "from users JOIN applications ON users.id = client_id where applications.id = ?";

        List<User> users = jdbcTemplate.query(query,new Object[] { id }, new UsersRowMapper());

        List<Application> test = jdbcTemplate.query("select * from applications",
                new Object[] {}, new ApplicationsRowMapper());

        query = "select * from applications where client_id = ? and status = 2";
        List<Application> applications = jdbcTemplate.query(query, new Object[] { users.get(0).getId() }, new ApplicationsRowMapper());

        for(Application app: applications){
            if(app.getAmount() != null){
                totalAmount+=Integer.parseInt(app.getAmount());
            }
            if(app.getLimit() != null){
                totalAmount+=Integer.parseInt(app.getLimit());
            }
        }
        query = "select * from applications where id = ?";
        applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());

        if(applications.get(0).getLimit() != null){
            if((Integer.parseInt(applications.get(0).getLimit())+totalAmount) <= 1000){
                return false;
            }
        }

        if(applications.get(0).getAmount() != null){
            if((Integer.parseInt(applications.get(0).getAmount())+totalAmount) <= 1000){
                return false;
            }
        }
        return true;
    }
    public boolean verificationOfBelongingApplicationToClient(long id, String token){
        String query = "select * from users where token = ?";
        List<User> users = jdbcTemplate.query(query,new Object[] { token }, new UsersRowMapper());

        query = "select * from applications where id = ? and client_id = ?";
        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id, users.get(0).getId()}, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkExistenceOfApplication(long id){
        String query = "select * from applications where id = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkForChangeStatusApplication(long id){
        String query = "select * from applications where id = ? and status = 1";

        List<Application> applications = jdbcTemplate.query(query, new Object[] { id }, new ApplicationsRowMapper());

        return !applications.isEmpty();
    }

    public boolean checkIsEmptyOfApplication(long id){
        String query = "select * from applications where id = ?";
        List<Application> applications = jdbcTemplate.query(query,
                new Object[] { id }, new ApplicationsRowMapper());

        return applications.get(0).getProduct() == null;
    }

    public boolean checkTokenInDatabase(String token){
        String query = "select * from users where token = ?";
        List<User> users = jdbcTemplate.query(query,
                new Object[] { token }, new UsersRowMapper());

        return !users.isEmpty();
    }
}
