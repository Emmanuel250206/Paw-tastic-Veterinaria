package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.Especie;
import com.mycompany.aplicacion.modelo.Raza;
import com.mycompany.aplicacion.persistencia.MascotaDAO;
import com.mycompany.aplicacion.util.Toast;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Optional;

public class GestionRazasController {

    @FXML private ComboBox<Especie> cbEspecie;
    @FXML private TextField txtNombreRaza;
    @FXML private TextArea txtDescEspecie;
    @FXML private TextArea txtDescRaza;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Right Side: Registered Breeds Table
    @FXML private TableView<Raza> tblRazasExistentes;
    @FXML private TableColumn<Raza, String> colNombre;
    @FXML private TableColumn<Raza, String> colDescripcion;

    @FXML
    public void initialize() {
        cargarEspecies();
        
        // Setup table column value factories
        if (colNombre != null) {
            colNombre.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        }
        if (colDescripcion != null) {
            colDescripcion.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDescripcion()));
        }

        cbEspecie.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtDescEspecie.setText(newVal.getDescripcion());
                cargarRazasPorEspecie();
            } else {
                txtDescEspecie.clear();
                if (tblRazasExistentes != null) {
                    tblRazasExistentes.setItems(FXCollections.observableArrayList());
                }
            }
        });
    }

    private void cargarEspecies() {
        cbEspecie.setItems(MascotaDAO.listarEspecies());
    }

    private void cargarRazasPorEspecie() {
        Especie especieSeleccionada = cbEspecie.getValue();
        if (especieSeleccionada != null && tblRazasExistentes != null) {
            tblRazasExistentes.setItems(MascotaDAO.listarRazasPorEspecie(especieSeleccionada.getId()));
        } else if (tblRazasExistentes != null) {
            tblRazasExistentes.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void agregarEspecie(ActionEvent event) {
        Dialog<Especie> dialog = new Dialog<>();
        dialog.setTitle("Nueva Especie");
        dialog.setHeaderText("Registrar nueva especie y su contexto clínico");
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/estilos.css").toExternalForm());

        ButtonType btnOk = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField tfNombre = new TextField(); tfNombre.setPromptText("Nombre de la especie");
        TextArea taDesc = new TextArea(); taDesc.setPromptText("Contexto general (ej. Cuidados básicos, temperatura...)");
        taDesc.setPrefHeight(100); taDesc.setWrapText(true);

        grid.add(new Label("Nombre:"), 0, 0); grid.add(tfNombre, 1, 0);
        grid.add(new Label("Descripción:"), 0, 1); grid.add(taDesc, 1, 1);
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(b -> {
            if (b == btnOk) return new Especie(0, tfNombre.getText(), taDesc.getText());
            return null;
        });

        Optional<Especie> result = dialog.showAndWait();
        result.ifPresent(esp -> {
            if (esp.getNombre().trim().isEmpty() || esp.getDescripcion().trim().isEmpty()) {
                Toast.showToast("Nombre y descripción obligatorios", 2);
                return;
            }
            boolean ok = MascotaDAO.insertarEspecie(esp.getNombre(), esp.getDescripcion());
            if (ok) {
                Toast.showToast("Especie registrada", 2);
                cargarEspecies();
                cbEspecie.getItems().stream()
                        .filter(e -> e.getNombre().equalsIgnoreCase(esp.getNombre().trim()))
                        .findFirst().ifPresent(e -> cbEspecie.getSelectionModel().select(e));
            } else {
                Toast.showToast("La especie ya existe o hubo un error", 3);
            }
        });
    }

    @FXML
    private void guardar(ActionEvent event) {
        Especie esp = cbEspecie.getValue();
        String nombre = txtNombreRaza.getText().trim();
        String desc = txtDescRaza.getText().trim();

        if (esp == null || nombre.isEmpty() || desc.isEmpty()) {
            Toast.showToast("Todos los campos (incluida descripción) son obligatorios", 2);
            return;
        }

        boolean ok = MascotaDAO.insertarRaza(nombre, esp.getId(), desc);
        if (ok) {
            Toast.showToast("Raza guardada con éxito 🐾", 2);
            txtNombreRaza.clear();
            txtDescRaza.clear();
            cargarRazasPorEspecie();
        } else {
            Toast.showToast("La raza ya existe o hubo un error", 3);
        }
    }

    @FXML
    private void cerrar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
