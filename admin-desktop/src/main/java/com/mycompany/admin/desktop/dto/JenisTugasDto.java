package com.mycompany.admin.desktop.dto;

public record JenisTugasDto(String id, String nama) {
    @Override public String toString() { return id + " - " + nama; }
}
