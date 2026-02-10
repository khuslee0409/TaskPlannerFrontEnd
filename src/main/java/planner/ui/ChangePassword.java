package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import planner.SceneNavigator;
import planner.api.ApiClient;
import planner.api.ForgotPassword;
import planner.api.VerifyResetCodeApi;
import planner.api.dto.VerifyResetCodeResponse;

public class ChangePassword {
    private final ForgotPassword api = new ForgotPassword(new ApiClient("http://localhost:8080"));
    private final VerifyResetCodeApi verifyApi = new VerifyResetCodeApi(new ApiClient("http://localhost:8080"));

    @FXML
    private PasswordField changePassCode;

    @FXML
    private TextField changePassEmail;

    @FXML
    private Label warningMessageChangePass;

    @FXML
    void sendCode(ActionEvent event) {
        try {
            String email = changePassEmail.getText().trim();
            
            if (email.isEmpty()) {
                warningMessageChangePass.setText("Please enter your email");
                warningMessageChangePass.setStyle("-fx-text-fill: red;");
                return;
            }
            
            api.forgotPassword(email);
            
            warningMessageChangePass.setText("Reset code sent to your email!");
            warningMessageChangePass.setStyle("-fx-text-fill: green;");
            
        } catch (Exception e) {
            warningMessageChangePass.setText("Error: " + e.getMessage());
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void submitCode(ActionEvent event) {
        try {
            String email = changePassEmail.getText().trim();
            String code = changePassCode.getText().trim();
            
            if (email.isEmpty() || code.isEmpty()) {
                warningMessageChangePass.setText("Please enter email and code");
                warningMessageChangePass.setStyle("-fx-text-fill: red;");
                return;
            }
            
            VerifyResetCodeResponse response = verifyApi.verifyCode(email, code);
            
            if (response != null && response.getResetToken() != null) {
                // Code verified successfully, navigate to new password page
                SceneNavigator.goToNewPassword(response.getEmail(), response.getResetToken());
            } else {
                warningMessageChangePass.setText("Invalid or expired code");
                warningMessageChangePass.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) {
            warningMessageChangePass.setText("Error: " + e.getMessage());
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        SceneNavigator.goToLogin();
    }
}