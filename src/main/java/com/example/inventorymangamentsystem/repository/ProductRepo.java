package com.example.inventorymangamentsystem.repository;

import com.example.inventorymangamentsystem.entity.Company;
import com.example.inventorymangamentsystem.entity.Product;
import com.example.inventorymangamentsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Integer> {
    List<Product> findByCompany(Company company);

    List<Product> findByCompanyAndAndProductNameContainingIgnoreCase(Company company, String productName);

    Optional<Product> findById(int id);

    List<Product> findByCreatedBy(User user);

    Optional<Product> findByCompanyAndProductId(Company company, int productId);


}
