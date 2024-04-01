package com.example.reproductormusica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import org.ini4j.Ini;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ScreenPlayerMusic.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 653, 420);
        stage.setTitle("Thiscrapify");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        ServerMain customServer = new ServerMain();
        customServer.enviarMsg("hola");
    }
}