/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.DatosSimulados;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author emmanuel
 */
public class MascotasController {
    // TABLA

    @FXML
    private TableView<Mascota> tablaMascotas;
    @FXML
    private TableColumn<Mascota, String> colNombre;
    @FXML
    private TableColumn<Mascota, String> colEspecie;
    @FXML
    private TableColumn<Mascota, Integer> colEdad;

    // FICHA
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblEspecie;
    @FXML
    private Label lblRaza;
    @FXML
    private Label lblEdad;
    @FXML
    private Label lblPropietario;

    @FXML
    private TextArea txtHistorial;

    @FXML private TextField txtBuscar;

    // ── Perfil header ───────────────────────────────────────────────────
    @FXML private ImageView imgPerfilMascotas;
    @FXML private Label     lblNombreMascotas;
    @FXML private Label     lblRolMascotas;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;
    
    @FXML
    public void initialize() {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilMascotas);
        lblNombreMascotas.setText(UserSession.getInstance().getUserName());
        lblRolMascotas.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        txtBuscar.textProperty().addListener((obs, oldText, newText) -> {
    tablaMascotas.setItems(DatosSimulados.buscarPorNombre(newText));
});
        // 🔹 Configurar columnas
        colNombre.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre())
        );

        colEspecie.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEspecie())
        );

        colEdad.setCellValueFactory(cellData
                -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getEdad()).asObject()
        );

        // 🔹 Cargar datos simulados
        tablaMascotas.setItems(DatosSimulados.getMascotas());
        tablaMascotas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 🔥 EVENTO: clic en tabla
        tablaMascotas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, mascota) -> {
                    if (mascota != null) {
                        mostrarMascota(mascota);
                    }
                }
        );
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
            UserSession.loadProfileImage(imgPerfilMascotas);
            lblNombreMascotas.setText(UserSession.getInstance().getUserName());
            lblRolMascotas.setText(UserSession.getInstance().getUserRole());
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

    // 🔥 Método clave
    private void mostrarMascota(Mascota m) {
        lblNombre.setText(m.getNombre());
        lblEspecie.setText(m.getEspecie());
        lblRaza.setText(m.getRaza());
        lblEdad.setText(String.valueOf(m.getEdad()));
        lblPropietario.setText(m.getNombrePropietario());
        txtHistorial.setText(m.getHistorialClinico());
    }
}
