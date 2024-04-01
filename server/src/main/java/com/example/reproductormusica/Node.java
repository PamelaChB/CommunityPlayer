package com.example.reproductormusica;

import java.io.File;

public class Node {
    Node prev;
    Node next;
    File mp3File;
    public Node(File mp3File) {
        this.mp3File = mp3File;
        this.prev = null;
        this.next = null;
    }

}