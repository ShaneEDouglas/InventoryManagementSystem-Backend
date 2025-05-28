package com.example.inventorymangamentsystem.repository;


import com.example.inventorymangamentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String Email);

    Optional<User>findUserByID(String id);

    Optional<User> findByUsername(String username);

    Optional<User> findByFirstName(String FirstName);

    Optional<User> findByLastName(String LastName);

}
