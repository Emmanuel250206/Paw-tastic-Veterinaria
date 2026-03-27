/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.DatosSimulados;
import com.mycompany.aplicacion.modelo.Mascota;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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
    
    @FXML
    public void initialize() {

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
