package com.example.reproductormusica;

public class CircularSinglyLinkedList {
    public NodeCircular inicio;
    public void insertLast(String dato,) {
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
    public void printList() {
        if (inicio == null) {
            System.out.println("La lista está vacía.");
            return;
        }
        NodeCircular temp = inicio;
        do {
            System.out.print(temp.dato + " -> ");
            temp = temp.siguiente;
        } while (temp != inicio);
        System.out.println();
    }
}
