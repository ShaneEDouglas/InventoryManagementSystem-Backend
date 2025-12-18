package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.ProductRequest;
import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.dto.responsedto.ProductResponse;
import com.example.inventorymangamentsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<ProductResponse>> createProduct(@RequestBody ProductRequest request, Authentication authentication) {
        try {
            return productService.createProduct(request,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);

        }

    }

    @GetMapping("/get")
    public ResponseEntity<ResponseHandler<List<ProductResponse>>> getProducts(Authentication authentication) {
        try {
            return productService.getAllProducts(authentication);
        } catch (RuntimeException e) {
             System.out.println("Error: " + e.getMessage());
             return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseHandler<ProductResponse>> updateProduct(
            
            @RequestBody ProductRequest request, 
            @PathVariable int id,
            Authentication authentication) {
        try {
            return productService.updateProduct(request,id,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseHandler<ProductResponse>> deleteProduct(
            @PathVariable int id,
            Authentication authentication) {
        try {
            return productService.deleteProduct(id,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }

    @PatchMapping("/stock/{id}")
    public ResponseEntity<ResponseHandler<ProductResponse>> adjustStock(
            @PathVariable int id,
            @RequestParam int quantity, // Use positive for add, negative for remove
            Authentication authentication) {
        try {
            return productService.adjustStock(id, quantity, authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

}
