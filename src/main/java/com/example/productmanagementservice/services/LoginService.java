package com.example.productmanagementservice.services;

import com.example.productmanagementservice.database.CreatorInDatabase;
import com.example.productmanagementservice.database.DatabaseHandler;
import com.example.productmanagementservice.database.VerificationDatabase;
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
    private final CreatorInDatabase creatorInDatabase;
    private final DatabaseHandler databaseHandler;
    private final VerificationDatabase verificationDatabase;

    @Autowired
    public LoginService(VerificationDatabase verificationDatabase, CreatorInDatabase creatorInDatabase,
                        DatabaseHandler databaseHandler) {
        this.verificationDatabase = verificationDatabase;
        this.creatorInDatabase = creatorInDatabase;
        this.databaseHandler = databaseHandler;
    }

    public ResponseEntity<Token> login(String login, String password) {
        ResponseEntity<Token> responseEntity;

        if (verificationDatabase.checkingUser(login, password)) {
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
                .setSubject("" + databaseHandler.getIdByLogin(login))
                .signWith(SignatureAlgorithm.HS512, login)
                .setExpiration(calendar.getTime())
                .setAudience(databaseHandler.getStatusByLogin(login))
                .compact();

        creatorInDatabase.addTokenInDatabase(token, login);

        return token;
    }

    public boolean checkTokenOnValidation(String token) {
        Date now = new Date();
        String key = databaseHandler.getLoginByToken(token);
        Date dateToken = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();

        return dateToken.after(now);
    }
}
