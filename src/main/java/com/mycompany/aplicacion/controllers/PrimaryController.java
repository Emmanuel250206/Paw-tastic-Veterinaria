package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.persistencia.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void switchToVeterinario() throws IOException {
        try {
            // Intenta cargarlo con la ruta completa
            App.setRoot("fxml/LoginVeterinario");
        } catch (IOException e) {
            System.out.println("ERROR: No se encontró el archivo FXML. Revisa el nombre.");
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("fxml/VeterinariaP1");
    }

    @FXML
    private AnchorPane rootPane;

    @FXML
    private void mostrarRecuperarContrasena(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecuperarContrasena.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Recuperar Contraseña");
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirRegistro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SeccionRegistro.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Paw-tastic - Crear Cuenta");
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();

            // Cerrar la ventana actual del Login
            Stage loginStage = (Stage) rootPane.getScene().getWindow();
            loginStage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private PasswordField txtContrasenaOculta;
    @FXML
    private TextField txtContrasenaVisible;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Text txtErrorNombre;
    @FXML
    private Text txtErrorPassword;
    @FXML
    private Text txtErrorDatos;
    @FXML
    private Text txtErrorDatosContra;

    @FXML
    private void mostrarContrasena() {
        txtContrasenaVisible.setText(txtContrasenaOculta.getText());
        txtContrasenaVisible.setVisible(true);
        txtContrasenaOculta.setVisible(false);
    }

    @FXML
    private void ocultarContrasena() {
        txtContrasenaOculta.setText(txtContrasenaVisible.getText());
        txtContrasenaVisible.setVisible(false);
        txtContrasenaOculta.setVisible(true);
    }

    @FXML
    private void switchToRegistro() throws IOException {
        App.setRoot("fxml/Registro");
    }



    private boolean validarCampos() {
        boolean valido = true;

        txtErrorNombre.setVisible(false);
        txtErrorPassword.setVisible(false);
        txtErrorDatos.setVisible(false);
        txtErrorDatosContra.setVisible(false);

        if (txtNombre.getText().trim().isEmpty()) {
            txtErrorNombre.setText("Debes rellenar este campo");
            txtErrorNombre.setVisible(true);
            valido = false;
        
        }

        if (txtContrasenaOculta.getText().isEmpty()) {
            txtErrorPassword.setText("Debes rellenar este campo");
            txtErrorPassword.setVisible(true);
            valido = false;
        }

        return valido;
    }
    public String validarUsuarioBD(String usuario, String contrasena) {
    Conexion conexion = new Conexion();
    Connection con = conexion.estableceConexion();

    try {
        String sql = "SELECT rol FROM login_pawtastic WHERE nombre = ? AND contrasenia = ?";
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, usuario);
        ps.setString(2, contrasena);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return rs.getString("rol");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

    @FXML
    private void iniciarSesion() throws IOException {
        if (!validarCampos()) {
            return;
        }

        // Configurar rol según el nombre ingresado
        String usuario = txtNombre.getText();
        String contrasenia = txtContrasenaOculta.getText();

        // --- BYPASS TEMPORAL (sin BD) ---
        String rol;
        if (usuario.equalsIgnoreCase("staff")) {
            rol = "Staff";
        } else if (usuario.equalsIgnoreCase("veterinario")) {
            rol = "Veterinario";
        } else {
            rol = validarUsuarioBD(usuario, contrasenia);
        }
        // --- FIN BYPASS ---

        if (rol != null) {

            App.setRolUsuario(rol);

            if (rol.equalsIgnoreCase("Veterinario") || rol.equalsIgnoreCase("Staff")) {

                // 1. Primero habilitamos el redimensionamiento ANTES de cambiar la vista
                Stage stage = App.getStage();
                if (stage != null) {
                    stage.setResizable(true);
                    stage.setMinWidth(836);
                    stage.setMinHeight(600);
                }

                // 2. Cambiamos la vista
                App.setRoot("fxml/InterfazVeterinario");

                // 3. Maximizamos en el siguiente ciclo del layout para que tome efecto
                javafx.application.Platform.runLater(() -> {
                    javafx.application.Platform.runLater(() -> {
                        Stage s = App.getStage();
                        if (s != null) {
                            javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
                            javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();
                            s.setX(bounds.getMinX());
                            s.setY(bounds.getMinY());
                            s.setWidth(bounds.getWidth());
                            s.setHeight(bounds.getHeight());
                            s.setMaximized(true);
                        }
                    });
                });
            }

        } else {
            txtErrorDatos.setText("Usuario o contraseña incorrectos");
            txtErrorDatos.setVisible(true);
        }
    }

}
