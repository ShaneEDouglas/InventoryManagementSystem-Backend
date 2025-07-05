package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.dto.CompanyRequest;
import com.example.inventorymangamentsystem.entity.Company;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.CompanyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.example.inventorymangamentsystem.utils.inviteKeyUtil.generateInviteKey;

@Service
public class CompanyService {

    private final CompanyRepo companyRepo;

    public CompanyService(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }


    public ResponseEntity<Map<String,Object>> createCompany(CompanyRequest companyRequest, Authentication authentication) {
        try {
            Company company = new Company();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();


            company.setCompanyName(companyRequest.getCompanyName());
            company.setCompanyAddress(companyRequest.getCompanyAddress());
            company.setCompanyPhone(companyRequest.getCompanyPhone());
            company.setCompanyEmail(companyRequest.getCompanyEmail());
            company.setCompanyWebsite(companyRequest.getCompanyWebsite());
            company.setOwner(userDetails.getUser());
            company.setInviteKey(generateInviteKey());


            companyRepo.save(company);

            return ResponseEntity.status(200).body(Map.of(
                    "message", "Company created successfully",
                    "Company", Map.of(
                            "CompanyId", company.getCompanyId(),
                            "CompanyName", company.getCompanyName(),
                            "CompanyEmail", company.getCompanyEmail(),
                            "CompanyPhoneNumber", company.getCompanyPhone(),
                            "CompanyWebsite", company.getCompanyWebsite(),
                            "CompanyOwner", userDetails.getUser().getUserId(),
                            "peopleInCompany", company.getUsers(),
                            "inviteKey", company.getInviteKey()
                    )
            ));

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }

    }
}
