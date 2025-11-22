package com.example.inventorymangamentsystem.repository;


import com.example.inventorymangamentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String Email);

    Optional<User> findByUserId(int id);

    Optional<User> findByEmailAndAuthProvider(String email, String authProvider);

    Optional<User> findByFirstName(String FirstName);

    Optional<User> findByLastName(String LastName);

}
