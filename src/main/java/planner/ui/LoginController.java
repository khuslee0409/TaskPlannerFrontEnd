package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.AuthApi;
import planner.api.dto.AuthResponse;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final AuthApi authApi =
            new AuthApi(new ApiClient("http://localhost:8080"));

    @FXML
    private void onLogin() {
        statusLabel.setText("");

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        try {
            AuthResponse res = authApi.login(username, password);
            Session.set(username, res.getToken());
            SceneNavigator.goToTasks();
        } catch (Exception e) {
            statusLabel.setText("Login failed, please enter correct credentials");
        }
    }

     @FXML
    void onRegister(ActionEvent event) {
        SceneNavigator.goToRegister();
    }

     @FXML
    void forgotPassword(ActionEvent event) {
        SceneNavigator.goToChangePassword();
    }

    
}
