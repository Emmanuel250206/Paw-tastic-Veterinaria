package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.DatosSimulados;
import com.mycompany.aplicacion.modelo.Mascota;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import com.mycompany.aplicacion.App;

import java.net.URL;
import java.util.ResourceBundle;

public class CitasController implements Initializable {

    @FXML
    private AnchorPane vboxSiguienteCita;
    @FXML
    private VBox vboxDetallesClinicos;
    @FXML
    private VBox vboxSignosVitales;
    @FXML
    private Button bAccionCita;
    @FXML
    private TextArea txtReporteCita;
    @FXML
    private Button btnGuardarReporte;
    @FXML
    private VBox vboxReportesGuardados;

    private Citas citaSeleccionada;
    private TextField txtTemp;
    private TextField txtFreqCard;
    private TextField txtFreqResp;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarSignosVitales();

        if (!DatosSimulados.getCitas().isEmpty()) {
            cargarCitaEnPantalla(DatosSimulados.getCitas().get(0));
        }

        // Si es Staff, agregamos un reporte simulado.
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            VBox reportCard = new VBox(5);
            reportCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

            Label lblFecha = new Label("26/10/2026 15:30 - Paciente: Buddy");
            lblFecha.setStyle("-fx-font-weight: bold; -fx-text-fill: #3d8d7a; -fx-font-size: 14px;");

            Label lblTemp = new Label("Temp: 38.5 °C | FC: 110 ppm | FR: 30 rpm");
            lblTemp.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-style: italic;");

            Label lblTexto = new Label("El perro llegó con decaimiento, se administraron fluidos y desparasitante oral.");
            lblTexto.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
            lblTexto.setWrapText(true);

