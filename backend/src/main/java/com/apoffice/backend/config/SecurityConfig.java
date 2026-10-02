package com.apoffice.backend.config;

import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> {})
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                     session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    )
                
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/api/auth/login").permitAll()
                    .requestMatchers("/uploads/**").permitAll()

                    .requestMatchers(HttpMethod.PUT, "/api/users/change-password")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                    .requestMatchers(HttpMethod.GET, "/api/users")
                        .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/users")
                        .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/users/*")
                        .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/users/*")
                        .hasRole("ADMIN")

                    .requestMatchers(HttpMethod.GET, "/api/attendance")
                        .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/tugas/*/verify")
                        .hasRole("ADMIN")

                    .requestMatchers(HttpMethod.POST, "/api/tugas")
                        .hasRole("EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/api/attendance/check-in")
                        .hasRole("EMPLOYEE")
                    .requestMatchers(HttpMethod.POST, "/api/attendance/check-out")
                        .hasRole("EMPLOYEE")
                    .requestMatchers(HttpMethod.GET, "/api/attendance/my")
                        .hasRole("EMPLOYEE")

                    .requestMatchers(HttpMethod.GET, "/api/tugas")
                        .hasAnyRole("ADMIN", "EMPLOYEE")

                    .anyRequest().authenticated()
                )
                
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}