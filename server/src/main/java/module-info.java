module com.example.reproductormusica {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.dlsc.formsfx;
    requires jaudiotagger;
    requires jlayer;
    opens com.example.reproductormusica to javafx.fxml;
    requires javafx.media;
    exports com.example.reproductormusica;
    requires org.apache.logging.log4j;
}