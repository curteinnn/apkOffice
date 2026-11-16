package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.CreateTaskRequest;
import com.mycompany.admin.desktop.dto.CreateBulkTaskRequest;
import com.mycompany.admin.desktop.dto.TaskDto;
import com.mycompany.admin.desktop.dto.VerifyTaskRequest;
import java.io.IOException;
import java.util.List;

public final class TaskApiService {
    private final ApiClient api;
    public TaskApiService(ApiClient api) { this.api = api; }
    public List<TaskDto> getAll() throws IOException, InterruptedException {
        List<TaskDto> rows = api.get("/api/tugas", new TypeReference<List<TaskDto>>() {});
        return rows == null ? List.of() : rows;
    }
    public TaskDto create(CreateTaskRequest request) throws IOException, InterruptedException {
        return api.post("/api/tugas", request, TaskDto.class);
    }
    public TaskDto createBulk(CreateBulkTaskRequest request) throws IOException, InterruptedException {
        return api.post("/api/tugas/bulk", request, TaskDto.class);
    }
    public TaskDto verify(String id, VerifyTaskRequest request) throws IOException, InterruptedException {
        return api.put("/api/tugas/" + id + "/verify", request, TaskDto.class);
    }
}
