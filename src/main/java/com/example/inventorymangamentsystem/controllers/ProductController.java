package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.dto.ProductRequest;
import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    private int productId;

    @PostMapping("/createproduct")
    public ResponseEntity<Map<String,Object>> createProduct(@RequestBody ProductRequest request, Authentication authentication) {
        try {
            return productService.createProduct(request,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));

        }

    }

    @GetMapping("/getproducts")
    public ResponseEntity<Map<String,Object>> getProducts(Authentication authentication) {
        try {
            return productService.getAllProducts(authentication);
        } catch (RuntimeException e) {
             System.out.println("Error: " + e.getMessage());
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));
        }
    }


    @PutMapping("/updateprodcut/{id}")
    public ResponseEntity<Map<String,Object>> updateProduct(
            
            @RequestBody ProductRequest request, 
            @PathVariable int id,
            Authentication authentication) {
        try {
            return productService.updateProduct(request,id,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));
        }
    }

    @DeleteMapping("/deletepoduct/{id}")
    public ResponseEntity<Map<String,Object>> deleteProduct(
            @PathVariable int id,
            @RequestBody ProductRequest request,
            Authentication authentication) {
        try {
            return productService.deleteProduct(id,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("Message: ", e.getMessage()));
        }

    }

}
