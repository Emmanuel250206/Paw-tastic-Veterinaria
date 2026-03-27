/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.DatosSimulados;

/**
 *
 * @author emmanuel
 */
public class DashboardController {
    private VeterinarioController padre;

    // Contenedores directos FXML
    @FXML
    private VBox cardCitas;
    @FXML
    private VBox cardMascotas;
    @FXML
    private VBox cardInventario;
    @FXML
    private VBox cardStaff;

    @FXML
    private void initialize() {
        if ("Staff".equals(App.getRolUsuario())) {
            if (cardInventario != null) {
                cardInventario.setVisible(false);
                cardInventario.setManaged(false);
            }
            if (cardStaff != null) {
                cardStaff.setVisible(false);
                cardStaff.setManaged(false);
            }
        }

        renderizarTarjetas();
    }

    private void renderizarTarjetas() {
        // Citas (Info rica con AM/PM)
        java.util.List<String> detalleCitas = new java.util.ArrayList<>();
        int maxCitas = Math.min(3, DatosSimulados.getCitas().size());
        for (int i = 0; i < maxCitas; i++) {
            var c = DatosSimulados.getCitas().get(i);
            String horaStr = c.getHora();
            try {
                String[] partes = horaStr.split(":");
                int horaInt = Integer.parseInt(partes[0].trim());
                String ampm = (horaInt >= 12) ? "PM" : "AM";
                int hora12 = (horaInt > 12) ? horaInt - 12 : (horaInt == 0 ? 12 : horaInt);
                horaStr = String.format("%02d:%s %s", hora12, partes[1].trim(), ampm);
            } catch (Exception ignore) {
            }
            detalleCitas.add("• " + c.getNombreMascota() + " - " + c.getMotivo() + " (" + horaStr + ")");
        }
        crearTarjetaDinamica(cardCitas, "Citas pendientes", DatosSimulados.getCitas().size(), detalleCitas);

        // Mascotas (Info rica)
        java.util.List<String> detalleMascotas = new java.util.ArrayList<>();
        int maxMas = Math.min(3, DatosSimulados.getMascotas().size());
        for (int i = 0; i < maxMas; i++) {
            var m = DatosSimulados.getMascotas().get(i);
            detalleMascotas.add("• " + m.getNombre() + " (" + m.getRaza() + ")");
        }
        crearTarjetaDinamica(cardMascotas, "Mascotas", DatosSimulados.getMascotas().size(), detalleMascotas);

        // Inventario (Info rica)
        java.util.List<String> detalleInv = new java.util.ArrayList<>();
        int maxInv = Math.min(3, DatosSimulados.getInventario().size());
        for (int i = 0; i < maxInv; i++) {
            var inv = DatosSimulados.getInventario().get(i);
            detalleInv.add("• " + inv.getNombreProducto() + " - Stock: " + inv.getCantidad());
        }
        crearTarjetaDinamica(cardInventario, "Inventario", DatosSimulados.getInventario().size(), detalleInv);

        // Staff (Info rica)
        java.util.List<String> detalleStaff = new java.util.ArrayList<>();
        int maxStaff = Math.min(3, DatosSimulados.getPersonal().size());
        for (int i = 0; i < maxStaff; i++) {
            var s = DatosSimulados.getPersonal().get(i);
            detalleStaff.add("• " + s.getNombre() + " (" + s.getEspecialidad() + ")");
        }
        crearTarjetaDinamica(cardStaff, "Staff", DatosSimulados.getPersonal().size(), detalleStaff);
    }

    private void crearTarjetaDinamica(VBox tarjeta, String titulo, int cantidad, java.util.List<String> detallesList) {
        if (tarjeta == null)
            return;

        tarjeta.getChildren().clear();
        tarjeta.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // PADDING inicial.
        tarjeta.setPadding(new javafx.geometry.Insets(20, 25, 20, 25));
        tarjeta.setCursor(javafx.scene.Cursor.HAND);

        // Se asignan los COLORES fijos mediante CSS una sola vez.
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: #3d8d7a;");

        Label lblCantidad = new Label(String.valueOf(cantidad));
        lblCantidad.setStyle("-fx-text-fill: #2c3e50;");

        VBox detallesContainer = new VBox();
        Label lblSubtitulo = new Label(detallesList.isEmpty() ? "Sin registros recientes" : "Registros recientes:");
        lblSubtitulo.setStyle("-fx-text-fill: #7f8c8d;");

        detallesContainer.getChildren().add(lblSubtitulo);

        java.util.List<Label> labelsDetalle = new java.util.ArrayList<>();
        for (String det : detallesList) {
            Label lblDetalle = new Label(det);
            lblDetalle.setStyle("-fx-text-fill: #34495e; -fx-font-style: italic;");
            detallesContainer.getChildren().add(lblDetalle);
            labelsDetalle.add(lblDetalle);
        }

        tarjeta.getChildren().addAll(lblTitulo, lblCantidad, detallesContainer);

        // --- LÓGICA DE FUENTES DINÁMICAS (RESPONSIVE POR ESCALONES) ---
        // Al usar escalones (Tiers) evitamos el recálculo circular que causa el
        // parpadeo (jitter) en el GridPane.
        tarjeta.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            double f = 1.0;

            if (w > 700) {
                f = 1.6;
            } else if (w > 500) {
                f = 1.35;
            } else if (w > 400) {
                f = 1.15;
            }
            // Si el factor no cambia significativamente, no recaculamos (Evita
            // re-renderizado infinito)
            if (tarjeta.getUserData() != null && (double) tarjeta.getUserData() == f) {
                return;
            }
            tarjeta.setUserData(f);

            tarjeta.setSpacing(8 * f);
            detallesContainer.setSpacing(4 * f);

            lblTitulo.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 18.0 * f));
            lblCantidad.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 36.0 * f));
            lblSubtitulo.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 13.0 * f));

            for (Label lDet : labelsDetalle) {
                lDet.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.NORMAL, 13.0 * f));
            }
        });
    }

    // Método para que el VeterinarioController se "presente"
    public void setPadre(VeterinarioController padre) {
        this.padre = padre;
    }

    @FXML
    private void irAMascotas(MouseEvent event) {
        if (padre != null && padre.getbMascotas() != null) {
            padre.navegar(padre.getbMascotas(), "SeccionMascotas");
        }
    }

    @FXML
    private void irACitas(MouseEvent event) {
        if (padre != null && padre.getbCitas() != null) {
            padre.navegar(padre.getbCitas(), "SeccionCitas");
        }
    }

    @FXML
    private void irAInventario(MouseEvent event) {
        if (padre != null && padre.getbInventario() != null) {
            padre.navegar(padre.getbInventario(), "SeccionInventario");
        }
    }

    @FXML
    private void irAReportes(MouseEvent event) {
        if (padre != null && padre.getbReportes() != null) {
            padre.navegar(padre.getbReportes(), "SeccionReportes");
        }
    }

    @FXML
    private void irAStaff(MouseEvent event) {
        if (padre != null) {
            // El veterinario controller no tiene getter público getbStaff, pero invocamos
            // su método nativo
            padre.mostrarVistaStaff(null);
        }
    }

}
