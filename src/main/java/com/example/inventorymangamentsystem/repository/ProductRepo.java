package com.example.inventorymangamentsystem.repository;

import com.example.inventorymangamentsystem.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Long> {

}
