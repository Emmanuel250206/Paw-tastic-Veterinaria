package com.mycompany.aplicacion.controllers;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.scene.layout.VBox;
import javafx.scene.control.PasswordField;

public class RecuperarContrasenaController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane mainCard;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    private TextField txtCorreo;

    @FXML
    private Button btnEnviar;

    @FXML
    private VBox emailStep;

    @FXML
    private VBox passwordStep;

    @FXML
    private PasswordField txtNuevaContra;

    @FXML
    private PasswordField txtConfirmaContra;

    @FXML
    private Button btnActualizar;

    /**
     * Initializes the controller class. Automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        // Setup dragging for the independent window
        mainCard.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        mainCard.setOnMouseDragged(event -> {
            javafx.stage.Window window = mainCard.getScene().getWindow();
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });

        // Process the "loading" state and alert when the button is clicked
        btnEnviar.setOnAction(event -> {
            // Disable the button and change the text to show loading state
            btnEnviar.setDisable(true);
            btnEnviar.setText("Enviando...");

            // Simulate a 2-second loading state
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> {
                // Restore the button's initial state
                btnEnviar.setDisable(false);
                btnEnviar.setText("Enviar Código");

                // Show the alert as requested
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Recuperar Acceso");
                alert.setHeaderText(null);
                alert.setContentText("Código enviado. Revisa tu bandeja de entrada.");
                alert.showAndWait();
                
                // Transition to new password fields
                emailStep.setVisible(false);
                emailStep.setManaged(false);
                passwordStep.setVisible(true);
                passwordStep.setManaged(true);
            });
            pause.play();
        });

        // Set action for the Update Password button
        btnActualizar.setOnAction(event -> {
            // In a real scenario, validate fields match and call handlePasswordResetDBUpdate
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actualización");
            alert.setHeaderText(null);
            alert.setContentText("Contraseña actualizada exitosamente.");
            alert.showAndWait();
        });
    }

    /**
     * TEMPLATE METHOD: Handle safe password reset connection and execution.
     * Note: This is an unexecuted template.
     * In a production environment, you must use a strong hashing algorithm like BCrypt
     * before storing the password in the database (e.g., BCrypt.hashpw(password, BCrypt.gensalt())).
     */
    private void handlePasswordResetDBUpdate(String usernameOrEmail, String newPassword) {
        String sql = "UPDATE login SET ingresoContrasenia = ? WHERE ingresoUsuario = ? OR correo = ?";
        
        /* 
        Conexion conexion = new Conexion();
        try (java.sql.Connection con = conexion.estableceConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
            
            // NOTE: newPassword MUST be securely hashed here before setting it!
            ps.setString(1, newPassword); 
            ps.setString(2, usernameOrEmail);
            ps.setString(3, usernameOrEmail);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Contraseña actualizada con éxito.");
            } else {
                System.out.println("Usuario o correo no encontrado.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    /**
     * Closes the overlay by removing it from the parent StackPane.
     * Assumes the parent root of the Login is a StackPane.
     *
     * @param event The action event triggered by clicking "Cancelar"
     */
    @FXML
    private void cerrarAccion(ActionEvent event) {
        try {
            // Close the independent window
            javafx.stage.Window window = rootPane.getScene().getWindow();
            if (window instanceof javafx.stage.Stage) {
                ((javafx.stage.Stage) window).close();
            } else {
                window.hide();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
