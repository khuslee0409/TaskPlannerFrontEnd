package planner;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        SceneNavigator.init(stage);
        SceneNavigator.goToLandingPage();
        stage.setTitle("Student Planner");
        
        try {
            var iconStream = getClass().getResourceAsStream("/planner/icon.png");
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            }
        } catch (Exception e) {
            System.err.println("Could not load application icon: " + e.getMessage());
        }

        stage.setResizable(false);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    } 
}
