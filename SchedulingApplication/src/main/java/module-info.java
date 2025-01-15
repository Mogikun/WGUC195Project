module com.example.schedulingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;    // This includes java.time

    opens com.scheduler to javafx.fxml;
    opens com.scheduler.controllers to javafx.fxml;
    exports com.scheduler;
    exports com.scheduler.controllers;
}