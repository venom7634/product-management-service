package com.example.productmanagementservice.database.handlers;

import com.example.productmanagementservice.database.mappers.ApplicationsRowMapper;
import com.example.productmanagementservice.database.mappers.UsersRowMapper;
import com.example.productmanagementservice.entity.Application;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.services.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ApplicationsHandler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    LoginService loginService;

    public void createNewApplicationInDatabase(String token) {
        jdbcTemplate.update("INSERT INTO applications " +
                        "(client_id, status, product, limit_on_card, amount, time_in_month, description) " +
                        "VALUES (?, ?, null, null, null, null, null)",
                loginService.getIdByToken(token), Application.status.CREATED.ordinal());
    }

    public List<Application> getAllClientApplications(String token) {
        String query = "select * from applications where client_id = ? AND status = ?";
        return jdbcTemplate.query(query,
                new Object[]{loginService.getIdByToken(token), Application.status.CREATED.ordinal()}, new ApplicationsRowMapper());
    }

    public void sendApplicationToConfirmation(long idApplication) {
        jdbcTemplate.update("UPDATE applications SET status = ? WHERE id = ?", Application.status.SENT.ordinal(),
                idApplication);
    }

    public List<Application> getListSentApplicationsOfDataBase(long userId) {
        String query = "select applications.id, status, client_id, product, limit_on_card, amount, time_in_month " +
                "from applications " +
                "INNER JOIN users ON client_id = users.id " +
                "where client_id = ? AND status = ?";

        List<Application> applications = jdbcTemplate.query(query, new Object[]{userId,
                Application.status.SENT.ordinal()}, new ApplicationsRowMapper());
        return applications;
    }

    public List<Application> getListApprovedApplicationsOfDatabase(long userId) {
        String query = "select * from applications where client_id = ? and status = ?";
        return jdbcTemplate.query(query, new Object[]{userId,
                Application.status.APPROVED.ordinal()}, new ApplicationsRowMapper());
    }

    public void approveApplication(long idApplication) {
        jdbcTemplate.update("UPDATE applications SET status = ? WHERE id = ?",
                Application.status.APPROVED.ordinal(), idApplication);
    }

    public void setNegativeOfAllIdenticalProducts(String product) {
        jdbcTemplate.update("UPDATE applications SET status = ?, " +
                        "description = 'One user can have only one product of the same type' WHERE product = ?",
                Application.status.NEGATIVE.ordinal(), product);
    }

    public void negativeApplication(long idApplication, String reason) {
        jdbcTemplate.update("UPDATE applications SET status = ?, description = ? WHERE id = ?",
                Application.status.NEGATIVE.ordinal(), reason, idApplication);
    }

    public User getUserByIdApplication(long idApplication) {
        String query = "select users.id, login, password, token, security, users.name, users.description " +
                "from users JOIN applications ON users.id = client_id where applications.id = ?";

        return ((List<User>) jdbcTemplate.query(query, new Object[]{idApplication}, new UsersRowMapper())).get(0);
    }

}
