/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.modelo.DatosSimulados;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.UserSession;
import java.io.IOException;
import java.io.InputStream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

/**
 *
 * @author emmanuel
 */
public class MascotasController {

    // Lista de mascotas
    @FXML
    private ListView<Mascota> listaMascotas;

    // FICHA
    @FXML private Label lblNombre;
    @FXML private Label lblEspecie;
    @FXML private Label lblRaza;
    @FXML private Label lblEdad;
    @FXML private Label lblPropietario;

    @FXML private TextArea   txtHistorial;
    @FXML private TextField  txtBuscar;

    // Perfil header
    @FXML private ImageView imgPerfilMascotas;
    @FXML private Label     lblNombreMascotas;
    @FXML private Label     lblRolMascotas;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    // Ficha avatar + badge de estado
    @FXML private ImageView imgMascotaFicha;
    @FXML private Label     lblEstadoFicha;

    @FXML
    public void initialize() {
        // Perfil de usuario en el header
        UserSession.loadProfileImage(imgPerfilMascotas);
        lblNombreMascotas.setText(UserSession.getInstance().getUserName());
        lblRolMascotas.setText(UserSession.getInstance().getUserRole());
        construirMenuPerfil();

        // Busqueda en tiempo real
        txtBuscar.textProperty().addListener((obs, oldText, newText) ->
                listaMascotas.setItems(DatosSimulados.buscarPorNombre(newText))
        );

        // Cargar datos y configurar celdas
        listaMascotas.setFocusTraversable(false);
        listaMascotas.setItems(DatosSimulados.getMascotas());
        listaMascotas.setCellFactory(lv -> new ListCell<Mascota>() {

            // Keeps a reference to the current card so the selection
            // listener can toggle the style class without a full updateItem.
            private HBox currentCard = null;

            {
                // This listener fires on EVERY selection change — even when
                // the underlying item doesn't change — making it the only
                // reliable way to drive custom selection visuals in JavaFX.
                selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    if (currentCard != null) {
                        Label lblN = (Label) currentCard.lookup("#lblNombre");
                        Label lblE = (Label) currentCard.lookup("#lblEspecie");
                        if (isSelected) {
                            currentCard.setStyle("-fx-border-color: #3D8D7A; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-color: #F4FAF7;");
                            if (lblN != null) lblN.setStyle("-fx-text-fill: #0D2621; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                            if (lblE != null) lblE.setStyle("-fx-text-fill: #0D2621; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px;");
                        } else {
                            currentCard.setStyle("-fx-border-color: transparent; -fx-background-color: white;");
                            if (lblN != null) lblN.setStyle("-fx-text-fill: #3D8D7A; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                            if (lblE != null) lblE.setStyle("-fx-text-fill: #7F8C8D; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px;");
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Mascota mascota, boolean empty) {
                super.updateItem(mascota, empty);
                currentCard = null;
                if (empty || mascota == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/fxml/ItemMascota.fxml"));
                    HBox card = loader.load();

                    // Referencias a los controles del template
                    ImageView avatar  = (ImageView) card.lookup("#imgAvatar");
                    Label     nombre  = (Label)     card.lookup("#lblNombre");
                    Label     especie = (Label)     card.lookup("#lblEspecie");
                    Label     estado  = (Label)     card.lookup("#lblEstado");

                    // Datos
                    nombre.setText(mascota.getNombre());
                    especie.setText(mascota.getEspecie() + " · " + mascota.getRaza());
                    estado.setText("Activo");

                    // Avatar segun especie
                    String ruta = resolverRutaAvatar(mascota.getEspecie());
                    InputStream stream = getClass().getResourceAsStream(ruta);
                    if (stream != null) {
                        avatar.setImage(new Image(stream));
                    }

                    // Apply selected style immediately if this cell is already selected
                    // (handles reuse of cells during scroll / data refresh)
                    if (isSelected()) {
                        card.setStyle("-fx-border-color: #3D8D7A; -fx-border-width: 3; -fx-border-radius: 15; -fx-background-color: #F4FAF7;");
                        if (nombre != null) nombre.setStyle("-fx-text-fill: #0D2621; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        if (especie != null) especie.setStyle("-fx-text-fill: #0D2621; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px;");
                    } else {
                        card.setStyle("-fx-border-color: transparent; -fx-background-color: white;");
                        if (nombre != null) nombre.setStyle("-fx-text-fill: #3D8D7A; -fx-font-weight: bold; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px;");
                        if (especie != null) especie.setStyle("-fx-text-fill: #7F8C8D; -fx-font-family: 'Segoe UI'; -fx-font-size: 12px;");
                    }

                    currentCard = card;
                    setGraphic(card);
                    setText(null);
                    setStyle("-fx-background-color: transparent; -fx-padding: 0;");
                } catch (IOException e) {
                    setText(mascota.getNombre());
                    setGraphic(null);
                }
            }
        });

        // Seleccion -> panel de detalles
        listaMascotas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, mascota) -> {
                    if (mascota != null) {
                        mostrarMascota(mascota);
                    }
                }
        );
    }

    /** Devuelve la ruta de imagen segun la especie. */
    private String resolverRutaAvatar(String especie) {
        if (especie == null)                        return "/images/Icon_Mascotas.png";
        if (especie.equalsIgnoreCase("Perro"))      return "/images/Ava_perro1.png";
        if (especie.equalsIgnoreCase("Gato"))       return "/images/Ava_gato.png";
        return "/images/Icon_Mascotas.png";
    }

    /**
     * Actualiza el badge de estado en la ficha de detalle segun el contenido
     * del historial clinico. Usa heuristica por palabras clave ya que el modelo
     * Mascota no tiene un campo de estado explicito.
     */
    private void actualizarBadgeEstado(String historial) {
        if (lblEstadoFicha == null) return;

        lblEstadoFicha.getStyleClass().removeAll("status-healthy", "status-treatment");

        boolean enTratamiento = historial != null && (
            historial.contains("Urgencia")    ||
            historial.contains("Tratamiento") ||
            historial.contains("nfecci")      ||   // infeccion / infeccioso
            historial.contains("l\u00e9rgic")      ||   // alergica / alergico
            historial.contains("Luxaci\u00f3n")    ||
            historial.contains("Dermatitis")
        );

        if (enTratamiento) {
            lblEstadoFicha.getStyleClass().add("status-treatment");
            lblEstadoFicha.setText("En Tratamiento");
        } else {
            lblEstadoFicha.getStyleClass().add("status-healthy");
            lblEstadoFicha.setText("Saludable");
        }
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
        Label lbl = new Label("\u2699  Configurar Perfil");
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
            UserSession.loadProfileImage(imgPerfilMascotas);
            lblNombreMascotas.setText(UserSession.getInstance().getUserName());
            lblRolMascotas.setText(UserSession.getInstance().getUserRole());
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

    // Metodo clave: rellena la ficha de detalle
    private void mostrarMascota(Mascota m) {
        lblNombre.setText(m.getNombre());
        lblEspecie.setText(m.getEspecie());
        lblRaza.setText(m.getRaza());
        lblEdad.setText(m.getEdad() + " a\u00f1os");
        lblPropietario.setText(m.getNombrePropietario());
        txtHistorial.setText(m.getHistorialClinico());

        // Badge de estado segun historial
        actualizarBadgeEstado(m.getHistorialClinico());

        // Avatar en la ficha segun especie
        if (imgMascotaFicha != null) {
            String ruta = resolverRutaAvatar(m.getEspecie());
            InputStream stream = getClass().getResourceAsStream(ruta);
            if (stream != null) {
                imgMascotaFicha.setImage(new Image(stream));
            } else {
                InputStream fallback = getClass().getResourceAsStream("/images/Icon_Mascotas.png");
                if (fallback != null) {
                    imgMascotaFicha.setImage(new Image(fallback));
                }
            }
        }
    }
}
