package planner.api;

import planner.api.dto.RegisterRequest;
import planner.api.dto.VerifyRequest;

public class VerifyApi {

    private final ApiClient theClient;

    public VerifyApi(ApiClient client){
        theClient = client;
    }

    public boolean verifyCode(String email, String code) throws Exception {
        try {
            theClient.postJson("/api/auth/verify-code", new VerifyRequest(email, code), Void.class, null);
            return true;  // API call succeeded, code is valid
        } catch (Exception e) {
            // Check if it's a "wrong code" error vs a real system error
            if (e.getMessage() != null && (e.getMessage().contains("Wrong code") || e.getMessage().contains("400"))) {
                return false;  // Code is wrong, but user exists
            }
            // Real error (network, server down, user not found, etc.)
            throw e;
        }
    }

    
}
