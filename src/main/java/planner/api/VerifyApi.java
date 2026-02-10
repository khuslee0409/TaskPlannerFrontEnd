package planner.api;

import planner.api.dto.RegisterRequest;
import planner.api.dto.VerifyRequest;

public class VerifyApi {

    private final ApiClient theClient;

    public VerifyApi(ApiClient client){
        theClient = client;
    }

    public void verifyCode(String email, String code) throws Exception{
    // Just send request, ignore response parsing
    try {
        theClient.postJson("/api/auth/verify-code", new VerifyRequest(email, code), String.class, null);
    } catch (Exception e) {
        if (!e.getMessage().contains("Wrong code")) {
            throw e;
        }
    }
}

    
}
