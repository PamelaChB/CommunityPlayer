package com.example.reproductormusica;
import javazoom.jl.player.Player;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DoublyLinkedList {
    Node head;
    Node tail;
    String artist;
    File mp3File;


    // Método para agregar un archivo MP3 a la lista
    public void add(File mp3File) {
        Node newNode = new Node(mp3File);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }
    public void printListContents() {
        Node current = head;
        int index = 1;
        while (current != null) {
            System.out.println("Song " + index++ + ": " + current.mp3File.getAbsolutePath());
            current = current.next;
        }
    }
    public int size() {
        int size = 0;
        Node current = head;
        while (current != null) {
            size++;
            current = current.next;
        }
        return size;
    }

    public void remove(int index) {
        if (index < 1 || index > size()) {
            System.out.println("Índice fuera de rango");
            return;
        }
        Node current = head;
        int currentIndex = 1;
        // Caso especial: si se elimina el primer nodo
        if (index == 1) {
            if (head == tail) { // Solo hay un nodo en la lista
                head = null;
                tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }
            System.out.println("Canción eliminada correctamente");
            return;
        }
        // Caso especial: si se elimina el último nodo
        if (index == size()) {
            tail = tail.prev;
            tail.next = null;
            System.out.println("Canción eliminada correctamente");
            return;
        }
        // Buscar el nodo en la posición especificada
        while (currentIndex < index) {
            current = current.next;
            currentIndex++;
        }
        // Eliminar el nodo actual
        current.prev.next = current.next;
        current.next.prev = current.prev;
        System.out.println("Canción eliminada correctamente");
    }


    // Método para reproducir un archivo MP3
    public void play(Node node) {
        try {
            FileInputStream fis = new FileInputStream(node.mp3File);
            Player player = new Player(fis);
            player.play();
        } catch (FileNotFoundException | javazoom.jl.decoder.JavaLayerException e) {
            e.printStackTrace();
        }
    }
}