package planner.ui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import planner.SceneNavigator;
import planner.api.ApiClient;
import planner.api.ForgotPassword;
import planner.api.VerifyResetCodeApi;
import planner.api.dto.VerifyResetCodeResponse;

public class ChangePassword {
    private final ForgotPassword api = new ForgotPassword(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));
    private final VerifyResetCodeApi verifyApi = new VerifyResetCodeApi(new ApiClient("https://taskplanner-production-7e23.up.railway.app"));

    @FXML
    private PasswordField changePassCode;

    @FXML
    private TextField changePassEmail;

    @FXML
    private Label warningMessageChangePass;

    @FXML
    void sendCode(ActionEvent event) {
        String email = changePassEmail.getText().trim();
        
        if (email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Email field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        if (!isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        warningMessageChangePass.setText("Sending code...");
        warningMessageChangePass.setStyle("-fx-text-fill: blue;");

        Task<Void> sendCodeTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                api.forgotPassword(email);
                return null;
            }
        };

        sendCodeTask.setOnSucceeded(e -> {
            warningMessageChangePass.setText("Reset code sent to your email!");
            warningMessageChangePass.setStyle("-fx-text-fill: green;");
        });

        sendCodeTask.setOnFailed(e -> {
            warningMessageChangePass.setText("Error");
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        });

        new Thread(sendCodeTask).start();
    }

    @FXML
    void submitCode(ActionEvent event) {
        String email = changePassEmail.getText().trim();
        String code = changePassCode.getText().trim();
        
        if (email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Email field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        if (!isValidEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid email address");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        if (code.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Empty Field");
            alert.setHeaderText(null);
            alert.setContentText("Reset code field is empty");
            setAlertIcon(alert);
            alert.showAndWait();
            return;
        }
        
        warningMessageChangePass.setText("Verifying code...");
        warningMessageChangePass.setStyle("-fx-text-fill: blue;");

        Task<VerifyResetCodeResponse> verifyTask = new Task<>() {
            @Override
            protected VerifyResetCodeResponse call() throws Exception {
                return verifyApi.verifyCode(email, code);
            }
        };

        verifyTask.setOnSucceeded(e -> {
            VerifyResetCodeResponse response = verifyTask.getValue();
            if (response != null && response.getResetToken() != null) {
                SceneNavigator.goToNewPassword(response.getEmail(), response.getResetToken());
            } else {
                warningMessageChangePass.setText("Invalid or expired code");
                warningMessageChangePass.setStyle("-fx-text-fill: red;");
            }
        });

        verifyTask.setOnFailed(e -> {
            warningMessageChangePass.setText("Error: No user exist with such email");
            warningMessageChangePass.setStyle("-fx-text-fill: red;");
        });

        new Thread(verifyTask).start();
    }

    @FXML
    void goBack(ActionEvent event) {
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