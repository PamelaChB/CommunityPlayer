package com.example.reproductormusica;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.IOException;

public class Song {
    // Atributos
    File mp3File;
    String title;
    String artist;
    String album;
    String genre;
    int up_votes = 0;
    int down_votes = 0;


    public Song(File mp3File){
        this.mp3File = mp3File;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.genre = "";
    }

    public String getTitle(){
        return this.title;
    }

    public String getArtist(){
        return this.artist;
    }

    public String getAlbum(){return this.album;}

    public String getGenre(){
        return this.genre;
    }
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
            }
        } catch (IOException | TagException | org.jaudiotagger.audio.exceptions.ReadOnlyFileException | org.jaudiotagger.audio.exceptions.InvalidAudioFrameException | org.jaudiotagger.audio.exceptions.CannotReadException e) {
            e.printStackTrace();
        }
    }

    public void printMetadata(){
        System.out.println("Nombre: " + this.title);
        System.out.println("Artista: " + this.artist);
        System.out.println("Album: " + this.album);
        System.out.println("Genero: " + this.genre);
        System.out.println("-----------------------------");
    }
}
