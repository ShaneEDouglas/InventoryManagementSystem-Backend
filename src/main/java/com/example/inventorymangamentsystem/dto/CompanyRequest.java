package com.example.inventorymangamentsystem.dto;

import lombok.Data;

@Data
public class CompanyRequest {
    private String companyName;
    protected String companyAddress;
    protected String companyPhone;
    protected String companyEmail;
    protected String companyWebsite;
    protected String inviteKey;



}
