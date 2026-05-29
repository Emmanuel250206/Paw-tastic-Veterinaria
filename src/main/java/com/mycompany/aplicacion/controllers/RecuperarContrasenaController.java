package com.mycompany.aplicacion.controllers;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RecuperarContrasenaController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane mainCard;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Habilitar arrastre de la ventana flotante
        mainCard.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        mainCard.setOnMouseDragged(event -> {
            javafx.stage.Window window = mainCard.getScene().getWindow();
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });

        playFloatingCardEntrance();
    }

    private void playFloatingCardEntrance() {
        mainCard.setOpacity(0);
        mainCard.setScaleX(0.8);
        mainCard.setScaleY(0.8);

        FadeTransition fade = new FadeTransition(Duration.millis(250), mainCard);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(250), mainCard);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
        scale.play();
    }

    @FXML
    private void handleRegresarClick(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}
