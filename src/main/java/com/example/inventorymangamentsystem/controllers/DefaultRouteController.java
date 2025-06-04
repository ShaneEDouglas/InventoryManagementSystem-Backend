package com.example.inventorymangamentsystem.controllers;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DefaultRouteController {

    @GetMapping("/")
    public Map<String, String> index(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString();
        return Map.of("Message:", "Um, I think your server is working");

    }


}
