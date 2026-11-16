package com.mycompany.admin.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mycompany.admin.desktop.api.ApiClient;
import com.mycompany.admin.desktop.dto.PelangganDto;
import com.mycompany.admin.desktop.dto.SavePelangganRequest;
import java.io.IOException;
import java.util.List;

public final class PelangganApiService {
    private final ApiClient api;

    public PelangganApiService(ApiClient api) { this.api = api; }

    public List<PelangganDto> getAll() throws IOException, InterruptedException {
        List<PelangganDto> rows = api.get("/api/pelanggan", new TypeReference<List<PelangganDto>>() {});
        return rows == null ? List.of() : rows;
    }

    public PelangganDto create(SavePelangganRequest request) throws IOException, InterruptedException {
        return api.post("/api/pelanggan", request, PelangganDto.class);
    }

    public PelangganDto update(long id, SavePelangganRequest request) throws IOException, InterruptedException {
        return api.put("/api/pelanggan/" + id, request, PelangganDto.class);
    }

    public void delete(long id) throws IOException, InterruptedException {
        api.delete("/api/pelanggan/" + id);
    }
}
