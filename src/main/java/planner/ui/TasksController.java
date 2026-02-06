package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.TaskApi;
import planner.api.dto.TaskDto;

import java.util.List;

public class TasksController {

    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private ListView<TaskDto> tasksList;

    private final TaskApi taskApi =
            new TaskApi(new ApiClient("http://localhost:8080"));

    @FXML
    private void initialize() {
        userLabel.setText("Logged in as: " + Session.getUsername());
        loadTasks();
    }

    @FXML
    private void onRefresh() {
        loadTasks();
    }

    @FXML
    void logOut(ActionEvent event) {
        Session.clear();
        SceneNavigator.goToLogin();
    }

    private void loadTasks() {
        statusLabel.setText("");

        try {
            TaskDto[] tasks = taskApi.getTasks(Session.getToken());
            tasksList.getItems().setAll(List.of(tasks));
        } catch (Exception e) {
            statusLabel.setText("Failed to load tasks: " + e.getMessage());
        }
    }
}
