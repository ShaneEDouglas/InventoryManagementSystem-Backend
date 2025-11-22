package com.example.inventorymangamentsystem.dto.responsedto.mappers;


import com.example.inventorymangamentsystem.entity.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UsersInCompany {
    private int userId;
    private String firstName;
    private String lastName;
    private String email;
    private Set<Role> roles;

}
