package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.Citas.Prioridad;
import com.mycompany.aplicacion.modelo.Especie;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.Raza;
import com.mycompany.aplicacion.modelo.TipoCita;
import com.mycompany.aplicacion.persistencia.CitasDAO;
import com.mycompany.aplicacion.persistencia.MascotaDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de la ventana modal "Agendar Cita" (Widescreen 850x540px).
 */
public class AgendarCitaModalController {

    // --- Columna Izquierda ---
    @FXML private ComboBox<String> cbModo;
    @FXML private TextField tfDuenoNombre;
    @FXML private TextField tfDuenoApellidos;
    @FXML private TextField tfDuenoTelefono;
    @FXML private TextField tfDuenoDireccion;
    @FXML private TextField tfMascotaNombre;
    @FXML private DatePicker dpMascotaFechaNac;
    @FXML private ListView<Mascota> lvMascotas;
    @FXML private VBox boxFechaNac;
    @FXML private Label lblMascotaNombreLabel;

    // --- Columna Derecha ---
    @FXML private ComboBox<Especie> cbEspecie;
    @FXML private ComboBox<Raza> cbRaza;
    @FXML private Label lblAlertaClinica;
    @FXML private ComboBox<TipoCita> cbTipoCita;
    @FXML private DatePicker dpFechaCita;
    @FXML private ComboBox<String> cbHoraCita;
    @FXML private TextField tfMotivo;

    // --- Botones ---
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    // --- State ---
    private Mascota mascotaSeleccionadaAutocompletada = null;
    private boolean success = false;

