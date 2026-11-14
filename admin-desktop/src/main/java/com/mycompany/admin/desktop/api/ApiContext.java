package com.mycompany.admin.desktop.api;

import java.io.IOException;

public final class ApiContext {
    private static ApiClient client;
    private ApiContext() {}

    public static synchronized ApiClient client() throws IOException {
        if (client == null) client = new ApiClient(ApiConfig.load());
        return client;
    }
}
