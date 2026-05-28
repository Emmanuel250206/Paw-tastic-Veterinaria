package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.Inventario;
import com.mycompany.aplicacion.persistencia.InventarioDAO;
import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.util.Toast;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {

    // --- ELEMENTOS DE VISTA PRINCIPAL (SeccionInventario) ---
    @FXML private TableView<Inventario> tablaInventario;
    @FXML private TableColumn<Inventario, Integer> colCodigo;
    @FXML private TableColumn<Inventario, String> colNombre;
    @FXML private TableColumn<Inventario, String> colCategoria;
    @FXML private TableColumn<Inventario, Integer> colStock;
    @FXML private TableColumn<Inventario, Integer> colStockMinimo;
    @FXML private TableColumn<Inventario, String> colUnidad;
    @FXML private TableColumn<Inventario, Double> colCosto;
    @FXML private TableColumn<Inventario, Double> colPrecio;
    @FXML private TableColumn<Inventario, String> colCaducidad;

    @FXML private HBox btnControlInventario;
    @FXML private ImageView imgPerfilInventario;
    @FXML private Label lblNombreInventario;
    @FXML private Label lblRolInventario;
    @FXML private HBox hboxPerfil;
    private ContextMenu menuPerfil;

    // --- ELEMENTOS DE VISTA REGISTRO (ModalProducto) ---
    @FXML private Label lblSucursalInventario;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> comboCategoria;
    @FXML private ComboBox<String> comboUnidad;
    @FXML private TextField txtCosto;
    @FXML private TextField txtPrecio;
    @FXML private TextField txtExistencia;
    @FXML private TextField txtStockMinimo;
    @FXML private DatePicker dpCaducidad;
    @FXML private TextArea txtDescripcion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- 1. Inicialización de la vista principal (Tabla de Inventario) ---
        if (imgPerfilInventario != null) {
            UserSession.loadProfileImage(imgPerfilInventario);
        }
        if (lblNombreInventario != null) {
            lblNombreInventario.setText(UserSession.getInstance().getUserName());
        }
        if (lblRolInventario != null) {
            lblRolInventario.setText(UserSession.getInstance().getUserRole());
        }
        if (hboxPerfil != null) {
            construirMenuPerfil();
        }

        if (tablaInventario != null) {
            colCodigo.setCellValueFactory(new PropertyValueFactory<>("id"));
            colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
            colStock.setCellValueFactory(new PropertyValueFactory<>("stock_actual"));
            colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stock_minimo"));
            colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad_medida"));
            colCosto.setCellValueFactory(new PropertyValueFactory<>("precio_compra"));
            colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio_venta"));
            colCaducidad.setCellValueFactory(new PropertyValueFactory<>("fecha_caducidad"));
            
            actualizarTablaInventario();
        }

        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            if (btnControlInventario != null) {
                btnControlInventario.setVisible(false);
                btnControlInventario.setManaged(false);
            }
        }

        // --- 2. Inicialización de la vista modal (Formulario de Registro) ---
        UserSession sesion = UserSession.getInstance();
        if (lblSucursalInventario != null) {
            if (sesion != null && sesion.getNombreClinica() != null) {
                lblSucursalInventario.setText("Sucursal: " + sesion.getNombreClinica());
            } else {
                lblSucursalInventario.setText("Sucursal: No identificada");
            }
        }

        if (comboCategoria != null) {
            comboCategoria.getItems().addAll("Medicamento", "Alimento", "Higiene", "Accesorios", "Material de Curación");
        }
        if (comboUnidad != null) {
            comboUnidad.getItems().addAll("Pieza", "Caja", "Frasco", "Bolsa", "Ampolleta", "Tableta");
            comboUnidad.setValue("Pieza");
        }

        // Sanitización en tiempo real de entradas de texto
        if (txtExistencia != null) {
            txtExistencia.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.matches("\\d*")) {
                    txtExistencia.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
        }
        if (txtStockMinimo != null) {
            txtStockMinimo.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.matches("\\d*")) {
                    txtStockMinimo.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
        }
        if (txtPrecio != null) {
            txtPrecio.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.matches("\\d*(\\.\\d*)?")) {
                    txtPrecio.setText(oldVal != null ? oldVal : "");
                }
            });
        }
        if (txtCosto != null) {
            txtCosto.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.matches("\\d*(\\.\\d*)?")) {
                    txtCosto.setText(oldVal != null ? oldVal : "");
                }
            });
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
        lbl.setOnMouseClicked(e -> { 
            menuPerfil.hide(); 
            ConfigurarPerfilController.abrir(hboxPerfil, () -> {
                UserSession.loadProfileImage(imgPerfilInventario);
                lblNombreInventario.setText(UserSession.getInstance().getUserName());
                lblRolInventario.setText(UserSession.getInstance().getUserRole());
            }); 
        });
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

    private void actualizarTablaInventario() {
        if (tablaInventario != null) {
            tablaInventario.setItems(InventarioDAO.getTodos());
        }
    }

    // --- ACCIONES DE LA VISTA PRINCIPAL ---
    @FXML
    private void abrirModalAgregarProducto(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModalProducto.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Blocks background interaction
            stage.setTitle("Registrar Nuevo Producto");
            stage.showAndWait();

            // Refresh the main TableView automatically after modal closes
            actualizarTablaInventario(); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarProductoSeleccionado(ActionEvent event) {
        Inventario seleccionado = tablaInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Toast.show(App.getStage(), "Por favor, seleccione un producto para editar.", 2);
            return;
        }
        mostrarDialogoEditar(seleccionado);
    }

    @FXML
    private void eliminarProductoSeleccionado(ActionEvent event) {
        Inventario seleccionado = tablaInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Toast.show(App.getStage(), "Por favor, seleccione un producto para desactivar.", 2);
            return;
        }
        boolean ok = InventarioDAO.desactivar(seleccionado.getId());
        if (ok) {
            Toast.show(App.getStage(), "¡Producto desactivado exitosamente!", 2);
            actualizarTablaInventario();
        } else {
            Toast.show(App.getStage(), "Error: No se pudo desactivar el producto.", 2);
        }
    }

    @FXML
    private void buscarProductoGlobal(ActionEvent event) {
        Inventario seleccionado = tablaInventario.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Toast.show(App.getStage(), "Por favor, seleccione un producto para buscar en otras sucursales.", 2);
            return;
        }
        javafx.collections.ObservableList<String> results = InventarioDAO.buscarDisponibilidadGlobal(seleccionado.getNombre());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Disponibilidad Global");
        alert.setHeaderText("Disponibilidad de: " + seleccionado.getNombre());
        if (results.isEmpty()) {
            alert.setContentText("No hay stock en ninguna otra sucursal.");
        } else {
            alert.setContentText(String.join("\n", results));
        }
        alert.showAndWait();
    }

    // --- ACCIONES DEL FORMULARIO DE REGISTRO EN EL MODAL ---
    @FXML
    private void handleGuardarProducto() {
        if (txtCodigo.getText().isEmpty() || txtNombre.getText().isEmpty() || 
            comboCategoria.getValue() == null || txtPrecio.getText().isEmpty() || txtCosto.getText().isEmpty()) {
            Toast.show(App.getStage(), "Por favor, llene todos los campos obligatorios (*)", 2);
            return;
        }
        try {
            String codigo = txtCodigo.getText().trim();
            String nombre = txtNombre.getText().trim();
            String categoria = comboCategoria.getValue();
            String unidad = comboUnidad.getValue() != null ? comboUnidad.getValue() : "Pieza";
            String descripcion = txtDescripcion.getText().trim();

            double costo = Double.parseDouble(txtCosto.getText());
            double precio = Double.parseDouble(txtPrecio.getText());
            int existencia = Integer.parseInt(txtExistencia.getText());
            int stockMinimo = txtStockMinimo.getText().isEmpty() ? 0 : Integer.parseInt(txtStockMinimo.getText());
            LocalDate fechaCaducidad = dpCaducidad.getValue();

            int idClinicaActual = UserSession.getInstance().getIdClinica();

            InventarioDAO dao = new InventarioDAO();
            boolean exito = dao.guardarProducto(codigo, idClinicaActual, nombre, descripcion, precio, costo, 
                                                existencia, stockMinimo, categoria, unidad, fechaCaducidad);

            if (exito) {
                Toast.show(App.getStage(), "¡Producto registrado exitosamente!", 2);
                limpiarCamposFormulario();
                cerrarModal();
            } else {
                Toast.show(App.getStage(), "Error: El código de barras ya existe o hubo un fallo en la BD.", 2);
            }
        } catch (NumberFormatException e) {
            Toast.show(App.getStage(), "Verifique que los precios y existencias tengan formatos válidos.", 2);
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        cerrarModal();
    }

    private void limpiarCamposFormulario() {
        if (txtCodigo != null) txtCodigo.clear();
        if (txtNombre != null) txtNombre.clear();
        if (comboCategoria != null) comboCategoria.setValue(null);
        if (comboUnidad != null) comboUnidad.setValue("Pieza");
        if (txtCosto != null) txtCosto.clear();
        if (txtPrecio != null) txtPrecio.clear();
        if (txtExistencia != null) txtExistencia.clear();
        if (txtStockMinimo != null) txtStockMinimo.clear();
        if (dpCaducidad != null) dpCaducidad.setValue(null);
        if (txtDescripcion != null) txtDescripcion.clear();
    }

    private void cerrarModal() {
        Stage stage = null;
        if (txtCodigo != null && txtCodigo.getScene() != null) {
            stage = (Stage) txtCodigo.getScene().getWindow();
        }
        if (stage != null) {
            stage.close();
        }
    }

    private void mostrarDialogoEditar(Inventario inv) {
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Editar Producto");
        dialog.getDialogPane().setStyle("-fx-background-color: #DFF5E1; -fx-font-family: 'System';");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));

        String styleLabel = "-fx-font-weight: bold; -fx-text-fill: #3d8d7a; -fx-font-size: 14px;";
        String styleTextField = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 5;";

        TextField tfNombre = new TextField(inv.getNombre()); tfNombre.setStyle(styleTextField);
        TextField tfCategoria = new TextField(inv.getCategoria()); tfCategoria.setStyle(styleTextField);
        TextField tfDescripcion = new TextField(inv.getDescripcion()); tfDescripcion.setStyle(styleTextField);
        TextField tfStockActual = new TextField(String.valueOf(inv.getStock_actual())); tfStockActual.setStyle(styleTextField);
        TextField tfStockMinimo = new TextField(String.valueOf(inv.getStock_minimo())); tfStockMinimo.setStyle(styleTextField);
        TextField tfUnidadMedida = new TextField(inv.getUnidad_medida()); tfUnidadMedida.setStyle(styleTextField);
        TextField tfPrecioCompra = new TextField(String.valueOf(inv.getPrecio_compra())); tfPrecioCompra.setStyle(styleTextField);
        TextField tfPrecioVenta = new TextField(String.valueOf(inv.getPrecio_venta())); tfPrecioVenta.setStyle(styleTextField);
        TextField tfFechaCaducidad = new TextField(inv.getFecha_caducidad()); tfFechaCaducidad.setStyle(styleTextField);

        Label lbl1 = new Label("Producto:"); lbl1.setStyle(styleLabel);
        Label lbl2 = new Label("Categoría:"); lbl2.setStyle(styleLabel);
        Label lbl3 = new Label("Descripción:"); lbl3.setStyle(styleLabel);
        Label lbl4 = new Label("Stock Actual:"); lbl4.setStyle(styleLabel);
        Label lbl5 = new Label("Stock Mínimo:"); lbl5.setStyle(styleLabel);
        Label lbl6 = new Label("Unidad Medida:"); lbl6.setStyle(styleLabel);
        Label lbl7 = new Label("Precio Compra:"); lbl7.setStyle(styleLabel);
        Label lbl8 = new Label("Precio Venta:"); lbl8.setStyle(styleLabel);
        Label lbl9 = new Label("Fecha Caducidad:"); lbl9.setStyle(styleLabel);

        grid.add(lbl1, 0, 0); grid.add(tfNombre, 1, 0);
        grid.add(lbl2, 0, 1); grid.add(tfCategoria, 1, 1);
        grid.add(lbl3, 0, 2); grid.add(tfDescripcion, 1, 2);
        grid.add(lbl4, 0, 3); grid.add(tfStockActual, 1, 3);
        grid.add(lbl5, 0, 4); grid.add(tfStockMinimo, 1, 4);
        grid.add(lbl6, 0, 5); grid.add(tfUnidadMedida, 1, 5);
        grid.add(lbl7, 0, 6); grid.add(tfPrecioCompra, 1, 6);
        grid.add(lbl8, 0, 7); grid.add(tfPrecioVenta, 1, 7);
        grid.add(lbl9, 0, 8); grid.add(tfFechaCaducidad, 1, 8);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
        grid.add(lblError, 0, 9, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType btnTypeGuardar = new ButtonType("Guardar Cambios", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);

        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnTypeGuardar);
        if (btnGuardarNode != null && btnGuardarNode instanceof Button) {
            Button btnG = (Button) btnGuardarNode;
            btnG.setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            
            btnG.addEventFilter(ActionEvent.ACTION, ev -> {
                if (tfNombre.getText().trim().isEmpty() || tfCategoria.getText().trim().isEmpty() ||
                    tfDescripcion.getText().trim().isEmpty() || tfStockActual.getText().trim().isEmpty() ||
                    tfStockMinimo.getText().trim().isEmpty() || tfUnidadMedida.getText().trim().isEmpty() ||
                    tfPrecioCompra.getText().trim().isEmpty() || tfPrecioVenta.getText().trim().isEmpty()) {
                    lblError.setText("⚠ Error: Por favor, rellena todos los campos.");
                    ev.consume();
                } else {
                    try {
                        Integer.parseInt(tfStockActual.getText().trim());
                        Integer.parseInt(tfStockMinimo.getText().trim());
                        Double.parseDouble(tfPrecioCompra.getText().trim());
                        Double.parseDouble(tfPrecioVenta.getText().trim());
                    } catch (NumberFormatException e) {
                        lblError.setText("⚠ Error: Stocks y Precios deben ser números.");
                        ev.consume();
                    }
                }
            });
        }
        
        javafx.scene.Node btnCancelarNode = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancelarNode != null) {
            btnCancelarNode.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeGuardar) {
                inv.setNombre(tfNombre.getText().trim());
                inv.setCategoria(tfCategoria.getText().trim());
                inv.setDescripcion(tfDescripcion.getText().trim());
                inv.setStock_actual(Integer.parseInt(tfStockActual.getText().trim()));
                inv.setStock_minimo(Integer.parseInt(tfStockMinimo.getText().trim()));
                inv.setUnidad_medida(tfUnidadMedida.getText().trim());
                inv.setPrecio_compra(Double.parseDouble(tfPrecioCompra.getText().trim()));
                inv.setPrecio_venta(Double.parseDouble(tfPrecioVenta.getText().trim()));
                inv.setFecha_caducidad(tfFechaCaducidad.getText().trim());
                return true;
            }
            return false;
        });

        dialog.showAndWait().ifPresent(actualizado -> {
            if (actualizado) {
                InventarioDAO.actualizar(inv);
                actualizarTablaInventario();
            }
        });
    }
}
