package com.example.reproductormusica;

import java.io.File;

public class NodePriority {
    NodePriority prev;
    NodePriority next;
    File mp3File;
    int priority;
    String Guid;
    public NodePriority(File mp3File, int priority) {
        this.mp3File = mp3File;
        this.priority = priority;
        this.prev = null;
        this.next = null;
        this.Guid = "";
    }
}
