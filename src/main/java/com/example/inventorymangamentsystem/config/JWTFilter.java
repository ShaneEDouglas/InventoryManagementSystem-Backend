package com.example.inventorymangamentsystem.config;

import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.service.JWTService;
import com.example.inventorymangamentsystem.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JWTFilter extends OncePerRequestFilter {



    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsServiceimpl;


    public JWTFilter(JWTService jwtService, UserDetailsServiceImpl userDetailsServiceimpl) {
        this.jwtService = jwtService;
        this.userDetailsServiceimpl = userDetailsServiceimpl;
    }






    ApplicationContext applicationContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Don't allow registration/login to get token (Will get it on registration/login)

        String path = request.getServletPath();
        if (path.startsWith("/api/auth")) {
            filterChain.doFilter(request, response);
            return;
        }




        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userID= null;


        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            userID =  jwtService.extractID(jwtToken);
        }

        if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetailsPrinciple userDetails = userDetailsServiceimpl.loadUserById(userID);
            if (jwtService.validateToken(jwtToken, userDetails )) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
