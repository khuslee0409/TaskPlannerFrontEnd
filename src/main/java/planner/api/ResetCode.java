package planner.api;

import planner.api.dto.ResetPasswordRequest;
import planner.api.dto.VerifyResetCodeRequest;

public class ResetCode {
    private final ApiClient client;

    public ResetCode(ApiClient client) {
        this.client = client;
    }

    public void setNewPassword(String email, String token, String newPassword) throws Exception {
        client.postJson("/api/auth/reset-password",
                new ResetPasswordRequest(email, token, newPassword),
                Void.class,
                null);
    }
}
