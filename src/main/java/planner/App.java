package planner;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneNavigator.init(stage);
        SceneNavigator.goToLogin();
        stage.setTitle("Student Planner");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
