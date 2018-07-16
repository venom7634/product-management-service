package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.repositories.DataRepository;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.User;
import com.example.productmanagementservice.entity.data.Token;
import com.example.productmanagementservice.exceptions.NoAccessException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class LoginService {

    private final UserVerificator userVerificator;
    private final DataRepository dataRepository;

    @Autowired
    public LoginService(UserVerificator userVerificator, DataRepository dataRepository) {
        this.userVerificator = userVerificator;
        this.dataRepository = dataRepository;
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

        User user = dataRepository.getUsersByLogin(login).get(0);

        String token = Jwts.builder()
                .setSubject("" + user.getId())
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(calendar.getTime())
                .setAudience(user.getSecurity_id() + "")
                .compact();

        dataRepository.addTokenInDatabase(token, login);

        return token;
    }

    public long getIdByToken(String token) {
        String key = dataRepository.getUsersByToken(token).get(0).getLogin();
        return Long.parseLong(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean checkTokenOnValidation(String token) {
        Date now = new Date();
        String key = dataRepository.getUsersByToken(token).get(0).getLogin();
        Date dateToken = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();

        return dateToken.after(now);
    }
}
