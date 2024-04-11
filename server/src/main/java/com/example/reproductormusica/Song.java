package com.example.reproductormusica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Song {

    private static final Logger logger = LogManager.getLogger(MainSceneController.class);
    // Atributos
    File mp3File;
    String title;
    String artist;
    String album;
    String genre;
    String RefMp3;
    int up_votes = 0;
    int down_votes = 0;

    // Constructor
    public Song(File mp3File){
        this.mp3File = mp3File;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.genre = "";
        this.RefMp3 = "";
    }
    // Setters de los votos de cada cancion
    public void setUp_votes(int num){
        this.up_votes += num;
    }
    public void setDown_votes(int num){
        this.down_votes += num;
    }
    // Getters de los atributos
    public String getTitle(){
        return this.title;
    }
    public String getUp_votes(){
        return String.valueOf(this.up_votes);
    }
    public String getDown_votes(){
        return String.valueOf(this.down_votes);
    }
    public String getArtist(){
        return this.artist;
    }
    public String getAlbum(){return this.album;}
    public String getGenre(){
        return this.genre;
    }
    public String getRef(){
        return this.RefMp3;
    }



    // Funcion para extraer la metadata de cada cancion y guardarla
    public void extractMetadata(){
        try {
            AudioFile audioFile = AudioFileIO.read(mp3File);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                title = tag.getFirst(FieldKey.TITLE);
                this.title = title;
                artist = tag.getFirst(FieldKey.COMPOSER);
                this.artist = artist;
                album = tag.getFirst(FieldKey.ALBUM);
                this.album = album;
                genre = tag.getFirst(FieldKey.COMMENT);
                this.genre = genre;
                this.RefMp3 = String.valueOf(mp3File);

            }
        } catch (IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException | CannotReadException e) {
            logger.error(e);
        }
    }
    // Funcion que printea la metadata en consola
    public void printMetadata(){
        System.out.println("Nombre: " + this.title);
        System.out.println("Artista: " + this.artist);
        System.out.println("Album: " + this.album);
        System.out.println("Genero: " + this.genre);
        System.out.println("-----------------------------");
    }
}
