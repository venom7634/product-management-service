package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.DataRepository;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserVerificator {

    private final DataRepository dataRepository;


    @Autowired
    public UserVerificator(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public boolean checkingUser(String login, String password) {
        List<User> users = dataRepository.getUsersByLogin(login);

        if (!users.isEmpty()) {
            return users.get(0).getPassword().equals(password);
        }

        return false;
    }

    public boolean authenticationOfBankEmployee(String token) {
        User user = getUserOfToken(token);
        return user.getSecurity_id() == User.access.EMPLOYEE_BANK.ordinal();
    }

    public boolean checkTokenInDatabase(String token) {
        User user = getUserOfToken(token);
        return !(user == null);
    }

    public User getUserOfToken(String token) {
        List<User> users = dataRepository.getUsersByToken(token);

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}
