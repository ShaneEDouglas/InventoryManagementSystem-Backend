package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.dto.CompanyRequest;
import com.example.inventorymangamentsystem.dto.InviteRequest;
import com.example.inventorymangamentsystem.entity.Company;
import com.example.inventorymangamentsystem.entity.Role;
import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.CompanyRepo;
import com.example.inventorymangamentsystem.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.example.inventorymangamentsystem.utils.inviteKeyUtil.generateInviteKey;

@Service
public class CompanyService {

    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public CompanyService(CompanyRepo companyRepo, PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.companyRepo = companyRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }



    public ResponseEntity<Map<String,Object>> createCompany(CompanyRequest companyRequest, Authentication authentication) {
        try {
            Company company = new Company();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();

            /*
             * Here we will hash the comapny invitekey and the hasehd key is what will be stored in the database
             * it will have similar functionality to hashing the password form our login/rwgister /auth serivce functions
             *
             * */

            String rawInviteKey = generateInviteKey();

            // Hash the inivtekey
            String hashedInviteKey = passwordEncoder.encode(rawInviteKey);

            company.setCompanyName(companyRequest.getCompanyName());

            // Check if the company name already exists
            if (companyRepo.findByCompanyName(companyRequest.getCompanyName()).isPresent()) {
                return ResponseEntity.status(400).body(Map.of("Error message", "Company already exist"));
            }
            company.setCompanyAddress(companyRequest.getCompanyAddress());
            company.setCompanyPhone(companyRequest.getCompanyPhone());
            company.setCompanyEmail(companyRequest.getCompanyEmail());
            company.setCompanyWebsite(companyRequest.getCompanyWebsite());
            company.setOwner(userDetails.getUser());
            company.setInviteKey(hashedInviteKey);

            User currentUser = userDetails.getUser();
            currentUser.setCompany(company);
            currentUser.setRoles(Set.of(Role.ADMIN));

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
                            "inviteKey", company.getInviteKey(),
                            "rawInviteKey", rawInviteKey
                    )
            ));

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }

    }


    @Transactional
    public ResponseEntity<Map<String,Object>> updateCompany(CompanyRequest companyRequest, Authentication authentication) {

        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();

            // Grab current company
            Optional<Company> currentCompany = companyRepo.findByCompanyName(companyRequest.getCompanyName());

            if (currentCompany.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("Error message", "Company does not exist"));
            }

            if (!currentUser.getRoles().contains(Role.ADMIN)) {
                return ResponseEntity.status(403).body(Map.of("error:", "Only admins can update the company"));
            }

            // Check if the user is a part of the comapny

            if (!currentCompany.get().getUsers().contains(userDetails.getUser())) {
                return ResponseEntity.status(400).body(Map.of("error:", "User is not part of the company"));
            }

            // Update the company
            currentCompany.get().setCompanyName(companyRequest.getCompanyName());
            currentCompany.get().setCompanyAddress(companyRequest.getCompanyAddress());
            currentCompany.get().setCompanyPhone(companyRequest.getCompanyPhone());
            currentCompany.get().setCompanyEmail(companyRequest.getCompanyEmail());
            currentCompany.get().setCompanyWebsite(companyRequest.getCompanyWebsite());
            currentCompany.get().setOwner(userDetails.getUser());



            return ResponseEntity.status(200).body(Map.of(
                    "Message", "Updated Company Successfully",
                    "Company", Map.of(
                            "CompanyId", currentCompany.get().getCompanyId(),
                            "CompanyName", currentCompany.get().getCompanyName(),
                            "CompanyEmail", currentCompany.get().getCompanyEmail(),
                            "CompanyAddress", currentCompany.get().getCompanyAddress(),
                            "CompanyWebsite", currentCompany.get().getCompanyWebsite(),
                            "CompanyPhoneNumber", currentCompany.get().getCompanyPhone()
                    )
            ));

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }
    }

    public ResponseEntity<Map<String,Object>> joinCompany(InviteRequest request, Authentication authentication) {
        try {

            /*
             * Here when the  company is already created
             *
             * We will make an invite request DTO
             *
             * the user wanting to input
             * */
            String inviteKey = request.getInviteKey();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User user = userDetails.getUser();

            if (companyRepo.findByInviteKey(request.getInviteKey()).isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("Error: ", "Please enter an Invite Key"));
            }

            // Check if the company exist
            Optional<Company> existingCompany = companyRepo.findByCompanyName(request.getCompanyName());

            if (existingCompany.isEmpty()) {
                return ResponseEntity.status(400).body(Map.of("Error: ", "Company does not exist"));
            }

            // Make sure the user isn't already part of the company
            if (existingCompany.get().getUsers().contains(user)) {
                return ResponseEntity.status(400).body(Map.of("Error: ", "User is already in the company"));
            }

            // Check if the invite key is correct
            if (!passwordEncoder.matches(inviteKey, existingCompany.get().getInviteKey())) {
                return ResponseEntity.status(401).body(Map.of("Error", "Incorrect password"));
            }

            // Post company checks, add the current user to the company users (people) list
            Company company = existingCompany.get();
            company.getUsers().add(user);
            companyRepo.save(company);

            // Update the User's company status
            user.setCompany(existingCompany.get());
            user.setRoles(Set.of(Role.EMPLOYEE));


            return ResponseEntity.status(201).body(Map.of(
                    "message", "Company joined successfully",
                    "User Role",userDetails.getUser().getRoles(),
                    "Company", Map.of(
                            "CompanyId", existingCompany.get().getCompanyId(),
                            "CompanyName", existingCompany.get().getCompanyName(),
                            "CompanyEmail", existingCompany.get().getCompanyEmail(),
                            "CompanyAddress", existingCompany.get().getCompanyAddress(),
                            "CompanyWebsite", existingCompany.get().getCompanyWebsite(),
                            "CompanyPhoneNumber", existingCompany.get().getCompanyPhone()
                    )

            ));

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(Map.of("Error message", e.getMessage()));
        }

    }

    public ResponseEntity<Map<String,Object>> deleteCompany(CompanyRequest companyRequest, Authentication authentication) {

        Optional<Company> existingCompany = companyRepo.findByCompanyName(companyRequest.getCompanyName());
        UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        if (!currentUser.getRoles().contains(Role.ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error:", "Only admins can delete the company"));
        }

        if (!existingCompany.get().getUsers().contains(userDetails.getUser())) {
            return ResponseEntity.status(400).body(Map.of("error:", "User is not part of the company"));
        }

        if (existingCompany.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("Error: ", "Company does not exist"));
        }

        companyRepo.delete(existingCompany.get());


        return ResponseEntity.status(200).body(Map.of("message", "Company deleted successfully"));
    }


}
