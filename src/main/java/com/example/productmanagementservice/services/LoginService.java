package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.handlers.DataHandler;
import com.example.productmanagementservice.database.verificators.UserVerificator;
import com.example.productmanagementservice.entity.data.Token;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class LoginService {

    private final UserVerificator userVerificator;
    private final DataHandler dataHandler;

    @Autowired
    public LoginService(UserVerificator userVerificator, DataHandler dataHandler) {
        this.userVerificator = userVerificator;
        this.dataHandler = dataHandler;
    }

    public ResponseEntity<Token> login(String login, String password) {
        ResponseEntity<Token> responseEntity;

        if (userVerificator.checkingUser(login, password)) {
            responseEntity = new ResponseEntity<>(new Token(createToken(login)), HttpStatus.OK);
        } else {
            responseEntity = new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return responseEntity;
    }

    private String createToken(String login) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);

        String token = Jwts.builder()
                .setSubject("" + dataHandler.getIdByLogin(login))
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(calendar.getTime())
                .setAudience(dataHandler.getStatusByLogin(login))
                .compact();

        dataHandler.addTokenInDatabase(token, login);

        return token;
    }

    public long getIdByToken(String token) {
        String key = dataHandler.getLoginByToken(token);
        return Long.parseLong(Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject());
    }

    public boolean checkTokenOnValidation(String token) {
        Date now = new Date();
        String key = dataHandler.getLoginByToken(token);
        Date dateToken = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();

        return dateToken.after(now);
    }
}
