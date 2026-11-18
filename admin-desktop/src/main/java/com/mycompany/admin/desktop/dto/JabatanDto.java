package com.mycompany.admin.desktop.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JabatanDto(long id, String nama, String status) {
    @Override
    public String toString() {
        return "ACTIVE".equals(status) ? nama : nama + " (nonaktif)";
    }
}
