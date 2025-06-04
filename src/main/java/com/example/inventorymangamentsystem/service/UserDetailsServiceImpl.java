package com.example.inventorymangamentsystem.service;

import com.example.inventorymangamentsystem.entity.User;
import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.repository.UserRepo;
import io.jsonwebtoken.lang.Collections;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    @Autowired
    UserRepo userRepo;


    @Override
    public UserDetailsPrinciple loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new UserDetailsPrinciple(user);


    }


    public UserDetailsPrinciple loadUserById(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        return new UserDetailsPrinciple(user);


    }



}
