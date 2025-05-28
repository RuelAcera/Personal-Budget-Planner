module com.example.personalbudgetplanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.personalbudgetplanner to javafx.fxml;
    exports com.example.personalbudgetplanner;
}