package com.example.reproductormusica;

import java.io.File;
import java.util.UUID;

public class PriorityPlayList implements PriorityQueue {

    private NodePriority front;
    private NodePriority rear;
    private int size;
    private String GuidId;
    private int priority;


    public PriorityPlayList() {
        front = null;
        rear = null;
        size = 0;
    }

    @Override
    public void enqueue(File song, int priority) {
        NodePriority newNode = new NodePriority(song, priority);
        newNode.Guid = UUID.randomUUID().toString();
        if (front == null) {
            front = newNode;
            rear = newNode;
        } else if (priority < front.priority) {
            newNode.next = front;
            front = newNode;
        } else {
            NodePriority current = front;
            while (current.next != null && current.next.priority <= priority) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
            if (newNode.next == null) {
                rear = newNode;
            }
        }
        size++;
    }

    @Override
    public File dequeue() {
        if (front == null) {
            System.out.println("Queue Underflow");
        } else {
            File song = front.mp3File;
            front = front.next;
            size--;
            return song;
        }
        return null;
    }

    @Override
    public File front() {
        if (front == null) {
            System.out.println("Queue Underflow");
        } else {
            return front.mp3File;
        }
        return null;
    }
    public File getSong(int index) {
        if (front == null) {
            System.out.println("Playlist is empty");
            return null;
        }

        NodePriority current = front;
        int currentIndex = 0;

        while (current != null && currentIndex < index) {
            current = current.next;
            currentIndex++;
        }

        if (current != null) {
            return current.mp3File;
        } else {
            System.out.println("Index out of bounds");
            return null;
        }
    }
    public int getSize() {
        return size; // Método para obtener el tamaño de la lista
    }


    public void printPlaylist() {
        if (front == null) {
            System.out.println("Playlist is empty");
            return;
        }
        NodePriority current = front;
        System.out.println("Playlist Content:");
        while (current != null) {
            System.out.println("Guid:" + current.Guid + current.mp3File.getName()); // Imprimir solo el nombre del archivo
            current = current.next;
        }
    }
    public int getPriority(int index){
        if(index < 0 || index >= size){
            return -1;
        }
        NodePriority current = front;
        int currentIndex = 0;
        while(current != null && currentIndex < index){
            current = current.next;
            currentIndex++;
        }
        if(current != null){
            return current.priority;
        }else{
            return -1;
        }
    }
}


