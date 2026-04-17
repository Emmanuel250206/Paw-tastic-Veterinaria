/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

/**
 *
 * @author emmanuel
 */
import com.mycompany.aplicacion.modelo.Inventario;
import com.mycompany.aplicacion.modelo.DatosSimulados;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import com.mycompany.aplicacion.modelo.UserSession;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class InventarioController implements Initializable {
    
    @FXML
    private VBox vboxInventarioContainer;
    @FXML
    private HBox btnControlInventario;
    @FXML
    private ImageView imgPerfilInventario;
    @FXML
    private Label lblNombreInventario;
    @FXML
    private Label lblRolInventario;
    @FXML
    private HBox hboxPerfil;
    private ContextMenu menuPerfil;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilInventario);
        lblNombreInventario.setText(UserSession.getInstance().getUserName());
        lblRolInventario.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        cargarInventarioEnPantalla();

        if ("Staff".equalsIgnoreCase(com.mycompany.aplicacion.App.getRolUsuario())) {
            if (btnControlInventario != null) {
                btnControlInventario.setVisible(false);
                btnControlInventario.setManaged(false);
            }
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
    
    private void cargarInventarioEnPantalla() {
        if (vboxInventarioContainer == null) return;
        vboxInventarioContainer.getChildren().clear();

        boolean esStaff = "Staff".equalsIgnoreCase(com.mycompany.aplicacion.App.getRolUsuario());

        for (Inventario inv : DatosSimulados.getInventario()) {
            HBox tarjeta = new HBox(15);
            tarjeta.setPadding(new Insets(15, 20, 15, 20));
            tarjeta.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            tarjeta.setAlignment(Pos.CENTER_LEFT);

            VBox infoProducto = new VBox(5);
            
            Label lblNombre = new Label("Producto: " + inv.getNombreProducto());
            lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
            
            Label lblCategoria = new Label("Categoría: " + inv.getCategoria());
            lblCategoria.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
            
            Label lblStock = new Label("Stock: " + inv.getCantidad() + " unidades");
            lblStock.setStyle("-fx-font-size: 14px; -fx-text-fill: #3d8d7a; -fx-font-weight: bold;");

            infoProducto.getChildren().addAll(lblNombre, lblCategoria, lblStock);

            VBox infoPrecio = new VBox(5);
            infoPrecio.setAlignment(Pos.CENTER_RIGHT);
            Label lblPrecio = new Label("$" + String.format("%.2f", inv.getPrecio()));
            lblPrecio.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            infoPrecio.getChildren().add(lblPrecio);

            // Espaciador
            javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
            HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

            tarjeta.getChildren().addAll(infoProducto, spacer, infoPrecio);

            if (!esStaff) {
                Button btnEliminar = new Button("Eliminar");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btnEliminar.setOnAction(e -> {
                    DatosSimulados.getInventario().remove(inv);
                    cargarInventarioEnPantalla();
                });
                
                VBox accionesContenedor = new VBox(btnEliminar);
                accionesContenedor.setAlignment(Pos.CENTER);
                accionesContenedor.setPadding(new Insets(0, 0, 0, 15));
                tarjeta.getChildren().add(accionesContenedor);
            }

            vboxInventarioContainer.getChildren().add(tarjeta);
        }
    }

    @FXML
    private void mostrarDialogoAgregar() {
        Dialog<Inventario> dialog = new Dialog<>();
        dialog.setTitle("Agregar Nuevo Producto");
        
        dialog.getDialogPane().setStyle("-fx-background-color: #DFF5E1; -fx-font-family: 'System';");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));

        String styleLabel = "-fx-font-weight: bold; -fx-text-fill: #3d8d7a; -fx-font-size: 14px;";
        String styleTextField = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #bdc3c7; -fx-padding: 5;";

        TextField tfNombre = new TextField(); tfNombre.setPromptText("Nombre del Producto"); tfNombre.setStyle(styleTextField);
        TextField tfCategoria = new TextField(); tfCategoria.setPromptText("Categoría"); tfCategoria.setStyle(styleTextField);
        TextField tfCantidad = new TextField(); tfCantidad.setPromptText("Stock Inicial"); tfCantidad.setStyle(styleTextField);
        TextField tfPrecio = new TextField(); tfPrecio.setPromptText("Precio"); tfPrecio.setStyle(styleTextField);

        Label lbl1 = new Label("Producto:"); lbl1.setStyle(styleLabel);
        Label lbl2 = new Label("Categoría:"); lbl2.setStyle(styleLabel);
        Label lbl3 = new Label("Stock:"); lbl3.setStyle(styleLabel);
        Label lbl4 = new Label("Precio:"); lbl4.setStyle(styleLabel);

        grid.add(lbl1, 0, 0); grid.add(tfNombre, 1, 0);
        grid.add(lbl2, 0, 1); grid.add(tfCategoria, 1, 1);
        grid.add(lbl3, 0, 2); grid.add(tfCantidad, 1, 2);
        grid.add(lbl4, 0, 3); grid.add(tfPrecio, 1, 3);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 12px; -fx-font-weight: bold;");
        grid.add(lblError, 0, 4, 2, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType btnTypeGuardar = new ButtonType("Guardar Producto", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);

        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnTypeGuardar);
        if (btnGuardarNode != null && btnGuardarNode instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button btnGuardar = (javafx.scene.control.Button) btnGuardarNode;
            btnGuardar.setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
            
            btnGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                if (tfNombre.getText().trim().isEmpty() || tfCategoria.getText().trim().isEmpty() ||
                    tfCantidad.getText().trim().isEmpty() || tfPrecio.getText().trim().isEmpty()) {
                    lblError.setText("⚠ Error: Por favor, rellena todos los campos.");
                    event.consume();
                } else {
                    try {
                        Integer.parseInt(tfCantidad.getText().trim());
                        Double.parseDouble(tfPrecio.getText().trim());
                    } catch (NumberFormatException e) {
                        lblError.setText("⚠ Error: Stock y Precio deben ser números.");
                        event.consume();
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
                int stock = Integer.parseInt(tfCantidad.getText().trim());
                double precio = Double.parseDouble(tfPrecio.getText().trim());
                int newId = DatosSimulados.getInventario().size() + 1;
                return new Inventario(newId, tfNombre.getText().trim(), tfCategoria.getText().trim(), stock, precio);
            }
            return null;
        });

        java.util.Optional<Inventario> result = dialog.showAndWait();
        result.ifPresent(nuevo -> {
            DatosSimulados.getInventario().add(nuevo);
            cargarInventarioEnPantalla();
        });
    }
}
