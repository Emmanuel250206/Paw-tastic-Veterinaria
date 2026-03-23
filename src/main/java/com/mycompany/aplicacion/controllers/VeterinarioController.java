/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

public class VeterinarioController {

    @FXML
    private Button bDashboard1;
    @FXML
    private Button btnCerrarSesion;

    private void switchToPrimary() throws IOException {
        // Asegúrate de que el nombre aquí coincida con tu FXML principal
        App.setRoot("fxml/VeterinariaP1");
    }

    private void iniciarSesion() throws IOException {
        // 1. Cambias la interfaz
        App.setRoot("fxml/InterfazVeterinario");

        // 2. Ejecutas el maximizado "un momento después" de que cargue la interfaz
        javafx.application.Platform.runLater(() -> {
            Stage stage = App.getStage();
            if (stage != null) {
                stage.setResizable(true); // Desbloqueas el tamaño
                stage.setMaximized(true); // Lo haces grande
            }
        });

    }

    private void cerrarSesion() throws IOException {
        App.setRoot("fxml/VeterinariaP1");
        Stage stage = (Stage) App.getScene().getWindow(); // Asegúrate de tener el getter en App.java
        stage.setMaximized(false);
        stage.setResizable(false);
        stage.setWidth(836);  // El tamaño de tu login
        stage.setHeight(496);
    }
    @FXML
    private Button bDashboard;
    private Button bMascotas;

    @FXML
    private Button bCitas, bInventario; // Agrega todos tus IDs

    private void limpiarSeleccion() {
        // Creamos una lista con todos los botones del menú
        Button[] botones = {bDashboard, bMascotas, bCitas, bInventario};

        for (Button b : botones) {
            if (b != null) {
                // Quitamos la clase de "activo" y nos aseguramos de que tenga la normal
                b.getStyleClass().remove("boton-menu-activo");
                if (!b.getStyleClass().contains("boton-menu")) {
                    b.getStyleClass().add("boton-menu");
                }
            }
        }
    }

    @FXML
    private void gestionarSeleccion(ActionEvent event) {
        // 1. Limpiamos todos los botones primero
        limpiarSeleccion();

        // 2. Identificamos cuál botón se pulsó
        Button botonPulsado = (Button) event.getSource();

        // 3. Le ponemos el estilo oscuro
        botonPulsado.getStyleClass().remove("boton-menu");
        botonPulsado.getStyleClass().add("boton-menu-activo");

        // Aquí puedes agregar el cambio de pantallas (Dashboard, Mascotas, etc.)
    }

@FXML
private void onCerrarSesionClick(ActionEvent event) {
    try {
        // 1. Cambiamos el FXML primero
        App.setRoot("fxml/VeterinariaP1");

        // 2. Usamos Platform.runLater para darle tiempo a la ventana de reaccionar
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) App.getScene().getWindow();
            
            // IMPORTANTE: Primero quitamos el maximizado
            stage.setMaximized(false);
            
            // Luego aplicamos el tamaño (le sumamos un poquito por los bordes de Windows)
            stage.setResizable(false);
            stage.setWidth(852); // 836 + bordes
            stage.setHeight(535); // 496 + barra de título
            
            // Finalmente centramos
            stage.centerOnScreen();
        });

    } catch (IOException e) {
        System.err.println("Error: " + e.getMessage());
    }
}
}
