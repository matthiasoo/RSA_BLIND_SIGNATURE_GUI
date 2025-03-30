module org.example.view {
    requires org.example.model;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;

    opens org.example to javafx.fxml;
}