    @FXML
    public void initialize() {
        // 1. Configurar Modo de Registro
        cbModo.getItems().addAll("Mascota Registrada", "Mascota Nueva");
        cbModo.setValue("Mascota Registrada");

        // 2. Cargar combos dinámicos
        cbEspecie.setItems(MascotaDAO.listarEspecies());
        cbTipoCita.setItems(FXCollections.observableArrayList(CitasDAO.listarTiposCita()));
        if (!cbTipoCita.getItems().isEmpty()) {
            cbTipoCita.getSelectionModel().selectFirst();
        }

        dpFechaCita.setValue(LocalDate.now());
        actualizarHorasDisponibles();

        // 3. Autocompletado del ListView para Mascota Registrada
        lvMascotas.setCellFactory(lv -> new ListCell<Mascota>() {
            @Override
            protected void updateItem(Mascota item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " (" + item.getNombrePropietario() + ")");
                }
            }
        });

        // 4. Listeners para cambios dinámicos
        cbModo.valueProperty().addListener((obs, oldModo, newModo) -> cambiarModo(newModo));
        
        tfMascotaNombre.textProperty().addListener((obs, oldText, newText) -> {
            if ("Mascota Registrada".equals(cbModo.getValue())) {
                filtrarMascotas(newText);
            }
        });

        lvMascotas.setOnMouseClicked(event -> {
            Mascota sel = lvMascotas.getSelectionModel().getSelectedItem();
            if (sel != null) {
                seleccionarMascota(sel);
            }
        });

        cbEspecie.valueProperty().addListener((obs, oldEsp, newEsp) -> {
            if ("Mascota Nueva".equals(cbModo.getValue())) {
                if (newEsp != null) {
                    cbRaza.setItems(MascotaDAO.listarRazasPorEspecie(newEsp.getId()));
                    cbRaza.setDisable(false);
                } else {
                    cbRaza.getItems().clear();
                    cbRaza.setDisable(true);
                }
            }
        });

        cbRaza.valueProperty().addListener((obs, oldRaza, newRaza) -> {
            if (newRaza != null && newRaza.getDescripcion() != null && !newRaza.getDescripcion().trim().isEmpty()) {
                lblAlertaClinica.setText("⚠️ Alerta Clínica: " + newRaza.getDescripcion());
            } else {
                lblAlertaClinica.setText("");
            }
        });

        dpFechaCita.valueProperty().addListener((obs, oldV, newV) -> actualizarHorasDisponibles());

        // Inicializar con el modo seleccionado por defecto
        cambiarModo("Mascota Registrada");
    }

    private void cambiarModo(String modo) {
        limpiarCampos();
        if ("Mascota Registrada".equals(modo)) {
            lblMascotaNombreLabel.setText("Buscar Mascota Registrada* (Escriba para buscar)");
            boxFechaNac.setVisible(false);
            boxFechaNac.setManaged(false);
            
            cbEspecie.setDisable(true);
            cbRaza.setDisable(true);
            
            tfDuenoNombre.setEditable(false);
            tfDuenoApellidos.setEditable(false);
            tfDuenoTelefono.setEditable(false);
            tfDuenoDireccion.setEditable(false);
            
            // Estilo visual deshabilitado
            tfDuenoNombre.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");
            tfDuenoApellidos.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");
            tfDuenoTelefono.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");
            tfDuenoDireccion.setStyle("-fx-opacity: 0.85; -fx-background-color: #f5f5f5;");
        } else {
            lblMascotaNombreLabel.setText("Nombre de la Mascota*");
            boxFechaNac.setVisible(true);
            boxFechaNac.setManaged(true);
            
            cbEspecie.setDisable(false);
            cbRaza.setDisable(true); // Se activa al elegir especie
            
            tfDuenoNombre.setEditable(true);
            tfDuenoApellidos.setEditable(true);
            tfDuenoTelefono.setEditable(true);
            tfDuenoDireccion.setEditable(true);

            // Restaurar estilo normal
            tfDuenoNombre.setStyle("");
            tfDuenoApellidos.setStyle("");
            tfDuenoTelefono.setStyle("");
            tfDuenoDireccion.setStyle("");
        }
    }

    private void filtrarMascotas(String query) {
        lvMascotas.getItems().clear();
        if (query == null || query.trim().isEmpty() || (mascotaSeleccionadaAutocompletada != null && mascotaSeleccionadaAutocompletada.getNombre().equals(query))) {
            lvMascotas.setVisible(false);
            lvMascotas.setManaged(false);
        } else {
            List<Mascota> matches = MascotaDAO.getTodas().stream()
                    .filter(m -> m.getNombre().toLowerCase().contains(query.toLowerCase()))
                    .collect(Collectors.toList());
            if (!matches.isEmpty()) {
                lvMascotas.getItems().addAll(matches);
                lvMascotas.setVisible(true);
                lvMascotas.setManaged(true);
            } else {
                lvMascotas.setVisible(false);
                lvMascotas.setManaged(false);
            }
        }
    }

    private void seleccionarMascota(Mascota m) {
        this.mascotaSeleccionadaAutocompletada = m;
        tfMascotaNombre.setText(m.getNombre());
        
        tfDuenoNombre.setText(extraerPrimerNombre(m.getNombrePropietario()));
        tfDuenoApellidos.setText(extraerApellidos(m.getNombrePropietario()));
        tfDuenoTelefono.setText(m.getTelefonoPropietario() != null ? m.getTelefonoPropietario() : "");
        tfDuenoDireccion.setText(m.getDireccionPropietario() != null ? m.getDireccionPropietario() : "Sin dirección");

        // Seleccionar Especie y Raza correspondientes
        for (Especie esp : cbEspecie.getItems()) {
            if (esp.getNombre().equalsIgnoreCase(m.getEspecie())) {
                cbEspecie.setValue(esp);
                break;
            }
        }
        
        if (cbEspecie.getValue() != null) {
            ObservableList<Raza> razas = MascotaDAO.listarRazasPorEspecie(cbEspecie.getValue().getId());
            cbRaza.setItems(razas);
            for (Raza r : razas) {
                if (r.getNombre().equalsIgnoreCase(m.getRaza())) {
                    cbRaza.setValue(r);
                    break;
                }
            }
        }

        lvMascotas.setVisible(false);
        lvMascotas.setManaged(false);
    }

    private String extraerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) return "";
        String[] parts = nombreCompleto.trim().split("\\s+");
        if (parts.length > 0) return parts[0];
        return "";
    }

    private String extraerApellidos(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) return "";
        String[] parts = nombreCompleto.trim().split("\\s+");
        if (parts.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < parts.length; i++) {
                if (i > 1) sb.append(" ");
                sb.append(parts[i]);
            }
            return sb.toString();
        }
        return "";
    }

    private void actualizarHorasDisponibles() {
        cbHoraCita.getItems().clear();
        String[] todasLasHoras = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", 
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
        };
        
        LocalDate fecha = dpFechaCita.getValue();
        if (fecha == null) {
            cbHoraCita.getItems().addAll(todasLasHoras);
            return;
        }

        String fechaStr = fecha.toString();
        java.util.List<String> horasOcupadas = new java.util.ArrayList<>();
        for (Citas c : CitasDAO.listarCitasPorFecha(fecha)) {
            if (c.getFecha().equals(fechaStr) && c.getPrioridad() != Prioridad.URGENTE) {
                horasOcupadas.add(c.getHora());
            }
        }

        for (String h : todasLasHoras) {
            if (!horasOcupadas.contains(h)) {
                cbHoraCita.getItems().add(h);
            }
        }
        
        if (!cbHoraCita.getItems().isEmpty()) {
            cbHoraCita.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void manejarCancelar(ActionEvent event) {
        cerrarModal();
    }

    @FXML
    private void manejarGuardar(ActionEvent event) {
        if (!validarCampos()) return;

        String modo = cbModo.getValue();
        LocalDate fechaCita = dpFechaCita.getValue();
        String horaCita = cbHoraCita.getValue();
        String motivoStr = tfMotivo.getText().trim();
        TipoCita tipoSeleccionado = cbTipoCita.getValue();

        // Combinar fecha y hora para el formato datetime de la BD
        String fechaHoraStr = fechaCita.toString() + " " + horaCita + ":00";
        int idUsuarioWebHardcoded = 1; // Hardcoded a ID 1 (JUCA) por bypass temporal

        boolean ok;

        if ("Mascota Nueva".equals(modo)) {
            String mNombre = tfMascotaNombre.getText().trim();
            String mFechaNac = dpMascotaFechaNac.getValue() != null ? dpMascotaFechaNac.getValue().toString() : "";
            int idEsp = cbEspecie.getValue().getId();
            int idRaz = cbRaza.getValue().getId();

            String pNombre = tfDuenoNombre.getText().trim();
            String pApellidos = tfDuenoApellidos.getText().trim();
            String pTelefono = tfDuenoTelefono.getText().trim();
            String pDireccion = tfDuenoDireccion.getText().trim();

            ok = CitasDAO.insertarCitaMascotaNueva(
                mNombre, mFechaNac, idEsp, idRaz, 
                pNombre, pApellidos, pTelefono, pDireccion, 
                tipoSeleccionado.getId(), motivoStr, fechaHoraStr, idUsuarioWebHardcoded
            );
        } else {
            // Mascota Registrada
            ok = CitasDAO.insertarCitaMascotaRegistrada(
                mascotaSeleccionadaAutocompletada.getId(), 
                tipoSeleccionado.getId(), motivoStr, fechaHoraStr, idUsuarioWebHardcoded
            );
        }

        if (ok) {
            success = true;
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La cita se ha programado correctamente.");
            cerrarModal();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la cita en la base de datos.");
        }
    }

    private boolean validarCampos() {
        String modo = cbModo.getValue();
        
        if ("Mascota Registrada".equals(modo)) {
            if (mascotaSeleccionadaAutocompletada == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Debe buscar y seleccionar una mascota registrada de la lista autocompletada.");
                return false;
            }
        } else {
            // Mascota Nueva
            if (tfMascotaNombre.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe ingresar el nombre de la mascota.");
                return false;
            }
            if (cbEspecie.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe seleccionar una especie.");
                return false;
            }
            if (cbRaza.getValue() == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe seleccionar una raza.");
                return false;
            }
            if (tfDuenoNombre.getText().trim().isEmpty() || tfDuenoApellidos.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe ingresar el nombre y apellidos del propietario.");
                return false;
            }
            if (tfDuenoTelefono.getText().trim().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe ingresar el teléfono del propietario.");
                return false;
            }
        }

        if (dpFechaCita.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Programación Requerida", "Debe seleccionar una fecha para la cita.");
            return false;
        }
        if (cbHoraCita.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Programación Requerida", "Debe seleccionar una hora para la cita.");
            return false;
        }
        if (tfMotivo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Vacío", "Debe describir el motivo o los síntomas de la consulta.");
            return false;
        }

        return true;
    }

    private void limpiarCampos() {
        mascotaSeleccionadaAutocompletada = null;
        tfMascotaNombre.clear();
        dpMascotaFechaNac.setValue(null);
        tfDuenoNombre.clear();
        tfDuenoApellidos.clear();
        tfDuenoTelefono.clear();
        tfDuenoDireccion.clear();
        cbEspecie.setValue(null);
        cbRaza.setValue(null);
        cbRaza.setDisable(true);
        lblAlertaClinica.setText("");
        tfMotivo.clear();
        lvMascotas.setVisible(false);
        lvMascotas.setManaged(false);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String title, String content) {
        Alert alert = new Alert(tipo);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/estilos.css").toExternalForm());
        alert.showAndWait();
    }

    public boolean isSuccess() {
        return success;
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
