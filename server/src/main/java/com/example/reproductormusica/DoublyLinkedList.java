package com.example.reproductormusica;
import java.io.File;


public class DoublyLinkedList {
    Node head;
    Node tail;
    Node current;
    int size = 0;

    // Método para agregar un archivo MP3 a la lista
    public void add(File mp3File) {
        Node newNode = new Node(mp3File);
        if (head == null) {
            head = newNode;
            tail = newNode;
            this.size++;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
            this.size++;
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
        return this.size;
    }

    public void remove(int index) {
        if (index < 0 || index >= size) {
            // Índice fuera de rango, no se puede eliminar
            System.out.println("Índice fuera de rango.");
            return;
        }

        // Caso especial: si la lista está vacía
        if (size == 0) {
            System.out.println("La lista está vacía.");
            return;
        }

        // Caso especial: si se está eliminando el primer elemento
        if (index == 0) {
            if (size == 1) {
                // Si solo hay un elemento en la lista
                head = null;
                tail = null;
            } else {
                head = head.next;
                head.prev = null;
            }
        } else if (index == size - 1) {
            // Caso especial: si se está eliminando el último elemento
            tail = tail.prev;
            tail.next = null;
        } else {
            // Caso general: eliminar un nodo en medio de la lista
            Node current = head;
            int currentIndex = 0;
            while (currentIndex < index) {
                current = current.next;
                currentIndex++;
            }
            // Enlazar el nodo anterior y el siguiente al nodo actual para eliminarlo
            current.prev.next = current.next;
            current.next.prev = current.prev;
        }
        size--;
    }

    public File get(int index) {
        Node current = head;
        int currentNumber = 0;
        while (current != null && currentNumber < index) {
            current = current.next;
            currentNumber ++;

        }
        if (current == null) {
            return this.head.mp3File;
        }else {
            return current.mp3File;
        }
    }
}