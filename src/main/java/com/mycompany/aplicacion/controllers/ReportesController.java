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
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ReportesController {

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

    public void initialize() {
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
