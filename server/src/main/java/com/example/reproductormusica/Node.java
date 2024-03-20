package com.example.reproductormusica;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.IOException;

public class Node {
    Node prev;
    Node next;
    File mp3File;
    String title;
    String artist;
    String album;
    String genre;
    public Node(File mp3File) {
        this.mp3File = mp3File;
        this.title = "";
        this.artist = "";
        this.album = "";
        this.genre = "";
        this.prev = null;
        this.next = null;
    }

    // Método para extraer metadatos del archivo MP3
    public void extractMetadata(){
        try {
            AudioFile audioFile = AudioFileIO.read(mp3File);
            Tag tag = audioFile.getTag();
            if (tag != null) {
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.COMPOSER);
                album = tag.getFirst(FieldKey.ALBUM);
                genre = tag.getFirst(FieldKey.COMMENT);
            }
        } catch (IOException | TagException | org.jaudiotagger.audio.exceptions.ReadOnlyFileException | org.jaudiotagger.audio.exceptions.InvalidAudioFrameException | org.jaudiotagger.audio.exceptions.CannotReadException e) {
            e.printStackTrace();
        }
    }
}