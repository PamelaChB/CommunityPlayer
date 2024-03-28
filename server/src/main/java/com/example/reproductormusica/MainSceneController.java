package com.example.reproductormusica;

import javafx.fxml.Initializable;

import java.io.File;
import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainSceneController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainSceneController.class);
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
    private Label upvoteLabel1;
    @FXML
    private Label downvoteLabel1;
    @FXML
    private Slider volumeController;
    @FXML
    private Slider progressSlider;
    @FXML
    private Timeline timeline;
    @FXML
    private Label songList;
    @FXML
    private VBox artistLabelsContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.setProperty("log4j.configurationFile", "./server/src/main/resources/log4j2.xml");
        logger.info("Prueba de escritura de errores");

        generateError(); //Genera un error al propio para observarlo en el app.log

        directory = new File("server\\src\\main\\java\\com\\example\\reproductormusica\\mp3");
        songs = directory.listFiles();
        if(songs != null) {
            for(File file : songs) {
                playlist.add(file);
                logger.info(file);
            }
        }
        try {
            if (playlist.size()>0) {
                media = new Media(playlist.get(SongNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                logger.info(media);
                logger.info(mediaPlayer);

            }else{
                logger.info("No se encontraron archivos de música en el directorio.");
            }

        } catch (Exception e) {
            logger.error("Error loading media: " + e.getMessage());
        }
        // Recorre la lista de canciones y extraer el nombre del artista de cada una
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            artistList.insertLast(song.getArtist());
            logger.info(file);
            logger.info(song);
        }

        // Eliminar duplicados usando HashSet
        HashSet<String> uniqueArtists = new HashSet<>();

        // Recorrer la lista de canciones y extraer el nombre del artista de cada una
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            uniqueArtists.add(song.getArtist());
            logger.info(file);
            logger.info(song);
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
            logger.info(artist);
            logger.info(artistLabel);
        }
        if (songs != null) {
            // Crea un HashMap para almacenar las canciones por cada artista
            HashMap<String, List<String>> artistSongsMap = new HashMap<>();
            for (File file : songs) {
                Song song = new Song(file);
                song.extractMetadata();
                String artist = song.getArtist();
                String title = song.getTitle();

                logger.info(file);
                logger.info(song);
                logger.info(artist);
                logger.info(title);

                // Si el artista ya está en el mapa, agrega la canción a la lista existente
                if (artistSongsMap.containsKey(artist)) {
                    List<String> artistSongs = artistSongsMap.get(artist);
                    artistSongs.add(title);

                    logger.info(artistSongs);

                } else {
                    // Si el artista no está en el mapa, crea una nueva lista de canciones
                    List<String> artistSongs = new ArrayList<>();
                    artistSongs.add(title);
                    artistSongsMap.put(artist, artistSongs);

                    logger.info(artistSongs);
                }
            }

            // Itera sobre el mapa y muestra los artistas y sus canciones correspondientes
            for (Map.Entry<String, List<String>> entry : artistSongsMap.entrySet()) {
                String artist = entry.getKey();
                List<String> songs = entry.getValue();
                // Imprime el nombre del artista y la lista de canciones correspondientes
                logger.info("Artista: " + artist);
                logger.info("Canciones: " + songs);
            }
        } else {
            logger.info("No se encontraron archivos de música en el directorio.");
        }
        // Controlar el volumen
        volumeController.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeController.getValue() * 0.01);
            }
        });
        //Parte del codigo del timeline de la cancion
        mediaPlayer.setOnReady(() -> {
            double duration = mediaPlayer.getTotalDuration().toSeconds();
            progressSlider.setMax(duration);
            // Inicia el Timeline para actualizar el Slider
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                        !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                    // Actualiza el valor del Slider según el tiempo actual de reproducción
                    progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play(); // Comienza la actualización del Slider
        });
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressSlider.setValue(newValue.toSeconds());
        });

        // Manejar cambios de posición en el slider
        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (progressSlider.isValueChanging()) {
                mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
    }
    private void generateError() {
        try {
            // Intentamos dividir por cero, lo que generará una ArithmeticException
            int result = 1 / 0;
        } catch (ArithmeticException e) {
            // Capturamos la excepción y la imprimimos
            logger.error("Error generado por dividir por cero: " + e.getMessage());
        }
    }
    @FXML
    public void playMedia() {
        File mp3 = playlist.get(SongNumber);
        Song song = new Song(mp3);
        song.extractMetadata();
        artistLabel.setText(song.getArtist());
        songLabel.setText(song.getTitle());
        albumLabel.setText(song.getAlbum());
        generoLabel.setText(song.getGenre());
        upvoteLabel1.setText(song.getUp_votes());
        downvoteLabel1.setText(song.getDown_votes());
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
                upvoteLabel1.setText(nextSong.getUp_votes());
                downvoteLabel1.setText(nextSong.getDown_votes());
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
    public void stopMedia() {
        mediaPlayer.pause();
    }
    @FXML
    public void previousMedia() {
        if(SongNumber>0){
            SongNumber--;
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
            upvoteLabel1.setText(song.getUp_votes());
            downvoteLabel1.setText(song.getDown_votes());
            mediaPlayer.play();
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            //Parte del codigo del timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Inicia el Timeline para actualizar el Slider
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Actualiza el valor del Slider según el tiempo actual de reproducción
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualización del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });
        }else{
            SongNumber = playlist.size()-1;
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
            upvoteLabel1.setText(song.getUp_votes());
            downvoteLabel1.setText(song.getDown_votes());
            mediaPlayer.play();
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            //Parte del codigo del timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Inicia el Timeline para actualizar el Slider
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Actualiza el valor del Slider según el tiempo actual de reproducción
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualización del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });
        }
    }
    @FXML
    public void nextMedia() {
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
            upvoteLabel1.setText(song.getUp_votes());
            downvoteLabel1.setText(song.getDown_votes());
            mediaPlayer.play();
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            //Parte del codigo del timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Inicia el Timeline para actualizar el Slider
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Actualiza el valor del Slider según el tiempo actual de reproducción
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualización del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });
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
            upvoteLabel1.setText(song.getUp_votes());
            downvoteLabel1.setText(song.getDown_votes());
            mediaPlayer.play();
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            //Parte del codigo del timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Inicia el Timeline para actualizar el Slider
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Actualiza el valor del Slider según el tiempo actual de reproducción
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualización del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });
        }
    }
    public void advanceMedia(){
        // Obtenemos la posición actual de reproducción
        Duration currentDuration = mediaPlayer.getCurrentTime();

        // Avanzamos 5 segundos desde la posición actual
        Duration newDuration = currentDuration.add(Duration.seconds(5));

        // Nos aseguramos de que no excedamos la duración total de la canción
        if (newDuration.lessThanOrEqualTo(mediaPlayer.getTotalDuration())) {
            mediaPlayer.seek(newDuration);
        } else {
            // Si excedemos la duración total, simplemente lo llevamos al final de la canción
            mediaPlayer.seek(mediaPlayer.getTotalDuration());
        }
    }
    public void delayMedia(){
            // Obtenemos la posición actual de reproducción
            Duration currentDuration = mediaPlayer.getCurrentTime();

            // Retrocedemos 5 segundos desde la posición actual
            Duration newDuration = currentDuration.subtract(Duration.seconds(5));

            // Nos aseguramos de que no vayamos más allá del principio de la canción
            if (newDuration.greaterThanOrEqualTo(Duration.ZERO)) {
                mediaPlayer.seek(newDuration);
            } else {
                // Si intentamos retroceder más allá del principio, simplemente lo llevamos al inicio de la canción
                mediaPlayer.seek(Duration.ZERO);
            }
    }

    public void deleteMedia(){
        mediaPlayer.pause();
        playlist.remove(SongNumber);
        SongNumber--;
        nextMedia();

    }
}