            reportCard.getChildren().addAll(lblFecha, lblTemp, lblTexto);
            if (vboxReportesGuardados != null) {
                vboxReportesGuardados.getChildren().add(1, reportCard); // Debajo del titulo
            }
        }
    }

    public void cargarCitaEnPantalla(Citas cita) {
        this.citaSeleccionada = cita;

        // Habilitar o Deshabilitar guardado
        txtReporteCita.setDisable(!cita.getEstado().equalsIgnoreCase("En Consulta"));
        btnGuardarReporte.setDisable(!cita.getEstado().equalsIgnoreCase("En Consulta"));
        txtReporteCita.clear();

        // 1. Siguiente Cita
        VBox infoCita = new VBox(8);
        infoCita.setPadding(new Insets(20, 0, 0, 0));

        Label lblMascota = new Label("Paciente: " + cita.getNombreMascota());
        lblMascota.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        Label lblMotivo = new Label("Motivo: " + cita.getMotivo());
        lblMotivo.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");

        Label lblHora = new Label("Hora: " + cita.getHora());
        lblHora.setStyle("-fx-font-size: 15px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Label lblEstado = new Label("Estado: " + cita.getEstado());
        lblEstado.setStyle("-fx-font-size: 14px; -fx-text-fill: #3d8d7a; -fx-font-weight: bold;");

        infoCita.getChildren().addAll(lblMascota, lblMotivo, lblHora, lblEstado);
        infoCita.setLayoutX(25);
        infoCita.setLayoutY(45);

        // Remover elementos previos (excepto boton y titulo que vienen de origen FXML)
        vboxSiguienteCita.getChildren().removeIf(node -> node instanceof VBox);
        vboxSiguienteCita.getChildren().add(infoCita);

        // 2. Detalles Clínicos
        vboxDetallesClinicos.getChildren().removeIf(node -> !(node instanceof Label));

        Mascota mascotaEncontrada = null;
        for (Mascota m : DatosSimulados.getMascotas()) {
            if (m.getNombre().equalsIgnoreCase(cita.getNombreMascota())) {
                mascotaEncontrada = m;
                break;
            }
        }

        VBox infoClinica = new VBox(10);
        infoClinica.setPadding(new Insets(15, 20, 10, 25));

        if (mascotaEncontrada != null) {
            Label lblEdad = new Label("Edad: " + mascotaEncontrada.getEdad() + " años");
            lblEdad.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");

            Label lblPeso = new Label("Último Peso: " + (5 + Math.round(Math.random() * 15)) + " kg");
            lblPeso.setStyle("-fx-font-size: 15px; -fx-text-fill: #34495e;");

            boolean tieneAlergia = Math.random() > 0.5;
            Label lblAlergias = new Label("Alergias: " + (tieneAlergia ? "Prednisona, Polvo" : "Ninguna reportada"));
            lblAlergias.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; "
                    + (tieneAlergia ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #3d8d7a;"));

            infoClinica.getChildren().addAll(lblEdad, lblPeso, lblAlergias);
        } else {
            Label noInfo = new Label("No hay expediente previo.");
            noInfo.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            infoClinica.getChildren().add(noInfo);
        }
        vboxDetallesClinicos.getChildren().add(infoClinica);

        actualizarBotonEstado();
    }

    private void configurarSignosVitales() {
        VBox signosContainer = new VBox(15);
        signosContainer.setPadding(new Insets(15, 25, 10, 25));

        txtTemp = new TextField();
        txtTemp.setPromptText("Temperatura (°C)");
        txtTemp.setStyle("-fx-background-color: white; -fx-border-color: #3d8d7a; -fx-border-radius: 5;");
        txtTemp.setDisable(true);

        txtFreqCard = new TextField();
        txtFreqCard.setPromptText("Frecuencia Cardíaca (ppm)");
        txtFreqCard.setStyle("-fx-background-color: white; -fx-border-color: #3d8d7a; -fx-border-radius: 5;");
        txtFreqCard.setDisable(true);

        txtFreqResp = new TextField();
        txtFreqResp.setPromptText("Frecuencia Respiratoria (rpm)");
        txtFreqResp.setStyle("-fx-background-color: white; -fx-border-color: #3d8d7a; -fx-border-radius: 5;");
        txtFreqResp.setDisable(true);

        signosContainer.getChildren().addAll(txtTemp, txtFreqCard, txtFreqResp);
        vboxSignosVitales.getChildren().add(signosContainer);
    }

    @FXML
    private void manejarAccionCita(ActionEvent event) {
        if (citaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una cita primero.");
            alert.showAndWait();
            return;
        }

        if (!citaSeleccionada.getEstado().equalsIgnoreCase("En Consulta")
                && !citaSeleccionada.getEstado().equalsIgnoreCase("Finalizada")) {
            citaSeleccionada.setEstado("En Consulta");
            cargarCitaEnPantalla(citaSeleccionada);
        } else if (citaSeleccionada.getEstado().equalsIgnoreCase("En Consulta")) {
            // Finalizar consulta
            citaSeleccionada.setEstado("Finalizada");
            cargarCitaEnPantalla(citaSeleccionada);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Consulta Finalizada");
            alert.setContentText("La consulta ha terminado y el expediente ha sido bloqueado.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("La consulta ya finalizó previamente.");
            alert.showAndWait();
        }
    }

    @FXML
    private void guardarReporte(ActionEvent event) {
        if (citaSeleccionada == null || txtReporteCita.getText().trim().isEmpty()) {
            return;
        }

        String notas = txtReporteCita.getText().trim();
        
        VBox reportCard = new VBox(5);
        reportCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label lblFecha = new Label(citaSeleccionada.getFecha() + " " + citaSeleccionada.getHora() + " - Paciente: "
                + citaSeleccionada.getNombreMascota());
        lblFecha.setStyle("-fx-font-weight: bold; -fx-text-fill: #3d8d7a; -fx-font-size: 14px;");

        Label lblTemp = new Label(String.format("Temp: %s °C | FC: %s ppm | FR: %s rpm",
                txtTemp.getText().isEmpty() ? "--" : txtTemp.getText(),
                txtFreqCard.getText().isEmpty() ? "--" : txtFreqCard.getText(),
                txtFreqResp.getText().isEmpty() ? "--" : txtFreqResp.getText()));
        lblTemp.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-font-style: italic;");

        Label lblTexto = new Label(notas);
        lblTexto.setStyle("-fx-text-fill: #2c3e50; -fx-font-size: 14px;");
        lblTexto.setWrapText(true);

        reportCard.getChildren().addAll(lblFecha, lblTemp, lblTexto);
        vboxReportesGuardados.getChildren().add(1, reportCard); // 0 es el Titulo

        txtReporteCita.clear();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Reporte guardado en el historial local.");
        alert.show();
    }

    private void actualizarBotonEstado() {
        if (citaSeleccionada == null)
            return;

        if (citaSeleccionada.getEstado().equalsIgnoreCase("En Consulta")) {
            bAccionCita.setText("Finalizar Consulta");
            bAccionCita.setDisable(false);
            bAccionCita.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            txtTemp.setDisable(false);
            txtFreqCard.setDisable(false);
            txtFreqResp.setDisable(false);
        } else if (citaSeleccionada.getEstado().equalsIgnoreCase("Finalizada")) {
            bAccionCita.setText("Consulta Terminada");
            bAccionCita.setDisable(true);
            bAccionCita.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-font-weight: bold;");

            txtTemp.setDisable(true);
            txtFreqCard.setDisable(true);
            txtFreqResp.setDisable(true);
            txtTemp.clear();
            txtFreqCard.clear();
            txtFreqResp.clear();
        } else {
            bAccionCita.setText("Iniciar Consulta");
            bAccionCita.setDisable(false);
            bAccionCita.setStyle(
                    "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            txtTemp.setDisable(true);
            txtFreqCard.setDisable(true);
            txtFreqResp.setDisable(true);

            txtTemp.clear();
            txtFreqCard.clear();
            txtFreqResp.clear();
        }

        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            bAccionCita.setDisable(true);
            bAccionCita.setText("Solo Veterinario");
            bAccionCita.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        }
    }
}
