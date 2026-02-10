package planner.api;

import planner.api.dto.RegisterRequest;

public class RegisterApi {

    private final ApiClient client;

    public RegisterApi(ApiClient theClient){
        this.client = theClient;
    }

   public void register(String username, String password, String email) throws Exception{
    // Just send request, ignore response parsing
    try {
        client.postJson("/api/auth/register", new RegisterRequest(username, password, email), String.class, null);
    } catch (Exception e) {
        // If it's just the parsing error but registration worked, ignore it
        if (!e.getMessage().contains("Unrecognized token")) {
            throw e;
        }
    }
}





    
}
