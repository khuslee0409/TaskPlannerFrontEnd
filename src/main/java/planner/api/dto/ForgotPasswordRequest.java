package planner.api.dto;

public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String theEmail){
        this.email = theEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
