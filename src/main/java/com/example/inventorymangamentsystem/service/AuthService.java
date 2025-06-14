package com.example.inventorymangamentsystem.service;


import com.example.inventorymangamentsystem.dto.LoginRequest;
import com.example.inventorymangamentsystem.dto.RegisterRequest;
import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.UserRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class AuthService {


    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private JWTService jwtService;


    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder, JWTService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }



    public ResponseEntity<Map<String, Object>> Register( @RequestBody RegisterRequest request) {
        try {
            if (userRepo.findByEmail(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already in use");
            }
            String hashedPW = new BCryptPasswordEncoder().encode(request.getPassword());

            User user = new User();

            // Creates a new user and saves them to the database
            user.setEmail(request.getEmail());

            // check email format
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(user.getEmail());

            if (!matcher.matches()) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email format is not valid"));
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setProfilePicture(request.getProfilePicture());

            if (user.getPassword().length() < 6) {
                return ResponseEntity.badRequest().body(Map.of("message", "Password must be at least 6 characters"));
            }

            userRepo.save(user);

            String token = jwtService.generateToken(user.getUserId(), Map.of(
                    "userId", user.getUserId()));

            return ResponseEntity.status(201)
                    .header("Set-Cookie", "token=" + token + "; HttpOnly; Path=/; SameSite=Strict")
                    .body(Map.of(
                            "message", "user registered successfully",
                            "user", Map.of(
                                    "id", user.getUserId(),
                                    "email", user.getEmail(),
                                    "firstName", user.getFirstName(),
                                    "lastName", user.getLastName(),
                                    "phoneNumber", user.getPhoneNumber(),
                                    "profilePicture", user.getProfilePicture()
                            )
                    ));

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity.status(500).body(Map.of("Error", e.getMessage()));


        }


    }





    // Login Controller
    public ResponseEntity<Map<String,Object>> Login ( @RequestBody LoginRequest request) {

        try {

            String email = request.getEmail();
            String password = request.getPassword();

            // Check if email matches the correct regex format
            if (!email.matches("[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}")) {
                return ResponseEntity.status(401).body(Map.of("Error", "Email format is incorrect"));
            }

            if (userRepo.findByEmail(request.getEmail()).isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("Error", "User not found"));
            }

            // Check to see if the user exist through email and local auth provider
            Optional<User> existingUser = userRepo.findByEmailAndAuthProvider(request.getEmail(), "local");

            // Checks if the exsiting user is not found

            if (existingUser.isEmpty()) {
                return ResponseEntity.status(401).body(Map.of("Error", "User not found"));
            }

            // Check if the exiting User is using the correct auth provider
            if (existingUser.isPresent() && existingUser.get().getAuthProvider().equals("google")) {
                return ResponseEntity.status(401).body(Map.of("Error", "Email is already in use.PLease sign in with Google"));
            }

            // Check if the passwords match
            if (!passwordEncoder.matches(password, existingUser.get().getPassword())) {
                return ResponseEntity.status(401).body(Map.of("Error", "Incorrect password"));
            }


            String token = jwtService.generateToken(existingUser.get().getUserId(), Map.of(
                    "userId", existingUser.get().getUserId()));

            return ResponseEntity.status(201)
                    .header("Set-Cookie", "token=" + token + "; HttpOnly; Path=/; SameSite=Strict")
                    .body(Map.of(
                            "message", "user log in successfully",
                            "user", Map.of(
                                    "userId", existingUser.get().getUserId(),
                                    "email", existingUser.get().getEmail(),
                                    "firstName", existingUser.get().getFirstName(),
                                    "lastName", existingUser.get().getLastName(),
                                    "phoneNumber", existingUser.get().getPhoneNumber(),
                                    "profilePicture", existingUser.get().getProfilePicture()
                            )
                    ));

        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("Error", e.getMessage()));
        }

    }

    // Log out by expiring the token
    public ResponseEntity<Map<String,Object>> Logout() {
        return ResponseEntity.status(200)
                // Expire the token (set max age to 0)
                .header("Set-Cookie", "token=; HttpOnly; Path=/; Max-Age=0; SameSite=Strict")
                .body(Map.of(
                        "message", "user logout successful"
                ));
    }


    public ResponseEntity<Map<String, Object>> getMe(Authentication authentication) {
        try {

            if (authentication == null  || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("message", "User is not authenticated"));
            }

            UserDetailsPrinciple userDetails = (UserDetailsPrinciple) authentication.getPrincipal();

            Map<String, Object> userData = Map.of(
                    "userId", userDetails.getUserId(),
                    "firstName", userDetails.getFirstName(),
                    "lastName", userDetails.getLastName(),
                    "email", userDetails.getEmail(),
                    "phoneNumber", userDetails.getPhoneNumber(),
                    "profilePicture", userDetails.getProfilePicture()
            );

            return  ResponseEntity.ok(Map.of(
                    "User Retrieved Successfully", userData

            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("Error", e.getMessage()));
        }

    }
}
