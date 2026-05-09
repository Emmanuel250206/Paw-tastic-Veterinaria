package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.persistencia.Conexion;
import com.mycompany.aplicacion.util.Toast;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del diálogo "Configurar Perfil Profesional".
 *
 * Uso: ConfigurarPerfilController.abrir(ownerNode);
 */
public class ConfigurarPerfilController {

    @FXML
    private StackPane rootWrapper;
    @FXML
    private VBox profileCard;
    @FXML
    private HBox hboxHeader;
    @FXML
    private VBox vboxBody;
    @FXML
    private VBox sectionAvatar;
    @FXML
    private GridPane gridForm;
    @FXML
    private HBox hboxFooter;
    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML
    private ImageView imgAvatarActual;
    @FXML
    private Label lblSubtituloRol;

    @FXML
    private HBox hboxAvatares;

    @FXML
    private TextField txtNombreCompleto;
    @FXML
    private TextField txtAlias;
    @FXML
    private TextField txtRol;
    @FXML
    private TextField txtCedula;
    @FXML
    private TextField txtHorario;
    @FXML
    private Label lblCedula;

    // Avatar seleccionado actualmente en la galería
    private String avatarSeleccionado;
    // Lista de nodos visuales de la galería para resetear borde al cambiar
    // selección
    private final List<ImageView> galeria = new ArrayList<>();

    // Callback ejecutado tras guardar cambios (actualiza el header de la vista
    // activa)
    private Runnable refreshCallback;

    // ── Estilos reutilizables ─────────────────────────────────────────────────
    private static final String STYLE_AVATAR_BASE = "-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 2; -fx-background-radius: 25; -fx-cursor: hand;";
    private static final String STYLE_AVATAR_HOVER = "-fx-background-color: #E9F5F2; -fx-border-color: #3D8D7A; -fx-border-width: 2; -fx-background-radius: 25; -fx-cursor: hand;";
    private static final String STYLE_AVATAR_SELECTED = "-fx-background-color: #D1E8E2; -fx-border-color: #3D8D7A; -fx-border-width: 2.5; -fx-background-radius: 25;";

    // Avatares disponibles — solo los que existen en /images/
    private static final String[] AVATARES = {
            "Ava_huella.png",
            "Ava_perro1.png",
            "Ava_perro2.png",
            "Ava_perro3.png",
            "Ava_perro4.png",
            "Ava_perro5.png",
            "Ava_perro6.png",
            "Ava_gato.png",
            "Ava_conejo.png"
    };

    // ── Estilo de campo bloqueado (identidad del sistema) ─────────────────────
    /**
     * Bloquea visualmente un campo: disabled + opacidad reducida para señalizar que
     * es solo lectura.
     */
    private static void bloquearCampo(TextField tf) {
        tf.setDisable(true);
        tf.setOpacity(0.7);
    }

    // ── initialize ────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        avatarSeleccionado = session.getCurrentAvatarName();

        // Cargar avatar actual en el header
        cargarImagenEnView(imgAvatarActual, avatarSeleccionado);

        // Obtenemos el rol actual de la sesión
        String currentRole = session.getUserRole();

        // Subtítulo con rol
        lblSubtituloRol.setText(currentRole + "  ·  Clínica Paw-tastic");

        // Poblar formulario (datos editables)
        txtNombreCompleto.setText(session.getUserName());

        // ── Campos de identidad — BLOQUEADOS permanentemente ─────────────────
        // txtAlias: el username se genera una sola vez; nunca debe cambiar para
        // evitar romper el login del usuario.
        txtAlias.setText(session.getUserAlias());
        bloquearCampo(txtAlias);

        // txtRol: asignado por el administrador, no editable por el propio usuario.
        txtRol.setText(currentRole != null ? currentRole : "");
        bloquearCampo(txtRol);

        // txtHorario: asignado por administración/sistema.
        txtHorario.setText("Lunes–Viernes  8:00 – 17:00");
        bloquearCampo(txtHorario);

        // txtCedula: solo visible para veterinarios; siempre bloqueada (dato oficial).
        // Se carga desde la BD para que muestre el valor real, no el placeholder del
        // FXML.
        boolean isStaff = currentRole != null && currentRole.trim().equalsIgnoreCase("Staff");

