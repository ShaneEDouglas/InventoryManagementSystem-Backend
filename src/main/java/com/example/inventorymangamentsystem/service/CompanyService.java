package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.ResponseObject.ResponseHandler;
import com.example.inventorymangamentsystem.dto.CompanyRequest;
import com.example.inventorymangamentsystem.dto.InviteRequest;
import com.example.inventorymangamentsystem.dto.responsedto.CompanyResponse;
import com.example.inventorymangamentsystem.dto.responsedto.mappers.UsersInCompany;
import com.example.inventorymangamentsystem.entity.Company;
import com.example.inventorymangamentsystem.entity.Role;
import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.CompanyRepo;
import com.example.inventorymangamentsystem.repository.UserRepo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.example.inventorymangamentsystem.utils.inviteKeyUtil.generateInviteKey;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepo companyRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;

    public CompanyService(CompanyRepo companyRepo, PasswordEncoder passwordEncoder, UserRepo userRepo) {
        this.companyRepo = companyRepo;
        this.passwordEncoder = passwordEncoder;
        this.userRepo = userRepo;
    }

    // Comapny response mapper
    private UsersInCompany mapUserToCompany(User user) {
        UsersInCompany dto = new UsersInCompany();
        dto.setUserId(user.getUserId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles()); // Gets the roles from the USer eneity
        return dto;
    }



    public ResponseEntity<ResponseHandler<CompanyResponse>> createCompany(CompanyRequest companyRequest, Authentication authentication) {
        try {
            Company company = new Company();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            CompanyResponse companyResponse = new CompanyResponse();
            User currentUser = userDetails.getUser();

            User managedUser = userRepo.findByUserId(currentUser.getUserId())
                    .orElseThrow( () -> new RuntimeException("User not found"));

            /*
             * Here we will hash the company invite key and the hashed key is what will be stored in the database
             * it will have similar functionality to hashing the password form our login/register/auth service functions
             *
             * */

            String rawInviteKey = generateInviteKey();

            // Hash the inivtekey
            String hashedInviteKey = passwordEncoder.encode(rawInviteKey);

            company.setCompanyName(companyRequest.getCompanyName());

            // Check if the company name already exists
            if (companyRepo.findByCompanyName(companyRequest.getCompanyName()).isPresent()) {
                return ResponseHandler.responseBuilder("Company already exists", HttpStatus.BAD_REQUEST, companyResponse);
            }
            company.setCompanyAddress(companyRequest.getCompanyAddress());
            company.setCompanyPhone(companyRequest.getCompanyPhone());
            company.setCompanyEmail(companyRequest.getCompanyEmail());
            company.setCompanyWebsite(companyRequest.getCompanyWebsite());
            company.setOwner(userDetails.getUser());
            company.setUsers(new ArrayList<>());
            company.getUsers().add(managedUser);
            company.setInviteKey(hashedInviteKey);


            companyRepo.save(company);

            companyResponse.setCompanyId(company.getCompanyId());
            companyResponse.setCompanyName(company.getCompanyName());
            companyResponse.setCompanyAddress(company.getCompanyAddress());
            companyResponse.setCompanyPhone(company.getCompanyPhone());
            companyResponse.setCompanyEmail(company.getCompanyEmail());
            companyResponse.setCompanyWebsite(company.getCompanyWebsite());
            companyResponse.setOwner(mapUserToCompany(currentUser));

            List<UsersInCompany> companyUsers = company.getUsers()
                            .stream()
                            .map(this::mapUserToCompany)
                            .toList();

            companyResponse.setPeopleInCompany(companyUsers);

            currentUser.setCompany(company);
            currentUser.setRoles(Set.of(Role.ADMIN));

            userRepo.save(currentUser);
            
            return ResponseHandler.responseBuilder("Company Created Successfully", HttpStatus.CREATED, companyResponse);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, new CompanyResponse());
        }
    }


    @Transactional
    public ResponseEntity<ResponseHandler<CompanyResponse>> updateCompany(CompanyRequest companyRequest, Authentication authentication) {

        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();
            CompanyResponse companyResponse = new CompanyResponse();

            // Grab current company
            Company currentCompany = currentUser.getCompany();

            if (currentCompany == null) {
                return ResponseHandler.responseBuilder("company doesn't exits", HttpStatus.BAD_REQUEST, companyResponse);
            }

            if (!currentUser.getRoles().contains(Role.ADMIN)) {
                return ResponseHandler.responseBuilder("Only admins can update the company", HttpStatus.BAD_REQUEST, companyResponse);
            }

            // Check if the user is a part of the comapny

            if (!currentCompany.getUsers().contains(userDetails.getUser())) {
                return ResponseHandler.responseBuilder("User not part of company", HttpStatus.BAD_REQUEST, companyResponse);
            }

            // Update the company
            currentCompany.setCompanyName(companyRequest.getCompanyName());
            currentCompany.setCompanyAddress(companyRequest.getCompanyAddress());
            currentCompany.setCompanyPhone(companyRequest.getCompanyPhone());
            currentCompany.setCompanyEmail(companyRequest.getCompanyEmail());
            currentCompany.setCompanyWebsite(companyRequest.getCompanyWebsite());
            currentCompany.setOwner(userDetails.getUser());


            // Put it in the company repsonse
            companyResponse.setCompanyId(currentCompany.getCompanyId());
            companyResponse.setCompanyName(currentCompany.getCompanyName());
            companyResponse.setCompanyAddress(currentCompany.getCompanyAddress());
            companyResponse.setCompanyPhone(currentCompany.getCompanyPhone());
            companyResponse.setCompanyEmail(currentCompany.getCompanyEmail());
            companyResponse.setCompanyWebsite(currentCompany.getCompanyWebsite());

            return ResponseHandler.responseBuilder("Company updated successfully", HttpStatus.OK, companyResponse);

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Transactional
    public ResponseEntity<ResponseHandler<CompanyResponse>> joinCompany(InviteRequest request, Authentication authentication) {
        try {
            /*
             * Here when the  company is already created
             *
             * We will make an invite request DTO
             *
             * the user wanting to input
             * */
            String CompanyName = request.getCompanyName();
            String inviteKey = request.getInviteKey();
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            CompanyResponse companyResponse = new CompanyResponse();
            User user = userDetails.getUser();



            if (companyRepo.findByCompanyName(CompanyName).isEmpty()) {
                return ResponseHandler.responseBuilder("Error: Company doesn't exist", HttpStatus.BAD_REQUEST, null);
            }

            // Check if the company exist
            Optional<Company> existingCompany = companyRepo.findByCompanyName(request.getCompanyName());

            if (existingCompany.isEmpty()) {
                return ResponseHandler.responseBuilder("Error: Company doesn't exist", HttpStatus.BAD_REQUEST, null);
            }

            // Make sure the user isn't already part of the company
            if (existingCompany.get().getUsers().contains(user)) {
                return ResponseHandler.responseBuilder("Error: User is already in the company", HttpStatus.BAD_REQUEST, null);
            }

            // Check if the invite key is correct
            if (!passwordEncoder.matches(inviteKey, existingCompany.get().getInviteKey())) {
                return ResponseHandler.responseBuilder("Error: Invalid invite key", HttpStatus.BAD_REQUEST, null);
            }



            // Post company checks, add the current user to the company users (people) list
            Company company = existingCompany.get();
            company.getUsers().add(user);
            companyRepo.save(company);

            // Update the User's company status
            user.setCompany(existingCompany.get());
            user.setRoles(Set.of(Role.EMPLOYEE));
            userRepo.save(user);

            companyResponse.setCompanyId(company.getCompanyId());
            companyResponse.setCompanyName(company.getCompanyName());
            companyResponse.setCompanyAddress(company.getCompanyAddress());
            companyResponse.setCompanyPhone(company.getCompanyPhone());
            companyResponse.setCompanyEmail(company.getCompanyEmail());
            companyResponse.setCompanyWebsite(company.getCompanyWebsite());
            companyResponse.setOwner(mapUserToCompany(company.getOwner()));


            List<UsersInCompany> companyUsers = company.getUsers()
                    .stream()
                    .map(this::mapUserToCompany)
                    .toList();

            companyResponse.setPeopleInCompany(companyUsers);
            companyResponse.setRole(user.getRoles());

            return ResponseHandler.responseBuilder("Joined company successfully", HttpStatus.CREATED, companyResponse);



        } catch (RuntimeException e) {
            e.printStackTrace();

            return ResponseHandler.responseBuilder("Error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }


    public ResponseEntity<ResponseHandler<CompanyResponse>> leaveCompany(Authentication authentication) {

        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();

            CompanyResponse companyResponse = new CompanyResponse();



            User currentUser = userDetails.getUser();


            //their current comapny
            Company currentCompany = currentUser.getCompany();
            Optional<Company> existingCompany = companyRepo.findByCompanyName(currentCompany.getCompanyName());

            if (existingCompany.isEmpty()) {

                return ResponseHandler.responseBuilder("Error: Company doesn't exist", HttpStatus.BAD_REQUEST, null);
            }

            if (!existingCompany.get().getUsers().contains(currentUser)) {

                return ResponseHandler.responseBuilder("Error: User is not in the company", HttpStatus.BAD_REQUEST, null);
            }

            // Leave the company set it to null
            currentUser.setCompany(null);
            currentCompany.getUsers().remove(currentUser);


            return ResponseHandler.responseBuilder("Leaving company successfully for user: " + currentUser.getUserId(), HttpStatus.CREATED, companyResponse);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseHandler.responseBuilder("Error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }


    }


    public ResponseEntity<ResponseHandler<CompanyResponse>> getUsersInCompany(Authentication authentication) {

        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            int currentUserId = userDetails.getUser().getUserId();
            User currentUser = userRepo.findById(currentUserId).get();
            CompanyResponse companyResponse = new CompanyResponse();

            Optional<Company> existingCompany = companyRepo.findByCompanyName(currentUser.getCompany().getCompanyName());

            if (existingCompany.isEmpty()) {
                return ResponseHandler.responseBuilder("Error: Company doesn't exist", HttpStatus.BAD_REQUEST, null);
            }

            if (!existingCompany.get().getUsers().contains(currentUser)) {
                return ResponseHandler.responseBuilder("Error: User is not in the company", HttpStatus.BAD_REQUEST, null);
            }

            List<User> peopleInCompany = existingCompany.get().getUsers();


            List<UsersInCompany> peopleInCompanyUsers = peopleInCompany
                    .stream()
                    .map(this::mapUserToCompany)
                    .toList();

            // Put in owner
            UsersInCompany ownrDto = new UsersInCompany();
            if (currentUser.getCompany().getOwner() != null) {
                UsersInCompany ownerDto = new UsersInCompany();
                ownerDto.setUserId(currentUser.getCompany().getOwner().getUserId());
                ownerDto.setFirstName(currentUser.getCompany().getOwner().getFirstName());
                ownerDto.setLastName(currentUser.getCompany().getOwner().getLastName());
                ownerDto.setEmail(currentUser.getCompany().getOwner().getEmail());
                ownerDto.setRoles(currentUser.getCompany().getOwner().getRoles());
                companyResponse.setOwner(ownerDto);
            }

            companyResponse.setCompanyName(currentUser.getCompany().getCompanyName());
            companyResponse.setCompanyAddress(currentUser.getCompany().getCompanyAddress());
            companyResponse.setCompanyPhone(currentUser.getCompany().getCompanyPhone());
            companyResponse.setCompanyEmail(currentUser.getCompany().getCompanyEmail());
            companyResponse.setCompanyWebsite(currentUser.getCompany().getCompanyWebsite());
            companyResponse.setCompanyId(currentUser.getCompany().getCompanyId());
            companyResponse.setPeopleInCompany(peopleInCompanyUsers);
            companyResponse.setRole(currentUser.getRoles());



            return ResponseHandler.responseBuilder("getUsersInCompany successfully", HttpStatus.OK, companyResponse);
        } catch (Exception e) {
            return ResponseHandler.responseBuilder("Error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }





    }

    public ResponseEntity<ResponseHandler<CompanyResponse>> deleteCompany(CompanyRequest companyRequest, Authentication authentication) {


        try {
            Optional<Company> existingCompany = companyRepo.findByCompanyName(companyRequest.getCompanyName());
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userDetails.getUser();
            CompanyResponse companyResponse = new CompanyResponse();

            if (!currentUser.getRoles().contains(Role.ADMIN)) {
                return ResponseHandler.responseBuilder("Error: Only admins can delete the company", HttpStatus.FORBIDDEN, null);
            }

            if (existingCompany.isEmpty()) {
                return ResponseHandler.responseBuilder("Error: Company doesn't exist", HttpStatus.BAD_REQUEST, null);
            }

            if (!existingCompany.get().getUsers().contains(userDetails.getUser())) {
                return ResponseHandler.responseBuilder("Error: User is not in the company", HttpStatus.BAD_REQUEST, null);
            }



            companyRepo.delete(existingCompany.get());


            return ResponseHandler.responseBuilder("Company deleted successfully" +companyRequest.getCompanyName(), HttpStatus.OK, companyResponse);
        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder("Error:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }

    }

    @Transactional
    public ResponseEntity<ResponseHandler<CompanyResponse>> getMyCompany(Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userRepo.findById(userDetails.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            CompanyResponse companyResponse = new CompanyResponse();
            Optional<Company> existingCompany = companyRepo.findByCompanyName(currentUser.getCompany().getCompanyName());

            if (existingCompany.isEmpty()) {
                return ResponseHandler.responseBuilder("Company not found", HttpStatus.BAD_REQUEST,null);
            }
            Company company = currentUser.getCompany();


            companyResponse.setCompanyId(company.getCompanyId());
            companyResponse.setCompanyName(company.getCompanyName());
            companyResponse.setCompanyAddress(company.getCompanyAddress());
            companyResponse.setCompanyPhone(company.getCompanyPhone());
            companyResponse.setCompanyEmail(company.getCompanyEmail());
            companyResponse.setCompanyWebsite(company.getCompanyWebsite());


            if (company.getOwner() != null) {
                UsersInCompany ownerDto = new UsersInCompany();
                ownerDto.setUserId(company.getOwner().getUserId());
                ownerDto.setFirstName(company.getOwner().getFirstName());
                ownerDto.setLastName(company.getOwner().getLastName());
                ownerDto.setEmail(company.getOwner().getEmail());
                ownerDto.setRoles(company.getOwner().getRoles());
                companyResponse.setOwner(ownerDto);
            }


            List<UsersInCompany> usersList = company.getUsers()
                    .stream()
                    .map(user -> {
                        UsersInCompany dto = new UsersInCompany();
                        dto.setUserId(user.getUserId());
                        dto.setFirstName(user.getFirstName());
                        dto.setLastName(user.getLastName());
                        dto.setEmail(user.getEmail());
                        dto.setRoles(user.getRoles());
                        return dto;
                    })
                    .toList();
            companyResponse.setPeopleInCompany(usersList);


            companyResponse.setRole(currentUser.getRoles());

            return ResponseHandler.responseBuilder("Company of user", HttpStatus.OK,companyResponse);

        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }



    public ResponseEntity<ResponseHandler<Map<String, Object>>> regenerateInviteKey(Authentication authentication) {
        try {
            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();
            User currentUser = userRepo.findById(userDetails.getUser().getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Company company = currentUser.getCompany();


            if (company == null) {
                return ResponseHandler.responseBuilder("Error: Company not found", HttpStatus.BAD_REQUEST, null);
            }

            // Only the admin can use this to make a new invite key
            if (!currentUser.getRoles().contains(Role.ADMIN) || currentUser.getRoles().contains(Role.MANAGER)) {
                return ResponseHandler.responseBuilder("Error: Only admins can regenerate the key", HttpStatus.FORBIDDEN, null);
            }

            String newInvitekey = generateInviteKey();
            String hashedKey = passwordEncoder.encode(newInvitekey);
            company.setInviteKey(hashedKey);
            companyRepo.save(company);

            return ResponseHandler.responseBuilder(
                    "Successfully generated new key For Company " + company.getCompanyName(),
                    HttpStatus.OK,
                    Map.of(
                            "New Key", newInvitekey
                    ));

        } catch (RuntimeException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }



}
