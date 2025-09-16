package com.example.inventorymangamentsystem.controllers;


import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.CompanyRequest;
import com.example.inventorymangamentsystem.dto.responsedto.CompanyResponse;
import com.example.inventorymangamentsystem.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping("/create")
    public ResponseEntity<ResponseHandler<CompanyResponse>> create (@RequestBody CompanyRequest companyRequest, Authentication authentication ) {
        try {
            return companyService.createCompany(companyRequest, authentication);
        } catch (Exception e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, new CompanyResponse());
        }
    }



}
