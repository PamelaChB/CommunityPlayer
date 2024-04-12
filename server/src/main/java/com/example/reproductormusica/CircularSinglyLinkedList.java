package com.example.reproductormusica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashSet;

public class CircularSinglyLinkedList {

    private static final Logger logger = LogManager.getLogger(MainSceneController.class);

    public NodeCircular inicio;

    // Inserta al final de la lista.
    public void insertLast(String dato) {
        NodeCircular nuevo = new NodeCircular(dato);
        if (inicio == null) {
            inicio = nuevo;
            inicio.siguiente = inicio;
        } else {
            // Si la lista no está vacía, se busca el último nodo y se agrega el nuevo nodo después de este.
            NodeCircular temp = inicio;
            while (temp.siguiente != inicio) {
                temp = temp.siguiente;
            }
            temp.siguiente = nuevo;
            nuevo.siguiente = inicio;
        }
    }

    //Imprime los elementos de la lista.
    public void printList() {
        if (inicio == null) {
            logger.info("La lista está vacía.");
            return;
        }
        NodeCircular temp = inicio;
        do {
            logger.info(temp.dato + ",");
            temp = temp.siguiente;
        } while (temp != inicio);
        System.out.println();
    }

    //Limpia la lista.
    public void clear() {
        inicio = null;
    }

    // Agrega todos los uniqueArtists del HashSet a la lista.
    public void addAll(HashSet<String> uniqueArtists) {
        for (String artist : uniqueArtists) {
            insertLast(artist);
        }
    }
}
