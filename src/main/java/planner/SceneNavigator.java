package planner;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import planner.ui.NewPassword;

import java.io.IOException;

public class SceneNavigator {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void goToLogin() {
        setScene("login.fxml", 700, 560);
    }

    public static void goToTasks() {
        setScene("tasks.fxml", 700, 560);
    }

    public static void goToRegister(){
        setScene("register.fxml", 700, 650);
    }

    public static void goToChangePassword(){
        setScene("changePassword.fxml", 700, 650);
    }

    public static void goToConfirmationPage(){
        setScene("confirmation.fxml", 700, 650);
    }

    public static void goToNewPassword(String email, String resetToken) {
    try {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("newPasswordPage.fxml"));
        Scene scene = new Scene(loader.load(), 700, 650);
        
        // Get the controller and pass the data
        NewPassword controller = loader.getController();  // Changed to NewPassword
        controller.setData(email, resetToken);  // Changed from initialize() to setData()
        
        stage.setScene(scene);
    } catch (IOException e) {
        throw new RuntimeException("Failed to load FXML: newPasswordPage.fxml", e);
    }
}

    private static void setScene(String fxml, int w, int h) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource(fxml));
            Scene scene = new Scene(loader.load(), w, h);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxml, e);
        }
    }
}