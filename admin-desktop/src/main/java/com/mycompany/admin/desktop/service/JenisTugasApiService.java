package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.JenisTugasDto;
import com.mycompany.admin.desktop.dto.JenisTugasRequest;
import java.io.IOException;
import java.util.List;

public final class JenisTugasApiService {
    private final ApiClient api;

    public JenisTugasApiService(ApiClient api) { this.api = api; }

    public List<JenisTugasDto> getAll() throws IOException, InterruptedException {
        List<JenisTugasDto> rows = api.get("/api/jenis-tugas", new TypeReference<List<JenisTugasDto>>() {});
        return rows == null ? List.of() : rows;
    }

    public JenisTugasDto create(JenisTugasRequest request) throws IOException, InterruptedException {
        return api.post("/api/jenis-tugas", request, JenisTugasDto.class);
    }

    public void update(String id, JenisTugasRequest request) throws IOException, InterruptedException {
        api.put("/api/jenis-tugas/" + pathSegment(id), request);
    }

    public void delete(String id) throws IOException, InterruptedException {
        api.delete("/api/jenis-tugas/" + pathSegment(id));
    }

    private String pathSegment(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
