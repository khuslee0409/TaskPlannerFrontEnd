package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.ProgressBarTableCell;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.TaskApi;
import planner.api.dto.TaskDto;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TasksController {

   @FXML
    private Label dateL;

    @FXML
    private Label priorityL;

    @FXML
    private Label progressL;

    @FXML
    private Label statusLabel;

    @FXML
    private Label taskL;

    @FXML
    private Label user;


    @FXML
    void addTask(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText("Create a new task");
        dialog.setContentText("Task title : ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
        String title = result.get().trim();
        
        try {
            taskApi.createTask(Session.getToken(), title);
            

            loadTasks();
            
        } catch (Exception e) {
            statusLabel.setText("Failed to create task: " + e.getMessage());
        }
        }

    }
    @FXML private TableView<TaskDto> tasksTable;

    private final TaskApi taskApi =
            new TaskApi(new ApiClient("http://localhost:8080"));

    @FXML
    private void initialize() {
        user.setText("Logged in as: " + Session.getUsername());
        setupTableColumns();
        loadTasks();
    }

    private void setupTableColumns() {
    TableColumn<TaskDto, Integer> priorityCol = new TableColumn<>("Priority");
    priorityCol.setPrefWidth(80);
    priorityCol.setCellValueFactory(cellData -> {
    int index = tasksTable.getItems().indexOf(cellData.getValue());
    return new SimpleIntegerProperty(index + 1).asObject();
    });
    
    // 2. Task Column - shows title
    TableColumn<TaskDto, String> taskCol = new TableColumn<>("Task");
    taskCol.setPrefWidth(250);
    taskCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().title)
    );
    
    // 3. Progress Column - shows progress bar + percentage
    TableColumn<TaskDto, Integer> progressCol = new TableColumn<>("Progress");
    progressCol.setPrefWidth(200);
    progressCol.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(cellData.getValue().progress).asObject()
    );
    progressCol.setCellFactory(col -> new ProgressBarTableCell());
    
    // 4. Date Column - placeholder for now
    TableColumn<TaskDto, String> dateCol = new TableColumn<>("Date");
    dateCol.setPrefWidth(100);
    dateCol.setCellValueFactory(cellData -> {
    String dateStr = cellData.getValue().createdAt;
    if (dateStr == null || dateStr.isEmpty()) {
        return new SimpleStringProperty("N/A");
    }
    
    try {
        // Parse the date from backend format
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);
        
        // Format to dd-MM-yyyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formatted = dateTime.format(formatter);
        
        return new SimpleStringProperty(formatted);
    } catch (Exception e) {
        // If parsing fails, return as-is
        return new SimpleStringProperty(dateStr);
    }
    });
    
    // Add all columns to the table
    tasksTable.getColumns().addAll(priorityCol, taskCol, progressCol, dateCol);
}

    @FXML
    void logOut(ActionEvent event) {
        Session.clear();
        SceneNavigator.goToLogin();
    }

    private void loadTasks() {

        try {
            TaskDto[] tasks = taskApi.getTasks(Session.getToken());
            tasksTable.getItems().setAll(List.of(tasks));
        } catch (Exception e) {
            statusLabel.setText("Failed to load tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ProgressBarTableCell extends TableCell<TaskDto, Integer> {
        private final ProgressBar progressBar = new ProgressBar();
        private final Label percentLabel = new Label();
        private final HBox container = new HBox(10, progressBar, percentLabel);
        
        public ProgressBarTableCell() {
            progressBar.setPrefWidth(120);
            container.setAlignment(Pos.CENTER_LEFT);
        }
        
        @Override
        protected void updateItem(Integer progress, boolean empty) {
            super.updateItem(progress, empty);
            
            if (empty || progress == null) {
                setGraphic(null);
            } else {
                progressBar.setProgress(progress / 100.0);
                percentLabel.setText(progress + "%");
                setGraphic(container);
            }
        }
    }
}
