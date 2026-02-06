module planner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;


    opens planner to javafx.fxml;
    opens planner.ui to javafx.fxml;
    opens planner.api.dto to com.fasterxml.jackson.databind;
    exports planner;
}
