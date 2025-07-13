package com.example.inventorymangamentsystem.repository;

import com.example.inventorymangamentsystem.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepo extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyName(String CompanyName);

    Optional<Company> findByInviteKey(String inviteKey);

    Optional<Company> findByCompanyId(int companyId);
}
