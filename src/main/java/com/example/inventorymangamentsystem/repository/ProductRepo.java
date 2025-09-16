package com.example.inventorymangamentsystem.repository;

import com.example.inventorymangamentsystem.entity.Product;
import com.example.inventorymangamentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findByUser(User user);


}
