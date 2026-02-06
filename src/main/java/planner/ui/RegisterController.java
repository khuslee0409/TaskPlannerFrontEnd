package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import planner.SceneNavigator;
import planner.api.ApiClient;
import planner.api.RegisterApi;
import planner.api.dto.RegisterRequest;

public class RegisterController {

    
    @FXML
    private TextField confirmCodeEmail;

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

        String username = newUsername.getText();
        String password = newPassword.getText();
        String coPass = confirmPass.getText();



        try {
            if(coPass.equals(password)){
                registerApi.register(username, password);
                statusLabel.setText("Register successfull"); 
            }else{
                statusLabel.setText("Passwords don't match");
            }
           
        } catch (Exception e) {
            statusLabel.setText("Login failed: " + e.getMessage());
        }

    }

    @FXML
    void onBack(ActionEvent event) {
        SceneNavigator.goToLogin();
    }

    @FXML
    void sendCode(ActionEvent event) {

    }

}

