package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.UsersRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.data.Token;
import com.example.productmanagementservice.exceptions.NoAccessException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;


@Service
public class LoginService {

    private final UsersRepository usersRepository;
    private final UserVerificator userVerificator;

    @Autowired
    public LoginService(UserVerificator userVerificator, UsersRepository usersRepository) {
        this.userVerificator = userVerificator;
        this.usersRepository = usersRepository;
    }

    public Token login(String login, String password) {
        if (!userVerificator.checkingUser(login, password)) {
            throw new NoAccessException();
        }

        return new Token(createToken(login));
    }

    private String createToken(String login) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);

        User user = usersRepository.getUsersByLogin(login).get(0);

        String token = Jwts.builder()
                .setSubject("" + user.getId())
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(calendar.getTime())
                .setAudience(user.getSecurity() + "")
                .compact();

        usersRepository.addTokenInDatabase(token, login);

        return token;
    }

    public long getIdByToken(String token) {
        String key = usersRepository.getUsersByToken(token).get(0).getLogin();
        return Long.parseLong(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean checkTokenOnValidation(String token) {
        String key = usersRepository.getUsersByToken(token).get(0).getLogin();

        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();
        } catch (ExpiredJwtException e) {
            return false;
        }

        return true;
    }
}
