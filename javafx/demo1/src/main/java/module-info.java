module com.example1 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example1 to javafx.fxml;
    exports com.example1;
}
