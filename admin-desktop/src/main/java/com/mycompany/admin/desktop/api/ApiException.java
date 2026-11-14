package com.mycompany.admin.desktop.api;

import java.io.IOException;

public final class ApiException extends IOException {
    private final int statusCode;

    public ApiException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
