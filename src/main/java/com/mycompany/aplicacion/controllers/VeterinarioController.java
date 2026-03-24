package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VeterinarioController implements Initializable {

    @FXML private BorderPane bpPrincipal;
    
    // Declaración de todos los botones de la barra lateral
    @FXML private Button bDashboard;
    @FXML private Button bMascotas;
    @FXML private Button bCitas;
    @FXML private Button bInventario;
    @FXML private Button bStaff;
    @FXML private Button bReportes;
    @FXML private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Opcional: Cargar el Dashboard por defecto al iniciar la interfaz
        // navegar(bDashboard, "DashboardContenido");
    }

    /**
     * MÉTODO MAESTRO: Gestiona el cambio de color de los botones 
     * y carga el FXML en el centro del BorderPane.
     */
    private void navegar(Button botonPulsado, String nombreFXML) {
        // 1. Lista de todos los botones para resetear estilos
        Button[] botonesMenu = {bDashboard, bMascotas, bCitas, bInventario, bStaff, bReportes};

        for (Button b : botonesMenu) {
            if (b != null) {
                // Quitamos la clase de "activo" y nos aseguramos de que tenga la normal
                b.getStyleClass().remove("boton-menu-activo");
                if (!b.getStyleClass().contains("boton-menu")) {
                    b.getStyleClass().add("boton-menu");
                }
            }
        }

        // 2. Aplicar estilo activo al botón que se presionó
        botonPulsado.getStyleClass().remove("boton-menu");
        botonPulsado.getStyleClass().add("boton-menu-activo");

        // 3. Cargar la nueva vista en el centro
        try {
            Node vista = FXMLLoader.load(getClass().getResource("/fxml/" + nombreFXML + ".fxml"));
            bpPrincipal.setCenter(vista);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + nombreFXML);
            e.printStackTrace();
        }
    }

    // --- MÉTODOS ON ACTION PARA LOS BOTONES ---

    @FXML
    private void mostrarVistaDashboard(ActionEvent event) {
        navegar(bDashboard, "DashboardContenido");
    }

    @FXML
    private void mostrarVistaMascotas(ActionEvent event) {
        navegar(bMascotas, "SeccionMascotas");
    }

    @FXML
    private void mostrarVistaCitas(ActionEvent event) {
        navegar(bCitas, "SeccionCitas");
    }

    @FXML
    private void mostrarVistaInventario(ActionEvent event) {
        navegar(bInventario, "SeccionInventario");
    }

    @FXML
    private void mostrarVistaStaff(ActionEvent event) {
        navegar(bStaff, "SeccionStaff");
    }

    @FXML
    private void mostrarVistaReportes(ActionEvent event) {
        navegar(bReportes, "SeccionReportes");
    }

    // --- LÓGICA DE SESIÓN (CORREGIDA PARA UBUNTU/WINDOWS) ---

    @FXML
    private void onCerrarSesionClick(ActionEvent event) {
        try {
            App.setRoot("fxml/VeterinariaP1");

            Platform.runLater(() -> {
                Stage stage = (Stage) App.getScene().getWindow();
                stage.setMaximized(false);
                stage.setResizable(true); 
                stage.sizeToScene(); 

                // Medidas estándar para tu Login en Xalapa
                stage.setWidth(852); 
                stage.setHeight(535);
                stage.setResizable(false);
                
                Platform.runLater(() -> stage.centerOnScreen());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}