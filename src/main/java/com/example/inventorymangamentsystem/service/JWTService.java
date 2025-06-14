package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



@Service
public class JWTService {


    @Value("${jwt.secret}") // Similar to env
    private String jwtSecret;

    private String secretkey = "";



    private AuthService authService;
    private AuthenticationManager authenticationManager;




    Map<String, Object> claims = new HashMap<>();

    public String generateToken(int userID, Map<String, Object> claims) {
         return Jwts.builder()
                .claims()
                .add(claims)
                .subject(String.valueOf(userID))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .and()
                .signWith(generateKey())
                .compact();
    }


    // Extracts the data from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    // Keys
    public SecretKey generateKey() {
        return new SecretKeySpec(jwtSecret.getBytes(), "HmacSHA256");
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractID(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId").toString();
    }


    public boolean validateToken(String jwtToken, UserDetailsPrinciple userDetails) {
        String extractedId = extractID(jwtToken);
        return extractedId.equals(String.valueOf(userDetails.getUserId()));

    }
}
