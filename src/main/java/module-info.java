module com.algorithmexplorer {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.algorithmexplorer to javafx.fxml;
    exports com.algorithmexplorer;
}
