package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.persistencia.StaffDAO;
import com.mycompany.aplicacion.modelo.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ModalMiembroController {

    @FXML private ComboBox<String> comboRol;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private PasswordField txtContrasenia;

    // Campos de Veterinario y su contenedor
    @FXML private HBox contenedorEspecialidad;
    @FXML private TextField txtCedula;
    @FXML private TextField txtEspecialidad;

    private boolean success = false;

    public boolean isSuccess() {
        return success;
    }

    @FXML
    public void initialize() {
        // 1. Poblar ComboBox con opciones legibles
        comboRol.getItems().addAll("Administrador", "Veterinario", "Recepcionista", "Asistente");

        // 2. Controlar la visibilidad dinámica del contenedor de especialidad
        comboRol.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esVeterinario = "Veterinario".equalsIgnoreCase(newVal);
            if (contenedorEspecialidad != null) {
                contenedorEspecialidad.setVisible(esVeterinario);
                contenedorEspecialidad.setManaged(esVeterinario);
            }
            if (!esVeterinario) {
                if (txtCedula != null) txtCedula.clear();
                if (txtEspecialidad != null) txtEspecialidad.clear();
            }
        });

        // Rol predeterminado
        comboRol.setValue("Recepcionista");
    }

    @FXML
    private void manejarCancelar(ActionEvent event) {
        cerrarModal();
    }

    @FXML
    private void manejarGuardar(ActionEvent event) {
        // 1. Validar que campos esenciales no estén en blanco
        if (estaVacio(txtUsuario) || estaVacio(txtContrasenia) || estaVacio(txtNombre) || estaVacio(txtEmail)) {
            mostrarAlerta("Campos obligatorios", "Los campos Usuario, Contraseña, Nombre y Correo Electrónico son requeridos (*).");
            return;
        }

        String rolSeleccionado = comboRol.getValue();
        // Convertir rol a minúsculas para sincronizar con enum en base de datos
        String rolParaBD = (rolSeleccionado != null) ? rolSeleccionado.toLowerCase() : "asistente";

        String cedula = null;
        String especialidad = null;

        // 2. Validar campos de Veterinario si corresponde
        if ("veterinario".equals(rolParaBD)) {
            if (estaVacio(txtCedula) || estaVacio(txtEspecialidad)) {
                mostrarAlerta("Campos obligatorios", "Para el rol de Veterinario, la Cédula Profesional y Especialidad son requeridas.");
                return;
            }
            cedula = txtCedula.getText().trim();
            especialidad = txtEspecialidad.getText().trim();
        }

        // 3. Garantizar clínica válida
        if (UserSession.getInstance().getClinicId() <= 0) {
            UserSession.getInstance().setClinicId(1);
        }

        // 4. Guardar a través de la capa DAO
        boolean ok = StaffDAO.insertar(
            txtUsuario.getText().trim(),
            txtNombre.getText().trim(),
            txtApellidos != null ? txtApellidos.getText().trim() : "",
            rolParaBD,
            especialidad,
            cedula,
            txtTelefono != null ? txtTelefono.getText().trim() : "",
            txtEmail.getText().trim(),
            txtContrasenia.getText()
        );

        if (ok) {
            success = true;
            cerrarModal();
        } else {
            mostrarAlerta("Error al registrar", "No se pudo registrar el miembro del staff. Verifique si el usuario ya existe o revise la BD.");
        }
    }

    private boolean estaVacio(TextField tf) {
        return tf == null || tf.getText() == null || tf.getText().trim().isEmpty();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarModal() {
        Stage stage = (Stage) comboRol.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
