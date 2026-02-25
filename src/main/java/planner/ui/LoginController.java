package planner.ui;

import javafx.concurrent.Task;
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
            new AuthApi(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));

    @FXML
    private void onLogin() {
        statusLabel.setText("Logging in...");

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            statusLabel.setText("Please enter username and password.");
            return;
        }

        Task<AuthResponse> loginTask = new Task<>() {
            @Override
            protected AuthResponse call() throws Exception {
                return authApi.login(username, password);
            }
        };

        loginTask.setOnSucceeded(event -> {
            AuthResponse res = loginTask.getValue();
            Session.set(username, res.getToken());
            SceneNavigator.goToTasks();
        });

        loginTask.setOnFailed(event -> {
            statusLabel.setText("Login failed, please enter correct credentials");
        });

        new Thread(loginTask).start();
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
