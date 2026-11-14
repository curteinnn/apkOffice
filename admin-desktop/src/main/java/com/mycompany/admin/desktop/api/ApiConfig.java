package com.mycompany.admin.desktop.api;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Loads the external API configuration from ./config.properties. */
public final class ApiConfig {
    private static final String CONFIG_FILE = "config.properties";
    private final URI baseUri;

    private ApiConfig(URI baseUri) {
        this.baseUri = baseUri;
    }

    public static ApiConfig load() throws IOException {
        Path path = Path.of(CONFIG_FILE).toAbsolutePath().normalize();
        if (!Files.isRegularFile(path)) {
            throw new IOException("File konfigurasi tidak ditemukan: " + path
                    + ". Buat file tersebut dan isi API_BASE_URL.");
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(path)) {
            properties.load(input);
        }

        String value = properties.getProperty("API_BASE_URL", "").trim();
        if (value.isEmpty()) {
            throw new IOException("Property API_BASE_URL belum diisi pada " + path);
        }

        try {
            URI uri = URI.create(value).normalize();
            String scheme = uri.getScheme();
            if (uri.getHost() == null || !("http".equalsIgnoreCase(scheme)
                    || "https".equalsIgnoreCase(scheme))
                    || uri.getUserInfo() != null || uri.getRawQuery() != null
                    || uri.getRawFragment() != null) {
                throw new IllegalArgumentException("URL harus berupa alamat HTTP/HTTPS yang valid.");
            }
            String normalized = value.replaceAll("/+$", "");
            return new ApiConfig(URI.create(normalized));
        } catch (IllegalArgumentException ex) {
            throw new IOException("Nilai API_BASE_URL tidak valid pada " + path + ".", ex);
        }
    }

    public URI endpoint(String path) {
        return baseUri.resolve(path.startsWith("/") ? path : "/" + path);
    }
}
