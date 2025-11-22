package com.example.inventorymangamentsystem.dto.responsedto;

import com.example.inventorymangamentsystem.dto.responsedto.mappers.UsersInCompany;
import com.example.inventorymangamentsystem.entity.Role;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CompanyResponse {
    protected int companyId;
    protected String companyName;
    protected String companyAddress;
    protected String companyPhone;
    protected String companyEmail;
    protected String companyWebsite;
    protected List<UsersInCompany> peopleInCompany;
    protected UsersInCompany owner;
    protected Set<Role> role;
}
