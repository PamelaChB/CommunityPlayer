package com.example.reproductormusica;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import java.util.Random;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.*;
import java.util.prefs.Preferences;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.Ini;
import org.ini4j.IniPreferences;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.prefs.Preferences;

public class MainSceneController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainSceneController.class);
    private Map<String, Set<String>> artistSongs = new HashMap<>(); // Ref: https://www.w3schools.com/java/java_hashmap.asp
    private Media media;
    private MediaPlayer mediaPlayer;
    private Media media1;
    private MediaPlayer mediaPlayer1;
    private File directory;
    private File[] songs;
    private DoublyLinkedList playlist = new DoublyLinkedList();
    private PriorityPlayList CommunityPlaylist = new PriorityPlayList();
    private CircularSinglyLinkedList artistList = new CircularSinglyLinkedList();
    private boolean Server = false;
    private int SongNumber = 0;
    private int SongNumberCommunity = 0;
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
    private Socket clientSocket; // Campo para almacenar el socket del cliente
    Thread serverThread = new Thread(() -> {
        try {
            int port = 0;
            String libraryPath = null; // Variable para almacenar la ruta de la biblioteca musical

            try {
                File fileToParse = new File("C:\\Users\\XPC\\OneDrive - Estudiantes ITCR\\Escritorio\\PDatos\\CommunityPlayer\\conf.ini");
                Ini ini = new Ini(fileToParse);
                Preferences prefs = new IniPreferences(ini);
                port = Integer.parseInt(prefs.node("Configuration").get("Port", "8080"));
                libraryPath = prefs.node("Configuration").get("LibraryPath", null);

                if (libraryPath != null) {
                    libraryPath = libraryPath.replace("\\", File.separator);
                }

            } catch (Exception e) {
                // Manejar la excepción adecuadamente
                System.out.println("Error al leer la configuración: " + e.getMessage());
            }

            if (libraryPath == null) {
                // Manejar el caso en que la ruta de la biblioteca no esté configurada
                System.out.println("La ruta de la biblioteca musical no está configurada. Esperando conexiones...");
            } else {
                // Utilizar la ruta de la biblioteca musical según sea necesario
                System.out.println("Servidor iniciado en puerto:" + port + ". Ruta de la biblioteca musical:" + libraryPath + ". Esperando conexiones...");
            }


            ServerSocket serverSocket = new ServerSocket(port); // Puerto a escuchar


            while (true) {
                try {
                    clientSocket = serverSocket.accept(); // Almacenar el socket del cliente
                    System.out.println("¡Conexión establecida con: " + clientSocket.getInetAddress().getHostAddress() + "!");

                    // Leer la señal enviada por el cliente
                    BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String signal = reader.readLine();
                    System.out.println("Señal recibida del cliente: " + signal);

                    // Manejar la señal recibida
                    if (signal.equals("vote:up")) {
                        this.handleUpVote();
                    } else if (signal.equals("vote:down")) {
                        this.handleDownVote();
                    }

                    // Envía una confirmación al cliente
                    this.enviarMsg("Señal recibida: " + signal);
                } catch (SocketException e) {
                    System.out.println("Excepción de Socket: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

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

        // Recorre la lista de canciones y extrae el nombre del artista de cada una
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            artistList.insertLast(song.getArtist());
            logger.info(file);
            logger.info(song);
        }

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int RandomSong = random.nextInt(6);
            File song = songs[RandomSong];
            int upVotes = CommunityPlaylist.getUpVotes(SongNumberCommunity); // Votos aleatorios
            int downVotes = CommunityPlaylist.getDownVotes(SongNumberCommunity);; // Votos aleatorios
            CommunityPlaylist.enqueue(song, upVotes - downVotes);
        }

        // Elimina duplicados usando HashSet. Ref: https://www.w3schools.com/java/java_hashset.asp
        HashSet<String> uniqueArtists = new HashSet<>();

        // Recorre la lista de canciones y extrae el nombre del cantante de cada una.
        for (File file : songs) {
            Song song = new Song(file);
            song.extractMetadata();
            uniqueArtists.add(song.getArtist());
            logger.info(file);
            logger.info(song);

            // Agrega la canción al nombre del cantante en artistSongs
            Set<String> artistSongsSet = artistSongs.computeIfAbsent(song.getArtist(), k -> new HashSet<>());
            artistSongsSet.add(song.getTitle());
        }

        // Limpia la lista y agrega los elementos únicos al conjunto
        artistList.clear();
        artistList.addAll(uniqueArtists);

        // Imprimir la lista actualizada
        artistList.printList();

        // Crea y agrega un Label para cada cantante al VBox
        for (String artist : uniqueArtists) {
            Label artistLabel = new Label(artist);
            artistLabelsContainer.getChildren().add(artistLabel);
            logger.info(artist);
            logger.info(artistLabel);

            // Lee los clicks en los Label de los cantantes
            artistLabel.setOnMouseClicked(event -> {
                // Aquí se obtiene el nombre del artista al que se le hizo click
                String clickedArtist = artistLabel.getText();

                // Busca y muestra las canciones del cantante clickeado
                Set<String> songs = artistSongs.get(clickedArtist);
                if (songs != null) {
                    //System.out.println("Canciones de " + clickedArtist);

                    // String que almacena todas las canciones de cada cantante
                    StringBuilder allSongs = new StringBuilder();

                    for (String song : songs) {
                        // Añade cada canción al String y los ecribe con un salto de renglón
                        allSongs.append(song).append("\n");
                    }
                    //System.out.println(allSongs);
                    //Setea el texto del label songList al String generado anteriormente
                    songList.setText(allSongs.toString());
                }
            });
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
    // Método para enviar mensaje al cliente
    public void enviarMsg(String mensaje) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(mensaje.getBytes());
            outputStream.flush(); // Forzar el envío del mensaje
            System.out.println("Mensaje enviado al cliente: " + mensaje);
        } catch (IOException e) {
            System.out.println("Error al enviar mensaje al cliente: " + e.getMessage());
        }
    }

    // Método para manejar el up-vote recibido
    public void handleUpVote() {
        Platform.runLater(() -> {
            CommunityPlaylist.setUpVotes(SongNumberCommunity);
            CommunityPlaylist.updatePriorityOrder();
            upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
            System.out.println(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
        });
        System.out.println("Se recibió un up-vote");
    }

    // Método para manejar el down-vote recibido
    public void handleDownVote() {
        Platform.runLater(() -> {
            CommunityPlaylist.setDownVotes(SongNumberCommunity);
            CommunityPlaylist.updatePriorityOrder();
            downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
            System.out.println(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
        });
        System.out.println("Se recibió un down-vote");
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
        if (!Server){
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
        }else{
            System.out.println(CommunityPlaylist.getGuidId(SongNumberCommunity));
            mediaPlayer1.play();
            File mp3 = CommunityPlaylist.getSong(SongNumberCommunity);
            Song song = new Song(mp3);
            song.extractMetadata();
            artistLabel.setText(song.getArtist());
            songLabel.setText(song.getTitle());
            albumLabel.setText(song.getAlbum());
            generoLabel.setText(song.getGenre());
            upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
            downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
            RefLabel.setText((song.getRef()));
            }

    }
    // Funcion que se le asigna al boton de pausar la cancion
    @FXML
    public void stopMedia() {
        if(!Server){
            mediaPlayer.pause();
        }else{
            mediaPlayer1.pause();
        }
    }
    // Funcion que se le asigna al boton para devolverse a la cancion anterior
    @FXML
    public void previousMedia() {
        if(!Server){
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
                upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
                downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
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
        }else{
            if(SongNumberCommunity>0){
                SongNumberCommunity--; // Se disminuye en uno, el valor actual de la variable
                mediaPlayer1.stop(); // Se para el reproductor actual
                media1 = new Media(CommunityPlaylist.getSong(SongNumberCommunity).toURI().toString()); // Se guarda el nuevo media con la cancion actual
                mediaPlayer1 = new MediaPlayer(media1); // Se crea el mediaplayer nuevo
                File mp3 = CommunityPlaylist.getSong(SongNumberCommunity); // Se guarda la cancion actual en una variable
                Song song = new Song(mp3); // Se crea un nuevo objeto cancion con el mp3
                song.extractMetadata(); // Se extrae la metadata de esa cancion
                // Se asigna cada atributo de la cancion en los label de la interfaz
                artistLabel.setText(song.getArtist());
                songLabel.setText(song.getTitle());
                albumLabel.setText(song.getAlbum());
                generoLabel.setText(song.getGenre());
                upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
                downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
                RefLabel.setText((song.getRef()));
                mediaPlayer1.play();
                // Controlador de volumen
                volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                    // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        mediaPlayer1.setVolume(volumeController.getValue() * 0.01);
                    }
                });
                // Timeline de la cancion
                mediaPlayer1.setOnReady(() -> {
                    double duration = mediaPlayer1.getTotalDuration().toSeconds();
                    progressSlider.setMax(duration);
                    // Se crea el Timeline para actualizar el Slider en la interfaz
                    timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                        if (!mediaPlayer1.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                                !mediaPlayer1.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                            // Se asigna el tiempo de la cancion al slider
                            progressSlider.setValue(mediaPlayer1.getCurrentTime().toSeconds());
                        }
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play(); // Comienza la actualizacion del Slider
                });
                mediaPlayer1.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressSlider.setValue(newValue.toSeconds());
                });

                // Se encarga de actualizar los cambios en el slider
                progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (progressSlider.isValueChanging()) {
                        mediaPlayer1.seek(Duration.seconds(newValue.doubleValue()));
                    }
                });
            }else{
                SongNumberCommunity = CommunityPlaylist.getSize()-1;
                mediaPlayer1.stop();
                media1 = new Media(CommunityPlaylist.getSong(SongNumberCommunity).toURI().toString());
                mediaPlayer1 = new MediaPlayer(media1);
                File mp3 = CommunityPlaylist.getSong(SongNumberCommunity);
                Song song = new Song(mp3);
                song.extractMetadata();
                artistLabel.setText(song.getArtist());
                songLabel.setText(song.getTitle());
                albumLabel.setText(song.getAlbum());
                generoLabel.setText(song.getGenre());
                upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
                downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
                RefLabel.setText((song.getRef()));
                mediaPlayer1.play();
                // Controlador de volumen
                volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                    // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        mediaPlayer1.setVolume(volumeController.getValue() * 0.01);
                    }
                });
                // Timeline de la cancion
                mediaPlayer1.setOnReady(() -> {
                    double duration = mediaPlayer1.getTotalDuration().toSeconds();
                    progressSlider.setMax(duration);
                    // Se crea el Timeline para actualizar el Slider en la interfaz
                    timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                        if (!mediaPlayer1.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                                !mediaPlayer1.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                            // Se asigna el tiempo de la cancion al slider
                            progressSlider.setValue(mediaPlayer1.getCurrentTime().toSeconds());
                        }
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play(); // Comienza la actualizacion del Slider
                });
                mediaPlayer1.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressSlider.setValue(newValue.toSeconds());
                });

                // Se encarga de actualizar los cambios en el slider
                progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (progressSlider.isValueChanging()) {
                        mediaPlayer1.seek(Duration.seconds(newValue.doubleValue()));
                    }
                });
            }
        }
    }
    @FXML
    public void nextMedia() {
        if(!Server){
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
        }else{
            if(SongNumberCommunity < CommunityPlaylist.getSize()) {
                SongNumberCommunity++;
                mediaPlayer1.stop();
                media1 = new Media(CommunityPlaylist.getSong(SongNumberCommunity).toURI().toString());
                mediaPlayer1 = new MediaPlayer(media1);
                File mp3 = CommunityPlaylist.getSong(SongNumberCommunity);
                Song song = new Song(mp3);
                song.extractMetadata();
                artistLabel.setText(song.getArtist());
                songLabel.setText(song.getTitle());
                albumLabel.setText(song.getAlbum());
                generoLabel.setText(song.getGenre());
                upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
                downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
                RefLabel.setText((song.getRef()));
                mediaPlayer1.play();
                // Controlador de volumen
                volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                    // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        mediaPlayer1.setVolume(volumeController.getValue() * 0.01);
                    }
                });
                // Timeline de la cancion
                mediaPlayer1.setOnReady(() -> {
                    double duration = mediaPlayer1.getTotalDuration().toSeconds();
                    progressSlider.setMax(duration);
                    // Se crea el Timeline para actualizar el Slider en la interfaz
                    timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                        if (!mediaPlayer1.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                                !mediaPlayer1.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                            // Se asigna el tiempo de la cancion al slider
                            progressSlider.setValue(mediaPlayer1.getCurrentTime().toSeconds());
                        }
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play(); // Comienza la actualizacion del Slider
                });
                mediaPlayer1.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressSlider.setValue(newValue.toSeconds());
                });

                // Se encarga de actualizar los cambios en el slider
                progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (progressSlider.isValueChanging()) {
                        mediaPlayer1.seek(Duration.seconds(newValue.doubleValue()));
                    }
                });
            } else {
                SongNumberCommunity = 0;
                mediaPlayer1.stop();
                media1 = new Media(playlist.get(SongNumberCommunity).toURI().toString());
                mediaPlayer1 = new MediaPlayer(media1);
                File mp3 = playlist.get(SongNumberCommunity);
                Song song = new Song(mp3);
                song.extractMetadata();
                artistLabel.setText(song.getArtist());
                songLabel.setText(song.getTitle());
                albumLabel.setText(song.getAlbum());
                generoLabel.setText(song.getGenre());
                upvoteLabel1.setText(String.valueOf(CommunityPlaylist.getUpVotes(SongNumberCommunity)));
                downvoteLabel1.setText(String.valueOf(CommunityPlaylist.getDownVotes(SongNumberCommunity)));
                RefLabel.setText((song.getRef()));
                mediaPlayer1.play();
                // Controlador de volumen
                volumeController.valueProperty().addListener(new ChangeListener<Number>() {
                    // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
                    @Override
                    public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                        mediaPlayer1.setVolume(volumeController.getValue() * 0.01);
                    }
                });
                // Timeline de la cancion
                mediaPlayer1.setOnReady(() -> {
                    double duration = mediaPlayer1.getTotalDuration().toSeconds();
                    progressSlider.setMax(duration);
                    // Se crea el Timeline para actualizar el Slider en la interfaz
                    timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                        if (!mediaPlayer1.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                                !mediaPlayer1.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                            // Se asigna el tiempo de la cancion al slider
                            progressSlider.setValue(mediaPlayer1.getCurrentTime().toSeconds());
                        }
                    }));
                    timeline.setCycleCount(Timeline.INDEFINITE);
                    timeline.play(); // Comienza la actualizacion del Slider
                });
                mediaPlayer1.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                    progressSlider.setValue(newValue.toSeconds());
                });

                // Se encarga de actualizar los cambios en el slider
                progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (progressSlider.isValueChanging()) {
                        mediaPlayer1.seek(Duration.seconds(newValue.doubleValue()));
                    }
                });
            }
        }
    }
    public void advanceMedia(){
        if(!Server){
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
        }else{
            // Se obtiene el tiempo actual actual de reproduccion
            Duration currentDuration = mediaPlayer1.getCurrentTime();

            // Se anaden 5 segundos desde el tiempo actual
            Duration newDuration = currentDuration.add(Duration.seconds(5));

            // Se asegura de que no se exceda de la duración total de la cancion
            if (newDuration.lessThanOrEqualTo(mediaPlayer1.getTotalDuration())) {
                mediaPlayer1.seek(newDuration);
            } else {
                // Si se excede la duración total, simplemente se lleva al final de la cancion
                mediaPlayer1.seek(mediaPlayer1.getTotalDuration());
            }
        }
    }
    public void delayMedia(){
        if(!Server){
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
        }else{
            // Se obtiene el tiempo actual actual de reproduccion
            Duration currentDuration = mediaPlayer1.getCurrentTime();

            // Se restan 5 segundos desde el tiempo actual
            Duration newDuration = currentDuration.subtract(Duration.seconds(5));

            // Se asegura de que no se exceda del principio de la cancion
            if (newDuration.greaterThanOrEqualTo(Duration.ZERO)) {
                mediaPlayer1.seek(newDuration);
            } else {
                // Si se excede del principio, simplemente se lleva al inicio de la cancion
                mediaPlayer1.seek(Duration.ZERO);
            }
        }
    }
    // Funcion que se le asigna al boton de eliminar la cancion
    public void deleteMedia(){
            if(!Server){
                mediaPlayer.pause(); // Se pausa el reproductor
                playlist.remove(SongNumber); // Se remueve la cancion actual de la lista doblemente enlazada
                SongNumber--; // Se reduce en uno el songnumber
                nextMedia(); // Se reproduce la cancion siguiente \
            }else{
                mediaPlayer1.pause(); // Se pausa el reproductor
                playlist.remove(SongNumberCommunity); // Se remueve la cancion actual de la lista doblemente enlazada
                SongNumberCommunity--; // Se reduce en uno el songnumber
                nextMedia(); // Se reproduce la cancion siguiente \
            }
    }

    public void Servidor(){
        serverThread.start(); // Inicia el hilo del servidor
        mediaPlayer.stop();
        Server = true;
        try {
            if (CommunityPlaylist.getSize()>0) {
                // se guarda el numero de cancion, inicialmente en 0, y se le asigna al mediaplayer
                media1 = new Media(CommunityPlaylist.getSong(SongNumberCommunity).toURI().toString());
                mediaPlayer1 = new MediaPlayer(media1);
                logger.info(media1);
                logger.info(mediaPlayer1);

            }else{
                logger.info("No se encontraron archivos de música en el directorio.");
            }

        } catch (Exception e) {
            logger.error("Error loading media: " + e.getMessage());
        }
        // Controlador de volumen
        volumeController.valueProperty().addListener(new ChangeListener<Number>() {
            // Se crea la funcion changed, para actualizar el volumen junto el slider de la interfaz
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer1.setVolume(volumeController.getValue() * 0.01);
            }
        });
        // Timeline de la cancion
        mediaPlayer1.setOnReady(() -> {
            double duration = mediaPlayer1.getTotalDuration().toSeconds();
            progressSlider.setMax(duration);
            // Se crea el Timeline para actualizar el Slider en la interfaz
            timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), event -> {
                if (!mediaPlayer1.getStatus().equals(MediaPlayer.Status.PAUSED) &&
                        !mediaPlayer1.getStatus().equals(MediaPlayer.Status.STOPPED)) {
                    // Se asigna el tiempo de la cancion al slider
                    progressSlider.setValue(mediaPlayer1.getCurrentTime().toSeconds());
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play(); // Comienza la actualizacion del Slider
        });
        mediaPlayer1.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            progressSlider.setValue(newValue.toSeconds());
        });

        // Se encarga de actualizar los cambios en el slider
        progressSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (progressSlider.isValueChanging()) {
                mediaPlayer1.seek(Duration.seconds(newValue.doubleValue()));
            }
        });
        CommunityPlaylist.printPlaylist();
    }
}