package com.mycompany.aplicacion.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controlador para la ventana de Registro de nuevos usuarios.
 * Gestiona la validación de campos y la navegación entre ventanas.
 */
public class RegistroController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane mainCard;

    @FXML
    private TextField txtNombreCompleto;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private PasswordField txtConfirmarContrasena;

    @FXML
    private ComboBox<String> cmbRol;

    @FXML
    private Text txtErrorNombreCompleto;

    @FXML
    private Text txtErrorUsuario;

    @FXML
    private Text txtErrorContrasena;

    @FXML
    private Text txtErrorConfirmarContrasena;

    @FXML
    private Text txtErrorRol;

    @FXML
    private Button btnRegistrar;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Inicializa el controlador después de cargar el FXML.
     * Configura el arrastre de ventana y poblado del ComboBox.
     */
    @FXML
    public void initialize() {
        // Poblar el ComboBox con los roles disponibles
        cmbRol.setItems(FXCollections.observableArrayList("Veterinario", "Recepcionista", "Staff"));

        // Configurar arrastre de la ventana independiente
        mainCard.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        mainCard.setOnMouseDragged(event -> {
            javafx.stage.Window window = mainCard.getScene().getWindow();
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Valida que todos los campos estén correctamente rellenados.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos() {
        boolean valido = true;

        txtErrorNombreCompleto.setVisible(false);
        txtErrorUsuario.setVisible(false);
        txtErrorContrasena.setVisible(false);
        txtErrorConfirmarContrasena.setVisible(false);
        txtErrorRol.setVisible(false);

        if (txtNombreCompleto.getText().trim().isEmpty()) {
            txtErrorNombreCompleto.setText("Debes rellenar este campo");
            txtErrorNombreCompleto.setVisible(true);
            valido = false;
        }
        if (txtUsuario.getText().trim().isEmpty()) {
            txtErrorUsuario.setText("Debes rellenar este campo");
            txtErrorUsuario.setVisible(true);
            valido = false;
        }
        if (txtContrasena.getText().isEmpty()) {
            txtErrorContrasena.setText("Debes rellenar este campo");
            txtErrorContrasena.setVisible(true);
            valido = false;
        }
        if (!txtContrasena.getText().isEmpty() && !txtContrasena.getText().equals(txtConfirmarContrasena.getText())) {
            txtErrorConfirmarContrasena.setText("Las contraseñas no coinciden");
            txtErrorConfirmarContrasena.setVisible(true);
            valido = false;
        }
        if (cmbRol.getValue() == null) {
            txtErrorRol.setText("Debes seleccionar un rol");
            txtErrorRol.setVisible(true);
            valido = false;
        }
        return valido;
    }

    /**
     * Acción del botón "Registrar".
     * Por ahora solo valida los campos y muestra un mensaje de éxito.
     * La lógica de persistencia se implementará en una etapa posterior.
     */
    @FXML
    private void registrar(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        // TODO: Implementar la persistencia en la base de datos en una etapa posterior.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registro Exitoso");
        alert.setHeaderText(null);
        alert.setContentText("El usuario \"" + txtUsuario.getText() + "\" ha sido registrado correctamente como " + cmbRol.getValue() + ".");
        alert.showAndWait();

        // Cerrar esta ventana
        cerrarVentana(event);
    }

    /**
     * Cierra la ventana de registro.
     */
    @FXML
    private void cerrarVentana(ActionEvent event) {
        try {
            Stage stageActual = (Stage) rootPane.getScene().getWindow();
            stageActual.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
