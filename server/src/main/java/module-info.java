module com.example.reproductormusica {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.prefs;
    requires ini4j;

    opens com.example.reproductormusica to javafx.fxml;
    exports com.example.reproductormusica;
}