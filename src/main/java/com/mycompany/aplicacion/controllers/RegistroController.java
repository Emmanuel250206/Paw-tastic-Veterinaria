package com.mycompany.aplicacion.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
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
        cmbRol.setItems(FXCollections.observableArrayList("Veterinario", "Recepcionista"));

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
        if (txtNombreCompleto.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El Nombre Completo no puede estar vacío.");
            return false;
        }
        if (txtUsuario.getText().trim().isEmpty()) {
            mostrarAlerta("Campo requerido", "El Nombre de Usuario no puede estar vacío.");
            return false;
        }
        if (txtContrasena.getText().isEmpty()) {
            mostrarAlerta("Campo requerido", "La Contraseña no puede estar vacía.");
            return false;
        }
        if (!txtContrasena.getText().equals(txtConfirmarContrasena.getText())) {
            mostrarAlerta("Error de contraseña", "Las contraseñas no coinciden. Por favor verifica.");
            return false;
        }
        if (cmbRol.getValue() == null) {
            mostrarAlerta("Campo requerido", "Debes seleccionar un Rol.");
            return false;
        }
        return true;
    }

    /**
     * Muestra una alerta informativa con el mensaje indicado.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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

        // Cerrar esta ventana y volver al login
        volverAlLogin(event);
    }

    /**
     * Cierra la ventana de registro y abre el Login.
     */
    @FXML
    private void volverAlLogin(ActionEvent event) {
        try {
            Stage stageActual = (Stage) rootPane.getScene().getWindow();
            stageActual.close();

            // Abrir la ventana del Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VeterinariaP1.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setTitle("Paw-tastic - Iniciar Sesión");
            loginStage.setResizable(false);
            loginStage.setScene(new javafx.scene.Scene(root));
            loginStage.sizeToScene();
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
