package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.Citas.Prioridad;
import com.mycompany.aplicacion.modelo.DatosSimulados;

import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.modelo.Mascota;

public class CitasStaffController implements Initializable {

    @FXML private TableView<Citas>              tablaColaCitas;
    @FXML private TableColumn<Citas, String>    colHora;
    @FXML private TableColumn<Citas, String>    colPaciente;
    @FXML private TableColumn<Citas, String>    colDueno;
    @FXML private TableColumn<Citas, String>    colMotivo;
    @FXML private TableColumn<Citas, Prioridad> colPrioridad;
    @FXML private Label                         lblContadorCitas;

    // ── Perfil header ─────────────────────────────────────────────────────────
    @FXML private ImageView imgPerfilCitas;
    @FXML private Label     lblNombreCitas;
    @FXML private Label     lblRolCitas;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    private static final Comparator<Citas> COMPARADOR_PRIORIDAD =
        Comparator.comparing(Citas::getPrioridad).thenComparing(Citas::getHora);

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        UserSession.loadProfileImage(imgPerfilCitas);
        lblNombreCitas.setText(UserSession.getInstance().getUserName());
        lblRolCitas.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        configurarTabla();
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
        lbl.setOnMouseClicked(e -> { menuPerfil.hide(); ConfigurarPerfilController.abrir(hboxPerfil, () -> {
            UserSession.loadProfileImage(imgPerfilCitas);
            lblNombreCitas.setText(UserSession.getInstance().getUserName());
            lblRolCitas.setText(UserSession.getInstance().getUserRole());
        }); });
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

