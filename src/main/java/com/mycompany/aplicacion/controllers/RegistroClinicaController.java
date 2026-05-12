package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.persistencia.Conexion;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RegistroClinicaController {

    @FXML
    private StackPane rootPane;
    @FXML
    private VBox mainCard;

    // Clinic Info
    @FXML
    private TextField txtNombreClinica;
    @FXML
    private TextField txtRfc;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtTelefonoClinica;

    // Admin Info
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtNombreAdmin;
    @FXML
    private TextField txtApellidosAdmin;
    @FXML
    private TextField txtTelefonoAdmin;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnRegistrar;

    private double xOffset = 0;
    private double yOffset = 0;

    @FXML
    public void initialize() {
        // Asegurar centrado absoluto dentro del StackPane raíz
        StackPane.setAlignment(mainCard, Pos.CENTER);

        mainCard.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        mainCard.setOnMouseDragged(event -> {
            javafx.stage.Window window = mainCard.getScene().getWindow();
            window.setX(event.getScreenX() - xOffset);
            window.setY(event.getScreenY() - yOffset);
        });

        playTopSlideEntrance();
    }

    private void playTopSlideEntrance() {
        mainCard.setTranslateY(-800);
        mainCard.setOpacity(0);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), mainCard);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), mainCard);
        fade.setToValue(1);

        slide.play();
        fade.play();
    }

    private void playExitAnimation(Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.3), mainCard);
        slide.setToY(800);
        slide.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), mainCard);
        fade.setToValue(0);

        slide.setOnFinished(e -> onFinished.run());
        slide.play();
        fade.play();
    }

    @FXML
    private void registrarClinica(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        btnRegistrar.setDisable(true);
        btnRegistrar.setText("Registrando...");

        Conexion cx = new Conexion();
        Connection conn = cx.estableceConexion();

        if (conn == null) {
            mostrarAlerta("Error", "No hay conexión a la base de datos.");
            resetButton();
            return;
        }

        try {
            conn.setAutoCommit(false); // ATOMIC TRANSACTION

            // 1. Insert Clinic
            String sqlClinica = "INSERT INTO tb_clinicas (nombre, rfc, direccion, telefono, estado, created_at) VALUES (?, ?, ?, ?, '1', NOW())";
            PreparedStatement psClinica = conn.prepareStatement(sqlClinica, Statement.RETURN_GENERATED_KEYS);
            psClinica.setString(1, txtNombreClinica.getText().trim());
            psClinica.setString(2, txtRfc.getText().trim());
            psClinica.setString(3, txtDireccion.getText().trim());
            psClinica.setString(4, txtTelefonoClinica.getText().trim());
            psClinica.executeUpdate();

            ResultSet rsClinica = psClinica.getGeneratedKeys();
            int idClinica = -1;
            if (rsClinica.next()) {
                idClinica = rsClinica.getInt(1);
            }

            if (idClinica == -1) {
                conn.rollback();
                mostrarAlerta("Error", "No se pudo obtener el ID de la clínica.");
                resetButton();
                return;
            }

            // 2. Insert Admin User
            String sqlUsuario = "INSERT INTO tb_usuario_web (id_clinica, usuario, nombre, apellidos, tipo_rol, telefono, email, contrasenia, created_at) VALUES (?, ?, ?, ?, 'administrador', ?, ?, ?, NOW())";
            PreparedStatement psUsuario = conn.prepareStatement(sqlUsuario);
            psUsuario.setInt(1, idClinica);
            psUsuario.setString(2, txtUsuario.getText().trim());
            psUsuario.setString(3, txtNombreAdmin.getText().trim());
            psUsuario.setString(4, txtApellidosAdmin.getText().trim());
            psUsuario.setString(5, txtTelefonoAdmin.getText().trim());
            psUsuario.setString(6, txtEmail.getText().trim());
            psUsuario.setString(7, txtContrasena.getText().trim());
            psUsuario.executeUpdate();

            conn.commit(); // CONFIRM TRANSACTION
            
            mostrarAlerta("Éxito", "Clínica y administrador registrados correctamente.");
            cerrarAccion(null);

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {}
            e.printStackTrace();
            mostrarAlerta("Error", "Fallo al registrar la clínica: " + e.getMessage());
            resetButton();
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
            } catch (Exception e) {}
        }
    }

    private boolean validarCampos() {
        if (txtNombreClinica.getText().trim().isEmpty() ||
            txtRfc.getText().trim().isEmpty() ||
            txtDireccion.getText().trim().isEmpty() ||
            txtTelefonoClinica.getText().trim().isEmpty() ||
            txtUsuario.getText().trim().isEmpty() ||
            txtNombreAdmin.getText().trim().isEmpty() ||
            txtApellidosAdmin.getText().trim().isEmpty() ||
            txtEmail.getText().trim().isEmpty() ||
            txtContrasena.getText().trim().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Todos los campos son obligatorios.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (titulo.equals("Error")) alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void resetButton() {
        btnRegistrar.setDisable(false);
        btnRegistrar.setText("Registrar Clínica");
    }

    @FXML
    private void cerrarAccion(ActionEvent event) {
        playExitAnimation(() -> {
            javafx.stage.Window window = rootPane.getScene().getWindow();
            if (window instanceof javafx.stage.Stage) {
                ((javafx.stage.Stage) window).close();
            }
        });
    }
}
