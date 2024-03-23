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
        // Direccion de los mp3
        String mp3_1 = new String("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\server\\src\\main\\java\\com\\example\\reproductormusica\\mp3\\1.mp3");
        String mp3_2 = new String("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\server\\src\\main\\java\\com\\example\\reproductormusica\\mp3\\2.mp3");
        String mp3_3 = new String("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\server\\src\\main\\java\\com\\example\\reproductormusica\\mp3\\3.mp3");
        // Creacion de la lista doblemente enlazada
        DoublyLinkedList playlist = new DoublyLinkedList();
        // Agregar archivos MP3 a la lista
        playlist.add(new File(mp3_1));
        playlist.add(new File(mp3_2));
        playlist.add(new File(mp3_3));
        playlist.printListContents();
        System.out.println(playlist.size());
        // Creacion de los canciones como objetos
        Song song1 = new Song(new File(mp3_1));
        Song song2 = new Song(new File(mp3_2));
        Song song3 = new Song(new File(mp3_3));
        // Extraer metadata de los archivos MP3
        song1.extractMetadata();
        song2.extractMetadata();
        song3.extractMetadata();
        song1.printMetadata();
        song2.printMetadata();
        song3.printMetadata();
        playlist.remove(2);
        playlist.printListContents();
        CircularSinglyLinkedList Artists = new CircularSinglyLinkedList();
        Artists.insertLast(song1.getArtist());
        Artists.insertLast(song2.getArtist());
        Artists.insertLast(song3.getArtist());
        Artists.printList();
    }
    public static void main(String[] args) {
        launch();
    }
}