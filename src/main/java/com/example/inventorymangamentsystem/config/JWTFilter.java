package com.example.inventorymangamentsystem.config;

import com.example.inventorymangamentsystem.entity.UserDetailsPrinciple;
import com.example.inventorymangamentsystem.service.JWTService;
import com.example.inventorymangamentsystem.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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




    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Don't allow registration/login to get token (Will get it on registration/login)

        String path = request.getServletPath();
        if ((path.equals("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/auth/logout") ||
                path.equals("/api/auth/csrf"))) {

            filterChain.doFilter(request, response);
            return;
        }


        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userID= null;


        //Check auth headers and extract the token from the cookie
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);

        }
            // Check the cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {

                    jwtToken = cookie.getValue();

                    break;
                }
            }
        }

        // Extract userID from the token
        if (jwtToken != null) {
            try {
                userID = jwtService.extractID(jwtToken);
                System.out.println("userID: " + userID);
                System.out.println(jwtToken);
            } catch (Exception e) {
                System.out.println("Failed to extract ID from token: " + e.getMessage());
            }
        }



        if (userID != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            int id = Integer.parseInt(userID);
            UserDetailsPrinciple userDetails = userDetailsServiceimpl.loadUserById(id);
            if (jwtService.validateToken(jwtToken, userDetails )) {
                // Validates the token, sets the custom userdetails (userdetails principle) into the authentication contenxt
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
