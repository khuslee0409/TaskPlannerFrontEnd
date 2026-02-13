package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.RegisterApi;
import planner.api.dto.RegisterRequest;

public class RegisterController {

    
    @FXML
    private TextField confirmCodeEmail;

    @FXML
    private TextField email;

    @FXML
    private TextField confirmPass;

    @FXML
    private TextField newPassword;

    @FXML
    private Label statusLabel;

    @FXML
    private Label warningMessage;

    @FXML
    private TextField newUsername;

    private final RegisterApi registerApi = new RegisterApi(new ApiClient("http://localhost:8080"));

    @FXML
    void addUser(ActionEvent event) {

        String username = newUsername.getText().trim();
        String password = newPassword.getText().trim();
        String coPass = confirmPass.getText().trim();
        String theEmail = email.getText().trim();

        // Check if username is empty
        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Username field is empty");
            alert.showAndWait();
            return;
        }

        if (theEmail.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Email field is empty");
            alert.showAndWait();
            return;
        }

        // Validate email format
        if (!isValidEmail(theEmail)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address");
            alert.showAndWait();
            return;
        }

        if (password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Password field is empty");
            alert.showAndWait();
            return;
        }

        if (coPass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Confirm Password field is empty");
            alert.showAndWait();
            return;
        }

        try {
            if(coPass.equals(password)){
                registerApi.register(username, password, theEmail);
                Session.setPendingEmail(theEmail);
                statusLabel.setText("Register successfull"); 
            }else{
                statusLabel.setText("Passwords don't match");
            }
           
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
        }

        SceneNavigator.goToConfirmationPage();

    }

    @FXML
    void onBack(ActionEvent event) {
        SceneNavigator.goToLogin();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

}

