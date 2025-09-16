package com.example.inventorymangamentsystem.dto.responsedto;

import com.example.inventorymangamentsystem.entity.User;
import lombok.Data;

import java.util.List;

@Data
public class CompanyResponse {
    protected String companyId;
    protected String companyName;
    protected String companyAddress;
    protected String companyPhone;
    protected String companyEmail;
    protected String companyWebsite;
    protected List<User> peopleInCompany;
    protected User owner;
}
