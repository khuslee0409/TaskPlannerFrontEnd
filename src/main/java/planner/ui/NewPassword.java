package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import planner.SceneNavigator;
import planner.api.ApiClient;
import planner.api.ResetCode;

public class NewPassword {
    
    private final ResetCode resetApi = new ResetCode(new ApiClient("http://localhost:8080"));
    
    private String email;
    private String resetToken;
    
    @FXML
    private PasswordField newPasswordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Label warningMessageNewPass;
    
    // Changed from initialize() to setData()
    public void setData(String email, String resetToken) {
        this.email = email;
        this.resetToken = resetToken;
    }
    
    @FXML
    void submitNewPassword(ActionEvent event) {
        try {
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
            
            // Reset password using the stored email and token
            resetApi.setNewPassword(email, resetToken, newPassword);
            
            warningMessageNewPass.setText("Password reset successfully!");
            warningMessageNewPass.setStyle("-fx-text-fill: green;");
            
            // Navigate to login page after 2 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(SceneNavigator::goToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } catch (Exception e) {
            warningMessageNewPass.setText("Error: " + e.getMessage());
            warningMessageNewPass.setStyle("-fx-text-fill: red;");
        }
    }
}