package com.apoffice.backend.service;

import com.apoffice.backend.dto.LoginRequest;
import com.apoffice.backend.dto.LoginResponse;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Username atau password salah"
                        )
                );

        if (user.getStatus() != User.Status.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Akun tidak aktif"
            );
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Username atau password salah"
            );
        }

        String token = jwtService.generateToken(user);

        return new LoginResponse(user, token);
    }
}