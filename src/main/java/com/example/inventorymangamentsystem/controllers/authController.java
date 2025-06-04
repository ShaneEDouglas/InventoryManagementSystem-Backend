package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> register( @RequestBody RegisterRequest request) {
        try {
            return authService.Register(request);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));

        }

    }

        @GetMapping("/csrf")
        public CsrfToken csrf(CsrfToken token) {
            return token;
        }


}




