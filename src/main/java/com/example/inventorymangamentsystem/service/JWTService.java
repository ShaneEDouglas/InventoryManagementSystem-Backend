package com.example.inventorymangamentsystem.service;


import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Service
public class JWTService {


    @Value("${jwt.secret}") // Similar to env
    private String jwtSecret;



    private AuthService authService;
    private AuthenticationManager authenticationManager;


    Map<String, Object> claims = new HashMap<>();

    public String generateToken(String userID, Map<String, Object> claims) {
         return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userID)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .and()
                .signWith(generateKey())
                .compact();
    }

    public Key generateKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
    }


    public String extractEmail(String token) {

    }

    public String extractID(String token) {
        final String
    }


    public boolean validateToken(String jwtToken, UserDetails userDetails) {


    }
}
