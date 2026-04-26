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
    @FXML private Label     lblCedula;

    // Avatar seleccionado actualmente en la galería
    private String avatarSeleccionado;
    // Lista de nodos visuales de la galería para resetear borde al cambiar selección
    private final List<ImageView> galeria = new ArrayList<>();

    // Callback ejecutado tras guardar cambios (actualiza el header de la vista activa)
    private Runnable refreshCallback;

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
        System.out.println("[DEBUG] Current Session Role: " + UserSession.getInstance().getUserRole());
        UserSession session = UserSession.getInstance();
        avatarSeleccionado = session.getCurrentAvatarName();

        // Cargar avatar actual en el header
        cargarImagenEnView(imgAvatarActual, avatarSeleccionado);

        // Obtenemos el rol actual de la sesión
        String currentRole = session.getUserRole();

        // Subtítulo con rol
        lblSubtituloRol.setText(currentRole + "  ·  Clínica Paw-tastic");

        // Poblar formulario
        txtNombreCompleto.setText(session.getUserName());
        txtAlias.setText(session.getUserAlias());
        txtHorario.setText("Lunes–Viernes  8:00 – 17:00");

        // Lógica condicional según el rol
        boolean isStaff = currentRole != null && currentRole.trim().equalsIgnoreCase("Staff");
        boolean isVet = currentRole != null && currentRole.trim().equalsIgnoreCase("Veterinario");

        // Bloqueo General: el rol nunca debe ser editable por el usuario
        txtRol.setEditable(false);
        txtRol.setStyle(txtRol.getStyle() + " -fx-background-color: #EEEEEE; -fx-text-fill: #777777;");

        // Bloqueo Condicional: El usuario/alias ahora se autogenera, por lo que es de solo lectura
        txtAlias.setEditable(false);
        txtAlias.setStyle(txtAlias.getStyle() + " -fx-background-color: #EEEEEE; -fx-text-fill: #777777;");

        if (isStaff) {
            txtRol.setText("Staff");
            
            // Ocultar Cédula y su Label para Staff
            txtCedula.setVisible(false);
            txtCedula.setManaged(false);
            if (lblCedula != null) {
                lblCedula.setVisible(false);
                lblCedula.setManaged(false);
            }
        } else {
            txtRol.setText(currentRole);
            
            // Mostrar Cédula para Veterinarios
            txtCedula.setVisible(true);
            txtCedula.setManaged(true);
            txtCedula.setDisable(false);
            if (isVet) {
                txtCedula.setEditable(false); // Vet Special Lock
            }
            if (lblCedula != null) {
                lblCedula.setVisible(true);
                lblCedula.setManaged(true);
            }
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

    /** Permite inyectar una acción de refresco desde el controlador que abre este modal. */
    public void setRefreshCallback(Runnable callback) {
        this.refreshCallback = callback;
    }

    @FXML
    private void guardarCambios() {
        UserSession session = UserSession.getInstance();

        String full = txtNombreCompleto.getText().trim();
        String n = full;
        String a = "";
        int spaceIdx = full.indexOf(" ");
        if (spaceIdx > 0) {
            n = full.substring(0, spaceIdx).trim();
            a = full.substring(spaceIdx + 1).trim();
        }

        String genUsuario = "user";
        if (!n.isEmpty() && !a.isEmpty()) {
            genUsuario = n.substring(0, 1).toLowerCase() + a.split(" ")[0].toLowerCase();
        } else if (!n.isEmpty()) {
            genUsuario = n.toLowerCase();
        }

        // Persistir valores en el singleton
        session.setUserName(full);
        session.setUserAlias(genUsuario);
        session.setUserRole(txtRol.getText().trim());
        session.setCurrentAvatarName(avatarSeleccionado);

        // Actualizar en base de datos
        actualizarBaseDatos(n, a, genUsuario);

        cerrarModal();

        // Refrescar el header de la vista activa (si se registró un callback)
        if (refreshCallback != null) {
            refreshCallback.run();
        }
    }

    private void actualizarBaseDatos(String nombre, String apellidos, String usuario) {
        UserSession session = UserSession.getInstance();
        com.mycompany.aplicacion.persistencia.Conexion conexion = new com.mycompany.aplicacion.persistencia.Conexion();
        try (java.sql.Connection con = conexion.estableceConexion()) {
            if (con != null) {
                String sql = "UPDATE tb_usuarios SET nombre = ?, apellidos = ?, usuario = ? WHERE id = ?";
                
                try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                    ps.setString(1, nombre);
                    ps.setString(2, apellidos);
                    ps.setString(3, usuario);
                    ps.setInt(4, session.getUserId());
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar la base de datos: " + e.getMessage());
            e.printStackTrace();
        }
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
    //  MÉTODOS ESTÁTICOS DE APERTURA — punto de entrada desde cualquier controller
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Abre el modal "Configurar Perfil" como ventana modal bloqueante.
     * Llama desde cualquier controller: ConfigurarPerfilController.abrir(hboxPerfil);
     *
     * @param ownerNode cualquier Node de la escena padre (para calcular la Stage dueña)
     */
    public static void abrir(Node ownerNode) {
        abrir(ownerNode, null);
    }

    /**
     * Abre el modal "Configurar Perfil" con un callback de refresco.
     * El callback se ejecuta automáticamente tras guardar los cambios.
     *
     * @param ownerNode      cualquier Node de la escena padre (para calcular la Stage dueña)
     * @param onSaved        Runnable que actualiza el header de la vista activa; puede ser null
     */
    public static void abrir(Node ownerNode, Runnable onSaved) {
        try {
            FXMLLoader loader = new FXMLLoader(
                ConfigurarPerfilController.class.getResource("/fxml/ConfigurarPerfil.fxml")
            );
            VBox root = loader.load();

            // Inyectar el callback ANTES de mostrar el modal
            ConfigurarPerfilController controller = loader.getController();
            if (onSaved != null) {
                controller.setRefreshCallback(onSaved);
            }

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

            // Mostrar como modal bloqueante
            modal.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir ConfigurarPerfil: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
// End of file
