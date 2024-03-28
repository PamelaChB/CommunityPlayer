package com.example.reproductormusica;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

public class CircularSinglyLinkedList {

    private static final Logger logger = LogManager.getLogger(MainSceneController.class);

    public NodeCircular inicio;

    public void insertLast(String dato) {
        NodeCircular nuevo = new NodeCircular(dato);
        if (inicio == null) {
            inicio = nuevo;
            inicio.siguiente = inicio;
        } else {
            NodeCircular temp = inicio;
            while (temp.siguiente != inicio) {
                temp = temp.siguiente;
            }
            temp.siguiente = nuevo;
            nuevo.siguiente = inicio;
        }
    }

    public void removeDuplicates() {
        if (inicio == null || inicio.siguiente == inicio) {
            return; // Lista vacía o un solo elemento
        }

        HashSet<String> elementos = new HashSet<>();
        NodeCircular anterior = inicio;
        NodeCircular actual = inicio.siguiente;
        elementos.add(inicio.dato);

        while (actual != inicio) {
            if (elementos.contains(actual.dato)) {
                anterior.siguiente = actual.siguiente;
            } else {
                elementos.add(actual.dato);
                anterior = actual;
            }
            actual = actual.siguiente;
        }
    }

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

    public void clear() {
        inicio = null;
    }

    public void addAll(HashSet<String> uniqueArtists) {
        for (String artist : uniqueArtists) {
            insertLast(artist);
        }
    }
}
