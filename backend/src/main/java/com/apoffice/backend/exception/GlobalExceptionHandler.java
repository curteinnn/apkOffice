package com.apoffice.backend.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex
    ) {

        String message = ex.getReason() != null
                ? ex.getReason()
                : "Terjadi kesalahan";

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of(
                        "status", ex.getStatusCode().value(),
                        "message", message
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation() {
        return ResponseEntity
                .status(org.springframework.http.HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", org.springframework.http.HttpStatus.CONFLICT.value(),
                        "message", "Data bertentangan dengan data yang sudah tersimpan"
                ));
    }
}
