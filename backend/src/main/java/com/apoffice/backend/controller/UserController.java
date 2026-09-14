package com.apoffice.backend.controller;

import com.apoffice.backend.entity.User;
import com.apoffice.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.apoffice.backend.dto.CreateUserRequest;
import com.apoffice.backend.entity.User;

import com.apoffice.backend.dto.UpdateUserRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }
    
    @PutMapping("/{id}")
    public User updateUser(
        @PathVariable Long id,
        @RequestBody UpdateUserRequest request)
    {
    return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "user berhasil di hapus";
    }
    
}