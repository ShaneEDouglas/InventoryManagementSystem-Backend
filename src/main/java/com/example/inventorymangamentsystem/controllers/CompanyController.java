package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.CompanyRequest;
import com.example.inventorymangamentsystem.dto.InviteRequest;
import com.example.inventorymangamentsystem.dto.responsedto.CompanyResponse;
import com.example.inventorymangamentsystem.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/company")
@Tag(name = "Company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Operation(
            description = "Post endpoint for Creating a new Company."
    )
    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<CompanyResponse>> create (@RequestBody CompanyRequest companyRequest, Authentication authentication ) {
        try {
            return companyService.createCompany(companyRequest, authentication);
        } catch (Exception e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Operation(
            description = "Put endpoint for modifying company attributes (admin only)."
    )
    @PutMapping("/update")
    public ResponseEntity<ResponseHandler<CompanyResponse>> updateCompany(@RequestBody CompanyRequest companyRequest, Authentication authentication ) {
        try {
            return companyService.updateCompany(companyRequest,authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    @Operation(
            description = "Post endpoint for joining a created company with the invite key."
    )
    @PostMapping("/join")
    public ResponseEntity<ResponseHandler<CompanyResponse>> joinCompany(
            @RequestBody InviteRequest inviteRequest, Authentication authentication) {

        try {
            return companyService.joinCompany(inviteRequest, authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }

    @Operation(
            description = "Post endpoint for leaving a company"
    )
    @PostMapping("/leave")
    public ResponseEntity<ResponseHandler<CompanyResponse>> leaveCompany(Authentication authentication) {
        try {
            return companyService.leaveCompany(authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Operation(
            description = "get endpoint for grabbing all the users in a company."
    )
    @GetMapping("/users")
    public ResponseEntity<ResponseHandler<CompanyResponse>> getUsersInCompany(Authentication authentication) {
        try {
            return companyService.getUsersInCompany(authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Operation(
            description = "Get endpoint for retrieving the current company of the logged in user."
    )
    @GetMapping("/my-company")
    public ResponseEntity<ResponseHandler<CompanyResponse>> getMyCompany(Authentication authentication) {
        try {
            return companyService.getMyCompany(authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Operation(
            description = "Delete endpoint for deleting a company (admin only)."
    )
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseHandler<CompanyResponse>> deleteCompany(
            @RequestBody CompanyRequest companyRequest,
            Authentication authentication
    ) {
        try {
            return companyService.deleteCompany(companyRequest, authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }

    @Operation(
            description = "Post endpoint for generating a new for the company (Admin only)."
    )
    @PostMapping("/newkey")
    public ResponseEntity<ResponseHandler<Map<String,Object>>> newKey( Authentication authentication) {
        try {
            return companyService.regenerateInviteKey(authentication);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}









