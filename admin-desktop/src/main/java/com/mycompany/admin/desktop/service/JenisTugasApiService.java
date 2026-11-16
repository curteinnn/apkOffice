package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.JenisTugasDto;
import java.io.IOException;
import java.util.List;

public final class JenisTugasApiService {
    private final ApiClient api;

    public JenisTugasApiService(ApiClient api) { this.api = api; }

    public List<JenisTugasDto> getAll() throws IOException, InterruptedException {
        List<JenisTugasDto> rows = api.get("/api/jenis-tugas", new TypeReference<List<JenisTugasDto>>() {});
        return rows == null ? List.of() : rows;
    }
}
