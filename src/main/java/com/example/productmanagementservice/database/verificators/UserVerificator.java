package com.example.productmanagementservice.database.verificators;

import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserVerificator {

    private final UsersRepository usersRepository;

    @Autowired
    public UserVerificator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public boolean checkingUser(String login, String password) {
        List<User> users = usersRepository.getUsersByLogin(login);

        if (!users.isEmpty()) {
            return users.get(0).getPassword().equals(password);
        }

        return false;
    }

    public boolean authenticationOfBankEmployee(String token) {
        User user = getUserOfToken(token);
        return user.getSecurity() == User.access.EMPLOYEE_BANK.ordinal();
    }

    public boolean checkTokenInDatabase(String token) {
        User user = getUserOfToken(token);
        return !(user == null);
    }

    public User getUserOfToken(String token) {
        List<User> users = usersRepository.getUsersByToken(token);

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }
}
