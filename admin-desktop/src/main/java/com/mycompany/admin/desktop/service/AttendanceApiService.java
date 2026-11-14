package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.AttendanceDto;
import java.io.IOException;
import java.util.List;

public final class AttendanceApiService {
    private final ApiClient api;
    public AttendanceApiService(ApiClient api) { this.api = api; }
    public List<AttendanceDto> getAll() throws IOException, InterruptedException {
        List<AttendanceDto> rows = api.get("/api/attendance", new TypeReference<List<AttendanceDto>>() {});
        return rows == null ? List.of() : rows;
    }
}
