package planner.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import planner.SceneNavigator;
import planner.api.ApiClient;
import planner.api.ResetCode;

public class NewPassword {
    
    private final ResetCode resetApi = new ResetCode(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));
    
    private String email;
    private String resetToken;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label warningMessageNewPass;
    
    public void setData(String email, String resetToken) {
        this.email = email;
        this.resetToken = resetToken;
    }
    
    @FXML
    void submitNewPassword(ActionEvent event) {
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            warningMessageNewPass.setText("Please enter and confirm your password");
            warningMessageNewPass.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            warningMessageNewPass.setText("Passwords do not match");
            warningMessageNewPass.setStyle("-fx-text-fill: red;");
            return;
        }
        
        if (newPassword.length() < 6) {
            warningMessageNewPass.setText("Password must be at least 6 characters");
            warningMessageNewPass.setStyle("-fx-text-fill: red;");
            return;
        }
        
        warningMessageNewPass.setText("Resetting password...");
        warningMessageNewPass.setStyle("-fx-text-fill: blue;");

        Task<Void> resetTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                resetApi.setNewPassword(email, resetToken, newPassword);
                Thread.sleep(2000); // Wait 2 seconds before navigating
                return null;
            }
        };

        resetTask.setOnSucceeded(e -> {
            warningMessageNewPass.setText("Password reset successfully!");
            warningMessageNewPass.setStyle("-fx-text-fill: green;");
            SceneNavigator.goToLogin();
        });

        resetTask.setOnFailed(e -> {
            warningMessageNewPass.setText("Error: " + resetTask.getException().getMessage());
            warningMessageNewPass.setStyle("-fx-text-fill: red;");
        });

        new Thread(resetTask).start();
    }
}