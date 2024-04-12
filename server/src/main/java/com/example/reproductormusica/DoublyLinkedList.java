package com.example.reproductormusica;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;


public class DoublyLinkedList {
    // Atributos
    private static final Logger logger = LogManager.getLogger(MainSceneController.class);
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

    // Funcion que retorna el tamaño de la lista
    public int size() {
        return this.size;
    }

    // Funcion que elimina el contenido de la lista segun el indice dado
    public void remove(int index) {
        if (index < 0 || index >= size) {
            // indice fuera de rango, no se puede eliminar
            logger.info("Índice fuera de rango.");
            return;
        }
        // Caso especial: si la lista está vacía
        if (size == 0) {
            logger.info("La lista está vacía.");
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
        size--; // Se resta en uno el tamano de la lista
    }

    // Función que retorna el mp3 según el índice dado
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

    //Imprime los contenidos de la lista, mostrando la ruta absoluta de cada archivo MP3.
    public void printListContents() {
        Node current = head;
        int index = 1;
        while (current != null) {
            logger.info("Song " + index++ + ": " + current.mp3File.getAbsolutePath());
            current = current.next;
        }
    }
}