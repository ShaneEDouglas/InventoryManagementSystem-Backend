package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.ProductRequest;
import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.dto.responsedto.ProductResponse;
import com.example.inventorymangamentsystem.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
@Tag(name = "Product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
            description = "Post endpoint for creating a new product",
            summary = "Must have the role of admin,manager, or employee to access this endpoint"
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<ProductResponse>> createProduct(@RequestBody ProductRequest request, Authentication authentication) {
        try {
            return productService.createProduct(request,authentication);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);

        }

    }
    @Operation(
            description = "Get endpoint fro retrieving "
    )
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
    @Operation(
            description = "Put method to update the selected product"
    )
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

    @Operation(
            description = "Delete method to delete the selected product"
    )
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

    @Operation(
            description = "Patch method to update the stoc number of a selected product"
    )
    @PatchMapping("/stock/{id}")
    public ResponseEntity<ResponseHandler<ProductResponse>> adjustStock(
            @PathVariable int id,
            @RequestParam int quantity,
            Authentication authentication) {
        try {
            return productService.adjustStock(id, quantity, authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

}
