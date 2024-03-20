package com.example.reproductormusica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ScreenPlayerMusic.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 653, 420);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
        DoublyLinkedList playlist = new DoublyLinkedList();
        // Agregar archivos MP3 a la lista
        playlist.add(new File("1.mp3"));
        playlist.add(new File("2.mp3"));
        playlist.add(new File("3.mp3"));
        // Extraer metadatos de los archivos MP3
        Node current = playlist.head;
        playlist.printListContents();
        while (current != null) {
            current.extractMetadata();
            System.out.println("Nombre: " + current.title);
            System.out.println("Artista: " + current.artist);
            System.out.println("Album: " + current.album);
            System.out.println("Genero: " + current.genre);
            System.out.println("-----------------------------");
            current = current.next;
        }
        // Reproducir la lista de reproducción
        current = playlist.head;
        while (current != null) {
            playlist.play(current);
            current = current.next;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}