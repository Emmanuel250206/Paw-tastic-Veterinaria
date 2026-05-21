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
            // 1. Añadir overlay de oscurecimiento al login original
            javafx.scene.layout.Region dimmingOverlay = new javafx.scene.layout.Region();
            dimmingOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
            AnchorPane.setTopAnchor(dimmingOverlay, 0.0);
            AnchorPane.setBottomAnchor(dimmingOverlay, 0.0);
            AnchorPane.setLeftAnchor(dimmingOverlay, 0.0);
            AnchorPane.setRightAnchor(dimmingOverlay, 0.0);
            rootPane.getChildren().add(dimmingOverlay);

            // 2. Crear el nuevo Stage modal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RecuperarContrasena.fxml"));
            Parent root = loader.load();
            
            Stage modalStage = new Stage();
            modalStage.initOwner(rootPane.getScene().getWindow());
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modalStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            modalStage.setScene(scene);

            // 3. Quitar el overlay al cerrar el modal
            modalStage.setOnHidden(e -> rootPane.getChildren().remove(dimmingOverlay));

            modalStage.show();
            modalStage.centerOnScreen();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrarRegistroClinica(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegistroClinica.fxml"));
            Parent root = loader.load();
            
            Stage modalStage = new Stage();
            Stage owner = (Stage) rootPane.getScene().getWindow();
            modalStage.initOwner(owner);
            modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            modalStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            modalStage.setScene(scene);

            // Sincronizar tamaño y posición con la ventana principal para el efecto de overlay
            modalStage.setX(owner.getX());
            modalStage.setY(owner.getY());
            modalStage.setWidth(owner.getWidth());
            modalStage.setHeight(owner.getHeight());

            modalStage.show();
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
        if (con == null) {
            System.err.println("[Error] No se pudo establecer conexión con la base de datos.");
            return "CONNECTION_ERROR";
        }
        try {
            String sql = "SELECT * FROM tb_usuario_web WHERE usuario = ? AND contrasenia = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String bdContrasenia = rs.getString("contrasenia");
                if (contrasena.equals(bdContrasenia)) {
                    
                    // Extraer los datos correctos de la base de datos
                    int dbId = rs.getInt("id");
                    int dbClinicId = rs.getInt("id_clinica"); // EXTRAER CLÍNICA
                    String dbNombre = rs.getString("nombre");
                    String dbApellidos = rs.getString("apellidos");
                    String dbRol = rs.getString("tipo_rol");
                    String dbUsuario = rs.getString("usuario");
                    
                    String nombreCompleto = (dbNombre != null ? dbNombre : "") + " " + (dbApellidos != null ? dbApellidos : "");
                    nombreCompleto = nombreCompleto.trim();
                    
                    // Llenar la sesión activa (el 'backpack')
                    com.mycompany.aplicacion.modelo.UserSession session = com.mycompany.aplicacion.modelo.UserSession.getInstance();
                    session.setUserId(dbId);
                    session.setClinicId(dbClinicId); // GUARDAR CLÍNICA
                    session.setUserName(nombreCompleto.isEmpty() ? "Usuario sin Nombre" : nombreCompleto);
                    session.setUserAlias(dbUsuario != null ? dbUsuario : "Sin Alias");
                    session.setUsernameChanged(false);
                    
                    if (dbRol != null) {
                        String rolNorm = dbRol.trim().toLowerCase();
                        // Mapear ENUM de BD a roles internos de la app
                        if (rolNorm.equals("veterinario") || rolNorm.equals("administrador")) {
                            session.setUserRole("Veterinario");
                            try {
                                String sqlActivo = "UPDATE tb_usuario_web SET activo = 1 WHERE id = ?";
                                PreparedStatement psActivo = con.prepareStatement(sqlActivo);
                                psActivo.setInt(1, dbId);
                                psActivo.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (rolNorm.equals("recepcionista") || rolNorm.equals("asistente") || rolNorm.equals("staff")) {
                            session.setUserRole("Staff");
                            try {
                                String sqlActivo = "UPDATE tb_usuario_web SET activo = 1 WHERE id = ?";
                                PreparedStatement psActivo = con.prepareStatement(sqlActivo);
                                psActivo.setInt(1, dbId);
                                psActivo.executeUpdate();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
        if (!validarCampos()) return;

        String nombre = txtNombre.getText().trim();
        String contrasena = txtContrasenaOculta.getText();

        // 1. Validar contra la Base de Datos
        String rol = validarUsuarioBD(nombre, contrasena);

        if (rol != null) {
            // LOGIN EXITOSO
            String rolInterno = com.mycompany.aplicacion.modelo.UserSession.getInstance().getUserRole();
            App.setRolUsuario(rolInterno);
            String view = rolInterno.equalsIgnoreCase("Staff") ? "fxml/InterfazStaff" : "fxml/InterfazVeterinario";

            // Habilitar redimensionamiento y establecer límites mínimos
            Stage stage = App.getStage();
            if (stage != null) {
                stage.setResizable(true);
                stage.setMinWidth(1100);
                stage.setMinHeight(760);
            }

            // Navegar a la vista correspondiente
            App.setRoot(view);

            // Maximizar la ventana automáticamente
            javafx.application.Platform.runLater(() -> {
                Stage s = App.getStage();
                if (s != null) {
                    s.setFullScreen(true);
                }
            });
        } else {
            // LOGIN FALLIDO
            txtErrorDatos.setText("Usuario o contraseña incorrectos");
            txtErrorDatos.setVisible(true);
        }
    }

}
