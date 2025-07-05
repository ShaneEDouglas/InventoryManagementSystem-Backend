package com.example.inventorymangamentsystem.repository;

import com.example.inventorymangamentsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepo extends JpaRepository<Company, Long> {

}
