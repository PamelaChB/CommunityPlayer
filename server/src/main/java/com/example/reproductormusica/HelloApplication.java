package com.example.reproductormusica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.prefs.Preferences;

//Punto de entrada del reproductor de música en donde se configura y muestra la interfaz de usuario.
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ScreenPlayerMusic.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 653, 420);
        stage.setTitle("Reproductor de música");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}