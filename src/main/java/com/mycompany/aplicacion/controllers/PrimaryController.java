package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.CConexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.text.Text;

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

    // Este método se asegura de sincronizar el texto mientras presionas el ojo
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
        // Asegúrate de que el nombre coincida con tu nuevo archivo fxml
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
    CConexion conexion = new CConexion();
    Connection con = conexion.estableceConexion();

    try {
        String sql = "SELECT rol FROM login WHERE ingresoUsuario = ? AND ingresoContrasenia = ?";
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
        // 1. Cambiamos la interfaz
        if (!validarCampos()) {
            return;
        }

        // Configurar rol según el nombre ingresado
        String usuario = txtNombre.getText();
        String contrasenia = txtContrasenaOculta.getText();

        String rol = validarUsuarioBD(usuario, contrasenia);

        if (rol != null) {

            App.setRolUsuario(rol);

            if (rol.equalsIgnoreCase("Veterinario")) {
                App.setRoot("fxml/InterfazVeterinario");
            } else if (rol.equalsIgnoreCase("Staff")) {
                App.setRoot("fxml/InterfazStaff");
            }

        } else {
            txtErrorDatos.setText("Usuario o contraseña incorrectos");
            txtErrorDatos.setVisible(true);
        }

        // 2. Ejecutamos los ajustes de ventana
        javafx.application.Platform.runLater(() -> {
            Stage stage = App.getStage();
            if (stage != null) {
                // Desbloqueamos el redimensionamiento
                stage.setResizable(true);

                // --- NUEVAS LÍNEAS PARA EL TAMAÑO MÍNIMO ---
                stage.setMinWidth(836);
                stage.setMinHeight(600);
                // -------------------------------------------

                // OBTENEMOS EL TAMAÑO DEL MONITOR
                javafx.stage.Screen screen = javafx.stage.Screen.getPrimary();
                javafx.geometry.Rectangle2D bounds = screen.getVisualBounds();

                // FORZAMOS EL TAMAÑO MANUALMENTE PARA ASEGURAR EL CAMBIO
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());

                // Finalmente aplicamos el maximizado nativo
                stage.setMaximized(true);
            }
        });
    }

}
