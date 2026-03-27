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

    @FXML
    private BorderPane bpPrincipal;

    // Declaración de todos los botones de la barra lateral
    @FXML
    private Button bDashboard;
    @FXML
    private Button bMascotas;
    @FXML
    private Button bCitas;
    @FXML
    private Button bInventario;
    @FXML
    private Button bStaff;
    @FXML
    private Button bReportes;
    @FXML
    private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Verificar rol de usuario
        if ("Staff".equals(App.getRolUsuario())) {
            if (bInventario != null) {
                bInventario.setVisible(false);
                bInventario.setManaged(false);
            }
            if (bReportes != null) {
                bReportes.setVisible(false);
                bReportes.setManaged(false);
            }
            if (bStaff != null) {
                bStaff.setVisible(false);
                bStaff.setManaged(false);
            }
        }
        
        navegar(bDashboard, "SeccionDashboard");
    }

    /**
     * MÉTODO MAESTRO: Gestiona el cambio de color de los botones
     * y carga el FXML en el centro del BorderPane.
     */
    public void navegar(Button botonPulsado, String nombreFXML) {
        // 1. Lista de todos los botones para resetear estilos
        Button[] botonesMenu = { bDashboard, bMascotas, bCitas, bInventario, bStaff, bReportes };

        for (Button b : botonesMenu) {
            if (b != null) {
                b.getStyleClass().remove("boton-menu-activo");
                if (!b.getStyleClass().contains("boton-menu")) {
                    b.getStyleClass().add("boton-menu");
                }
            }
        }

        // 2. Aplicar estilo activo al botón que se presionó (si existe)
        if (botonPulsado != null) {
            botonPulsado.getStyleClass().remove("boton-menu");
            botonPulsado.getStyleClass().add("boton-menu-activo");
        }

        // 3. Cargar la nueva vista usando una instancia de FXMLLoader
        try {
            String ruta = "/fxml/" + nombreFXML + ".fxml";
            URL url = getClass().getResource(ruta);

            if (url == null) {
                System.err.println("❌ ERROR: No se encuentra el archivo en: " + ruta);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();

            if (bpPrincipal == null) {
                System.err.println("❌ ERROR: bpPrincipal es NULL. Revisa el fx:id en Scene Builder.");
                return;
            }

            if (nombreFXML.equals("SeccionDashboard")) {
                DashboardController dashCtrl = loader.getController();
                if (dashCtrl != null) {
                    dashCtrl.setPadre(this);
                } else {
                    System.err.println("⚠️ ADVERTENCIA: SeccionDashboard no tiene un controlador asignado.");
                }
            }

            bpPrincipal.setCenter(vista);
            System.out.println("✅ Vista cargada con éxito: " + nombreFXML);

        } catch (IOException e) {
            System.err.println("❌ ERROR CRÍTICO al cargar " + nombreFXML);
            e.printStackTrace();
        }
    }

    // --- MÉTODOS ON ACTION PARA LOS BOTONES ---

    @FXML
    public void mostrarVistaDashboard(ActionEvent event) {
        navegar(bDashboard, "SeccionDashboard");
    }

    @FXML
    public void mostrarVistaMascotas(ActionEvent event) {
        navegar(bMascotas, "SeccionMascotas");
    }

    @FXML
    public void mostrarVistaCitas(ActionEvent event) {
        navegar(bCitas, "SeccionCitas");
    }

    @FXML
    public void mostrarVistaInventario(ActionEvent event) {
        navegar(bInventario, "SeccionInventario");
    }

    @FXML
    public void mostrarVistaStaff(ActionEvent event) {
        navegar(bStaff, "SeccionStaff");
    }

    @FXML
    public void mostrarVistaReportes(ActionEvent event) {
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

                // Medidas estándar para tu Login
                stage.setWidth(852);
                stage.setHeight(535);
                stage.setResizable(false);

                Platform.runLater(() -> stage.centerOnScreen());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Getters para que el Dashboard pueda acceder a los botones del menú
    public Button getbMascotas() {
        return bMascotas;
    }

    public Button getbCitas() {
        return bCitas;
    }

    public Button getbInventario() {
        return bInventario;
    }

    public Button getbReportes() {
        return bReportes;
    }

}