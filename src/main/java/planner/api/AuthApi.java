package planner.api;

import planner.api.dto.AuthResponse;
import planner.api.dto.LoginRequest;

public class AuthApi {
    private final ApiClient client;

    public AuthApi(ApiClient client) {
        this.client = client;
    }

    public AuthResponse login(String username, String password) throws Exception {
        return client.postJson("/api/auth/login",
                new LoginRequest(username, password),
                AuthResponse.class,
                null);
    }
}
