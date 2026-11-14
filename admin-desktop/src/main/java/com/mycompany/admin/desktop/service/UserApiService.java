package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.CreateUserRequest;
import com.mycompany.admin.desktop.dto.UserDto;
import java.io.IOException;
import java.util.List;

public final class UserApiService {
    private final ApiClient api;
    public UserApiService(ApiClient api) { this.api = api; }
    public List<UserDto> getUsers() throws IOException, InterruptedException {
        List<UserDto> rows = api.get("/api/users", new TypeReference<List<UserDto>>() {});
        return rows == null ? List.of() : rows;
    }
    public UserDto create(CreateUserRequest request) throws IOException, InterruptedException {
        return api.post("/api/users", request, UserDto.class);
    }
}
