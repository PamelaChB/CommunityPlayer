package com.example.reproductormusica;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PriorityPlayList implements PriorityQueue {

    private NodePriority front; //Primer nodo de la lista.
    private NodePriority rear;  //Último nodo de la lista.
    private int size;
    private String GuidId; // Identificador único para cada nodo.
    private int priority; // Prioridad total de la lista.

    public PriorityPlayList() {
        front = null;
        rear = null;
        size = 0;
    }
    //Agrega un nuevo nodo a la lista
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

    //Elimina y devuelve el primer nodo de la lista
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

    //Devuelve el primer nodo de la lista sin eliminarlo.
    @Override
    public File front() {
        if (front == null) {
            System.out.println("Queue Underflow");
        } else {
            return front.mp3File;
        }
        return null;
    }

    // Devuelve el archivo de música en la posición especificada de la lista.
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

    //Devuelve el tamaño actual de la lista.
    public int getSize() {

        return size; // Método para obtener el tamaño de la lista
    }


    // Obtener el playlist actualizado
    public String getPlaylistAsString() {
        StringBuilder playlistString = new StringBuilder();
        NodePriority current = front;
        while (current != null) {
            playlistString.append(current.mp3File.getName()).append("\n");
            current = current.next;
        }
        return playlistString.toString();
    }


    //Imprime los elementos de la lista de reproducción junto con sus identificadores Guid.
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

    //Devuelve la prioridad del elemento en la posición especificada.
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

    //Devuelve el identificador único del elemento en la posición especificada.
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

    //Incrementa el número de votos positivos.
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

    // Incrementa el número de votos negativos.
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

    //Devuelve el número de votos positivos.
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

    //Devuelve el número de votos negativos.
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

    //Actualiza el orden de prioridad de la lista si es necesario, moviendo los elementos según sus prioridades.
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


