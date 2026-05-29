package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.Bitacora;
import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.persistencia.BitacoraDAO;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ModalBitacoraController {

    @FXML private StackPane rootWrapper;
    @FXML private VBox bitacoraCard;
    @FXML private Label lblSubtitulo;
    @FXML private TableView<Bitacora> tblBitacora;
    @FXML private TableColumn<Bitacora, String> colFecha;
    @FXML private TableColumn<Bitacora, String> colUsuario;
    @FXML private TableColumn<Bitacora, String> colModulo;
    @FXML private TableColumn<Bitacora, String> colDetalle;
    @FXML private Button btnSalir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @FXML
    public void initialize() {
        // Configurar subtitulo con el nombre de la clínica activa
        String clinicaNombre = UserSession.getInstance().getNombreClinica();
        if (clinicaNombre != null) {
            lblSubtitulo.setText("Historial de auditoría y operaciones en la clínica: " + clinicaNombre);
        }

        // Configurar enlace de datos con las columnas
        colFecha.setCellValueFactory(cellData -> {
            LocalDateTime ldt = cellData.getValue().getFechaHora();
            return new SimpleStringProperty(ldt != null ? ldt.format(FORMATTER) : "");
        });

        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsuarioNombre()));
        colModulo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getModulo()));
        colDetalle.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDetalle()));

        // Estilizar los módulos con badges coloridos
        colModulo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    String baseStyle = "-fx-font-weight: bold; -fx-padding: 4 10 4 10; -fx-background-radius: 5; -fx-alignment: center;";
                    if (item.equalsIgnoreCase("Citas")) {
                        setStyle(baseStyle + " -fx-background-color: #E3F2FD; -fx-text-fill: #1565C0;");
                    } else if (item.equalsIgnoreCase("Ventas") || item.equalsIgnoreCase("PuntoVenta")) {
                        setStyle(baseStyle + " -fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");
                    } else if (item.equalsIgnoreCase("Inventario") || item.equalsIgnoreCase("Productos")) {
                        setStyle(baseStyle + " -fx-background-color: #FFF3E0; -fx-text-fill: #E65100;");
                    } else if (item.equalsIgnoreCase("Sesión") || item.equalsIgnoreCase("Acceso")) {
                        setStyle(baseStyle + " -fx-background-color: #F3E5F5; -fx-text-fill: #7B1FA2;");
                    } else if (item.equalsIgnoreCase("Usuarios") || item.equalsIgnoreCase("Staff")) {
                        setStyle(baseStyle + " -fx-background-color: #E0F2F1; -fx-text-fill: #004D40;");
                    } else {
                        setStyle(baseStyle + " -fx-background-color: #ECEFF1; -fx-text-fill: #37474F;");
                    }
                }
            }
        });

        // Cargar registros
        cargarDatos();

        // Forzar visibilidad y ejecutar animación
        bitacoraCard.setOpacity(1.0);
        bitacoraCard.setVisible(true);
        Platform.runLater(this::playEntranceAnimation);
    }

    private void cargarDatos() {
        int clinicId = UserSession.getInstance().getClinicId();
        List<Bitacora> datos = BitacoraDAO.listarPorClinica(clinicId);
        tblBitacora.setItems(FXCollections.observableArrayList(datos));
    }

    private void playEntranceAnimation() {
        // Deslizar de arriba hacia el centro (cinemático y sin lag de opacidad)
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(380), bitacoraCard);
        slideDown.setFromY(-680);
        slideDown.setToY(0);
        slideDown.setInterpolator(Interpolator.EASE_OUT);
        slideDown.play();
    }

    @FXML
    private void salir() {
        cerrarModal();
    }

    private void cerrarModal() {
        // Deslizar hacia arriba al cerrar
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(300), bitacoraCard);
        slideUp.setToY(-680);
        slideUp.setInterpolator(Interpolator.EASE_IN);
        slideUp.setOnFinished(e -> {
            Parent parent = rootWrapper.getParent();
            if (parent instanceof Pane) {
                ((Pane) parent).getChildren().remove(rootWrapper);
            } else if (rootWrapper.getScene() != null && rootWrapper.getScene().getWindow() instanceof Stage stage) {
                stage.close();
            }
        });
        slideUp.play();
    }

    public static void abrir(Node ownerNode) {
        try {
            FXMLLoader loader = new FXMLLoader(ModalBitacoraController.class.getResource("/fxml/ModalBitacora.fxml"));
            StackPane overlay = loader.load();
            ModalBitacoraController controller = loader.getController();

            Scene ownerScene = ownerNode.getScene();
            StackPane mainStack = null;

            if (ownerScene.getRoot() instanceof StackPane) {
                mainStack = (StackPane) ownerScene.getRoot();
            } else {
                mainStack = (StackPane) ownerScene.getRoot().lookup("#stackPrincipal");
            }

            if (mainStack != null) {
                if (!mainStack.getChildren().contains(overlay)) {
                    mainStack.getChildren().add(overlay);
                }
                overlay.toFront();
                StackPane.setAlignment(controller.bitacoraCard, Pos.CENTER);
            } else {
                // Fallback si no hay StackPane
                Stage ownerStage = (Stage) ownerScene.getWindow();
                Stage modal = new Stage();
                modal.initModality(Modality.APPLICATION_MODAL);
                modal.initOwner(ownerStage);
                modal.initStyle(StageStyle.TRANSPARENT);

                Scene scene = new Scene(overlay, ownerStage.getWidth(), ownerStage.getHeight());
                scene.setFill(Color.TRANSPARENT);

                URL css = ModalBitacoraController.class.getResource("/fxml/estilos.css");
                if (css != null) scene.getStylesheets().add(css.toExternalForm());

                modal.setScene(scene);
                modal.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
