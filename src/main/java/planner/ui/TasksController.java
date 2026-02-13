package planner.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import planner.SceneNavigator;
import planner.Session;
import planner.api.ApiClient;
import planner.api.TaskApi;
import planner.api.dto.TaskDto;

import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Task");
        dialog.setHeaderText("Create a new task");
        dialog.setContentText("Task title : ");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
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
                taskApi.createTask(Session.getToken(), title);
                
                loadTasks();
                
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to create task");
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
    }

    private void setupTableColumns() {
    TableColumn<TaskDto, Integer> priorityCol = new TableColumn<>("Priority");
    priorityCol.setPrefWidth(80);
    priorityCol.setCellValueFactory(cellData -> {
    int index = tasksTable.getItems().indexOf(cellData.getValue());
    return new SimpleIntegerProperty(index + 1).asObject();
    });
    
    TableColumn<TaskDto, String> taskCol = new TableColumn<>("Task");
    taskCol.setPrefWidth(250);
    taskCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().title)
    );
    
    TableColumn<TaskDto, Integer> progressCol = new TableColumn<>("Progress");
    progressCol.setPrefWidth(200);
    progressCol.setCellValueFactory(cellData -> 
        new SimpleIntegerProperty(cellData.getValue().progress).asObject()
    );
    progressCol.setCellFactory(col -> new ProgressBarTableCell());
    
    TableColumn<TaskDto, String> dateCol = new TableColumn<>("Date");
    dateCol.setPrefWidth(100);
    dateCol.setCellValueFactory(cellData -> {
    String dateStr = cellData.getValue().createdAt;
    if (dateStr == null || dateStr.isEmpty()) {
        return new SimpleStringProperty("N/A");
    }
    
    try {
        LocalDateTime dateTime = LocalDateTime.parse(dateStr);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formatted = dateTime.format(formatter);
        
        return new SimpleStringProperty(formatted);
    } catch (Exception e) {
        return new SimpleStringProperty(dateStr);
    }
    });
    
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
            statusLabel.setText("Failed to load tasks");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

        @FXML
        void onMarkDone(ActionEvent event) throws Exception{
            TaskDto selected = tasksTable.getSelectionModel().getSelectedItem();
            
            if (selected == null) {
                statusLabel.setText("Please select a task");
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
                statusLabel.setText("Please select a task");
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
        TableRow<TaskDto> row = new TableRow<>();

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
