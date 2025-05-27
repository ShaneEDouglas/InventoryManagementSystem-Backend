package com.example.inventorymangamentsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


/*
Targeted approach to login security. When in development mode we
won't need the login but in production we will change it.
*
* */
@Configuration
public class SecurityConfig {

    @Bean
    @Profile("dev")
    public SecurityFilterChain devFilters(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests( auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable())
                .build();
    }



    @Bean
    @Profile("prod")
    public SecurityFilterChain prodFilters(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.requestMatchers("/public/**")
                        .permitAll().anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .build();
    }


    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws  Exception {
        return config.getAuthenticationManager();
    }

}


