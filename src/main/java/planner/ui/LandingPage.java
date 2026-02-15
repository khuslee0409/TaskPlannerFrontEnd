package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import planner.SceneNavigator;

public class LandingPage {

    @FXML
    void getStarted(ActionEvent event) {
        SceneNavigator.goToLogin();
    }
    
}
