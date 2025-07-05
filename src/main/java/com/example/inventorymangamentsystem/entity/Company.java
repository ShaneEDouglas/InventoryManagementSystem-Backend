package com.example.inventorymangamentsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.annotations.Cascade;

import java.util.List;

@Entity
@Table(name = "Companies")
@Data
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected String companyId;
    protected String companyName;
    protected String companyAddress;
    protected String companyPhone;
    protected String companyEmail;
    protected String companyWebsite;
    protected String inviteKey;

    /*
    * Every user is allowed to have multiple companies and cmpanies share a "many to one" relationship
    * with a single owenr (i.e company 1 and comapny 2 is owned by owner 1, etc.
    * */
    @ManyToOne()
    @JoinColumn(name = "user_id",nullable = false )
    protected User owner;

    /*
    * In each comapny theere will a certain amount of employes
    * The control priveledges will be given to the owner to decide who gets what role
    * */
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "company",fetch = FetchType.LAZY)
    protected List<User> users;









}
