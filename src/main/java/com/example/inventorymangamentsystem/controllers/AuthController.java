package com.example.inventorymangamentsystem.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("Status: ", "Auth Works");
    }
}
