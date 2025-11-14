package com.flowforge.workflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection, as it's not needed for this stateless service
            .csrf(AbstractHttpConfigurer::disable)
            // Define authorization rules
            .authorizeHttpRequests(auth -> auth
                // Allow ALL requests to any endpoint without authentication
                .anyRequest().permitAll()
            );
        return http.build();
    }
}