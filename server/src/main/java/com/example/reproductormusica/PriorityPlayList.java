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
    public String getGuidId(int index){
        if(index < 0 || index >= size){
            return "error";
        }
        NodePriority current = front;
        int currentIndex = 0;
        while(current != null && currentIndex < index){
            current = current.next;
            currentIndex++;
        }
        if(current != null){
            return current.Guid;
        }else{
            return "error";
        }
    }
    public void setUpVotes(int index){
        if(index < 0 || index >= size){
        }
        NodePriority current = front;
        int currentIndex = 0;
        while(current != null && currentIndex < index){
            current = current.next;
            currentIndex++;
        }
        if(current != null){
            current.UpVotes++;
        }
        priority++;
    }
    public void setDownVotes(int index){
        if(index < 0 || index >= size){
        }
        NodePriority current = front;
        int currentIndex = 0;
        while(current != null && currentIndex < index){
            current = current.next;
            currentIndex++;
        }
        if(current != null){
            current.DownVotes++;
        }
        priority--;
    }
    public int getUpVotes(int index){
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
            return current.UpVotes;
        }else{
            return -1;
        }
    }
    public int getDownVotes(int index){
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
            return current.DownVotes;
        }else{
            return -1;
        }
    }
    public void updatePriorityOrder() {
        if (size <= 1) {
            // No es necesario actualizar si la lista tiene 0 o 1 elemento
            return;
        }

        NodePriority current = front.next; // Empezamos desde el segundo nodo
        NodePriority prev = front;

        while (current != null) {
            NodePriority nextNode = current.next; // Guardamos el siguiente nodo antes de reordenar

            // Si la prioridad del nodo actual es menor que la del nodo anterior,
            // necesitamos reorganizarlo
            if (current.priority < prev.priority) {
                // Eliminamos el nodo actual de su posición
                prev.next = current.next;

                // Insertamos el nodo actual en la posición correcta
                NodePriority temp = front;
                NodePriority prevTemp = null;
                while (temp != null && temp.priority <= current.priority) {
                    prevTemp = temp;
                    temp = temp.next;
                }
                if (prevTemp == null) {
                    // Si el nodo actual tiene la prioridad más baja, se convierte en el nuevo frente
                    current.next = front;
                    front = current;
                } else {
                    // Insertamos el nodo actual entre prevTemp y temp
                    prevTemp.next = current;
                    current.next = temp;
                }
            } else {
                // No es necesario reorganizar este nodo, avanzamos
                prev = current;
            }

            // Avanzamos al siguiente nodo
            current = nextNode;
        }
    }
}


