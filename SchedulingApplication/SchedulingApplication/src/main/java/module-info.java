module com.example.schedulingapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens com.scheduler to javafx.fxml;
    opens com.scheduler.controllers to javafx.fxml;
    opens com.scheduler.models to javafx.base;

    exports com.scheduler;
    exports com.scheduler.controllers;
    exports com.scheduler.models;
    exports com.scheduler.dao;
    exports com.scheduler.utils;
}