package com.mycompany.aplicacion.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.geometry.Side;
import com.mycompany.aplicacion.modelo.UserSession;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class ReportesController implements Initializable {

    @FXML
    private TableView<VentaSimulada> tablaCierre;
    @FXML
    private TableColumn<VentaSimulada, String> colConcepto;
    @FXML
    private TableColumn<VentaSimulada, Integer> colCantidad;
    @FXML
    private TableColumn<VentaSimulada, Double> colTotal;

    @FXML
    private PieChart graficoIngresos;

    @FXML
    private Button btnAutorizar;

    // ── Perfil header ───────────────────────────────────────────────────
    @FXML private ImageView imgPerfilReportes;
    @FXML private Label     lblNombreReportes;
    @FXML private Label     lblRolReportes;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilReportes);
        lblNombreReportes.setText(UserSession.getInstance().getUserName());
        lblRolReportes.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        // Configurar la tabla
        colConcepto.setCellValueFactory(cell
                -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getConcepto())
        );

        colCantidad.setCellValueFactory(cell
                -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCantidad()).asObject()
        );

        colTotal.setCellValueFactory(cell
                -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getTotal()).asObject()
        );
        cargarDatosSimulados();
    }

    private void construirMenuPerfil() {
        menuPerfil = new ContextMenu();
        menuPerfil.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3D8D7A;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0, 0, 5);" +
            "-fx-padding: 4 0 4 0;"
        );
        Label lbl = new Label("⚙  Configurar Perfil");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPrefWidth(185);
        String base = "-fx-font-size:13px;-fx-text-fill:#2C3E50;-fx-padding:9 20 9 20;" +
                      "-fx-font-family:'Segoe UI';-fx-background-color:transparent;-fx-background-radius:7;-fx-cursor:hand;";
        String hover = "-fx-font-size:13px;-fx-text-fill:#2E7D6B;-fx-font-weight:bold;-fx-padding:9 20 9 20;" +
                       "-fx-font-family:'Segoe UI';-fx-background-color:#E9F5F2;-fx-background-radius:7;-fx-cursor:hand;";
        lbl.setStyle(base);
        lbl.setOnMouseEntered(e -> lbl.setStyle(hover));
        lbl.setOnMouseExited(e  -> lbl.setStyle(base));
        lbl.setOnMouseClicked(e -> { System.out.println("Abriendo configuración..."); menuPerfil.hide(); });
        CustomMenuItem item = new CustomMenuItem(lbl, true);
        item.setMnemonicParsing(false);
        menuPerfil.getItems().add(item);
    }

    @FXML
    private void manejarClickPerfil(MouseEvent event) {
        if (menuPerfil == null) return;
        if (menuPerfil.isShowing()) { menuPerfil.hide(); return; }
        menuPerfil.show(hboxPerfil, Side.BOTTOM, hboxPerfil.getWidth() - 185, 4);
    }

    private void cargarDatosSimulados() {
        Random random = new Random();

        // Generar 5 ventas simuladas aleatorias
        ObservableList<VentaSimulada> ventas = FXCollections.observableArrayList();
        List<String> conceptos = Arrays.asList("Vacuna", "Consulta", "Croquetas", "Desparasitante", "Baño y Estética", "Rayos X");

        for (int i = 0; i < 5; i++) {
            String concepto = conceptos.get(random.nextInt(conceptos.size()));
            int cantidad = random.nextInt(5) + 1;
            double total = (random.nextInt(500) + 100) * cantidad;
            ventas.add(new VentaSimulada(concepto, cantidad, total));
        }

        tablaCierre.setItems(ventas);

        // Generar datos aleatorios para el gráfico de pastel
        int porcentajeConsultas = random.nextInt(31) + 20; // 20-50%
        int porcentajeEstetica = random.nextInt(21) + 10;  // 10-30%
        int porcentajeFarmacia = 100 - porcentajeConsultas - porcentajeEstetica;

        ObservableList<PieChart.Data> datosGrafico = FXCollections.observableArrayList(
                new PieChart.Data("Consultas (" + porcentajeConsultas + "%)", porcentajeConsultas),
                new PieChart.Data("Estética (" + porcentajeEstetica + "%)", porcentajeEstetica),
                new PieChart.Data("Farmacia (" + porcentajeFarmacia + "%)", porcentajeFarmacia)
        );
        graficoIngresos.setData(datosGrafico);
    }

    @FXML
    private void autorizarCierre(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Firma Digital");
        alert.setHeaderText("Cierre Autorizado");
        alert.setContentText("El cierre de caja ha sido autorizado correctamente mediante firma digital simulada.");
        alert.showAndWait();
    }

    // Clase interna para la TableView
    public static class VentaSimulada {

        private String concepto;
        private int cantidad;
        private double total;

        public VentaSimulada(String concepto, int cantidad, double total) {
            this.concepto = concepto;
            this.cantidad = cantidad;
            this.total = total;
        }

        public String getConcepto() {
            return concepto;
        }

        public void setConcepto(String concepto) {
            this.concepto = concepto;
        }

        public int getCantidad() {
            return cantidad;
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }
    }
}
