package com.mycompany.admin.desktop.service;

import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.LoginRequest;
import com.mycompany.admin.desktop.dto.LoginResponse;
import java.io.IOException;

public final class AuthApiService {
    private final ApiClient api;

    public AuthApiService(ApiClient api) { this.api = api; }

    public LoginResponse login(String username, String password) throws IOException, InterruptedException {
        return api.postPublic("/api/auth/login", new LoginRequest(username, password), LoginResponse.class);
    }
}
