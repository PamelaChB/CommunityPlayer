package com.example.reproductormusica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ScreenPlayerMusic.fxml"));

    }

    public static void main(String[] args) {


        ServerMain customServer = new ServerMain();
        customServer.enviarMsg("hola");



    }
}