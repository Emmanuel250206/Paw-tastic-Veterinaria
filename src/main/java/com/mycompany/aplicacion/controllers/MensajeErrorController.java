package com.mycompany.aplicacion.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

public class MensajeErrorController implements Initializable {

    @FXML
    private Label lblMensaje;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setMensaje(String mensaje) {
        lblMensaje.setText(mensaje);
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}