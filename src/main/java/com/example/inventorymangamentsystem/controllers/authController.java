package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.inventorymangamentsystem.service.AuthService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class authController {


    @Autowired
    AuthService authService;


    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("Status: ", "Auth Works");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(RegisterRequest request) {
        try {
            User user = authService.Register(request);

            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));
        }
        return authService.Register(request);;
    }








}




