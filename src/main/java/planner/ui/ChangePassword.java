package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Field");
                alert.setHeaderText(null);
                alert.setContentText("Email field is empty");
                alert.showAndWait();
                return;
            }
            
            if (!isValidEmail(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Email");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid email address");
                alert.showAndWait();
                return;
            }
            
            api.forgotPassword(email);
            
            warningMessageChangePass.setText("Reset code sent to your email!");
            warningMessageChangePass.setStyle("-fx-text-fill: green;");
            
        } catch (Exception e) {
            warningMessageChangePass.setText("Error");
            System.out.println(e.getMessage());
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void submitCode(ActionEvent event) {
        try {
            String email = changePassEmail.getText().trim();
            String code = changePassCode.getText().trim();
            
            if (email.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Field");
                alert.setHeaderText(null);
                alert.setContentText("Email field is empty");
                alert.showAndWait();
                return;
            }
            
            if (!isValidEmail(email)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Email");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid email address");
                alert.showAndWait();
                return;
            }
            
            if (code.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Field");
                alert.setHeaderText(null);
                alert.setContentText("Reset code field is empty");
                alert.showAndWait();
                return;
            }
            
            VerifyResetCodeResponse response = verifyApi.verifyCode(email, code);
            
            if (response != null && response.getResetToken() != null) {
                SceneNavigator.goToNewPassword(response.getEmail(), response.getResetToken());
            } else {
                warningMessageChangePass.setText("Invalid or expired code");
                warningMessageChangePass.setStyle("-fx-text-fill: red;");
            }
            
        } catch (Exception e) {
            warningMessageChangePass.setText("Error: No user exist with such email");
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        SceneNavigator.goToLogin();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}