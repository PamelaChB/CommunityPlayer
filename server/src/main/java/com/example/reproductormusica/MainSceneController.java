package com.example.reproductormusica;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
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
    private CircularSinglyLinkedList artistList = new CircularSinglyLinkedList();
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
    @FXML
    private Label songList;
    @FXML
    private VBox artistLabelsContainer;






    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



        directory = new File("server\\src\\main\\java\\com\\example\\reproductormusica\\mp3");
        songs = directory.listFiles();
        if (songs != null) {
            for (File file : songs) {
                playlist.add(file);
                System.out.println(file);
            }
        }
        try {
            if (playlist.size() > 0) {
                media = new Media(playlist.get(SongNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

            } else {
                System.out.println("No se encontraron archivos de música en el directorio.");
            }

        } catch (Exception e) {
            System.out.println("Error loading media: " + e.getMessage());
        }


        // Recorre la lista de canciones y extraer el nombre del artista de cada una
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            artistList.insertLast(song.getArtist());
        }

        // Eliminar duplicados usando HashSet
        HashSet<String> uniqueArtists = new HashSet<>();

        // Recorrer la lista de canciones y extraer el nombre del artista de cada una
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            uniqueArtists.add(song.getArtist());
        }

        // Limpiar la lista enlazada y agregar los elementos únicos al conjunto
        artistList.clear();
        artistList.addAll(uniqueArtists);

        // Imprimir la lista actualizada
        artistList.printList();

        // Crear y agregar un Label para cada artista al VBox
        for (String artist : uniqueArtists) {
            Label artistLabel = new Label(artist);
            artistLabelsContainer.getChildren().add(artistLabel);
        }
        if (songs != null) {
            // Crea un HashMap para almacenar las canciones por cada artista
            HashMap<String, List<String>> artistSongsMap = new HashMap<>();
            for (File file : songs) {
                Song song = new Song(file);
                song.extractMetadata();
                String artist = song.getArtist();
                String title = song.getTitle();
                // Si el artista ya está en el mapa, agrega la canción a la lista existente
                if (artistSongsMap.containsKey(artist)) {
                    List<String> artistSongs = artistSongsMap.get(artist);
                    artistSongs.add(title);
                } else {
                    // Si el artista no está en el mapa, crea una nueva lista de canciones
                    List<String> artistSongs = new ArrayList<>();
                    artistSongs.add(title);
                    artistSongsMap.put(artist, artistSongs);
                }
            }

            // Itera sobre el mapa y muestra los artistas y sus canciones correspondientes
            for (Map.Entry<String, List<String>> entry : artistSongsMap.entrySet()) {
                String artist = entry.getKey();
                List<String> songs = entry.getValue();
                // Imprime el nombre del artista y la lista de canciones correspondientes
                System.out.println("Artista: " + artist);
                System.out.println("Canciones: " + songs);
            }
        } else {
            System.out.println("No se encontraron archivos de música en el directorio.");
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
        if (SongNumber > 0) {
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
        } else {
            SongNumber = playlist.size() - 1;
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
        if (SongNumber < playlist.size()) {
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


    public void delayMedia(ActionEvent actionEvent) {
    }

    public void advanceMedia(ActionEvent actionEvent) {
    }

    public void circularList(){

    }


    public void prueba(MouseEvent mouseEvent) {
        System.out.println("holaaaaa");
    }
}