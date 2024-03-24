package com.example.reproductormusica;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ServerMain extends Application {

    private Socket clientSocket; // Campo para almacenar el socket del cliente

    @Override
    public void start(Stage primaryStage) throws Exception {
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(12345); // Puerto a escuchar

                System.out.println("Servidor iniciado. Esperando conexiones...");

                while (true) {
                    try {
                        clientSocket = serverSocket.accept(); // Almacenar el socket del cliente
                        System.out.println("¡Conexión establecida con: " + clientSocket.getInetAddress().getHostAddress() + "!");

                        // Leer la señal enviada por el cliente
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String signal = reader.readLine();
                        System.out.println("Señal recibida del cliente: " + signal);
                        System.out.println("senal enviada");
                        enviarMsg("hola");
                    } catch (SocketException e) {
                        System.out.println("Excepción de Socket: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Método para enviar mensaje al cliente
    public void enviarMsg(String mensaje) {
        try {
            OutputStream outputStream = clientSocket.getOutputStream();
            outputStream.write(mensaje.getBytes());
            outputStream.flush(); // Forzar el envío del mensaje
            System.out.println("Mensaje enviado al cliente: " + mensaje);
        } catch (IOException e) {
            System.out.println("Error al enviar mensaje al cliente: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
