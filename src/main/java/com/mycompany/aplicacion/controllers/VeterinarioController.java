package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.persistencia.Conexion;
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
import com.mycompany.aplicacion.util.ExitDialog;
import java.sql.Connection;
import java.sql.PreparedStatement;

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
    private Button bPuntoVenta;
    @FXML
    private Button bStaff;
    @FXML
    private Button bReportes;
    @FXML
    private Button btnCerrarSesion;

    // Reloj digital de la barra lateral
    @FXML
    private javafx.scene.control.Label lblClockHours;
    @FXML
    private javafx.scene.control.Label lblClockColon;
    @FXML
    private javafx.scene.control.Label lblClockMinutes;
    @FXML
    private javafx.scene.control.Label lblClockDate;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Candado de tamaño para proteger el menú lateral
        Platform.runLater(() -> {
            Stage stage = (Stage) bpPrincipal.getScene().getWindow();
            if (stage != null) {
                // Bloqueamos la ventana para que no pueda ser más pequeña que el menú
                stage.setMinWidth(1100);
                stage.setMinHeight(760); // Se ajustó a 760 para cubrir todos los botones + márgenes reales

                // Forzamos a que la ventana se ajuste a este nuevo mínimo ahora mismo
                if (stage.getHeight() < 760) {
                    stage.setHeight(760);
                }
            }
        });
        // 2. Verificar rol de usuario (Tu lógica actual)
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            // El Staff puede ver Mascotas, Citas, Inventario y Reportes.
            // Ocultamos Sección Staff (Admin)
            if (bStaff != null) {
                bStaff.setVisible(false);
                bStaff.setManaged(false);
            }
        }

        navegar(bDashboard, "SeccionDashboard");

        // 3. Iniciar el reloj digital de la barra lateral
        iniciarRelojDigital();
    }

    private void iniciarRelojDigital() {
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("EEE, dd MMM", java.util.Locale.forLanguageTag("es-ES"));

        javafx.animation.Timeline clock = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, e -> {
                java.time.LocalTime now = java.time.LocalTime.now();
                if (lblClockHours != null) lblClockHours.setText(String.format("%02d", now.getHour()));
                if (lblClockMinutes != null) lblClockMinutes.setText(String.format("%02d", now.getMinute()));
                if (lblClockDate != null) {
                    String dateStr = java.time.LocalDate.now().format(dateFormatter);
                    // Capitalizar primera letra (ej: sáb -> Sáb)
                    lblClockDate.setText(dateStr.substring(0, 1).toUpperCase() + dateStr.substring(1));
                }
                
                // Efecto de parpadeo del colon para indicar que está activo
                if (lblClockColon != null) {
                    lblClockColon.setOpacity(lblClockColon.getOpacity() == 1.0 ? 0.3 : 1.0);
                }
            }),
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1))
        );
        clock.setCycleCount(javafx.animation.Animation.INDEFINITE);
        clock.play();
    }

    /**
     * MÉTODO MAESTRO: Gestiona el cambio de color de los botones
     * y carga el FXML en el centro del BorderPane.
     */
    public void navegar(Button botonPulsado, String nombreFXML) {
        // 1. Lista de todos los botones para resetear estilos
        Button[] botonesMenu = { bDashboard, bMascotas, bCitas, bInventario, bPuntoVenta, bStaff, bReportes };

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
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();

            if (bpPrincipal == null) {
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

        } catch (IOException e) {
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
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            navegar(bCitas, "SeccionCitasStaff");
        } else {
            navegar(bCitas, "SeccionCitas");
        }
    }

    @FXML
    public void mostrarVistaInventario(ActionEvent event) {
        navegar(bInventario, "SeccionInventario");
    }

    @FXML
    public void mostrarVistaPOS(ActionEvent event) {
        navegar(bPuntoVenta, "SeccionPOS");
    }

    @FXML
    public void mostrarVistaStaff(ActionEvent event) {
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) return; // Bloqueo de seguridad a nivel router
        navegar(bStaff, "SeccionStaff");
    }

    @FXML
    public void mostrarVistaReportes(ActionEvent event) {
        navegar(bReportes, "SeccionReportes");
    }

    // --- LÓGICA DE SESIÓN (CORREGIDA PARA UBUNTU/WINDOWS) ---

    @FXML
    private void onCerrarSesionClick(ActionEvent event) {
        Stage owner = (Stage) bpPrincipal.getScene().getWindow();

        ExitDialog.mostrar(owner, () -> {
            try {
                //marcar usuario como inactivo en BD
                Conexion cx = new Conexion();
                Connection conn = cx.estableceConexion();
            try {
                String sql = "UPDATE tb_usuario_web SET activo = 0 WHERE id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, com.mycompany.aplicacion.modelo.UserSession.getInstance().getUserId());
                ps.executeUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            com.mycompany.aplicacion.modelo.UserSession.clear();
                App.setRoot("fxml/VeterinariaP1");

                Platform.runLater(() -> {
                    Stage stage = (Stage) App.getScene().getWindow();
                    stage.setMinWidth(0);
                    stage.setMinHeight(0);
                    stage.setMaximized(false);
                    stage.setResizable(true);
                    stage.sizeToScene();

                    // Medidas estándar para el Login
                    stage.setWidth(852);
                    stage.setHeight(535);
                    stage.setResizable(false);

                    Platform.runLater(() -> stage.centerOnScreen());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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