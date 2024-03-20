package com.example.reproductormusica;
import javazoom.jl.player.Player;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DoublyLinkedList {
    Node head;
    Node tail;

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