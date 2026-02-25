package planner.ui;

import javafx.concurrent.Task;
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
        String pendingEmail = Session.getPendingEmail();
        final String email = (pendingEmail == null) ? null : pendingEmail.trim().toLowerCase();
        
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
        
        confirmLabel.setText("Verifying...");

        Task<Boolean> verifyTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                return api.verifyCode(email, code);
                
            }
        };

        verifyTask.setOnSucceeded(e -> {
            Boolean isValid = verifyTask.getValue();
            if (Boolean.TRUE.equals(isValid)) {
                confirmLabel.setText("Verification successful! You can now login.");
                confirmLabel.setStyle("-fx-text-fill: green;");
                
                new Thread(() -> {
                    try {
                        Thread.sleep(1500);
                        javafx.application.Platform.runLater(() -> SceneNavigator.goToLogin());
                    } catch (InterruptedException ex) {
                    }
                }).start();
            } else {
                confirmLabel.setText("Invalid verification code. Please try again.");
                confirmLabel.setStyle("-fx-text-fill: red;");
            }
        });

        verifyTask.setOnFailed(e -> {
            confirmLabel.setText("");
            Throwable ex = verifyTask.getException();
            String errorMsg = "Verification failed";
            
            if (ex != null && ex.getMessage() != null && !ex.getMessage().isEmpty()) {
                errorMsg = ex.getMessage();
                if (errorMsg.startsWith("HTTP ")) {
                    int colonIndex = errorMsg.indexOf(":");
                    if (colonIndex > 0 && colonIndex < errorMsg.length() - 1) {
                        errorMsg = errorMsg.substring(colonIndex + 1).trim();
                    }
                }
            }
            
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Verification Failed");
            alert.setHeaderText(null);
            alert.setContentText(errorMsg);
            setAlertIcon(alert);
            alert.showAndWait();
        });

        new Thread(verifyTask).start();
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
