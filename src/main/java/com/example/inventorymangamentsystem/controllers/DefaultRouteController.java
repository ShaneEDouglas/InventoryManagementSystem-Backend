package com.example.inventorymangamentsystem.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Health")
public class DefaultRouteController {

    @Operation(
            description = "The server might work......"
    )
    @GetMapping("/")
    public Map<String, String> index(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString();
        return Map.of("Message:", "Um, I think your server is working, bro");

    }
    
}
