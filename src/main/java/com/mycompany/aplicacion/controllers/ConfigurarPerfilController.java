package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Node;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador del diálogo "Configurar Perfil Profesional".
 *
 * Uso: ConfigurarPerfilController.abrir(ownerNode);
 */
public class ConfigurarPerfilController {

    // ── FXML fields ───────────────────────────────────────────────────────────
    @FXML private ImageView imgAvatarActual;
    @FXML private Label     lblSubtituloRol;

    @FXML private HBox      hboxAvatares;

    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtAlias;
    @FXML private TextField txtRol;
    @FXML private TextField txtCedula;
    @FXML private TextField txtHorario;

    // Avatar seleccionado actualmente en la galería
    private String avatarSeleccionado;
    // Lista de nodos visuales de la galería para resetear borde al cambiar selección
    private final List<ImageView> galeria = new ArrayList<>();

    // ── Estilos reutilizables ─────────────────────────────────────────────────
    private static final String STYLE_AVATAR_BASE =
        "-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 2; -fx-background-radius: 25; -fx-cursor: hand;";
    private static final String STYLE_AVATAR_HOVER =
        "-fx-background-color: #E9F5F2; -fx-border-color: #3D8D7A; -fx-border-width: 2; -fx-background-radius: 25; -fx-cursor: hand;";
    private static final String STYLE_AVATAR_SELECTED =
        "-fx-background-color: #D1E8E2; -fx-border-color: #3D8D7A; -fx-border-width: 2.5; -fx-background-radius: 25;";

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

    // ── initialize ────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        UserSession session = UserSession.getInstance();
        avatarSeleccionado = session.getCurrentAvatarName();

        // Cargar avatar actual en el header
        cargarImagenEnView(imgAvatarActual, avatarSeleccionado);

        // Subtítulo con rol
        lblSubtituloRol.setText(session.getUserRole() + "  ·  Clínica Paw-tastic");

        // Poblar formulario
        txtNombreCompleto.setText(session.getUserName());
        txtAlias.setText(session.getUserName());
        txtRol.setText(session.getUserRole());
        txtHorario.setText("Lunes–Viernes  8:00 – 17:00");

        // Cédula: solo editable para veterinarios
        boolean esVet = "Veterinario".equalsIgnoreCase(session.getUserRole());
        txtCedula.setDisable(!esVet);
        if (!esVet) {
            txtCedula.setPromptText("No aplica para este rol");
            txtCedula.setStyle(txtCedula.getStyle() + " -fx-opacity: 0.55;");
        }

        // Construir galería de avatares
        construirGaleria();
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

            // Eventos de hover y clic sobre el VBox contenedor
            final String nombreFinal = nombre;
            contenedor.setOnMouseEntered(e -> {
                if (!nombreFinal.equals(avatarSeleccionado)) {
                    contenedor.setStyle(STYLE_AVATAR_HOVER + " -fx-padding: 3;");
                }
            });
            contenedor.setOnMouseExited(e -> {
                if (!nombreFinal.equals(avatarSeleccionado)) {
                    contenedor.setStyle(STYLE_AVATAR_BASE + " -fx-padding: 3;");
                } else {
                    contenedor.setStyle(STYLE_AVATAR_SELECTED + " -fx-padding: 3;");
                }
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
    @FXML
    private void guardarCambios() {
        UserSession session = UserSession.getInstance();

        // Persistir valores en el singleton
        String nuevoNombre = txtNombreCompleto.getText().trim();
        if (!nuevoNombre.isEmpty()) {
            session.setUserName(nuevoNombre);
        }
        session.setUserRole(txtRol.getText().trim());
        session.setCurrentAvatarName(avatarSeleccionado);

        cerrarModal();
    }

    @FXML
    private void cancelar() {
        cerrarModal();
    }

    private void cerrarModal() {
        Stage stage = (Stage) txtNombreCompleto.getScene().getWindow();
        stage.close();
    }

    // ── Utilidad de imagen ────────────────────────────────────────────────────
    private void cargarImagenEnView(ImageView iv, String nombreArchivo) {
        if (iv == null || nombreArchivo == null) return;
        String path = "/images/" + nombreArchivo;
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            iv.setImage(new Image(stream));
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MÉTODO ESTÁTICO DE APERTURA — punto de entrada desde cualquier controller
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Abre el modal "Configurar Perfil" como ventana modal bloqueante.
     * Llama desde cualquier controller: ConfigurarPerfilController.abrir(hboxPerfil);
     *
     * @param ownerNode cualquier Node de la escena padre (para calcular la Stage dueña)
     */
    public static void abrir(Node ownerNode) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ConfigurarPerfilController.class.getResource("/fxml/ConfigurarPerfil.fxml")
            );
            VBox root = loader.load();

            Stage modal = new Stage();
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.initStyle(StageStyle.UNDECORATED); // Sin barra de título nativa → aspecto limpio
            modal.setResizable(false);

            // Centrar respecto al owner
            if (ownerNode != null && ownerNode.getScene() != null) {
                Stage owner = (Stage) ownerNode.getScene().getWindow();
                modal.initOwner(owner);
            }

            Scene scene = new Scene(root);
            // Cargar hoja de estilos de la app para que los TextField hereden .text-field:focused etc.
            java.net.URL css = ConfigurarPerfilController.class.getResource("/fxml/estilos.css");
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            modal.setScene(scene);

            // Sobreponer un fondo oscuro semitransparente sobre la ventana owner
            modal.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir ConfigurarPerfil: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
