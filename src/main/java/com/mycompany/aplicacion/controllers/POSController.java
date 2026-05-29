package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.Inventario;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.persistencia.InventarioDAO;
import com.mycompany.aplicacion.persistencia.MascotaDAO;
import com.mycompany.aplicacion.persistencia.VentasDAO;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class POSController implements Initializable {

    @FXML private ImageView imgPerfil;
    @FXML private Label lblNombre;
    @FXML private Label lblRol;

    @FXML private TextField tfBuscar;
    @FXML private ListView<Inventario> lvProductos;

    @FXML private TableView<ItemCarrito> tvCarrito;
    @FXML private TableColumn<ItemCarrito, String> colProducto;
    @FXML private TableColumn<ItemCarrito, Integer> colCant;
    @FXML private TableColumn<ItemCarrito, String> colPrecio;
    @FXML private TableColumn<ItemCarrito, String> colTotal;

    @FXML private ComboBox<Mascota> cbMascota;
    @FXML private Spinner<Integer> spDescuento;

    @FXML private Label lblSubtotal;
    @FXML private Label lblDescuento;
    @FXML private Label lblIva;
    @FXML private Label lblTotal;

    @FXML private ComboBox<String> cbMetodoPago;
    @FXML private HBox boxEfectivo;
    @FXML private HBox boxCambio;
    @FXML private TextField tfRecibido;
    @FXML private Label lblCambio;
    @FXML private Button btnCobrar;

    private ObservableList<ItemCarrito> carrito = FXCollections.observableArrayList();
    private ObservableList<Inventario> catalogoCompleto = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserSession.loadProfileImage(imgPerfil);
        lblNombre.setText(UserSession.getInstance().getUserName());
        lblRol.setText(UserSession.getInstance().getUserRole());

        // Inicializar carrito
        colProducto.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProducto().getNombre()));
        colCant.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(cell -> new SimpleStringProperty(String.format("$%.2f", cell.getValue().getProducto().getPrecio_venta())));
        colTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("$%.2f", cell.getValue().getTotal())));
        tvCarrito.setItems(carrito);

        // Inicializar Catálogo
        catalogoCompleto.addAll(InventarioDAO.getTodos());
        lvProductos.setItems(catalogoCompleto);
        
        lvProductos.setCellFactory(param -> new ListCell<Inventario>() {
            @Override
            protected void updateItem(Inventario item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox cellLayout = new HBox(10);
                    cellLayout.setAlignment(Pos.CENTER_LEFT);
                    cellLayout.setPadding(new Insets(4, 8, 4, 8));
                    
                    String stockStatus = item.getStock_actual() > item.getStock_minimo() ? "🟢" : (item.getStock_actual() > 0 ? "🟡" : "🔴");
                    Label lblInfo = new Label(stockStatus + " " + item.getNombre() + " - $" + item.getPrecio_venta() + " (Stock: " + item.getStock_actual() + ")");
                    lblInfo.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 13px;");
                    
                    Region region = new Region();
                    HBox.setHgrow(region, Priority.ALWAYS);
                    
                    Button btnAgregar = new Button("+ Cantidad");
                    btnAgregar.setStyle(
                        "-fx-background-color: #3d8d7a;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 3 10 3 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-font-size: 11px;"
                    );
                    
                    if (item.getStock_actual() == 0) {
                        btnAgregar.setDisable(true);
                        btnAgregar.setStyle(
                            "-fx-background-color: #bdc3c7;" +
                            "-fx-text-fill: #7f8c8d;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 12;" +
                            "-fx-padding: 3 10 3 10;" +
                            "-fx-font-size: 11px;"
                        );
                    }
                    
                    btnAgregar.setOnAction(ev -> {
                        mostrarDialogoCantidad(item);
                    });
                    
                    cellLayout.getChildren().addAll(lblInfo, region, btnAgregar);
                    
                    setText(null);
                    setGraphic(cellLayout);
                    if (item.getStock_actual() == 0) setDisable(true);
                    else setDisable(false);
                }
            }
        });

        tfBuscar.textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                lvProductos.setItems(catalogoCompleto);
            } else {
                ObservableList<Inventario> filtrados = FXCollections.observableArrayList(
                    catalogoCompleto.stream().filter(p -> p.getNombre().toLowerCase().contains(newV.toLowerCase())).collect(Collectors.toList())
                );
                lvProductos.setItems(filtrados);
            }
        });

        lvProductos.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Inventario prod = lvProductos.getSelectionModel().getSelectedItem();
                if (prod != null) {
                    if (prod.getStock_actual() > 0) {
                        agregarAlCarrito(prod);
                    } else {
                        javafx.collections.ObservableList<String> results = InventarioDAO.buscarDisponibilidadGlobal(prod.getNombre());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Disponibilidad Global");
                        alert.setHeaderText("Disponibilidad de: " + prod.getNombre());
                        if (results.isEmpty()) {
                            alert.setContentText("No hay stock en ninguna otra sucursal.");
                        } else {
                            alert.setContentText(String.join("\n", results));
                        }
                        alert.showAndWait();
                    }
                }
            }
        });

        // Extras
        cbMascota.setItems(FXCollections.observableArrayList(MascotaDAO.getTodas()));
        cbMetodoPago.getItems().addAll("Efectivo", "Tarjeta");
        cbMetodoPago.setValue("Efectivo");

        cbMetodoPago.valueProperty().addListener((obs, oldV, newV) -> {
            boolean esEfectivo = "Efectivo".equals(newV);
            boxEfectivo.setVisible(esEfectivo); boxEfectivo.setManaged(esEfectivo);
            boxCambio.setVisible(esEfectivo); boxCambio.setManaged(esEfectivo);
            calcularTotales();
        });

        tfRecibido.textProperty().addListener((obs, o, n) -> calcularTotales());

        // Descuento solo para Veterinario
        SpinnerValueFactory<Integer> svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        spDescuento.setValueFactory(svf);
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            spDescuento.setDisable(true);
        }
        spDescuento.valueProperty().addListener((obs, o, n) -> calcularTotales());
    }

    private void agregarAlCarrito(Inventario p) {
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId() == p.getId()) {
                if (item.getCantidad() < p.getStock_actual()) {
                    item.setCantidad(item.getCantidad() + 1);
                    tvCarrito.refresh();
                    calcularTotales();
                }
                return;
            }
        }
        carrito.add(new ItemCarrito(p, 1));
        calcularTotales();
    }

    @FXML
    private void quitarDelCarrito(ActionEvent event) {
        ItemCarrito sel = tvCarrito.getSelectionModel().getSelectedItem();
        if (sel != null) {
            carrito.remove(sel);
            calcularTotales();
        }
    }

    private void calcularTotales() {
        double subtotal = 0;
        for (ItemCarrito i : carrito) subtotal += i.getTotal();

        double descPct = spDescuento.getValue() / 100.0;
        double descuento = subtotal * descPct;
        double subDesc = subtotal - descuento;
        double iva = subDesc * 0.16;
        double total = subDesc + iva;

        lblSubtotal.setText(String.format("$%.2f", subtotal));
        lblDescuento.setText(String.format("-$%.2f", descuento));
        lblIva.setText(String.format("$%.2f", iva));
        lblTotal.setText(String.format("$%.2f", total));

        if ("Efectivo".equals(cbMetodoPago.getValue())) {
            try {
                double recibido = Double.parseDouble(tfRecibido.getText().trim());
                double cambio = recibido - total;
                lblCambio.setText(String.format("$%.2f", Math.max(0, cambio)));
                if (cambio < 0) {
                    lblCambio.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
                    btnCobrar.setDisable(true);
                } else {
                    lblCambio.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 16px;");
                    btnCobrar.setDisable(carrito.isEmpty());
                }
            } catch (Exception e) {
                lblCambio.setText("$0.00");
                btnCobrar.setDisable(true);
            }
        } else {
            btnCobrar.setDisable(carrito.isEmpty());
        }
    }

    @FXML
    private void procesarCobro(ActionEvent event) {
        // 1. Obtener datos de la venta
        double totalVenta = 0;
        try {
            totalVenta = Double.parseDouble(lblTotal.getText().replace("$", ""));
        } catch(Exception e) {
            // Fallback si el parseo falla por formato
        }
        
        Mascota m = cbMascota.getValue();
        int idM = (m != null) ? m.getId() : -1;
        char metodoPago = "Tarjeta".equals(cbMetodoPago.getValue()) ? 'T' : 'E';

        java.util.Map<Integer, Integer> carritoMap = new java.util.HashMap<>();
        java.util.Map<Integer, Double> preciosMap = new java.util.HashMap<>();
        for (ItemCarrito item : carrito) {
            carritoMap.put(item.getProducto().getId(), item.getCantidad());
            preciosMap.put(item.getProducto().getId(), item.getProducto().getPrecio_venta());
        }

        // 2. Registrar Venta en BD y descontar stock automáticamente
        boolean ok = VentasDAO.registrarVentaCompleta(idM, metodoPago, totalVenta, carritoMap, preciosMap);

        if (ok) {
            // Actualizar stock local en memoria para mantener sincronizada la interfaz
            for (ItemCarrito item : carrito) {
                Inventario p = item.getProducto();
                p.setStock_actual(p.getStock_actual() - item.getCantidad());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cobro Exitoso");
            alert.setHeaderText("Ticket Generado");
            alert.setContentText("El pago se procesó correctamente y se guardó en la BD real.");
            alert.showAndWait();

            carrito.clear();
            catalogoCompleto.clear();
            catalogoCompleto.addAll(InventarioDAO.getTodos());
            calcularTotales();
            tfRecibido.setText("");
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error de Cobro");
            alert.setContentText("Ocurrió un error al procesar el cobro. Verifique el stock o la conexión.");
            alert.showAndWait();
        }
    }

    private void mostrarDialogoCantidad(Inventario item) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Seleccionar Cantidad");
        dialog.setHeaderText("¿Cuántas unidades de '" + item.getNombre() + "' deseas agregar?");
        
        dialog.getDialogPane().setStyle("-fx-background-color: #F0F5F2; -fx-font-family: 'Segoe UI';");
        ButtonType btnTypeAceptar = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeAceptar, ButtonType.CANCEL);
        
        Button aceptarBtn = (Button) dialog.getDialogPane().lookupButton(btnTypeAceptar);
        if (aceptarBtn != null) {
            aceptarBtn.setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        }
        Button cancelarBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelarBtn != null) {
            cancelarBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        }

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Label lblStock = new Label("Stock disponible: " + item.getStock_actual());
        lblStock.setStyle("-fx-font-style: italic; -fx-text-fill: #7f8c8d;");
        
        Spinner<Integer> spinner = new Spinner<>(1, item.getStock_actual(), 1);
        spinner.setEditable(true);
        spinner.setPrefWidth(120);
        
        content.getChildren().addAll(lblStock, spinner);
        dialog.getDialogPane().setContent(content);
        
        dialog.setResultConverter(btn -> {
            if (btn == btnTypeAceptar) {
                return spinner.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(cant -> {
            if (cant > 0) {
                agregarAlCarritoConCantidad(item, cant);
            }
        });
    }

    private void agregarAlCarritoConCantidad(Inventario p, int cant) {
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId() == p.getId()) {
                int nuevaCantidad = item.getCantidad() + cant;
                if (nuevaCantidad <= p.getStock_actual()) {
                    item.setCantidad(nuevaCantidad);
                } else {
                    item.setCantidad(p.getStock_actual());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Límite de Stock");
                    alert.setHeaderText("Stock Ajustado");
                    alert.setContentText("Se ajustó la cantidad al máximo disponible (" + p.getStock_actual() + ").");
                    alert.showAndWait();
                }
                tvCarrito.refresh();
                calcularTotales();
                return;
            }
        }
        carrito.add(new ItemCarrito(p, cant));
        calcularTotales();
    }

    public static class ItemCarrito {
        private Inventario producto;
        private int cantidad;
        public ItemCarrito(Inventario p, int c) { this.producto = p; this.cantidad = c; }
        public Inventario getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public void setCantidad(int c) { this.cantidad = c; }
        public double getTotal() { return producto.getPrecio_venta() * cantidad; }
    }
}
