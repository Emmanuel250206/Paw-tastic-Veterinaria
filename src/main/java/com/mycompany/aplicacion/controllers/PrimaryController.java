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
            String sql = "SELECT * FROM tb_usuarios WHERE nombre = ? AND contrasenia = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String bdContrasenia = rs.getString("contrasenia");
                if (contrasena.equals(bdContrasenia)) {
                    
                    // Extraer los datos correctos de la base de datos
                    String dbNombre = rs.getString("nombre");
                    String dbApellidos = rs.getString("apellidos");
                    String dbRol = rs.getString("tipo_rol");
                    
                    String nombreCompleto = (dbNombre != null ? dbNombre : "") + " " + (dbApellidos != null ? dbApellidos : "");
                    nombreCompleto = nombreCompleto.trim();
                    
                    // Llenar la sesión activa (el 'backpack')
                    com.mycompany.aplicacion.modelo.UserSession session = com.mycompany.aplicacion.modelo.UserSession.getInstance();
                    session.setUserName(nombreCompleto.isEmpty() ? "Usuario sin Nombre" : nombreCompleto);
                    session.setUserAlias(dbNombre != null ? dbNombre : "Sin Alias");
                    session.setUsernameChanged(false);
                    
                    if (dbRol != null) {
                        if (dbRol.trim().equalsIgnoreCase("Staff")) {
                            session.setUserRole("Staff");
                        } else if (dbRol.trim().equalsIgnoreCase("Veterinario")) {
                            session.setUserRole("Veterinario");
                        } else {
                            session.setUserRole(dbRol.trim());
                        }
                    } else {
                        session.setUserRole("Desconocido");
                    }
                    
                    return dbRol;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Si llegamos hasta aquí, es porque la consulta falló o el acceso fue denegado
        com.mycompany.aplicacion.modelo.UserSession.clear();
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

    String rol = validarUsuarioBD(usuario, contrasenia);
        
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
            // Si validación es null, asegurar limpieza por seguridad
            com.mycompany.aplicacion.modelo.UserSession.clear();
            txtErrorDatos.setText("Usuario o contraseña incorrectos");
            txtErrorDatos.setVisible(true);
        }
    }

}
