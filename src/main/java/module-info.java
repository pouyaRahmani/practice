module com.example.practice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.practice to javafx.fxml;
    exports com.example.practice;
}