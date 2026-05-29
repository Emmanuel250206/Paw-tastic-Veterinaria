package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.Citas.Prioridad;
import com.mycompany.aplicacion.persistencia.CitasDAO;

import javafx.collections.FXCollections;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import com.mycompany.aplicacion.modelo.UserSession;

/**
 * Controlador del módulo Citas.
 *
 * Flujo: Programada → En Consulta → Finalizada (auto-guarda reporte con fecha actual).
 * "Completada" o "Finalizada" son estados terminales — no se pueden reabrir.
 * El historial soporta filtrado por rango de fechas y detalle expandido al hacer clic.
 */
public class CitasController implements Initializable {

    // ── Lateral izquierdo ─────────────────────────────────────────────────────
    @FXML private TableView<Citas>              tablaColaCitas;
    @FXML private TableColumn<Citas, String>    colHora;
    @FXML private TableColumn<Citas, String>    colPaciente;
    @FXML private TableColumn<Citas, Prioridad> colPrioridad;
    @FXML private Label                         lblContadorCitas;

    // ── Panel Consulta Activa ─────────────────────────────────────────────────
    @FXML private Label    lblNombrePaciente;
    @FXML private Label    lblMotivoConsulta;
    @FXML private Label    lblHoraCita;
    @FXML private Label    lblEstadoCita;
    @FXML private HBox     hboxSignosVitales;
    @FXML private TextArea txtReporteCita;
    @FXML private Button   bAccionCita;
    @FXML private Button   btnCancelarCita;

    // ── Historial ─────────────────────────────────────────────────────────────
    @FXML private VBox       vboxHistorialContent;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;

    // ── Perfil header ─────────────────────────────────────────────────────────
    @FXML private ImageView imgPerfilCitas;
    @FXML private Label     lblNombreCitas;
    @FXML private Label     lblRolCitas;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    // ── Signos Vitales (inyectados por código) ────────────────────────────────
    private TextField txtTemp;
    private TextField txtFreqCard;
    private TextField txtFreqResp;

    // ── Estado ────────────────────────────────────────────────────────────────
    private Citas citaSeleccionada;

    /** Lista maestra que alimenta la tabla (se actualiza con setAll para no romper el listener). */
    private final ObservableList<Citas> masterList = FXCollections.observableArrayList();

    /** Lista maestra de reportes guardados para filtrado. */
    private final List<ReporteLocal> listaReportes = new ArrayList<>();

    // ── Constantes ────────────────────────────────────────────────────────────
    private static final Comparator<Citas> COMPARADOR_PRIORIDAD =
        Comparator.comparing(Citas::getPrioridad).thenComparing(Citas::getHora);