        if (isStaff) {
            // Ocultar Cédula y su Label para Staff
            txtCedula.setVisible(false);
            txtCedula.setManaged(false);
            if (lblCedula != null) {
                lblCedula.setVisible(false);
                lblCedula.setManaged(false);
            }
        } else {
            // Veterinarios: cargar la cédula real desde la BD y mostrarla bloqueada
            cargarCedulaDesdeBD();
            String cedula = session.getUserCedula();
            txtCedula.setText(cedula != null ? cedula : ""); // texto real, nunca placeholder
            txtCedula.setVisible(true);
            txtCedula.setManaged(true);
            bloquearCampo(txtCedula); // disable + opacity DESPUÉS de setText
            if (lblCedula != null) {
                lblCedula.setVisible(true);
                lblCedula.setManaged(true);
            }
        }

        // Construir galería de avatares
        construirGaleria();

        // ── Garantizar visibilidad (Instrucción 3) ───────────────────────────
        profileCard.setOpacity(1.0);
        profileCard.setVisible(true);
        
        // Animación de entrada suave
        javafx.application.Platform.runLater(this::playSlideUpEntrance);
    }

    private void playSlideUpEntrance() {
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), profileCard);
        slide.setFromY(30);
        slide.setToY(0);
        slide.setInterpolator(Interpolator.EASE_OUT);
        slide.play();
    }

    /**
     * Consulta la cédula profesional del usuario actual desde
     * {@code tb_usuario_web}
     * y la persiste en {@link UserSession} para evitar consultas repetidas.
     * Si ya existe un valor en la sesión (de una apertura anterior), se omite la
     * consulta.
     */
    private void cargarCedulaDesdeBD() {
        UserSession session = UserSession.getInstance();

        // Evitar re-consulta si ya se cargó en esta sesión
        if (session.getUserCedula() != null && !session.getUserCedula().isEmpty()) {
            return;
        }

        int userId = session.getUserId();
        if (userId <= 0) {
            System.err.println("[ConfigurarPerfil] userId no disponible — no se puede consultar la cédula.");
            return;
        }

        Conexion conexion = new Conexion();
        try (Connection con = conexion.estableceConexion()) {
            if (con == null)
                return;
            String sql = "SELECT cedula FROM tb_usuario_web WHERE id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String cedula = rs.getString("cedula");
                        session.setUserCedula(cedula);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Galería ───────────────────────────────────────────────────────────────
    private void construirGaleria() {
        galeria.clear();
        hboxAvatares.getChildren().clear();

        for (String nombre : AVATARES) {
            VBox contenedor = new VBox();
            contenedor.setStyle(STYLE_AVATAR_BASE);
            contenedor.setPrefSize(52, 52);
            contenedor.setMaxSize(52, 52);

            ImageView iv = new ImageView();
            iv.setFitWidth(44);
            iv.setFitHeight(44);
            iv.setPreserveRatio(true);
            iv.setPickOnBounds(true);
            // Clip circular
            Circle clip = new Circle(22, 22, 22);
            iv.setClip(clip);
            cargarImagenEnView(iv, nombre);
            galeria.add(iv);

            // Padding dentro del contenedor
            contenedor.setStyle(STYLE_AVATAR_BASE + " -fx-padding: 3;");
            contenedor.getChildren().add(iv);

            // Resaltar el seleccionado actual
            if (nombre.equals(avatarSeleccionado)) {
                contenedor.setStyle(STYLE_AVATAR_SELECTED + " -fx-padding: 3;");
            }

            final String nombreFinal = nombre;

            // --- EFECTO HOVER: SCALE-UP (1.1x) ---
            ScaleTransition scaleIn = new ScaleTransition(Duration.millis(200), contenedor);
            scaleIn.setToX(1.1);
            scaleIn.setToY(1.1);

            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), contenedor);
            scaleOut.setToX(1.0);
            scaleOut.setToY(1.0);

            contenedor.setOnMouseEntered(e -> {
                if (!nombreFinal.equals(avatarSeleccionado)) {
                    contenedor.setStyle(STYLE_AVATAR_HOVER + " -fx-padding: 3;");
                }
                scaleIn.playFromStart();
            });
            contenedor.setOnMouseExited(e -> {
                if (!nombreFinal.equals(avatarSeleccionado)) {
                    contenedor.setStyle(STYLE_AVATAR_BASE + " -fx-padding: 3;");
                } else {
                    contenedor.setStyle(STYLE_AVATAR_SELECTED + " -fx-padding: 3;");
                }
                scaleOut.playFromStart();
            });
            contenedor.setOnMouseClicked(e -> seleccionarAvatar(nombreFinal, contenedor));

            hboxAvatares.getChildren().add(contenedor);
        }
    }

    private void seleccionarAvatar(String nombre, VBox contenedorElegido) {
        avatarSeleccionado = nombre;

        // Resetear todos los contenedores
        for (Node nodo : hboxAvatares.getChildren()) {
            if (nodo instanceof VBox v) {
                v.setStyle(STYLE_AVATAR_BASE + " -fx-padding: 3;");
            }
        }
        // Marcar el elegido
        contenedorElegido.setStyle(STYLE_AVATAR_SELECTED + " -fx-padding: 3;");

        // Actualizar preview en el header del modal
        cargarImagenEnView(imgAvatarActual, nombre);
    }

    // ── Acciones de botones ───────────────────────────────────────────────────

    /**
     * Permite inyectar una acción de refresco desde el controlador que abre este
     * modal.
     */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void guardarCambios() {
        UserSession session = UserSession.getInstance();

        // Solo los campos editables: nombre completo y avatar.
        String full = txtNombreCompleto.getText().trim();
        String n = full;
        String a = "";
        int spaceIdx = full.indexOf(" ");
        if (spaceIdx > 0) {
            n = full.substring(0, spaceIdx).trim();
            a = full.substring(spaceIdx + 1).trim();
        }

        // Persistir solo los datos que el usuario puede cambiar.
        // El alias/username NO se regenera: permanece igual al original.
        session.setUserName(full);
        session.setCurrentAvatarName(avatarSeleccionado);

        // Actualizar nombre en BD (sin tocar usuario, rol, horario, cédula)
        actualizarBaseDatos(n, a);

        cerrarModal();

        Toast.showToast("Perfil actualizado correctamente 🐾", 2);

        // Refrescar el header de la vista activa (si se registró un callback)
        if (refreshCallback != null) {
            refreshCallback.run();
        }

        // Sincronización inmediata: recargar las tarjetas del Staff en cuanto
        // aparece el Toast, sin esperar a que el usuario cambie de pestaña.
        StaffController.refreshStaffCards();
    }

    /**
     * Persiste únicamente los datos que el usuario puede modificar: nombre y
     * apellidos.
     * El campo 'usuario' (username de login) queda excluido del UPDATE para
     * garantizar
     * que el login nunca se rompa por un cambio de nombre.
     */
    private void actualizarBaseDatos(String nombre, String apellidos) {
        UserSession session = UserSession.getInstance();
        Conexion conexion = new Conexion();
        try (java.sql.Connection con = conexion.estableceConexion()) {
            if (con != null) {
                // Solo nombre y apellidos — usuario, rol, horario y cédula son inmutables
                String sql = "UPDATE tb_usuario_web SET nombre = ?, apellidos = ? WHERE id = ?";

                try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, apellidos);
                    ps.setInt(3, session.getUserId());
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cancelar() {
        cerrarModal();
    }

    private void cerrarModal() {
        if (rootWrapper != null && profileCard != null) {
            playDrawerExit(rootWrapper, profileCard, () -> {
                Parent parent = rootWrapper.getParent();
                if (parent instanceof Pane) {
                    ((Pane) parent).getChildren().remove(rootWrapper);
                }
            });
        }
    }

    // ── Animaciones Cinemáticas ──────────────────────────────────────────────

    public void playDrawerEntrance(Region dimming, VBox drawer) {
        // 1. Animación del Panel (Slide)
        TranslateTransition slide = new TranslateTransition(Duration.millis(400), drawer);
        slide.setFromX(520);
        slide.setToX(0);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        // 2. Animación de Dimming
        FadeTransition fadeDim = new FadeTransition(Duration.millis(400), dimming);
        fadeDim.setFromValue(0);
        fadeDim.setToValue(1);

        // 3. Staggered Elements Appearance
        List<Node> elements = new ArrayList<>();
        elements.add(hboxHeader);
        elements.add(sectionAvatar);
        elements.add(gridForm);
        elements.add(hboxFooter);

        int delay = 150;
        for (Node node : elements) {
            if (node != null) {
                node.setOpacity(0);
                node.setTranslateY(15);

                FadeTransition ft = new FadeTransition(Duration.millis(350), node);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(delay));

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), node);
                tt.setToY(0);
                tt.setDelay(Duration.millis(delay));

                ft.play();
                tt.play();
                delay += 60;
            }
        }

        slide.play();
        fadeDim.play();
    }

    public void playDrawerExit(Region dimming, VBox drawer, Runnable onFinished) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(350), drawer);
        slide.setToY(50);
        slide.setInterpolator(Interpolator.EASE_BOTH);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), drawer);
        fadeOut.setToValue(0);

        FadeTransition fadeDim = new FadeTransition(Duration.millis(350), dimming);
        fadeDim.setToValue(0);

        ParallelTransition pt = new ParallelTransition(slide, fadeOut, fadeDim);
        pt.setOnFinished(e -> onFinished.run());
        pt.play();
    }

    // ── Utilidad de imagen ────────────────────────────────────────────────────
    private void cargarImagenEnView(ImageView iv, String nombreArchivo) {
        if (iv == null || nombreArchivo == null)
            return;
        String path = "/images/" + nombreArchivo;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            iv.setImage(new Image(stream));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    // MÉTODOS ESTÁTICOS DE APERTURA — punto de entrada desde cualquier controller
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Abre el modal "Configurar Perfil" como ventana modal bloqueante.
     * Llama desde cualquier controller:
     * ConfigurarPerfilController.abrir(hboxPerfil);
     *
     * @param ownerNode cualquier Node de la escena padre (para calcular la Stage
     *                  dueña)
     */
    public static void abrir(Node ownerNode) {
        abrir(ownerNode, null);
    }

    /**
     * Abre el modal "Configurar Perfil" con un callback de refresco.
     * El callback se ejecuta automáticamente tras guardar los cambios.
     *
     * @param ownerNode cualquier Node de la escena padre (para calcular la Stage
     *                  dueña)
     * @param onSaved   Runnable que actualiza el header de la vista activa; puede
     *                  ser null
     */
    public static void abrir(Node ownerNode, Runnable onSaved) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ConfigurarPerfilController.class.getResource("/fxml/ConfigurarPerfil.fxml"));
            StackPane profileOverlay = loader.load();
            ConfigurarPerfilController controller = loader.getController();

            if (onSaved != null) {
                controller.setRefreshCallback(onSaved);
            }

            // 1. Intentar encontrar el StackPane principal (Overlay)
            Scene ownerScene = ownerNode.getScene();
            StackPane mainStack = null;

            if (ownerScene.getRoot() instanceof StackPane) {
                mainStack = (StackPane) ownerScene.getRoot();
            } else {
                mainStack = (StackPane) ownerScene.getRoot().lookup("#stackPrincipal");
            }

            if (mainStack != null) {
                // 2. Inyectar como Overlay
                if (!mainStack.getChildren().contains(profileOverlay)) {
                    mainStack.getChildren().add(profileOverlay);
                }
                profileOverlay.toFront();
                
                // Centrado dinámico
                StackPane.setAlignment(controller.profileCard, Pos.CENTER);
            } else {
                // 3. Fallback: Abrir en Stage independiente (Si no hay StackPane de soporte)
                Stage ownerStage = (Stage) ownerScene.getWindow();
                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.initOwner(ownerStage);
                modal.initStyle(StageStyle.TRANSPARENT);

                Scene scene = new Scene(profileOverlay, ownerStage.getWidth(), ownerStage.getHeight());
                scene.setFill(Color.TRANSPARENT);
                
                // Aplicar CSS al fallback
                URL css = ConfigurarPerfilController.class.getResource("/fxml/estilos.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());
                
                modal.setScene(scene);
                modal.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// End of file
