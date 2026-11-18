package com.mycompany.admin.desktop.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.admin.desktop.auth.AuthSession;
import java.io.IOException;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Shared JSON and JWT transport for all Admin Desktop API calls. */
public final class ApiClient {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final ApiConfig config;
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8)).build();

    public ApiClient(ApiConfig config) {
        this.config = config;
    }

    public <T> T get(String path, TypeReference<T> responseType) throws IOException, InterruptedException {
        return request("GET", path, null, responseType, false);
    }

    public <T> T post(String path, Object body, Class<T> responseType) throws IOException, InterruptedException {
        return request("POST", path, body, responseType, false);
    }

    public <T> T post(String path, Object body, TypeReference<T> responseType) throws IOException, InterruptedException {
        return request("POST", path, body, responseType, false);
    }

    public <T> T put(String path, Object body, Class<T> responseType) throws IOException, InterruptedException {
        return request("PUT", path, body, responseType, false);
    }

    public void put(String path, Object body) throws IOException, InterruptedException {
        send("PUT", path, body, false);
    }

    public void delete(String path) throws IOException, InterruptedException {
        send("DELETE", path, null, false);
    }

    private <T> T request(String method, String path, Object body,
            TypeReference<T> type, boolean publicRequest) throws IOException, InterruptedException {
        HttpResponse<String> response = send(method, path, body, publicRequest);
        if (response.body().isBlank()) return null;
        try {
            return JSON.readValue(response.body(), type);
        } catch (IOException ex) {
            throw new IOException("Backend mengembalikan JSON yang tidak sesuai format.", ex);
        }
    }

    private <T> T request(String method, String path, Object body,
            Class<T> type, boolean publicRequest) throws IOException, InterruptedException {
        HttpResponse<String> response = send(method, path, body, publicRequest);
        if (response.body().isBlank()) return null;
        try {
            return JSON.readValue(response.body(), type);
        } catch (IOException ex) {
            throw new IOException("Backend mengembalikan JSON yang tidak sesuai format.", ex);
        }
    }

    public <T> T postPublic(String path, Object body, Class<T> type) throws IOException, InterruptedException {
        return request("POST", path, body, type, true);
    }

    private HttpResponse<String> send(String method, String path, Object body,
            boolean publicRequest) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(config.endpoint(path))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json");
        String token = AuthSession.getToken();
        if (!publicRequest && token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
        if (body == null) {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        } else {
            builder.header("Content-Type", "application/json")
                    .method(method, HttpRequest.BodyPublishers.ofString(JSON.writeValueAsString(body)));
        }

        HttpResponse<String> response;
        try {
            response = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException ex) {
            throw new IOException("Backend tidak dapat dihubungi. Periksa API_BASE_URL dan status backend.", ex);
        } catch (java.net.http.HttpTimeoutException ex) {
            throw new IOException("Backend tidak merespons dalam batas waktu yang ditentukan.", ex);
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            String message = errorMessage(response.body(), response.statusCode());
            if (response.statusCode() == 401 && !publicRequest) {
                AuthSession.clear();
                message = "Sesi login berakhir atau tidak valid. Silakan login kembali.";
            }
            throw new ApiException(response.statusCode(), message);
        }
        return response;
    }

    private String errorMessage(String body, int statusCode) {
        if (body != null && !body.isBlank()) {
            try {
                JsonNode json = JSON.readTree(body);
                if (json.hasNonNull("message")) return json.get("message").asText();
                if (json.hasNonNull("error")) return json.get("error").asText();
            } catch (IOException ignored) {
                // Do not echo an arbitrary response body into the UI.
            }
        }
        return "Request gagal (HTTP " + statusCode + "). Periksa data dan hak akses.";
    }
}
