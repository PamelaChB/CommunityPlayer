package com.example.reproductormusica;

public class NodeCircular {
    String dato;
    NodeCircular siguiente;

    NodeCircular(String dato) { //Constructor
        this.dato = dato;
        this.siguiente = null;
    }
}

