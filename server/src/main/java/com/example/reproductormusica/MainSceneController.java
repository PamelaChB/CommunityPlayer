package com.example.reproductormusica;

import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class MainSceneController implements Initializable {

    private Media media;
    private MediaPlayer mediaPlayer;
    private File directory;
    private File[] songs;
    private DoublyLinkedList playlist = new DoublyLinkedList();
    private int SongNumber = 0;
    @FXML
    private Label artistLabel;
    @FXML
    private Label songLabel;
    @FXML
    private Label albumLabel;
    @FXML
    private Label generoLabel;
    @FXML
    private Slider volumeController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        directory = new File("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\server\\src\\main\\java\\com\\example\\reproductormusica\\mp3");
        songs = directory.listFiles();
        if(songs != null) {
            for(File file : songs) {
                playlist.add(file);
                System.out.println(file);
            }
        }
        try {
            if (playlist.size()>0) {
                media = new Media(playlist.get(SongNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

            }else{
                System.out.println("No se encontraron archivos de música en el directorio.");
            }

        } catch (Exception e) {
            System.out.println("Error loading media: " + e.getMessage());
        }
        volumeController.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeController.getValue() * 0.01);
            }
        });
    }
    @FXML
    public void playMedia(ActionEvent event) {
        File mp3 = playlist.get(SongNumber);
        Song song = new Song(mp3);
        song.extractMetadata();
        artistLabel.setText(song.getArtist());
        songLabel.setText(song.getTitle());
        albumLabel.setText(song.getAlbum());
        generoLabel.setText(song.getGenre());
        //mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> {
            // Incrementar el índice del siguiente medio en la lista de reproducción
            SongNumber++;
            // Comprobar si SongNumber está dentro del rango de la lista de reproducción
            if (SongNumber < playlist.size()) {
                // Obtener el siguiente medio de la lista de reproducción
                File nextMp3 = playlist.get(SongNumber);
                Song nextSong = new Song(nextMp3);
                nextSong.extractMetadata();
                artistLabel.setText(nextSong.getArtist());
                songLabel.setText(nextSong.getTitle());
                albumLabel.setText(nextSong.getAlbum());
                generoLabel.setText(nextSong.getGenre());
                // Cargar y reproducir el siguiente medio
                mediaPlayer.stop();
                mediaPlayer = new MediaPlayer(new Media(nextMp3.toURI().toString()));
                mediaPlayer.play();
            } else {
                // Si no hay más medios en la lista, detener la reproducción
                mediaPlayer.stop();
                // También puedes implementar alguna lógica para reiniciar la lista o hacer otra acción aquí
            }
        });

        // Iniciar la reproducción del medio actual
        mediaPlayer.play();
    }
    @FXML
    public void stopMedia(ActionEvent event) {
        mediaPlayer.pause();
    }
    @FXML
    public void previousMedia(ActionEvent event) {
        if(SongNumber>0){
            SongNumber--;
            //mediaPlayer.stop();
            media = new Media(playlist.get(SongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            File mp3 = playlist.get(SongNumber);
            Song song = new Song(mp3);
            song.extractMetadata();
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            mediaPlayer.play();
        }else{
            SongNumber = playlist.size()-1;
            //mediaPlayer.stop();
            media = new Media(playlist.get(SongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            File mp3 = playlist.get(SongNumber);
            Song song = new Song(mp3);
            song.extractMetadata();
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            mediaPlayer.play();
        }
    }
    @FXML
    public void nextMedia(ActionEvent event) {
        if(SongNumber < playlist.size()) {
            SongNumber++;
            mediaPlayer.stop();
            media = new Media(playlist.get(SongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            File mp3 = playlist.get(SongNumber);
            Song song = new Song(mp3);
            song.extractMetadata();
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            mediaPlayer.play();
        } else {
            SongNumber = 0;
            mediaPlayer.stop();
            media = new Media(playlist.get(SongNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            File mp3 = playlist.get(SongNumber);
            Song song = new Song(mp3);
            song.extractMetadata();
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            mediaPlayer.play();
        }
    }
}