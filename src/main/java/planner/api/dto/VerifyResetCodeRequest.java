package planner.api.dto;


public class VerifyResetCodeRequest {
    private String email;
    private String code;

    public VerifyResetCodeRequest() {}


    public VerifyResetCodeRequest(String theEmail, String theCode){
        this.email = theEmail;
        this.code = theCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

