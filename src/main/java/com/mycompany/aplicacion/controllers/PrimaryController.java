package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToVeterinario() throws IOException {
        try {
            // Intenta cargarlo con la ruta completa
            App.setRoot("fxml/LoginVeterinario");
        } catch (IOException e) {
            System.out.println("ERROR: No se encontró el archivo FXML. Revisa el nombre.");
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("fxml/VeterinariaP1");
    }

    @FXML
    private PasswordField txtContrasenaOculta;
    @FXML
    private TextField txtContrasenaVisible;

    // Este método se asegura de sincronizar el texto mientras presionas el ojo
    @FXML
    private void mostrarContrasena() {
        txtContrasenaVisible.setText(txtContrasenaOculta.getText());
        txtContrasenaVisible.setVisible(true);
        txtContrasenaOculta.setVisible(false);
    }

    @FXML
    private void ocultarContrasena() {
        txtContrasenaOculta.setText(txtContrasenaVisible.getText());
        txtContrasenaVisible.setVisible(false);
        txtContrasenaOculta.setVisible(true);
    }

    @FXML
    private void switchToRegistro() throws IOException {
        // Asegúrate de que el nombre coincida con tu nuevo archivo fxml
        App.setRoot("fxml/Registro");
    }
    
@FXML
private void iniciarSesion() throws IOException {
    // 1. Cambiamos la interfaz
    App.setRoot("fxml/InterfazVeterinario");
    
    // 2. Ejecutamos los ajustes de ventana
    javafx.application.Platform.runLater(() -> {
        Stage stage = App.getStage();
        if (stage != null) {
            // Desbloqueamos el redimensionamiento
            stage.setResizable(true);
            
            // --- NUEVAS LÍNEAS PARA EL TAMAÑO MÍNIMO ---
            stage.setMinWidth(836);
            stage.setMinHeight(600);
            // -------------------------------------------

            // OBTENEMOS EL TAMAÑO DEL MONITOR
            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

            // FORZAMOS EL TAMAÑO MANUALMENTE PARA ASEGURAR EL CAMBIO
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            
            // Finalmente aplicamos el maximizado nativo
            stage.setMaximized(true);
        }
    });
}
    
    
    
}


