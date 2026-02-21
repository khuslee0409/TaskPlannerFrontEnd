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
import planner.api.VerifyApi;
import planner.api.dto.RegisterRequest;

public class ConfirmationPage {

    

    private final VerifyApi api = new VerifyApi(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));

    @FXML
    private TextField verifyCode;

    @FXML
    private Label confirmLabel;

    @FXML
    void SubmitCode(ActionEvent event) {
        String code = verifyCode.getText().trim();
        String email = Session.getPendingEmail();
        
        if (code.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Verification code field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        if (email == null || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Email not found. Please register again.");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        try {
            api.verifyCode(email, code);
            confirmLabel.setText("You can now login");
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Verification Failed");
            alert.setHeaderText(null);
            alert.setContentText("Invalid verification code");
            System.out.println(e.getMessage());
            setAlertIcon(alert);
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        SceneNavigator.goToLogin();
    }

    private void setAlertIcon(Alert alert) {
    Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
    alertStage.getIcons().add(new Image(getClass().getResourceAsStream("/planner/icon.png")));
    }

}
