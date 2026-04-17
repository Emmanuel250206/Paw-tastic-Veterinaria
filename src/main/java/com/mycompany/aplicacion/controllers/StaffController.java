package com.mycompany.aplicacion.controllers;



import com.mycompany.aplicacion.modelo.DatosSimulados;
import com.mycompany.aplicacion.modelo.Staff;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
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

public class StaffController implements Initializable {

    @FXML
    private VBox vboxContenedor;

    // ── Perfil header ───────────────────────────────────────────────────
    @FXML private ImageView imgPerfilStaff;
    @FXML private Label     lblNombreStaff;
    @FXML private Label     lblRolStaff;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
            "-fx-padding: 4 0 4 0;"
        );
        Label lbl = new Label("⚙  Configurar Perfil");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setPrefWidth(185);
        String base = "-fx-font-size:13px;-fx-text-fill:#2C3E50;-fx-padding:9 20 9 20;" +
                      "-fx-font-family:'Segoe UI';-fx-background-color:transparent;-fx-background-radius:7;-fx-cursor:hand;";
        String hover = "-fx-font-size:13px;-fx-text-fill:#2E7D6B;-fx-font-weight:bold;-fx-padding:9 20 9 20;" +
                       "-fx-font-family:'Segoe UI';-fx-background-color:#E9F5F2;-fx-background-radius:7;-fx-cursor:hand;";
        lbl.setStyle(base);
        lbl.setOnMouseEntered(e -> lbl.setStyle(hover));
        lbl.setOnMouseExited(e  -> lbl.setStyle(base));
        lbl.setOnMouseClicked(e -> { menuPerfil.hide(); ConfigurarPerfilController.abrir(hboxPerfil); });
        CustomMenuItem item = new CustomMenuItem(lbl, true);
        item.setMnemonicParsing(false);
        menuPerfil.getItems().add(item);
    }

    @FXML
    private void manejarClickPerfil(MouseEvent event) {
        if (menuPerfil == null) return;
        if (menuPerfil.isShowing()) { menuPerfil.hide(); return; }
        menuPerfil.show(hboxPerfil, Side.BOTTOM, hboxPerfil.getWidth() - 185, 4);
    }

    private void cargarPersonalEnPantalla() {
        // Validación de seguridad por si el VBox no está enlazado correctamente
        if (vboxContenedor == null) {
            System.out.println("ADVERTENCIA: vboxContenedor es null. Verifica el fx:id en el Scene Builder.");
            return;
        }

        vboxContenedor.getChildren().clear();
        vboxContenedor.setSpacing(15);
        vboxContenedor.setPadding(new Insets(20));

        // Botón superior de Agregar
        Button btnAgregar = new Button("+ Agregar Miembro");
        btnAgregar.setStyle(
                "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20 10 20; -fx-background-radius: 5;");
        btnAgregar.setCursor(javafx.scene.Cursor.HAND);
        btnAgregar.setOnAction(e -> mostrarDialogoAgregar());

        // HBox para alinear el botón de agregar a la derecha
        HBox headerContainer = new HBox(btnAgregar);
        headerContainer.setAlignment(Pos.CENTER_RIGHT);
        headerContainer.setPadding(new Insets(0, 0, 10, 0));
        vboxContenedor.getChildren().add(headerContainer);

        ObservableList<Staff> listaStaff = DatosSimulados.getPersonal();

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

            Label lblRol = new Label(s.getRol() + " | Especialidad: " + s.getEspecialidad());
            lblRol.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            Label lblTurno = new Label("Turno: " + s.getTurno());
            lblTurno.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-font-style: italic;");

            infoPrincipal.getChildren().addAll(lblNombre, lblRol, lblTurno);

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

            // Botón de Eliminar
            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            btnEliminar.setCursor(javafx.scene.Cursor.HAND);
            btnEliminar.setOnAction(e -> {
                DatosSimulados.getPersonal().remove(s);
                cargarPersonalEnPantalla(); // Recargar visualmente
            });

            VBox accionesContenedor = new VBox(btnEliminar);
            accionesContenedor.setAlignment(Pos.CENTER);
            accionesContenedor.setPadding(new Insets(0, 0, 0, 15));

            tarjeta.getChildren().addAll(infoPrincipal, spacer, infoContacto, accionesContenedor);
            tarjeta.setMinHeight(Region.USE_PREF_SIZE);

            vboxContenedor.getChildren().add(tarjeta);
        }
    }


    private void mostrarDialogoAgregar() {
        Dialog<Staff> dialog = new Dialog<>();
        dialog.setTitle("Agregar Nuevo Staff");
        dialog.setHeaderText("Introduce los datos del nuevo miembro del personal");

        // Estilizar el Panel principal del Dialogo
        dialog.getDialogPane().setStyle("-fx-background-color: #DFF5E1; -fx-font-family: 'System';");

        ButtonType btnTypeGuardar = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 50, 10, 20));

        String styleTextField = "-fx-background-color: white; -fx-border-color: #3d8d7a; -fx-border-radius: 5; -fx-background-radius: 5; -fx-padding: 5px;";
        String styleLabel = "-fx-text-fill: #3d8d7a; -fx-font-weight: bold; -fx-font-size: 14px;";

        String styleError = "-fx-text-fill: #e74c3c; -fx-font-size: 11px;";

        TextField tfNombre = new TextField();
        tfNombre.setPromptText("Nombre");
        tfNombre.setStyle(styleTextField);
        Label errNombre = new Label("Debes rellenar este campo");
        errNombre.setStyle(styleError);
        errNombre.setVisible(false);

        TextField tfApellidos = new TextField();
        tfApellidos.setPromptText("Apellidos");
        tfApellidos.setStyle(styleTextField);
        Label errApellidos = new Label("Debes rellenar este campo");
        errApellidos.setStyle(styleError);
        errApellidos.setVisible(false);

        TextField tfRol = new TextField();
        tfRol.setPromptText("Rol (ej. Veterinario)");
        tfRol.setStyle(styleTextField);
        Label errRol = new Label("Debes rellenar este campo");
        errRol.setStyle(styleError);
        errRol.setVisible(false);

        TextField tfEspecialidad = new TextField();
        tfEspecialidad.setPromptText("Especialidad");
        tfEspecialidad.setStyle(styleTextField);
        Label errEspecialidad = new Label("Debes rellenar este campo");
        errEspecialidad.setStyle(styleError);
        errEspecialidad.setVisible(false);

        TextField tfTel = new TextField();
        tfTel.setPromptText("Teléfono");
        tfTel.setStyle(styleTextField);
        Label errTel = new Label("Debes rellenar este campo");
        errTel.setStyle(styleError);
        errTel.setVisible(false);

        Label lbl1 = new Label("Nombre:");
        lbl1.setStyle(styleLabel);
        Label lbl2 = new Label("Apellidos:");
        lbl2.setStyle(styleLabel);
        Label lbl3 = new Label("Rol:");
        lbl3.setStyle(styleLabel);
        Label lbl4 = new Label("Especialidad:");
        lbl4.setStyle(styleLabel);
        Label lbl5 = new Label("Teléfono:");
        lbl5.setStyle(styleLabel);

        grid.add(lbl1, 0, 0);
        grid.add(new VBox(2, tfNombre, errNombre), 1, 0);
        grid.add(lbl2, 0, 1);
        grid.add(new VBox(2, tfApellidos, errApellidos), 1, 1);
        grid.add(lbl3, 0, 2);
        grid.add(new VBox(2, tfRol, errRol), 1, 2);
        grid.add(lbl4, 0, 3);
        grid.add(new VBox(2, tfEspecialidad, errEspecialidad), 1, 3);
        grid.add(lbl5, 0, 4);
        grid.add(new VBox(2, tfTel, errTel), 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Estilizar los botones del dialogo nativo usando lookup
        javafx.scene.Node btnGuardarNode = dialog.getDialogPane().lookupButton(btnTypeGuardar);
        if (btnGuardarNode != null && btnGuardarNode instanceof javafx.scene.control.Button) {
            javafx.scene.control.Button btnGuardar = (javafx.scene.control.Button) btnGuardarNode;
            btnGuardar.setStyle(
                    "-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            // FILTRO DE EVENTOS (VALIDACIÓN)
            btnGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                boolean hasError = false;

                if (tfNombre.getText().trim().isEmpty()) {
                    errNombre.setVisible(true);
                    hasError = true;
                } else errNombre.setVisible(false);

                if (tfApellidos.getText().trim().isEmpty()) {
                    errApellidos.setVisible(true);
                    hasError = true;
                } else errApellidos.setVisible(false);

                if (tfRol.getText().trim().isEmpty()) {
                    errRol.setVisible(true);
                    hasError = true;
                } else errRol.setVisible(false);

                if (tfEspecialidad.getText().trim().isEmpty()) {
                    errEspecialidad.setVisible(true);
                    hasError = true;
                } else errEspecialidad.setVisible(false);

                if (tfTel.getText().trim().isEmpty()) {
                    errTel.setVisible(true);
                    hasError = true;
                } else errTel.setVisible(false);

                if (hasError) {
                    event.consume(); // Cancela que se cierre la ventana
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
                int nuevoId = DatosSimulados.getPersonal().size() + 1;
                return new Staff(nuevoId, tfNombre.getText(), tfApellidos.getText(), tfRol.getText(),
                        tfEspecialidad.getText(), tfTel.getText(), "nuevo@vet.com", "Matutino");
            }
            return null;
        });

        java.util.Optional<Staff> result = dialog.showAndWait();
        result.ifPresent(nuevoStaff -> {
            DatosSimulados.getPersonal().add(nuevoStaff);
            cargarPersonalEnPantalla(); // Recargar visualmente
        });
    }
}
