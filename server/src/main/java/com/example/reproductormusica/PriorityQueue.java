package com.example.reproductormusica;
import java.io.File;

public interface PriorityQueue {
    void enqueue(File song, int priority);
    File dequeue();
    File front();
}
