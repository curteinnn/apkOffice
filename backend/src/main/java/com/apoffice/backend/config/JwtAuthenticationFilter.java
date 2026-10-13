package com.apoffice.backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String secretKey;

    public JwtAuthenticationFilter(
            @Value("${jwt.secret}") String secretKey
    ) {
        this.secretKey = secretKey;
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Request tanpa token diteruskan ke SecurityConfig.
        // Endpoint publik tetap bisa diakses.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();
            String roleClaim = claims.get("role", String.class);

            if (username == null || username.isBlank()
                    || roleClaim == null || roleClaim.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Token tidak valid"
                );
                return;
            }

            // Normalisasi role:
            // ADMIN      -> ROLE_ADMIN
            // admin      -> ROLE_ADMIN
            // ROLE_ADMIN -> ROLE_ADMIN
            String role = roleClaim
                    .trim()
                    .toUpperCase(Locale.ROOT);

            if (role.startsWith("ROLE_")) {
                role = role.substring(5);
            }

            // Hanya role yang dikenal yang diterima.
            if (!"ADMIN".equals(role)
                    && !"EMPLOYEE".equals(role)) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Role pada token tidak valid"
                );
                return;
            }

            // Identitas untuk digunakan controller.
            request.setAttribute("username", username);
            request.setAttribute("role", role);

            // hasRole("ADMIN") membutuhkan authority ROLE_ADMIN.
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + role)
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

        } catch (io.jsonwebtoken.JwtException
                 | IllegalArgumentException e) {

            SecurityContextHolder.clearContext();

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(
                    "{\"message\":\"Token tidak valid atau kedaluwarsa\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}