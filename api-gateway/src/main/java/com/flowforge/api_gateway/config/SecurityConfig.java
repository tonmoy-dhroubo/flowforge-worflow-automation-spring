package com.flowforge.api_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // You still need to read the secret key from your application.yml
    @Value("${application.security.jwt.secret-key}")
    private String jwtSecretKey;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> exchange
                        // Public endpoints
                        .pathMatchers("/auth/register", "/auth/login", "/auth/refresh-token").permitAll()
                        // All other requests must be authenticated
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    /**
     * This is the new bean that resolves the startup error.
     * It provides a reactive JWT decoder that Spring Security can use.
     */
    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder() {
        // We use the same secret key from the auth-service
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecretKey.getBytes(), "HmacSHA256");
        // Use NimbusReactiveJwtDecoder for symmetric keys
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}