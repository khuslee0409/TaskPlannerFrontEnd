package planner.api;

import planner.api.dto.AuthResponse;
import planner.api.dto.ForgotPasswordRequest;
import planner.api.dto.LoginRequest;

public class ForgotPassword {
    private final ApiClient client;

    public ForgotPassword(ApiClient client) {
        this.client = client;
    }

    public void forgotPassword(String email) throws Exception {
        client.postJson("/api/auth/forgot-password",
                new ForgotPasswordRequest(email),
                Void.class,
                null);
    }
    
}
