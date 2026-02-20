package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.TaskApi;
import planner.api.dto.TaskDto;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class TasksController {

    @FXML
    private ContextMenu contextMenu;

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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Task");
        dialog.setHeaderText("Create a new task with deadline");
        
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        
        ComboBox<Integer> hourBox = new ComboBox<>(FXCollections.observableArrayList(
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
        ));
        hourBox.setValue(12);
        hourBox.setPrefWidth(70);
        
        ComboBox<String> minuteBox = new ComboBox<>(FXCollections.observableArrayList(
            "00", "15", "30", "45"
        ));
        minuteBox.setValue("00");
        minuteBox.setPrefWidth(70);
        minuteBox.setEditable(true);
        
        ComboBox<String> ampmBox = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));
        ampmBox.setValue("PM");
        ampmBox.setPrefWidth(70);
        
        HBox timeBox = new HBox(5, hourBox, new Label(":"), minuteBox, ampmBox);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(datePicker, 1, 1);
        grid.add(new Label("Time:"), 0, 2);
        grid.add(timeBox, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String title = titleField.getText().trim();
            
            if (title.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Field");
                alert.setHeaderText(null);
                alert.setContentText("Task title field is empty");
                alert.showAndWait();
                return;
            }
            
            if (datePicker.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setHeaderText(null);
                alert.setContentText("Please select a date");
                alert.showAndWait();
                return;
            }
            
            LocalDate date = datePicker.getValue();
            int hour = hourBox.getValue();
            String minute = minuteBox.getValue();
            String ampm = ampmBox.getValue();
            
            if (date.equals(LocalDate.now())) {
                LocalDateTime now = LocalDateTime.now();
                
                int hour24 = hour;
                if (ampm.equals("PM") && hour != 12) {
                    hour24 = hour + 12;
                } else if (ampm.equals("AM") && hour == 12) {
                    hour24 = 0;
                }
                
                int selectedMinute = Integer.parseInt(minute);
                LocalDateTime selectedDateTime = LocalDateTime.of(date, java.time.LocalTime.of(hour24, selectedMinute));
                
                if (selectedDateTime.isBefore(now)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Time");
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot set deadline in the past");
                    alert.showAndWait();
                    return;
                }
            }
            
            String deadline = String.format("%02d-%02d-%04d %02d:%s %s",
                date.getDayOfMonth(), date.getMonthValue(), date.getYear(),
                hour, minute, ampm);
            
            try {
                taskApi.createTask(Session.getToken(), title, deadline);
                loadTasks();
                
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to create task: " + e.getMessage());
                System.out.println(e.getMessage());
                alert.showAndWait();
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
        setupDragAndDrop();
        startAutoRefresh();
    }

    private void startAutoRefresh() {
        // Refresh table every 60 seconds to update overdue task colors
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(60), event -> {
            tasksTable.refresh();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void setupTableColumns() {
    TableColumn<TaskDto, Integer> priorityCol = new TableColumn<>("Priority");
    priorityCol.setPrefWidth(100);
    priorityCol.setCellValueFactory(cellData -> {
    int index = tasksTable.getItems().indexOf(cellData.getValue());
    return new SimpleIntegerProperty(index + 1).asObject();
    });
    
    TableColumn<TaskDto, String> taskCol = new TableColumn<>("Task");
    taskCol.setPrefWidth(200);
    taskCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().title)
    );
    
    TableColumn<TaskDto, Integer> progressCol = new TableColumn<>("Progress");
    progressCol.setPrefWidth(200);
    progressCol.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(cellData.getValue().progress).asObject()
    );
    progressCol.setCellFactory(col -> new ProgressBarTableCell());
    
    TableColumn<TaskDto, String> deadlineCol = new TableColumn<>("Deadline");
    deadlineCol.setPrefWidth(170);
    deadlineCol.setCellValueFactory(cellData -> {
    String dateStr = cellData.getValue().deadline;
    if (dateStr == null || dateStr.isEmpty()) {
        return new SimpleStringProperty("N/A");
    }
    
    try {
        LocalDateTime dateTime;
        
        if (dateStr.contains("T")) {
            dateTime = LocalDateTime.parse(dateStr);
        } else {
            long timestamp = Long.parseLong(dateStr);
            dateTime = LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp), 
                java.time.ZoneId.systemDefault()
            );
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        String formatted = dateTime.format(formatter);
        
        return new SimpleStringProperty(formatted);
    } catch (Exception e) {
        System.err.println("Failed to parse deadline: " + dateStr + " - " + e.getMessage());
        return new SimpleStringProperty(dateStr);
    }
    });
    
    tasksTable.getColumns().addAll(priorityCol, taskCol, progressCol, deadlineCol);
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
            statusLabel.setText("Failed to load tasks");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

        @FXML
        void onMarkDone(ActionEvent event) throws Exception{
            TaskDto selected = tasksTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No task selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task");
                alert.showAndWait();
                return;
            }
            
            try {
                taskApi.completeTask(Session.getToken(), selected.id);
                
                loadTasks();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Task completed!");
                alert.showAndWait();
                
            } catch (Exception e) {
                statusLabel.setText("Error: " + e.getMessage());
            }
        }

        @FXML
        void onSetProgress(ActionEvent event) {
            TaskDto selected = tasksTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No task selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task");
                alert.showAndWait();
                return;
            }

            TextInputDialog newProgress = new TextInputDialog(String.valueOf(selected.progress));            
            newProgress.setTitle("Update progress");
            newProgress.setHeaderText("Update your progress (0 - 100)");
            newProgress.setContentText("New progress : ");

            Optional<String> result = newProgress.showAndWait();
            if (!result.isPresent()) {
            return;
}
            try {
                int progress = Integer.parseInt(result.get().trim());

                if (progress < 0 || progress > 100) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Progress");
                alert.setHeaderText(null);
                alert.setContentText("Progress must be between 0 and 100");
                alert.showAndWait();
                return;
                }
                taskApi.updateProgress(Session.getToken(), selected.id, progress);
                
                loadTasks();
                        
            } catch (Exception e) {

                Alert alertExcept = new Alert(Alert.AlertType.ERROR);
                alertExcept.setTitle("Error");
                alertExcept.setHeaderText(null);
                alertExcept.setContentText("Progress must be between 0 and 100");
                System.out.println(e.getMessage());
                alertExcept.showAndWait();
                
            }
        }


        @FXML
        void renameTask(ActionEvent event) {

            TaskDto selected = tasksTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                 Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No task selected");
                alert.setHeaderText(null);
                alert.setContentText("Please select a task");
                alert.showAndWait();
                return;
            }

            TextInputDialog newTitle = new TextInputDialog(String.valueOf(selected.title));            
            newTitle.setTitle("Rename task");
            newTitle.setHeaderText("Rename your task");
            newTitle.setContentText("New title : ");

             Optional<String> result = newTitle.showAndWait();
            if (!result.isPresent()) {
                return;
            }
            
            String title = result.get().trim();
            
            if (title.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty Field");
                alert.setHeaderText(null);
                alert.setContentText("Task title field is empty");
                alert.showAndWait();
                return;
            }
            
            try {
                taskApi.renameTask(Session.getToken(), selected.id, title);
                
                loadTasks();
                
                
            } catch (Exception e) {
                statusLabel.setText("Error");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.showAndWait();
                return;
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

    private void setupDragAndDrop(){
    tasksTable.setRowFactory(tv -> {
        TableRow<TaskDto> row = new TableRow<>() {
            @Override
            protected void updateItem(TaskDto task, boolean empty){
                super.updateItem(task, empty);
                
                if(empty || task == null){
                    setStyle("");
                    return;
                }

                String deadlineStr = task.deadline;

                if(deadlineStr == null || deadlineStr.isEmpty()){
                    setStyle("");
                    return;
                }

                try{
                    LocalDateTime dateTime;
                    if (deadlineStr.contains("T")) {
                        dateTime = LocalDateTime.parse(deadlineStr);
                    } else {
                        long timestamp = Long.parseLong(deadlineStr);
                        dateTime = LocalDateTime.ofInstant(
                            java.time.Instant.ofEpochMilli(timestamp), 
                            java.time.ZoneId.systemDefault()
                        );
                    }

                    if (dateTime.isBefore(LocalDateTime.now())) {
                        setStyle("-fx-background-color: #ffcccc;");
                    } else {
                        setStyle("");
                    }
                } catch (Exception e) {
                    setStyle("");
                }
            }
        };

        row.setOnDragDetected(event -> {
            if (row.getItem() == null) return;  
            
            Dragboard dragboard = row.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(row.getIndex()));
            dragboard.setContent(content);

            event.consume();
        });

        row.setOnDragOver(event -> {
            if (row.getItem() != null && event.getDragboard().hasString()){
                event.acceptTransferModes(TransferMode.MOVE);
            }  
            
            event.consume();

            
        });

        row.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasString()) {
                int draggedIndex = Integer.parseInt(db.getString());
                int targetIndex = row.getIndex();
                
                ObservableList<TaskDto> items = tasksTable.getItems();
                TaskDto draggedTask = items.remove(draggedIndex);
                items.add(targetIndex, draggedTask);
                List<Long> orderedIds = items.stream()
                .map(task -> task.id)
                .toList();
                try {
                    taskApi.reorder(Session.getToken(), orderedIds);
                    success = true;
                } catch (Exception e) {
                    statusLabel.setText("Error reordering tasks");
                    System.out.println(e.getMessage());
                }
            }
            
            event.setDropCompleted(success);
            event.consume();
        });

        return row;
    });
}
}
