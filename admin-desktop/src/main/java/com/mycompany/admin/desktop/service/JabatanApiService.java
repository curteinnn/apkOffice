package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.JabatanDto;
import com.mycompany.admin.desktop.dto.JabatanRequest;
import com.mycompany.admin.desktop.dto.UpdateJabatanStatusRequest;
import java.io.IOException;
import java.util.List;

public final class JabatanApiService {
    private final ApiClient api;

    public JabatanApiService(ApiClient api) {
        this.api = api;
    }

    public List<JabatanDto> getAll() throws IOException, InterruptedException {
        List<JabatanDto> rows = api.get("/api/jabatan", new TypeReference<List<JabatanDto>>() {});
        return rows == null ? List.of() : rows;
    }

    public List<JabatanDto> getActive() throws IOException, InterruptedException {
        List<JabatanDto> rows = api.get("/api/jabatan?aktif=true", new TypeReference<List<JabatanDto>>() {});
        return rows == null ? List.of() : rows;
    }

    public JabatanDto create(JabatanRequest request) throws IOException, InterruptedException {
        return api.post("/api/jabatan", request, JabatanDto.class);
    }

    public JabatanDto update(long id, JabatanRequest request) throws IOException, InterruptedException {
        return api.put("/api/jabatan/" + id, request, JabatanDto.class);
    }

    public JabatanDto updateStatus(long id, UpdateJabatanStatusRequest request)
            throws IOException, InterruptedException {
        return api.put("/api/jabatan/" + id + "/status", request, JabatanDto.class);
    }

    public void delete(long id) throws IOException, InterruptedException {
        api.delete("/api/jabatan/" + id);
    }
}
