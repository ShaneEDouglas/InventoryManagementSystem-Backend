package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
public class AuthService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private JWTService jwtService;


    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }



    public ResponseEntity<?> Register(RegisterRequest request) {
        try {
            if (userRepo.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already in use");
            }
            String hashedPW = new BCryptPasswordEncoder().encode(request.getPassword());

            User user = new User();

            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setProfilePicture(request.getProfilePicture());

            userRepo.save(user);

            String token = jwtService.generateToken(String.valueOf(user.getId()), Map.of(
                    "id", user.getId()));

            return ResponseEntity.status(201)
                    .header("Set-Cookie", "token=" + token + "; HttpOnly; Path=/; SameSite=Strict")
                    .body(Map.of(
                            "message", "user registered successfully",
                            "user", Map.of(
                                    "id", user.getId(),
                                    "email", user.getEmail(),
                                    "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "phoneNumber", user.getPhoneNumber(),
                                    "profilePicture", user.getProfilePicture()
                            )
                    ));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(Map.of("Error", e.getMessage()));


        }


    }


    public User loadUserByID(int ID) {
        return userRepo.findUserByid(ID)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + ID));
    }





}
