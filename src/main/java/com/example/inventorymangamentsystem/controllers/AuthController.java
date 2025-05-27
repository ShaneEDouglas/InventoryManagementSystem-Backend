package com.example.inventorymangamentsystem.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.inventorymangamentsystem.service.AuthService;
import java.util.Map;

@RestController
public class AuthController {


    @Autowired
    AuthService authService;


    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("Status: ", "Auth Works");
    }








}