    private void configurarTabla() {
        colHora.setCellValueFactory(cell -> 
            new javafx.beans.property.SimpleStringProperty(cell.getValue().getFecha() + " " + cell.getValue().getHora())
        );
        colHora.setText("Fecha y Hora");

        colPaciente.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
        colDueno.setCellValueFactory(new PropertyValueFactory<>("nombrePropietario"));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));

        // Columna Prioridad con badge visual
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colPrioridad.setCellFactory(col -> new TableCell<Citas, Prioridad>() {
            @Override
            protected void updateItem(Prioridad item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null); setStyle("");
                } else {
                    Label badge = new Label(item.name());
                    if (item == Prioridad.URGENTE) {
                        badge.setStyle(
                            "-fx-background-color: #e74c3c;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 3 8 3 8;");
                    } else {
                        badge.setStyle(
                            "-fx-background-color: #3d8d7a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 11px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 3 8 3 8;");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        ObservableList<Citas> datos = DatosSimulados.getCitas();
        SortedList<Citas> sorted = new SortedList<>(datos, COMPARADOR_PRIORIDAD);
        tablaColaCitas.setItems(sorted);
        lblContadorCitas.setText(datos.size() + " citas");

        tablaColaCitas.setRowFactory(tv -> {
            TableRow<Citas> row = new TableRow<>();
            
            // Re-evaluar estilo cuando cambie el item (por ejemplo al cargar la tabla)
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null && newItem.esUrgente()) {
                    if (!row.isSelected()) {
                        row.setStyle("-fx-background-color: #ffebee;");
                    }
                } else {
                    if (!row.isSelected()) {
                        row.setStyle("");
                    }
                }
            });

            // Re-evaluar estilo cuando cambie la selección
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    row.setStyle(""); // El CSS de JavaFX se encarga de poner el fondo azul y texto blanco
                } else {
                    Citas item = row.getItem();
                    if (item != null && item.esUrgente()) {
                        row.setStyle("-fx-background-color: #ffebee;");
                    } else {
                        row.setStyle("");
                    }
                }
            });

            return row;
        });
    }

    private void actualizarHorasDisponibles(DatePicker dpFecha, ComboBox<String> cbHora, String horaActualIgnorada) {
        cbHora.getItems().clear();
        String[] todasLasHoras = {
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
            "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", 
            "15:00", "15:30", "16:00", "16:30", "17:00", "17:30", "18:00"
        };
        
        LocalDate fecha = dpFecha.getValue();
        if (fecha == null) {
            cbHora.getItems().addAll(todasLasHoras);
            return;
        }

        String fechaStr = fecha.toString();
        java.util.List<String> horasOcupadas = new java.util.ArrayList<>();
        for (Citas c : DatosSimulados.getCitas()) {
            if (c.getFecha().equals(fechaStr) && c.getPrioridad() != Prioridad.URGENTE) {
                if (horaActualIgnorada == null || !c.getHora().equals(horaActualIgnorada)) {
                    horasOcupadas.add(c.getHora());
                }
            }
        }

        for (String h : todasLasHoras) {
            if (!horasOcupadas.contains(h)) {
                cbHora.getItems().add(h);
            }
        }
        if (!cbHora.getItems().isEmpty()) {
            cbHora.getSelectionModel().selectFirst();
        }
    }

    private void aplicarEstiloDialogo(Dialog<?> dialog) {
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/fxml/estilos.css").toExternalForm());
        dialog.getDialogPane().setStyle("-fx-background-color: #F0F5F2; -fx-font-family: 'Segoe UI';");
        dialog.getDialogPane().lookup(".header-panel").setStyle("-fx-background-color: #3d8d7a;");
        dialog.getDialogPane().lookup(".header-panel .label").setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px;");
    }

    @FXML
    private void agendarCita(ActionEvent event) {
        Dialog<Citas> dialog = new Dialog<>();
        dialog.setTitle("Agendar Cita");
        dialog.setHeaderText("Programar nueva cita o registrar urgencia");
        aplicarEstiloDialogo(dialog);

        ButtonType btnTypeGuardar = new ButtonType("Guardar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);
        ((Button) dialog.getDialogPane().lookupButton(btnTypeGuardar)).setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        // UI Components
        ComboBox<String> cbModo = new ComboBox<>();
        cbModo.getItems().addAll("Mascota Registrada", "Mascota Nueva");
        cbModo.setValue("Mascota Registrada");

        // Mascota Registrada
        TextField tfBuscarNombre = new TextField(); tfBuscarNombre.setPromptText("Buscar nombre...");
        ListView<Mascota> lvMascotas = new ListView<>(); lvMascotas.setPrefHeight(100); lvMascotas.setVisible(false); lvMascotas.setManaged(false);

        // Mascota Nueva
        TextField tfRaza = new TextField(); tfRaza.setPromptText("Raza");
        TextField tfEspecie = new TextField(); tfEspecie.setPromptText("Especie");
        TextField tfNombreNueva = new TextField(); tfNombreNueva.setPromptText("Nombre de mascota");
        TextField tfEdad = new TextField(); tfEdad.setPromptText("Edad");
        TextField tfDescripcion = new TextField(); tfDescripcion.setPromptText("Descripción");

        // Shared
        TextField tfDueno = new TextField(); tfDueno.setPromptText("Dueño");
        TextField tfTelefono = new TextField(); tfTelefono.setPromptText("Teléfono");

        // Cita details
        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Revisión", "Vacunación", "Estética", "Urgente", "Otro");
        cbTipo.setValue("Revisión");
        TextField tfOtroMotivo = new TextField(); tfOtroMotivo.setPromptText("Motivo...");
        tfOtroMotivo.setVisible(false); tfOtroMotivo.setManaged(false);

        cbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtro = "Otro".equals(newVal);
            tfOtroMotivo.setVisible(esOtro);
            tfOtroMotivo.setManaged(esOtro);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        DatePicker dpFecha = new DatePicker(LocalDate.now());
        ComboBox<String> cbHora = new ComboBox<>();
        actualizarHorasDisponibles(dpFecha, cbHora, null);
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> actualizarHorasDisponibles(dpFecha, cbHora, null));

        // Logic for Buscar Mascota
        tfBuscarNombre.textProperty().addListener((obs, oldV, newV) -> {
            lvMascotas.getItems().clear();
            if (newV.isEmpty()) {
                lvMascotas.setVisible(false);
                lvMascotas.setManaged(false);
                tfDueno.setText(""); tfTelefono.setText("");
            } else {
                java.util.List<Mascota> matches = DatosSimulados.getMascotas().stream()
                        .filter(m -> m.getNombre().toLowerCase().contains(newV.toLowerCase()))
                        .collect(Collectors.toList());
                lvMascotas.getItems().addAll(matches);
                boolean show = !matches.isEmpty();
                lvMascotas.setVisible(show); lvMascotas.setManaged(show);
            }
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        lvMascotas.setOnMouseClicked(e -> {
            Mascota m = lvMascotas.getSelectionModel().getSelectedItem();
            if (m != null) {
                tfBuscarNombre.setText(m.getNombre());
                tfDueno.setText(m.getNombrePropietario());
                tfTelefono.setText(m.getTelefonoPropietario() != null ? m.getTelefonoPropietario() : "");
                lvMascotas.setVisible(false);
                lvMascotas.setManaged(false);
                dialog.getDialogPane().getScene().getWindow().sizeToScene();
            }
        });

        // Dynamic Layout
        GridPane gridDynamic = new GridPane();
        gridDynamic.setHgap(10); gridDynamic.setVgap(15);

        Runnable buildDynamic = () -> {
            gridDynamic.getChildren().clear();
            if ("Mascota Registrada".equals(cbModo.getValue())) {
                gridDynamic.add(new Label("Buscar:"), 0, 0);
                VBox boxBuscar = new VBox(5, tfBuscarNombre, lvMascotas);
                gridDynamic.add(boxBuscar, 1, 0);
                gridDynamic.add(new Label("Dueño:"), 0, 1);
                gridDynamic.add(tfDueno, 1, 1);
                gridDynamic.add(new Label("Teléfono:"), 0, 2);
                gridDynamic.add(tfTelefono, 1, 2);
            } else {
                gridDynamic.add(new Label("Nombre:"), 0, 0); gridDynamic.add(tfNombreNueva, 1, 0);
                gridDynamic.add(new Label("Especie:"), 0, 1); gridDynamic.add(tfEspecie, 1, 1);
                gridDynamic.add(new Label("Raza:"), 0, 2); gridDynamic.add(tfRaza, 1, 2);
                gridDynamic.add(new Label("Edad:"), 0, 3); gridDynamic.add(tfEdad, 1, 3);
                gridDynamic.add(new Label("Descripción:"), 0, 4); gridDynamic.add(tfDescripcion, 1, 4);
                gridDynamic.add(new Label("Dueño:"), 0, 5); gridDynamic.add(tfDueno, 1, 5);
                gridDynamic.add(new Label("Teléfono:"), 0, 6); gridDynamic.add(tfTelefono, 1, 6);
            }
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        };

        cbModo.valueProperty().addListener((obs, oldV, newV) -> buildDynamic.run());
        buildDynamic.run();

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20, 20, 10, 10));

        GridPane staticGrid = new GridPane();
        staticGrid.setHgap(10); staticGrid.setVgap(15);
        staticGrid.add(new Label("Tipo de Cita:"), 0, 0);
        VBox tipoBox = new VBox(5, cbTipo, tfOtroMotivo);
        staticGrid.add(tipoBox, 1, 0);
        staticGrid.add(new Label("Fecha:"), 0, 1);
        staticGrid.add(dpFecha, 1, 1);
        staticGrid.add(new Label("Hora:"), 0, 2);
        staticGrid.add(cbHora, 1, 2);

        contentBox.getChildren().addAll(new HBox(10, new Label("Modo:"), cbModo), gridDynamic, staticGrid);
        dialog.getDialogPane().setContent(contentBox);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeGuardar) {
                String mascota;
                if ("Mascota Nueva".equals(cbModo.getValue())) {
                    mascota = tfNombreNueva.getText().trim();
                    if (!mascota.isEmpty() && !tfDueno.getText().trim().isEmpty()) {
                        int mId = DatosSimulados.getMascotas().size() + 1;
                        int edadInt = 0;
                        try { edadInt = Integer.parseInt(tfEdad.getText().trim()); } catch (NumberFormatException ignored) {}
                        DatosSimulados.getMascotas().add(new Mascota(mId, mascota, tfEspecie.getText().trim(), tfRaza.getText().trim(), edadInt, "Sin collar", tfDueno.getText().trim(), tfTelefono.getText().trim(), "Sin dirección", tfDescripcion.getText().trim()));
                    }
                } else {
                    mascota = tfBuscarNombre.getText().trim();
                }
                
                String dueno = tfDueno.getText().trim();
                String tel = tfTelefono.getText().trim();
                String tipo = cbTipo.getValue();
                if ("Otro".equals(tipo)) {
                    tipo = tfOtroMotivo.getText().trim();
                    if (tipo.isEmpty()) tipo = "Otro";
                }
                String fechaStr = dpFecha.getValue() != null ? dpFecha.getValue().toString() : "";
                String horaStr = cbHora.getValue();

                if (mascota.isEmpty() || dueno.isEmpty()) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Datos incompletos", "Debe ingresar mascota y dueño.");
                    return null;
                }

                Prioridad prio = "Urgente".equals(tipo) ? Prioridad.URGENTE : Prioridad.NORMAL;

                int nuevoId = DatosSimulados.getCitas().size() + 1;
                return new Citas(nuevoId, fechaStr, horaStr, mascota, dueno, tel, "Pendiente", tipo, "Programada", prio);
            }
            return null;
        });

        java.util.Optional<Citas> result = dialog.showAndWait();
        result.ifPresent(nuevaCita -> {
            DatosSimulados.getCitas().add(nuevaCita);
            lblContadorCitas.setText(DatosSimulados.getCitas().size() + " citas");
            tablaColaCitas.refresh();
        });
    }

    @FXML
    private void editarCita(ActionEvent event) {
        Citas seleccionada = tablaColaCitas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Ninguna cita seleccionada", "Por favor, seleccione una cita de la cola para editar.");
            return;
        }

        Dialog<Citas> dialog = new Dialog<>();
        dialog.setTitle("Editar Cita");
        dialog.setHeaderText("Modificar datos de la cita");
        aplicarEstiloDialogo(dialog);

        ButtonType btnTypeGuardar = new ButtonType("Actualizar", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeGuardar, ButtonType.CANCEL);
        ((Button) dialog.getDialogPane().lookupButton(btnTypeGuardar)).setStyle("-fx-background-color: #3d8d7a; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 10, 10));

        TextField tfMascota = new TextField(seleccionada.getNombreMascota());
        tfMascota.setPrefWidth(200);
        TextField tfDueno = new TextField(seleccionada.getNombrePropietario());
        TextField tfTelefono = new TextField(seleccionada.getTelefonoDueno() != null ? seleccionada.getTelefonoDueno() : "");

        ComboBox<String> cbTipo = new ComboBox<>();
        cbTipo.getItems().addAll("Revisión", "Vacunación", "Estética", "Urgente", "Otro");
        
        TextField tfOtroMotivo = new TextField();
        tfOtroMotivo.setPromptText("Especifique el motivo...");
        
        String m = seleccionada.getMotivo();
        if (cbTipo.getItems().contains(m)) {
            cbTipo.setValue(m);
            tfOtroMotivo.setVisible(false);
            tfOtroMotivo.setManaged(false);
        } else {
            cbTipo.setValue("Otro");
            tfOtroMotivo.setText(m);
            tfOtroMotivo.setVisible(true);
            tfOtroMotivo.setManaged(true);
        }

        cbTipo.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean esOtro = "Otro".equals(newVal);
            tfOtroMotivo.setVisible(esOtro);
            tfOtroMotivo.setManaged(esOtro);
            dialog.getDialogPane().getScene().getWindow().sizeToScene();
        });

        DatePicker dpFecha = new DatePicker();
        try {
            dpFecha.setValue(LocalDate.parse(seleccionada.getFecha()));
        } catch(Exception e) {
            dpFecha.setValue(LocalDate.now());
        }

        ComboBox<String> cbHora = new ComboBox<>();
        actualizarHorasDisponibles(dpFecha, cbHora, seleccionada.getHora());
        cbHora.setValue(seleccionada.getHora());
        
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            actualizarHorasDisponibles(dpFecha, cbHora, null);
        });

        grid.add(new Label("Mascota:"), 0, 0);
        grid.add(tfMascota, 1, 0);
        grid.add(new Label("Dueño:"), 0, 1);
        grid.add(tfDueno, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(tfTelefono, 1, 2);
        grid.add(new Label("Tipo:"), 0, 3);
        VBox tipoBox = new VBox(5, cbTipo, tfOtroMotivo);
        grid.add(tipoBox, 1, 3);
        grid.add(new Label("Fecha:"), 0, 4);
        grid.add(dpFecha, 1, 4);
        grid.add(new Label("Hora:"), 0, 5);
        grid.add(cbHora, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnTypeGuardar) {
                seleccionada.setNombreMascota(tfMascota.getText().trim());
                seleccionada.setNombrePropietario(tfDueno.getText().trim());
                seleccionada.setTelefonoDueno(tfTelefono.getText().trim());
                
                String tipo = cbTipo.getValue();
                if ("Otro".equals(tipo)) {
                    tipo = tfOtroMotivo.getText().trim();
                    if (tipo.isEmpty()) tipo = "Otro";
                }
                seleccionada.setMotivo(tipo);
                
                if (dpFecha.getValue() != null) seleccionada.setFecha(dpFecha.getValue().toString());
                if (cbHora.getValue() != null) seleccionada.setHora(cbHora.getValue());
                
                Prioridad prio = "Urgente".equals(tipo) ? Prioridad.URGENTE : Prioridad.NORMAL;
                seleccionada.setPrioridad(prio);
                return seleccionada;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(cita -> {
            tablaColaCitas.refresh();
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String header, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }
}
