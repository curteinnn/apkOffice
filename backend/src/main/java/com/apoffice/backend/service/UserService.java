package com.apoffice.backend.service;

import com.apoffice.backend.dto.CreateUserRequest;
import com.apoffice.backend.entity.User;

import com.apoffice.backend.entity.User;
import com.apoffice.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.apoffice.backend.dto.UpdateUserRequest;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User createUser(CreateUserRequest request) {

    User user = new User();

    user.setUsername(request.getUsername());
    user.setPassword(request.getPassword());
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
}