    private static final DateTimeFormatter FMT_DISPLAY =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilCitas);
        lblNombreCitas.setText(UserSession.getInstance().getUserName());
        lblRolCitas.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        configurarSignosVitales();
        configurarTabla();
        bloquearPanelCentral();
        cargarHistorialDesdeBD();
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

    private void configurarSignosVitales() {
        txtTemp     = crearCampoSigno("Temp. (°C)");
        txtFreqCard = crearCampoSigno("FC (ppm)");
        txtFreqResp = crearCampoSigno("FR (rpm)");
        hboxSignosVitales.getChildren().addAll(txtTemp, txtFreqCard, txtFreqResp);
    }

    private TextField crearCampoSigno(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setDisable(true);
        tf.setPrefWidth(115);
        tf.setMaxWidth(130);
        tf.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #3d8d7a;" +
            "-fx-border-width: 0 0 2 0;" +
            "-fx-border-radius: 5;" +
            "-fx-background-radius: 5;" +
            "-fx-font-size: 12px;");

        // Solo acepta dígitos y un punto decimal (ej. 38.5)
        tf.setTextFormatter(new TextFormatter<>(change -> {
            String nuevo = change.getControlNewText();
            return nuevo.matches("\\d*\\.?\\d*") ? change : null;
        }));

        return tf;
    }

    private void configurarTabla() {
        String globalTextStyle = "-fx-text-fill: black !important; -fx-font-weight: 600; -fx-font-smoothing-type: gray;";
        String urgentRowStyle = "-fx-background-color: #FDEDEC !important; -fx-border-color: transparent transparent #F5B7B1 transparent; -fx-border-width: 0 0 1 0;";
        String urgentRowSelectedStyle = "-fx-background-color: #F1948A !important; -fx-border-color: #CB4335 !important; -fx-border-width: 1 !important;";
        String inCursoRowStyle = "-fx-background-color: #E8F8F5 !important; -fx-border-color: transparent transparent #A3E4D7 transparent; -fx-border-width: 0 0 1 0;";
        String inCursoRowSelectedStyle = "-fx-background-color: #76D7C4 !important; -fx-border-color: #117864 !important; -fx-border-width: 1 !important;";

        // Columna Hora: Forzar texto negro GLOBAL
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colHora.setCellFactory(col -> new TableCell<Citas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    setStyle(globalTextStyle);
                }
            }
        });

        // Columna Paciente: Forzar texto negro GLOBAL
        colPaciente.setCellValueFactory(new PropertyValueFactory<>("nombreMascota"));
        colPaciente.setCellFactory(col -> new TableCell<Citas, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setStyle("");
                } else {
                    setText(item);
                    setStyle(globalTextStyle);
                }
            }
        });

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
                            "-fx-font-size: 10px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 2 6 2 6;");
                    } else {
                        badge.setStyle(
                            "-fx-background-color: #3d8d7a;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 10px;" +
                            "-fx-background-radius: 8;" +
                            "-fx-padding: 2 6 2 6;");
                    }
                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        // Enlazar la tabla a la masterList (una sola vez, nunca se cambia el items)
        SortedList<Citas> sorted = new SortedList<>(masterList, COMPARADOR_PRIORIDAD);
        tablaColaCitas.setItems(sorted);

        // ── REGISTRAR el listener ANTES de cargar datos ──────────────────────
        // Si se registra después del primer select(0), la selección inicial no dispara el panel
        tablaColaCitas.getSelectionModel().selectedItemProperty()
            .addListener((obs, old, nueva) -> {
                if (nueva != null) cargarCitaEnPantalla(nueva);
            });

        // Carga inicial desde BD (ahora el listener ya está activo)
        refreshTable();

        // Filas URGENTE o EN CURSO: Forzado de Estilos con Selección Contrastada
        tablaColaCitas.setRowFactory(tv -> {
            TableRow<Citas> row = new TableRow<Citas>() {
                @Override
                protected void updateItem(Citas item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty && item != null) {
                        if (item.esUrgente()) {
                            setStyle(isSelected() ? urgentRowSelectedStyle : urgentRowStyle);
                        } else if ("En curso".equalsIgnoreCase(item.getEstado())) {
                            setStyle(isSelected() ? inCursoRowSelectedStyle : inCursoRowStyle);
                        } else {
                            setStyle("");
                        }
                    } else {
                        setStyle("");
                    }
                }
            };

            // Selection Lock: Cambiar tono al seleccionar
            row.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                Citas item = row.getItem();
                if (item != null) {
                    if (item.esUrgente()) {
                        row.setStyle(isNowSelected ? urgentRowSelectedStyle : urgentRowStyle);
                    } else if ("En curso".equalsIgnoreCase(item.getEstado())) {
                        row.setStyle(isNowSelected ? inCursoRowSelectedStyle : inCursoRowStyle);
                    }
                }
            });

            return row;
        });
    }

    /** Recarga la cola de citas del día desde la BD sin romper el listener de selección. */
    public void refreshTable() {
        Citas selActual = tablaColaCitas.getSelectionModel().getSelectedItem();
        ObservableList<Citas> datos = CitasDAO.getCitasHoy();
        masterList.setAll(datos);          // actualiza en-place, el SortedList re-ordena solo
        lblContadorCitas.setText(masterList.size() + " citas");
        tablaColaCitas.refresh();
        // Restaurar selección si aún existe, o seleccionar la primera
        if (selActual != null) {
            for (Citas c : tablaColaCitas.getItems()) {
                if (c.getId() == selActual.getId()) {
                    tablaColaCitas.getSelectionModel().select(c);
                    return;
                }
            }
        }
        if (!tablaColaCitas.getItems().isEmpty()) {
            tablaColaCitas.getSelectionModel().select(0);
        }
    }

    // ══ LÓGICA DE CONSULTA ═══════════════════════════════════════════════════

    public void cargarCitaEnPantalla(Citas cita) {
        this.citaSeleccionada = cita;
        lblNombrePaciente.setText(cita.getNombreMascota());
        lblMotivoConsulta.setText(cita.getMotivo());
        lblHoraCita.setText(cita.getHora());
        lblEstadoCita.setText(cita.getEstado());
        txtReporteCita.clear();

        // Cargar Signos Vitales del Triaje
        if (cita.getTemperatura() != null && cita.getTemperatura() > 0) {
            txtTemp.setText(String.valueOf(cita.getTemperatura()));
        } else {
            txtTemp.clear();
        }

        if (cita.getFrecuenciaCardiaca() != null && cita.getFrecuenciaCardiaca() > 0) {
            txtFreqCard.setText(String.valueOf(cita.getFrecuenciaCardiaca()));
        } else {
            txtFreqCard.clear();
        }

        if (cita.getFrecuenciaRespiratoria() != null && cita.getFrecuenciaRespiratoria() > 0) {
            txtFreqResp.setText(String.valueOf(cita.getFrecuenciaRespiratoria()));
        } else {
            txtFreqResp.clear();
        }

        actualizarBotonEstado();
    }

    private void bloquearPanelCentral() {
        lblNombrePaciente.setText("— Selecciona una cita —");
        lblMotivoConsulta.setText("—");
        lblHoraCita.setText("—");
        lblEstadoCita.setText("—");
        txtReporteCita.setDisable(true);
        txtReporteCita.setPromptText("Selecciona una cita para habilitar el editor...");
        bAccionCita.setDisable(true);
        btnCancelarCita.setDisable(true);
        setSignosVitalesDisable(true);
    }

    @FXML
    private void manejarAccionCita(ActionEvent event) {
        if (citaSeleccionada == null) return;

        String estado = citaSeleccionada.getEstado();

        if (estaTerminada(estado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, null,
                "Esta cita ya fue finalizada y no puede modificarse.");
            return;
        }

        // Estado "En curso" = consulta activa. Finalizar → Completada (ID 16)
        if (estado.equalsIgnoreCase("En curso")) {
            boolean ok = guardarReporteEnBD();
            citaSeleccionada.setEstado("Completada");
            CitasDAO.cambiarEstadoCita(citaSeleccionada.getId(), CitasDAO.STATE_COMPLETADA);
            cargarCitaEnPantalla(citaSeleccionada);
            cargarHistorialDesdeBD();
            mostrarAlerta(Alert.AlertType.INFORMATION,
                "Consulta Finalizada",
                ok ? "La consulta terminó y el diagnóstico fue guardado."
                   : "La consulta terminó, pero no se pudieron guardar las notas.");
        } else {
            // Pendiente, Confirmada, etc. → Iniciar consulta (En curso = ID 15)
            citaSeleccionada.setEstado("En curso");
            CitasDAO.cambiarEstadoCita(citaSeleccionada.getId(), CitasDAO.STATE_EN_CURSO);
            cargarCitaEnPantalla(citaSeleccionada);
        }

        tablaColaCitas.refresh();
    }

    /**
     * Cancela la cita seleccionada tras pedir confirmación y motivo.
     * Actualiza el estado en la tabla y registra un evento en el historial.
     */
    @FXML
    private void cancelarCita(ActionEvent event) {
        if (citaSeleccionada == null) return;

        String estado = citaSeleccionada.getEstado();
        if (estaTerminada(estado)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, null,
                "Esta cita ya fue finalizada y no puede cancelarse.");
            return;
        }
        if (estado.equalsIgnoreCase("Cancelada")) {
            mostrarAlerta(Alert.AlertType.INFORMATION, null,
                "Esta cita ya está cancelada.");
            return;
        }

        // Diálogo para elegir motivo de cancelación
        javafx.scene.control.ChoiceDialog<String> dialogo = new javafx.scene.control.ChoiceDialog<>(
            "Cliente no se presentó",
            "Cliente no se presentó",
            "Cliente rechazó la cita",
            "Mascota no apta para consulta",
            "Error de agenda / duplicado",
            "Cita cancelada por la clínica",
            "Otro motivo"
        );
        dialogo.setTitle("Cancelar Cita");
        dialogo.setHeaderText("Paciente: " + citaSeleccionada.getNombreMascota());
        dialogo.setContentText("Selecciona el motivo de cancelación:");

        java.util.Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isEmpty()) return; // usuario canceló el diálogo

        String motivo = resultado.get();

        citaSeleccionada.setEstado("Cancelada");
        CitasDAO.cambiarEstadoCita(citaSeleccionada.getId(), 18); // 'Cancelada' (ID 18)

        // Registrar en el historial
        ReporteLocal registro = new ReporteLocal(
            LocalDateTime.now(),
            citaSeleccionada.getNombreMascota(),
            "CANCELADA — " + citaSeleccionada.getMotivo(),
            "Motivo de cancelación: " + motivo,
            "Cita cancelada el " + LocalDateTime.now().format(FMT_DISPLAY) +
                ". Motivo: " + motivo
        );
        listaReportes.add(0, registro);
        renderizarReportes(listaReportes);

        // Refrescar tabla y panel
        tablaColaCitas.refresh();
        cargarCitaEnPantalla(citaSeleccionada);
    }

    // ══ HISTORIAL: GUARDADO Y FILTRADO ═══════════════════════════════════════

    /**
     * Guarda el diagnóstico en tb_diagnosticos y recarga el historial desde la BD.
     * @return true si se guardó correctamente.
     */
    private boolean guardarReporteEnBD() {
        String notas = txtReporteCita.getText().trim();
        if (notas.isEmpty()) notas = "(Sin notas registradas)";

        String tratamiento = String.format("Signos: Temp %s °C | FC %s ppm | FR %s rpm",
            textoODefecto(txtTemp), textoODefecto(txtFreqCard), textoODefecto(txtFreqResp));

        return CitasDAO.guardarDiagnostico(-1, citaSeleccionada.getId(), notas, tratamiento);
    }

    /**
     * Carga los diagnósticos desde tb_diagnosticos y los muestra en el historial.
     */
    private void cargarHistorialDesdeBD() {
        listaReportes.clear();
        String sql = """
            SELECT d.descripcion, d.tratamiento, d.fecha_registro,
                   m.nombre AS mascota, c.motivo
            FROM tb_diagnosticos d
            JOIN tb_citas c ON c.id = d.id_cita
            JOIN tb_mascotas m ON m.id = c.id_mascota
            ORDER BY d.fecha_registro DESC
            LIMIT 30
            """;
        try (java.sql.Connection con = new com.mycompany.aplicacion.persistencia.Conexion().estableceConexion();
             java.sql.PreparedStatement ps = con.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("fecha_registro");
                LocalDateTime ldt = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
                listaReportes.add(new ReporteLocal(
                    ldt,
                    rs.getString("mascota"),
                    rs.getString("motivo"),
                    rs.getString("tratamiento"),
                    rs.getString("descripcion")
                ));
            }
        } catch (Exception e) {
            System.err.println("[CitasController] Error cargando historial: " + e.getMessage());
        }
        renderizarReportes(listaReportes);
    }

    /** Renderiza la lista dada en el historial (reemplaza contenido anterior). */
    private void renderizarReportes(List<ReporteLocal> reportes) {
        vboxHistorialContent.getChildren().clear();

        if (reportes.isEmpty()) {
            Label vacio = new Label("No hay reportes que mostrar.");
            vacio.setStyle("-fx-text-fill: #aaaaaa; -fx-font-style: italic; -fx-font-size: 12px;");
            vboxHistorialContent.getChildren().add(vacio);
            return;
        }

        for (ReporteLocal r : reportes) {
            VBox card = construirTarjeta(r);
            // Clic → ventana de detalle
            card.setOnMouseClicked(e -> mostrarDetalleReporte(r));
            card.setStyle(card.getStyle() + "-fx-cursor: hand;");
            vboxHistorialContent.getChildren().add(card);
        }
    }

    private VBox construirTarjeta(ReporteLocal r) {
        VBox card = new VBox(4);
        card.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 10;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1;");

        Label lblEncabezado = new Label(
            r.fechaHora.format(FMT_DISPLAY) + " — " + r.paciente);
        lblEncabezado.setStyle(
            "-fx-font-weight: bold; -fx-text-fill: #3d8d7a; -fx-font-size: 12px;");

        Label lblSignos = new Label(r.signosVitales);
        lblSignos.setStyle(
            "-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-style: italic;");

        // Vista previa de notas (primeras 80 chars)
        String preview = r.notas.length() > 80
            ? r.notas.substring(0, 80) + "..."
            : r.notas;
        Label lblPreview = new Label(preview);
        lblPreview.setStyle("-fx-text-fill: #555555; -fx-font-size: 12px;");
        lblPreview.setWrapText(true);

        card.getChildren().addAll(lblEncabezado, lblSignos, lblPreview);
        return card;
    }

    /** Abre un diálogo modal con el detalle completo del reporte. */
    private void mostrarDetalleReporte(ReporteLocal r) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Detalle del Reporte");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(520);

        VBox contenido = new VBox(14);
        contenido.setPadding(new Insets(20));
        contenido.setStyle("-fx-background-color: white;");

        // Encabezado
        Label lblTitulo = new Label("📋  Reporte Clínico");
        lblTitulo.setStyle(
            "-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #3d8d7a;");

        // Datos del paciente
        HBox filaMeta = new HBox(24);
        filaMeta.setAlignment(Pos.CENTER_LEFT);
        filaMeta.setStyle(
            "-fx-background-color: #f0faf5; -fx-background-radius: 10; -fx-padding: 12;");
        filaMeta.getChildren().addAll(
            campoInfo("Paciente",  r.paciente),
            campoInfo("Motivo",    r.motivo),
            campoInfo("Fecha",     r.fechaHora.format(FMT_DISPLAY))
        );

        // Signos vitales
        Label lblSignosTitulo = new Label("Signos Vitales");
        lblSignosTitulo.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555555;");
        Label lblSignosValor = new Label(r.signosVitales);
        lblSignosValor.setStyle(
            "-fx-text-fill: #7f8c8d; -fx-font-size: 13px; -fx-font-style: italic;" +
            "-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-padding: 8;");
        lblSignosValor.setMaxWidth(Double.MAX_VALUE);

        // Notas completas
        Label lblNotasTitulo = new Label("Notas Clínicas");
        lblNotasTitulo.setStyle(
            "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #555555;");
        TextArea areaNotas = new TextArea(r.notas);
        areaNotas.setEditable(false);
        areaNotas.setWrapText(true);
        areaNotas.setPrefHeight(140);
        areaNotas.setStyle(
            "-fx-background-color: #f8f9fa;" +
            "-fx-border-color: #e0e0e0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-font-size: 13px;");

        contenido.getChildren().addAll(
            lblTitulo, filaMeta,
            lblSignosTitulo, lblSignosValor,
            lblNotasTitulo, areaNotas);

        dialog.getDialogPane().setContent(contenido);
        dialog.showAndWait();
    }

    /** Crea un pequeño bloque de etiqueta + valor para la fila de metadatos. */
    private VBox campoInfo(String etiqueta, String valor) {
        VBox box = new VBox(2);
        Label lbl = new Label(etiqueta);
        lbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");
        Label val = new Label(valor);
        val.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        box.getChildren().addAll(lbl, val);
        return box;
    }

    @FXML
    private void filtrarReportes(ActionEvent event) {
        LocalDate desde = dpDesde.getValue();
        LocalDate hasta = dpHasta.getValue();

        if (desde == null && hasta == null) {
            renderizarReportes(listaReportes);
            return;
        }

        List<ReporteLocal> filtrados = new ArrayList<>();
        for (ReporteLocal r : listaReportes) {
            LocalDate fechaReporte = r.fechaHora.toLocalDate();
            boolean pasaDesde = (desde == null) || !fechaReporte.isBefore(desde);
            boolean pasaHasta = (hasta == null) || !fechaReporte.isAfter(hasta);
            if (pasaDesde && pasaHasta) {
                filtrados.add(r);
            }
        }

        renderizarReportes(filtrados);
    }

    @FXML
    private void mostrarTodosReportes(ActionEvent event) {
        dpDesde.setValue(null);
        dpHasta.setValue(null);
        renderizarReportes(listaReportes);
    }

    // ══ MÉTODOS AUXILIARES ════════════════════════════════════════════════════

    private boolean estaTerminada(String estado) {
        return estado.equalsIgnoreCase("Completada")
            || estado.equalsIgnoreCase("Finalizada");
    }

    private void actualizarBotonEstado() {
        if (citaSeleccionada == null) return;

        boolean esStaff     = "Staff".equalsIgnoreCase(App.getRolUsuario());
        String  estado      = citaSeleccionada.getEstado();
        // "En curso" es el estado activo de consulta (id_state = 15 en BD)
        boolean enCurso     = estado.equalsIgnoreCase("En curso");
        boolean terminada   = estaTerminada(estado);
        boolean esCancelada = estado.equalsIgnoreCase("Cancelada");
        boolean bloqueado   = terminada || esCancelada;

        // El editor se habilita cuando la cita está activa (En curso)
        txtReporteCita.setDisable(!enCurso);
        txtReporteCita.setPromptText(enCurso
            ? "Escribe las notas médicas de la consulta..."
            : (bloqueado
                ? "Esta cita ya está cerrada."
                : "Presiona 'Iniciar Consulta' para habilitar el editor."));

        // Signos vitales: solo lectura siempre (cargados del triaje del staff)
        setSignosVitalesDisable(true);
        if (bloqueado) clearSignosVitales();

        // Veterinario puede iniciar/finalizar cualquier cita no bloqueada
        bAccionCita.setDisable(esStaff || bloqueado);
        btnCancelarCita.setDisable(esStaff || bloqueado);

        if (esStaff)         estilizarBoton(bAccionCita, "Solo Veterinario",   "#95a5a6");
        else if (esCancelada)estilizarBoton(bAccionCita, "Cita Cancelada",     "#bdc3c7");
        else if (terminada)  estilizarBoton(bAccionCita, "Consulta Terminada", "#bdc3c7");
        else if (enCurso)    estilizarBoton(bAccionCita, "Finalizar Consulta", "#e74c3c");
        else                 estilizarBoton(bAccionCita, "Iniciar Consulta",   "#3d8d7a");
    }

    private void estilizarBoton(Button btn, String texto, String color) {
        btn.setText(texto);
        btn.setStyle(
            "-fx-background-color:" + color + ";" +
            "-fx-text-fill:white;" +
            "-fx-font-weight:bold;" +
            "-fx-background-radius:20;" +
            "-fx-padding:7 20 7 20;" +
            "-fx-cursor:hand;");
    }

    private void setSignosVitalesDisable(boolean disable) {
        txtTemp.setDisable(disable);
        txtFreqCard.setDisable(disable);
        txtFreqResp.setDisable(disable);
    }

    private void clearSignosVitales() {
        txtTemp.clear(); txtFreqCard.clear(); txtFreqResp.clear();
    }

    private String textoODefecto(TextField tf) {
        return tf.getText().isEmpty() ? "--" : tf.getText();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String header, String contenido) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(header);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    // ══ MODELO INTERNO: Reporte Local ════════════════════════════════════════

    /**
     * Registro inmutable de un reporte clínico guardado localmente.
     * Tiene su propio timestamp para filtrado por fecha.
     */
    private static final class ReporteLocal {
        final LocalDateTime fechaHora;
        final String        paciente;
        final String        motivo;
        final String        signosVitales;
        final String        notas;

        ReporteLocal(LocalDateTime fechaHora, String paciente, String motivo,
                     String signosVitales, String notas) {
            this.fechaHora     = fechaHora;
            this.paciente      = paciente;
            this.motivo        = motivo;
            this.signosVitales = signosVitales;
            this.notas         = notas;
        }
    }
}
