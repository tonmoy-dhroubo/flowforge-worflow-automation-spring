package com.flowforge.trigger.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration for the Trigger Service.
 * Allows public access to webhook endpoints while protecting management endpoints.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - webhooks need to be accessible without auth
                        .requestMatchers("/webhook/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Explicitly permit API calls, as auth is handled by the gateway
                        .requestMatchers("/api/v1/triggers/**").permitAll()
                        // Secure everything else by default (if anything else exists)
                        .anyRequest().denyAll()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}