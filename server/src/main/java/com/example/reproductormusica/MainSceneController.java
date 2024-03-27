package com.example.reproductormusica;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class MainSceneController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainSceneController.class);


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        System.setProperty("log4j.configurationFile", "./server/src/main/resources/log4j2.xml");
        logger.info("Prueba de escritura de errores");

        generateError();

    }
    // Método que genera un error para testear el log4j 2
    private void generateError() {
        try {
            // Intentamos dividir por cero, lo que generará una ArithmeticException
            int result = 1 / 0;
        } catch (ArithmeticException e) {
            // Capturamos la excepción y la imprimimos
            logger.error("Error generado por dividir por cero: " + e.getMessage());
        }
    }


    public void playMedia() {

    }
    public void stopMedia() {

    }
    public void previousMedia() {

    }
    public void nextMedia() {

    }
    public void beginTimer() {

    }
    public void cancelTimer() {

    }


    public void openClient(ActionEvent actionEvent) {
    }

}