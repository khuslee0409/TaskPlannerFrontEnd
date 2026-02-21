package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
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

    private final RegisterApi registerApi = new RegisterApi(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));

    @FXML
    void addUser(ActionEvent event) {

        String username = newUsername.getText().trim();
        String password = newPassword.getText().trim();
        String coPass = confirmPass.getText().trim();
        String theEmail = email.getText().trim();

        if (username.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Username field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }

        if (theEmail.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Email field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }

        // Validate email format
        if (!isValidEmail(theEmail)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }

        if (password.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Password field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }

        if (coPass.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Confirm Password field is empty");
            setAlertIcon(alert);
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

    private void setAlertIcon(Alert alert) {
        try {
            alert.setOnShown(event -> {
                try {
                    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
                    alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/planner/icon.png")));
                } catch (Exception e) {
                }
            });
        } catch (Exception e) {
        }
    }

}

