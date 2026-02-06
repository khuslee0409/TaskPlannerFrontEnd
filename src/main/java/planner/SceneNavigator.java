package planner;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
