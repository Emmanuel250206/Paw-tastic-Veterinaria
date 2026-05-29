package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.Staff;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Side;
import com.mycompany.aplicacion.modelo.UserSession;
import java.util.List;
import com.mycompany.aplicacion.util.Toast;

public class StaffController implements Initializable {

    @FXML
    private VBox vboxContenedor;

    // ── Perfil header ───────────────────────────────────────────────────
    @FXML
    private ImageView imgPerfilStaff;
    @FXML
    private Label lblNombreStaff;
    @FXML
    private Label lblRolStaff;
    @FXML
    private HBox hboxPerfil;
    private ContextMenu menuPerfil;

    private static StaffController instance;

    private Button btnEditarStaff;
    private Button btnDesactivarStaff;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilStaff);
        lblNombreStaff.setText(UserSession.getInstance().getUserName());
        lblRolStaff.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        cargarPersonalEnPantalla();
    }

    private void construirMenuPerfil() {
        menuPerfil = new ContextMenu();
        menuPerfil.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #3D8D7A;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-width: 1.2;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0, 0, 5);" +
                        "-fx-padding: 4 0 4 0;");
        Label lbl = new Label("⚙  Configurar Perfil");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPrefWidth(185);
        String base = "-fx-font-size:13px;-fx-text-fill:#2C3E50;-fx-padding:9 20 9 20;" +
                "-fx-font-family:'Segoe UI';-fx-background-color:transparent;-fx-background-radius:7;-fx-cursor:hand;";
        String hover = "-fx-font-size:13px;-fx-text-fill:#2E7D6B;-fx-font-weight:bold;-fx-padding:9 20 9 20;" +
                "-fx-font-family:'Segoe UI';-fx-background-color:#E9F5F2;-fx-background-radius:7;-fx-cursor:hand;";
        lbl.setStyle(base);
        lbl.setOnMouseEntered(e -> lbl.setStyle(hover));
        lbl.setOnMouseExited(e -> lbl.setStyle(base));
        lbl.setOnMouseClicked(e -> {
            menuPerfil.hide();
            ConfigurarPerfilController.abrir(hboxPerfil, () -> {
                UserSession.loadProfileImage(imgPerfilStaff);
                lblNombreStaff.setText(UserSession.getInstance().getUserName());
                lblRolStaff.setText(UserSession.getInstance().getUserRole());
            });
        });
        CustomMenuItem item = new CustomMenuItem(lbl, true);
        item.setMnemonicParsing(false);
        menuPerfil.getItems().add(item);
    }

    @FXML
    private void manejarClickPerfil(MouseEvent event) {
        if (menuPerfil == null)
            return;
        if (menuPerfil.isShowing()) {
            menuPerfil.hide();
            return;
        }
        menuPerfil.show(hboxPerfil, Side.BOTTOM, hboxPerfil.getWidth() - 185, 4);
    }

    public static void refreshStaffCards() {
        if (instance != null) {
            javafx.application.Platform.runLater(() -> {
                instance.cargarPersonalEnPantalla();
            });
        }
    }

    private void cargarPersonalEnPantalla() {
        if (vboxContenedor == null)
            return;

        vboxContenedor.getChildren().clear();
        vboxContenedor.setSpacing(15);
        vboxContenedor.setPadding(new Insets(20));

        // Botón superior de Agregar
        Button btnAgregar = new Button("+ Agregar Miembro");
        btnAgregar.setStyle(
                "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5;");
        btnAgregar.setCursor(javafx.scene.Cursor.HAND);
        btnAgregar.setOnAction(e -> mostrarDialogoAgregar());

        btnAgregar.setOnMouseEntered(evt -> btnAgregar.setStyle(
                "-fx-background-color: #2E7D6B; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5; -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        btnAgregar.setOnMouseExited(evt -> btnAgregar.setStyle(
                "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

        // Botón de Ver Bitácora
        Button btnBitacora = new Button("📋 Ver Bitácora");
        btnBitacora.setStyle(
                "-fx-background-color: #E59866; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5;");
        btnBitacora.setCursor(javafx.scene.Cursor.HAND);
        btnBitacora.setOnAction(evt -> mostrarBitacora());

        btnBitacora.setOnMouseEntered(evt -> btnBitacora.setStyle(
                "-fx-background-color: #DC7633; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5; -fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        btnBitacora.setOnMouseExited(evt -> btnBitacora.setStyle(
                "-fx-background-color: #E59866; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5; -fx-scale-x: 1.0; -fx-scale-y: 1.0;"));

        HBox headerContainer = new HBox(15, btnBitacora, btnAgregar);
        headerContainer.setAlignment(Pos.CENTER_RIGHT);
        headerContainer.setPadding(new Insets(0, 0, 10, 0));
        vboxContenedor.getChildren().add(headerContainer);

        // Cargar staff desde BD usando StaffDAO (filtrado por clínica activa)
        List<Staff> listaStaff = com.mycompany.aplicacion.persistencia.StaffDAO.listarPorClinica();

        for (Staff s : listaStaff) {
            // Contenedor principal de la tarjeta
            HBox tarjeta = new HBox(15);
            tarjeta.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
            tarjeta.setPadding(new Insets(15, 20, 15, 20));
            // Estilos CSS integrados
            tarjeta.setStyle(
                    "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");
            tarjeta.getStyleClass().add("tarjeta-blanca");
            tarjeta.setAlignment(Pos.CENTER_LEFT);

            // Sección de Información Principal
            VBox infoPrincipal = new VBox(5);
            Label lblNombre = new Label(s.getNombre() + " " + s.getApellidos());
            lblNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            String extraCedula = (s.getCedula() != null && !s.getCedula().trim().isEmpty())
                    ? " | Cédula: " + s.getCedula()
                    : "";
            Label lblRol = new Label(s.getRol() + " | Especialidad: " + s.getEspecialidad() + extraCedula);
            lblRol.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            infoPrincipal.getChildren().addAll(lblNombre, lblRol);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // Sección de Contacto
            VBox infoContacto = new VBox(5);
            infoContacto.setAlignment(Pos.CENTER_RIGHT);

            Label lblTel = new Label("📞 " + s.getTelefono());
            lblTel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

            Label lblEmail = new Label("✉ " + s.getEmail());
            lblEmail.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

            infoContacto.getChildren().addAll(lblTel, lblEmail);

            // Botón de Editar
            Button btnEditar = new Button("Editar");
            btnEditar.setId("btnEditarStaff");
            btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
            btnEditar.setCursor(javafx.scene.Cursor.HAND);
            btnEditar.setOnAction(e -> mostrarDialogoEditar(s));

            // Botón de Desactivar
            Button btnEliminar = new Button("Desactivar");
            btnEliminar.setId("btnDesactivarStaff");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            btnEliminar.setCursor(javafx.scene.Cursor.HAND);
            btnEliminar.setOnAction(e -> {
                javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirmacion.setTitle("Confirmar desactivación");
                confirmacion.setHeaderText("¿Desactivar usuario?");
                confirmacion.setContentText("El usuario \"" + s.getNombre() + " " + s.getApellidos() +
                        "\" perderá acceso al sistema. Puedes reactivarlo más adelante.");
                java.util.Optional<ButtonType> result = confirmacion.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK)
                    return;

                boolean ok = com.mycompany.aplicacion.persistencia.StaffDAO.desactivar(s.getId());
                Toast.showToast(ok ? "Usuario desactivado correctamente." : "No se pudo desactivar el usuario.", 3);
                cargarPersonalEnPantalla();
            });

            VBox accionesContenedor = new VBox(10, btnEditar, btnEliminar);
            accionesContenedor.setAlignment(Pos.CENTER);
            accionesContenedor.setPadding(new Insets(0, 0, 0, 15));

            // Evitar auto-bloqueo: Ocultar y deshabilitar los botones solo si la fila
            // pertenece al usuario logueado
            String usuarioLogueado = UserSession.getInstance().getUsuario();
            if (s.getUsuario() != null && s.getUsuario().equalsIgnoreCase(usuarioLogueado)) {
                btnEditar.setVisible(false);
                btnEditar.setManaged(false);
                btnEliminar.setVisible(false);
                btnEliminar.setManaged(false);
                accionesContenedor.setVisible(false);
                accionesContenedor.setManaged(false);
            } else {
                // De lo contrario, mantenerlos totalmente visibles y funcionales para otros
                // usuarios
                btnEditar.setVisible(true);
                btnEditar.setManaged(true);
                btnEliminar.setVisible(true);
                btnEliminar.setManaged(true);
                accionesContenedor.setVisible(true);
                accionesContenedor.setManaged(true);
            }

            btnEditarStaff = btnEditar;
            btnDesactivarStaff = btnEliminar;

            tarjeta.getChildren().addAll(infoPrincipal, spacer, infoContacto, accionesContenedor);
            tarjeta.setMinHeight(Region.USE_PREF_SIZE);

            vboxContenedor.getChildren().add(tarjeta);
        }
    }

    private void mostrarDialogoAgregar() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/ModalMiembro.fxml"));
            javafx.scene.Parent root = loader.load();
            ModalMiembroController controller = loader.getController();

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Agregar Miembro de Staff");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initOwner(vboxContenedor.getScene().getWindow());
            stage.showAndWait();

            if (controller.isSuccess()) {
                cargarPersonalEnPantalla();
            }
        } catch (java.io.IOException e) {
            System.err.println("[StaffController] Error al abrir el modal de agregar miembro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarDialogoEditar(Staff staffAEditar) {
        Dialog<Staff> dialog = new Dialog<>();
        dialog.setTitle("Editar Staff");
        dialog.setHeaderText("Modificar los datos del miembro del personal");

        dialog.getDialogPane().setStyle("-fx-background-color: #DFF5E1; -fx-font-family: 'System';");

        ButtonType btnTypeGuardar = new ButtonType("Actualizar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 50, 10, 20));

        String styleTextField = "-fx-background-color: white; -fx-border-color: #3d8d7a; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5px;";
        String styleLabel = "-fx-text-fill: #3d8d7a; -fx-font-weight: bold; -fx-font-size: 14px;";
        String styleError = "-fx-text-fill: #e74c3c; -fx-font-size: 11px;";

        TextField tfNombre = new TextField(staffAEditar.getNombre());
        tfNombre.setStyle(styleTextField);
        Label errNombre = new Label("Debes rellenar este campo");
        errNombre.setStyle(styleError);
        errNombre.setVisible(false);

        TextField tfApellidos = new TextField(staffAEditar.getApellidos());
        tfApellidos.setStyle(styleTextField);
        Label errApellidos = new Label("Debes rellenar este campo");
        errApellidos.setStyle(styleError);
        errApellidos.setVisible(false);

        TextField tfRol = new TextField(staffAEditar.getRol());
        tfRol.setStyle(styleTextField);
        Label errRol = new Label("Debes rellenar este campo");
        errRol.setStyle(styleError);
        errRol.setVisible(false);

        TextField tfCedula = new TextField(staffAEditar.getCedula() != null ? staffAEditar.getCedula() : "");
        tfCedula.setStyle(styleTextField);
        tfCedula.setDisable(!staffAEditar.getRol().trim().equalsIgnoreCase("Veterinario"));
        Label errCedula = new Label("Debes rellenar este campo");
        errCedula.setStyle(styleError);
        errCedula.setVisible(false);

        tfRol.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean isVet = newVal != null && newVal.trim().equalsIgnoreCase("Veterinario");
            tfCedula.setDisable(!isVet);
            if (!isVet) {
                tfCedula.clear();
                errCedula.setVisible(false);
            }
        });

        TextField tfEspecialidad = new TextField(staffAEditar.getEspecialidad());
        tfEspecialidad.setStyle(styleTextField);
        Label errEspecialidad = new Label("Debes rellenar este campo");
        errEspecialidad.setStyle(styleError);
        errEspecialidad.setVisible(false);

        TextField tfTel = new TextField(staffAEditar.getTelefono());
        tfTel.setStyle(styleTextField);
        Label errTel = new Label("Debes rellenar este campo");
        errTel.setStyle(styleError);
        errTel.setVisible(false);

        TextField tfContrasena = new TextField(
                staffAEditar.getContrasenia() != null ? staffAEditar.getContrasenia() : "");
        tfContrasena.setStyle(styleTextField);
        Label errContrasena = new Label("Debes rellenar este campo");
        errContrasena.setStyle(styleError);
        errContrasena.setVisible(false);

        TextField tfEmail = new TextField(staffAEditar.getEmail());
        tfEmail.setStyle(styleTextField);
        Label errEmail = new Label("Debes rellenar este campo");
        errEmail.setStyle(styleError);
        errEmail.setVisible(false);

        TextField tfUsuario = new TextField(staffAEditar.getUsuario() != null ? staffAEditar.getUsuario() : "");
        tfUsuario.setStyle(styleTextField);
        Label errUsuario = new Label("Debes rellenar este campo");
        errUsuario.setStyle(styleError);
        errUsuario.setVisible(false);

        Label lbl1 = new Label("Nombre:");
        lbl1.setStyle(styleLabel);
        Label lbl2 = new Label("Apellidos:");
        lbl2.setStyle(styleLabel);
        Label lbl3 = new Label("Rol:");
        lbl3.setStyle(styleLabel);
        Label lblCedula = new Label("Cédula:");
        lblCedula.setStyle(styleLabel);
        Label lbl4 = new Label("Especialidad:");
        lbl4.setStyle(styleLabel);
        Label lbl5 = new Label("Teléfono:");
        lbl5.setStyle(styleLabel);
        Label lbl6 = new Label("Contraseña:");
        lbl6.setStyle(styleLabel);
        Label lbl7 = new Label("Email:");
        lbl7.setStyle(styleLabel);
        Label lbl8 = new Label("Usuario:");
        lbl8.setStyle(styleLabel);

        grid.add(lbl1, 0, 0);
        grid.add(new VBox(2, tfNombre, errNombre), 1, 0);
        grid.add(lbl2, 0, 1);
        grid.add(new VBox(2, tfApellidos, errApellidos), 1, 1);
        grid.add(lbl3, 0, 2);
        grid.add(new VBox(2, tfRol, errRol), 1, 2);
        grid.add(lblCedula, 0, 3);
        grid.add(new VBox(2, tfCedula, errCedula), 1, 3);
        grid.add(lbl4, 0, 4);
        grid.add(new VBox(2, tfEspecialidad, errEspecialidad), 1, 4);
        grid.add(lbl5, 0, 5);
        grid.add(new VBox(2, tfTel, errTel), 1, 5);
        grid.add(lbl6, 0, 6);
        grid.add(new VBox(2, tfContrasena, errContrasena), 1, 6);
        grid.add(lbl7, 0, 7);
        grid.add(new VBox(2, tfEmail, errEmail), 1, 7);
        grid.add(lbl8, 0, 8);
        grid.add(new VBox(2, tfUsuario, errUsuario), 1, 8);

        dialog.getDialogPane().setContent(grid);

        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnTypeGuardar);
        if (btnGuardarNode != null && btnGuardarNode instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button btnGuardar = (javafx.scene.control.Button) btnGuardarNode;
            btnGuardar.setStyle(
                    "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            btnGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                boolean hasError = false;
                if (tfNombre.getText().trim().isEmpty()) {
                    errNombre.setVisible(true);
                    hasError = true;
                } else
                    errNombre.setVisible(false);
                if (tfApellidos.getText().trim().isEmpty()) {
                    errApellidos.setVisible(true);
                    hasError = true;
                } else
                    errApellidos.setVisible(false);
                if (tfRol.getText().trim().isEmpty()) {
                    errRol.setVisible(true);
                    hasError = true;
                } else
                    errRol.setVisible(false);
                if (tfRol.getText().trim().equalsIgnoreCase("Veterinario") && tfCedula.getText().trim().isEmpty()) {
                    errCedula.setVisible(true);
                    hasError = true;
                } else
                    errCedula.setVisible(false);
                if (tfEspecialidad.getText().trim().isEmpty()) {
                    errEspecialidad.setVisible(true);
                    hasError = true;
                } else
                    errEspecialidad.setVisible(false);
                if (tfTel.getText().trim().isEmpty()) {
                    errTel.setVisible(true);
                    hasError = true;
                } else
                    errTel.setVisible(false);
                if (tfEmail.getText().trim().isEmpty()) {
                    errEmail.setVisible(true);
                    hasError = true;
                } else
                    errEmail.setVisible(false);
                if (tfContrasena.getText().trim().isEmpty()) {
                    errContrasena.setVisible(true);
                    hasError = true;
                } else
                    errContrasena.setVisible(false);
                if (tfUsuario.getText().trim().isEmpty()) {
                    errUsuario.setVisible(true);
                    hasError = true;
                } else
                    errUsuario.setVisible(false);

                if (hasError) {
                    event.consume();
                }
            });
        }

        javafx.scene.Node btnCancelarNode = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        if (btnCancelarNode != null) {
            btnCancelarNode.setStyle(
                    "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        }

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeGuardar) {
                return new Staff(staffAEditar.getId(), tfNombre.getText(), tfApellidos.getText(), tfRol.getText(),
                        tfEspecialidad.getText(), tfTel.getText(), tfEmail.getText(), tfContrasena.getText(),
                        tfCedula.getText(), tfUsuario.getText());
            }
            return null;
        });

        java.util.Optional<Staff> result = dialog.showAndWait();
        result.ifPresent(staffEditado -> {
            boolean ok = com.mycompany.aplicacion.persistencia.StaffDAO.actualizar(
                    staffEditado.getId(),
                    staffEditado.getNombre(),
                    staffEditado.getApellidos(),
                    staffEditado.getRol(),
                    staffEditado.getEspecialidad(),
                    staffEditado.getCedula(),
                    staffEditado.getTelefono(),
                    staffEditado.getEmail());
            if (ok) {
                Toast.showToast("Datos del staff actualizados correctamente.", 3);
            } else {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("No se pudieron guardar los cambios");
                alert.setContentText("Revisa la conexión con la base de datos.");
                alert.showAndWait();
            }
            cargarPersonalEnPantalla();
        });
    }

    private void mostrarBitacora() {
        ModalBitacoraController.abrir(vboxContenedor);
    }

}
