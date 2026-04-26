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
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.Initializable;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.modelo.Inventario;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.DatosSimulados;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Spinner;
import javafx.fxml.Initializable;

public class ReportesController implements Initializable {

    @FXML
    private TableView<VentaSimulada> tablaVentas;
    @FXML
    private TableColumn<VentaSimulada, String> colFecha;
    @FXML
    private TableColumn<VentaSimulada, String> colMascota;
    @FXML
    private TableColumn<VentaSimulada, String> colConcepto;
    @FXML
    private TableColumn<VentaSimulada, Double> colMonto;

    // ── Perfil header ───────────────────────────────────────────────────
    @FXML private ImageView imgPerfilReportes;
    @FXML private Label     lblNombreReportes;
    @FXML private Label     lblRolReportes;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    @FXML private Button btnAceptar;
    @FXML private Button btnRechazar;
    @FXML private Button btnVenta;
    @FXML private Button btnSolicitarAutorizacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilReportes);
        lblNombreReportes.setText(UserSession.getInstance().getUserName());
        lblRolReportes.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        // Configurar la tabla
        colFecha.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha()));
        colMascota.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getMascota()));
        colConcepto.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getConcepto()));
        colMonto.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getMonto()).asObject());

        cargarDatosSimulados();

        if ("Veterinario".equalsIgnoreCase(App.getRolUsuario())) {
            if (btnVenta != null) { btnVenta.setVisible(false); btnVenta.setManaged(false); }
            if (btnSolicitarAutorizacion != null) { btnSolicitarAutorizacion.setVisible(false); btnSolicitarAutorizacion.setManaged(false); }
        } else {
            if (btnAceptar != null) { btnAceptar.setVisible(false); btnAceptar.setManaged(false); }
            if (btnRechazar != null) { btnRechazar.setVisible(false); btnRechazar.setManaged(false); }
        }
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
        lbl.setOnMouseClicked(e -> { menuPerfil.hide(); ConfigurarPerfilController.abrir(hboxPerfil, () -> {
            UserSession.loadProfileImage(imgPerfilReportes);
            lblNombreReportes.setText(UserSession.getInstance().getUserName());
            lblRolReportes.setText(UserSession.getInstance().getUserRole());
        }); });
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
        ObservableList<VentaSimulada> ventas = FXCollections.observableArrayList();
        
        ventas.add(new VentaSimulada("26/04/2026", "Boby", "Consulta General", 500.00));
        ventas.add(new VentaSimulada("26/04/2026", "N/A", "Croquetas DogChow 2kg", 180.00));
        ventas.add(new VentaSimulada("25/04/2026", "Luna", "Vacuna Antirrábica + Desparasitante", 850.00));
        ventas.add(new VentaSimulada("25/04/2026", "Max", "Consulta Urgencia", 1200.00));
        ventas.add(new VentaSimulada("24/04/2026", "N/A", "Shampoo Antipulgas", 250.00));
        ventas.add(new VentaSimulada("23/04/2026", "Milo", "Estética (Baño y Corte)", 450.00));
        ventas.add(new VentaSimulada("22/04/2026", "N/A", "Juguete Hueso Goma", 120.00));
        ventas.add(new VentaSimulada("21/04/2026", "Rocky", "Rayos X", 900.00));

        tablaVentas.setItems(ventas);
    }

    private void aplicarEstiloDialogo(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/estilos.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #F0F5F2; -fx-font-family: 'Segoe UI';");
        dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #3d8d7a;");
        dialog.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
    }

    @FXML
    private void solicitarAutorizacion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Solicitud Enviada");
        alert.setHeaderText(null);
        alert.setContentText("Se ha enviado la solicitud de autorización al administrador.");
        alert.showAndWait();
    }

    @FXML
    private void aceptarReporte(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reporte Aceptado");
        alert.setHeaderText(null);
        alert.setContentText("Has aceptado el reporte de ventas y cierres.");
        alert.showAndWait();
    }

    @FXML
    private void rechazarReporte(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Reporte Rechazado");
        alert.setHeaderText(null);
        alert.setContentText("Has rechazado el reporte de ventas y cierres. Se notificará a Staff.");
        alert.showAndWait();
    }

    @FXML
    private void abrirDialogoVenta(ActionEvent event) {
        Dialog<VentaSimulada> dialog = new Dialog<>();
        dialog.setTitle("Nueva Venta");
        dialog.setHeaderText("Registrar venta de productos/servicios");
        aplicarEstiloDialogo(dialog);

        ButtonType btnConfirmar = new ButtonType("Confirmar Venta", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnConfirmar, ButtonType.CANCEL);
        ((Button) dialog.getDialogPane().lookupButton(btnConfirmar)).setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 10));

        // 1. Producto
        ComboBox<Inventario> cbProducto = new ComboBox<>();
        cbProducto.setItems(DatosSimulados.getInventario());
        cbProducto.setPrefWidth(200);

        // 2. Cantidad
        Spinner<Integer> spCantidad = new Spinner<>(1, 100, 1);
        spCantidad.setPrefWidth(100);
        spCantidad.setEditable(true);

        // 3. Mascota (búsqueda)
        TextField tfBuscarMascota = new TextField();
        tfBuscarMascota.setPromptText("Buscar mascota (Opcional)...");
        ListView<Mascota> lvMascota = new ListView<>();
        lvMascota.setPrefHeight(100);
        lvMascota.setVisible(false);
        lvMascota.setManaged(false);

        tfBuscarMascota.textProperty().addListener((obs, oldV, newV) -> {
            lvMascota.getItems().clear();
            if (newV.isEmpty()) {
                lvMascota.setVisible(false);
                lvMascota.setManaged(false);
            } else {
                java.util.List<Mascota> matches = DatosSimulados.getMascotas().stream()
                        .filter(m -> m.getNombre().toLowerCase().contains(newV.toLowerCase()))
                        .collect(Collectors.toList());
                lvMascota.getItems().addAll(matches);
                
                boolean hasItems = !matches.isEmpty();
                lvMascota.setVisible(hasItems);
                lvMascota.setManaged(hasItems);
            }
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        lvMascota.setOnMouseClicked(e -> {
            Mascota sel = lvMascota.getSelectionModel().getSelectedItem();
            if (sel != null) {
                // Al seleccionar, se pone en el TextField y se oculta la lista
                tfBuscarMascota.setText(sel.getNombre());
                lvMascota.setVisible(false);
                lvMascota.setManaged(false);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });

        // 4. Precio y Stock Total Info
        Label lblPrecioTotal = new Label("Total: $0.00");
        lblPrecioTotal.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-font-size: 14px;");

        Label lblStock = new Label("Seleccione un producto para ver el stock");
        lblStock.setStyle("-fx-text-fill: #e67e22; -fx-font-style: italic;");

        Runnable actualizarTotales = () -> {
            Inventario p = cbProducto.getValue();
            if (p != null) {
                double precio = p.getPrecio() * spCantidad.getValue();
                lblPrecioTotal.setText(String.format("Total: $%.2f", precio));
                int stockRestante = p.getCantidad() - spCantidad.getValue();
                if (stockRestante < 0) {
                    lblStock.setText("⚠️ No hay suficiente stock (Faltan " + Math.abs(stockRestante) + ")");
                    lblStock.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    dialog.getDialogPane().lookupButton(btnConfirmar).setDisable(true);
                } else {
                    lblStock.setText("Stock restante tras la venta: " + stockRestante);
                    lblStock.setStyle("-fx-text-fill: #27ae60;");
                    dialog.getDialogPane().lookupButton(btnConfirmar).setDisable(false);
                }
            } else {
                lblPrecioTotal.setText("Total: $0.00");
                lblStock.setText("Seleccione un producto para ver el stock");
                dialog.getDialogPane().lookupButton(btnConfirmar).setDisable(true);
            }
        };

        cbProducto.valueProperty().addListener((obs, oldV, newV) -> actualizarTotales.run());
        spCantidad.valueProperty().addListener((obs, oldV, newV) -> actualizarTotales.run());
        
        dialog.getDialogPane().lookupButton(btnConfirmar).setDisable(true);

        grid.add(new Label("Producto:"), 0, 0);
        grid.add(cbProducto, 1, 0);
        grid.add(new Label("Cantidad:"), 0, 1);
        grid.add(spCantidad, 1, 1);
        grid.add(new Label("Para mascota:"), 0, 2);
        VBox mascotaBox = new VBox(5, tfBuscarMascota, lvMascota);
        grid.add(mascotaBox, 1, 2);
        grid.add(lblPrecioTotal, 1, 3);
        grid.add(lblStock, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == btnConfirmar) {
                Inventario prod = cbProducto.getValue();
                if (prod != null && prod.getCantidad() >= spCantidad.getValue()) {
                    // Restar stock local
                    prod.setCantidad(prod.getCantidad() - spCantidad.getValue());
                    
                    String nombreMascota = tfBuscarMascota.getText().trim();
                    if (nombreMascota.isEmpty()) {
                        nombreMascota = "N/A";
                    }
                    String concepto = spCantidad.getValue() + "x " + prod.getNombreProducto();
                    double total = prod.getPrecio() * spCantidad.getValue();
                    String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return new VentaSimulada(fecha, nombreMascota, concepto, total);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(venta -> {
            tablaVentas.getItems().add(0, venta);
        });
    }

    // Clase interna para la TableView
    public static class VentaSimulada {
        private String fecha;
        private String mascota;
        private String concepto;
        private double monto;

        public VentaSimulada(String fecha, String mascota, String concepto, double monto) {
            this.fecha = fecha;
            this.mascota = mascota;
            this.concepto = concepto;
            this.monto = monto;
        }

        public String getFecha() { return fecha; }
        public String getMascota() { return mascota; }
        public String getConcepto() { return concepto; }
        public double getMonto() { return monto; }
    }
}
