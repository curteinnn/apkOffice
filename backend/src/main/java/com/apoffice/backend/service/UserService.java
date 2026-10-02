package com.apoffice.backend.service;

import com.apoffice.backend.dto.ChangePasswordRequest;
import com.apoffice.backend.dto.CreateUserRequest;
import com.apoffice.backend.dto.UpdateUserRequest;
import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
            
        
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
        public User createUser(CreateUserRequest request) {

        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNamaLengkap(request.getNamaLengkap());
        user.setEmail(request.getEmail());
        user.setJabatan(request.getJabatan());

        user.setRole(
                User.Role.valueOf(request.getRole())
        );

        user.setStatus(User.Status.ACTIVE);

        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);

    }
        public User updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User tidak ditemukan"
                        )
                );

        user.setUsername(request.getUsername());
        user.setNamaLengkap(request.getNamaLengkap());
        user.setEmail(request.getEmail());
        user.setJabatan(request.getJabatan());

        user.setRole(
                User.Role.valueOf(request.getRole())
        );

        user.setStatus(
                User.Status.valueOf(request.getStatus())
        );

        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
    
        public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User tidak ditemukan"
            );
        }

        userRepository.deleteById(id);
    }
        
        public void changePassword(
            String username,
            ChangePasswordRequest request
        ) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "User tidak ditemukan"
                            )
                    );

        if (!passwordEncoder.matches(
                request.getPasswordLama(),
                user.getPassword()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password lama salah"
            );
        }

        if (request.getPasswordBaru() == null ||
                request.getPasswordBaru().length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password baru minimal 6 karakter"
            );
        }

        user.setPassword(
                passwordEncoder.encode(request.getPasswordBaru())
        );

        userRepository.save(user);
    }
}