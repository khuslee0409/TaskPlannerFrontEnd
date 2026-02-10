package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.RegisterApi;
import planner.api.VerifyApi;
import planner.api.dto.RegisterRequest;

public class ConfirmationPage {

    

    private final VerifyApi api = new VerifyApi(new ApiClient("http://localhost:8080"));

    @FXML
    private TextField verifyCode;

    @FXML
    private Label confirmLabel;

    @FXML
    void SubmitCode(ActionEvent event) {
        try {
            api.verifyCode(Session.getPendingEmail(), verifyCode.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }

        confirmLabel.setText("You can now login");
    }

    @FXML
    void goToLogin(ActionEvent event) {
        SceneNavigator.goToLogin();
    }

}
