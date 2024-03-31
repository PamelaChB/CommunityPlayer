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
    private Label RefLabel;
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

        // Se le asigna la carpeta donde estan los mp3
        directory = new File("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\server\\src\\main\\java\\com\\example\\reproductormusica\\mp3");
        // Las canciones se guardan en esta variable
        songs = directory.listFiles();
        if(songs != null) {
            // Se van anadiendo una por una en la lista doblemente enlazada
            for(File file : songs) {
                playlist.add(file);
                logger.info(file);
            }
        }
        try {
            if (playlist.size()>0) {
                // se guarda el numero de cancion, inicialmente en 0, y se le asigna al mediaplayer
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
        // Controlador de volumen
        volumeController.valueProperty().addListener(new ChangeListener<Number>() {
            // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeController.getValue() * 0.01);
            }
        });
        // Timeline de la cancion
        mediaPlayer.setOnReady(() -> {
            double duration = mediaPlayer.getTotalDuration().toSeconds();
            progressSlider.setMax(duration);
            // Se crea el Timeline para actualizar el Slider en la interfaz
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                        !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                    // Se asigna el tiempo de la cancion al slider
                    progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play(); // Comienza la actualizacion del Slider
        });
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressSlider.setValue(newValue.toSeconds());
        });

        // Se encarga de actualizar los cambios en el slider
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
    // Funcion que se le asigna al boton de reproducir
    @FXML
    public void playMedia() {
        File mp3 = playlist.get(SongNumber); // Se guarda la cancion actual en una variable
        Song song = new Song(mp3); // Se crea un nuevo objeto cancion con el mp3
        song.extractMetadata(); // Se extrae la metadata de esa cancion
        // Se asigna cada atributo de la cancion en los label de la interfaz
        artistLabel.setText(song.getArtist());
        songLabel.setText(song.getTitle());
        albumLabel.setText(song.getAlbum());
        generoLabel.setText(song.getGenre());
        upvoteLabel1.setText(song.getUp_votes());
        downvoteLabel1.setText(song.getDown_votes());
        RefLabel.setText((song.getRef()));
        mediaPlayer.play();
    }
    // Funcion que se le asigna al boton de pausar la cancion
    @FXML
    public void stopMedia() {
        mediaPlayer.pause();
    }
    // Funcion que se le asigna al boton para devolverse a la cancion anterior
    @FXML
    public void previousMedia() {
        if(SongNumber>0){
            SongNumber--; // Se disminuye en uno, el valor actual de la variable
            mediaPlayer.stop(); // Se para el reproductor actual
            media = new Media(playlist.get(SongNumber).toURI().toString()); // Se guarda el nuevo media con la cancion actual
            mediaPlayer = new MediaPlayer(media); // Se crea el mediaplayer nuevo
            File mp3 = playlist.get(SongNumber); // Se guarda la cancion actual en una variable
            Song song = new Song(mp3); // Se crea un nuevo objeto cancion con el mp3
            song.extractMetadata(); // Se extrae la metadata de esa cancion
            // Se asigna cada atributo de la cancion en los label de la interfaz
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            upvoteLabel1.setText(song.getUp_votes());
            downvoteLabel1.setText(song.getDown_votes());
            RefLabel.setText((song.getRef()));
            mediaPlayer.play();
            // Controlador de volumen
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            // Timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Se crea el Timeline para actualizar el Slider en la interfaz
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Se asigna el tiempo de la cancion al slider
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualizacion del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });

            // Se encarga de actualizar los cambios en el slider
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
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
            RefLabel.setText((song.getRef()));
            mediaPlayer.play();
            // Controlador de volumen
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            // Timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Se crea el Timeline para actualizar el Slider en la interfaz
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Se asigna el tiempo de la cancion al slider
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualizacion del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });

            // Se encarga de actualizar los cambios en el slider
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
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
            RefLabel.setText((song.getRef()));
            mediaPlayer.play();
            // Controlador de volumen
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            // Timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Se crea el Timeline para actualizar el Slider en la interfaz
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Se asigna el tiempo de la cancion al slider
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualizacion del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });

            // Se encarga de actualizar los cambios en el slider
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
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
            RefLabel.setText((song.getRef()));
            mediaPlayer.play();
            // Controlador de volumen
            volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    mediaPlayer.setVolume(volumeController.getValue() * 0.01);
                }
            });
            // Timeline de la cancion
            mediaPlayer.setOnReady(() -> {
                double duration = mediaPlayer.getTotalDuration().toSeconds();
                progressSlider.setMax(duration);
                // Se crea el Timeline para actualizar el Slider en la interfaz
                timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                    if (!mediaPlayer.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                            !mediaPlayer.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                        // Se asigna el tiempo de la cancion al slider
                        progressSlider.setValue(mediaPlayer.getCurrentTime().toSeconds());
                    }
                }));
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play(); // Comienza la actualizacion del Slider
            });
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                progressSlider.setValue(newValue.toSeconds());
            });

            // Se encarga de actualizar los cambios en el slider
            progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    mediaPlayer.seek(Duration.seconds(newValue.doubleValue()));
                }
            });
        }
    }
    public void advanceMedia(){
        // Se obtiene el tiempo actual actual de reproduccion
        Duration currentDuration = mediaPlayer.getCurrentTime();

        // Se anaden 5 segundos desde el tiempo actual
        Duration newDuration = currentDuration.add(Duration.seconds(5));

        // Se asegura de que no se exceda de la duración total de la cancion
        if (newDuration.lessThanOrEqualTo(mediaPlayer.getTotalDuration())) {
            mediaPlayer.seek(newDuration);
        } else {
            // Si se excede la duración total, simplemente se lleva al final de la cancion
            mediaPlayer.seek(mediaPlayer.getTotalDuration());
        }
    }
    public void delayMedia(){
            // Se obtiene el tiempo actual actual de reproduccion
            Duration currentDuration = mediaPlayer.getCurrentTime();

            // Se restan 5 segundos desde el tiempo actual
            Duration newDuration = currentDuration.subtract(Duration.seconds(5));

            // Se asegura de que no se exceda del principio de la cancion
            if (newDuration.greaterThanOrEqualTo(Duration.ZERO)) {
                mediaPlayer.seek(newDuration);
            } else {
                // Si se excede del principio, simplemente se lleva al inicio de la cancion
                mediaPlayer.seek(Duration.ZERO);
            }
    }
    // Funcion que se le asigna al boton de eliminar la cancion
    public void deleteMedia(){
        mediaPlayer.pause(); // Se pausa el reproductor
        playlist.remove(SongNumber); // Se remueve la cancion actual de la lista doblemente enlazada
        SongNumber--; // Se reduce en uno el songnumber
        nextMedia(); // Se reproduce la cancion siguiente \
    }
}