package planner.api;

import planner.api.dto.ForgotPasswordRequest;
import planner.api.dto.VerifyResetCodeRequest;
import planner.api.dto.VerifyResetCodeResponse;

public class VerifyResetCodeApi {
    private final ApiClient client;

    public VerifyResetCodeApi(ApiClient client) {
        this.client = client;
    }

    
    public VerifyResetCodeResponse verifyCode(String email, String code) throws Exception {
       return client.postJson("/api/auth/verify-reset-code",
                new VerifyResetCodeRequest(email, code),
                VerifyResetCodeResponse.class,  // Changed from String.class
                null);
    }
}
