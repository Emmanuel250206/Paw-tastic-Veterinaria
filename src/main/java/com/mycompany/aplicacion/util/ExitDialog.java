package com.mycompany.aplicacion.util;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.InputStream;

/**
 * Modal de confirmación de salida con diseño Paw-Tastic.
 *
 * <p>Uso: {@code ExitDialog.mostrar(ownerStage, onConfirm);}
 *
 * @param ownerStage  La ventana padre (para centrado y modality).
 * @param onConfirm   Runnable ejecutado si el usuario confirma la salida.
 */
public class ExitDialog {

    // ── Paleta de marca ──────────────────────────────────────────────────────
    private static final String COLOR_BG        = "#468a6fff";   // 
    private static final String COLOR_BORDER    = "#2D5A4C";   // Verde marca — borde sutil de identidad
    private static final String COLOR_BTN_SI    = "#E74C3C";   // Soft Red — acción destructiva visible
    private static final String COLOR_BTN_SI_HV = "#C0392B";   // Rojo más oscuro al hover
    private static final String COLOR_SUBTITLE  = "#D1D1D1";   // Gris claro — texto secundario
    private static final String FONT_FAMILY     = "'Segoe UI', 'Inter', sans-serif";

    /**
     * Muestra el modal de confirmación de salida.
     *
     * @param ownerStage La Stage propietaria (para centrado y bloqueo modal).
     * @param onConfirm  Acción a ejecutar si el usuario pulsa "Sí".
     */
    public static void mostrar(Stage ownerStage, Runnable onConfirm) {

        Stage modal = new Stage();
        modal.initOwner(ownerStage);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);
        modal.setResizable(false);

        // ── Logo oficial Paw-Tastic ───────────────────────────────────────────
        ImageView imgLogo = new ImageView();
        imgLogo.setFitWidth(80);
        imgLogo.setPreserveRatio(true);
        imgLogo.setSmooth(true);
        InputStream logoStream = ExitDialog.class.getResourceAsStream("/images/logo_paw.png");
        if (logoStream != null) {
            imgLogo.setImage(new Image(logoStream));
            // Glow blanco sutil — hace que el logo flote sobre el fondo oscuro
            DropShadow logoGlow = new DropShadow();
            logoGlow.setColor(Color.rgb(255, 255, 255, 0.75));
            logoGlow.setRadius(18);
            logoGlow.setSpread(0.15);
            imgLogo.setEffect(logoGlow);
        } else {
            System.err.println("[ExitDialog] logo_paw.png no encontrado — usando emoji fallback");
        }

        // ── Pregunta principal ────────────────────────────────────────────────
        Label lblPregunta = new Label("¿Estás seguro de que\nquieres salir?");
        lblPregunta.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-text-alignment: center;"
        );
        lblPregunta.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // ── Subtexto — gris claro, nunca blanco puro ─────────────────────────────
        Label lblSub = new Label("Perderás la sesión activa.");
        lblSub.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: " + COLOR_SUBTITLE + ";" +
            "-fx-text-alignment: center;"
        );
        lblSub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // ── Botón "Sí, salir" (rojo) ──────────────────────────────────────────
        Button btnSi = new Button("Sí, salir");
        String siBase = styleBtn(COLOR_BTN_SI, "white", "transparent");
        String siHover = styleBtn(COLOR_BTN_SI_HV, "white", "transparent");
        btnSi.setStyle(siBase);
        btnSi.setCursor(javafx.scene.Cursor.HAND);
        btnSi.setOnMouseEntered(e -> btnSi.setStyle(siHover));
        btnSi.setOnMouseExited(e  -> btnSi.setStyle(siBase));
        btnSi.setOnAction(e -> {
            modal.close();
            if (onConfirm != null) onConfirm.run();
        });

        // ── Botón “No” (ghost — borde blanco, fondo transparente) ───────────────
        Button btnNo = new Button("No, quedarme");
        // Hover: gris muy sutil — sin volverse blanco (demasiado brillante)
        String noBase  = styleBtnGhost("transparent",            "white", "rgba(255,255,255,0.70)");
        String noHover = styleBtnGhost("rgba(255, 255, 255, 0.37)", "white", "white");
        btnNo.setStyle(noBase);
        btnNo.setCursor(javafx.scene.Cursor.HAND);
        btnNo.setOnMouseEntered(e -> btnNo.setStyle(noHover));
        btnNo.setOnMouseExited(e  -> btnNo.setStyle(noBase));
        btnNo.setOnAction(e -> modal.close());

        // ── Fila de botones ───────────────────────────────────────────────────
        HBox hboxBotones = new HBox(15, btnNo, btnSi);
        hboxBotones.setAlignment(Pos.CENTER);
        hboxBotones.setPadding(new Insets(8, 0, 0, 0));

        // ── Contenedor principal ───────────────────────────────────────────────
        VBox root = new VBox(16, imgLogo, lblPregunta, lblSub, hboxBotones);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 40, 32, 40));
        root.setStyle(
            "-fx-background-color: " + COLOR_BG + ";" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-border-color: " + COLOR_BORDER + ";" +   // borde verde marca
            "-fx-border-width: 1.5;"
        );
        root.setMaxWidth(360);
        // Sombra flotante — da profundidad sobre cualquier fondo
        root.setEffect(new DropShadow(60, Color.rgb(0, 0, 0, 0.60)));

        // ── Drag-to-move (arrastar el modal) ──────────────────────────────────
        // Captura el offset entre el cursor y la esquina del stage al presionar.
        final double[] dragOffset = new double[2];
        root.setOnMousePressed(e -> {
            dragOffset[0] = modal.getX() - e.getScreenX();
            dragOffset[1] = modal.getY() - e.getScreenY();
            root.setCursor(javafx.scene.Cursor.CLOSED_HAND);
        });
        root.setOnMouseDragged(e -> {
            modal.setX(e.getScreenX() + dragOffset[0]);
            modal.setY(e.getScreenY() + dragOffset[1]);
        });
        root.setOnMouseReleased(e -> root.setCursor(javafx.scene.Cursor.DEFAULT));

        // ── Escena transparente ───────────────────────────────────────────────
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

        // ── Animación de entrada: fade + scale ────────────────────────────────
        root.setOpacity(0);
        root.setScaleX(0.88);
        root.setScaleY(0.88);

        modal.show();

        FadeTransition fade = new FadeTransition(Duration.millis(220), root);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(220), root);
        scale.setFromX(0.88);
        scale.setFromY(0.88);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
        scale.play();

        // Centrar en pantalla — garantiza que el modal capte la atención
        // independientemente de la posición/tamaño de la ventana principal.
        modal.centerOnScreen();
    }

    // ── Helpers de estilo ─────────────────────────────────────────────────────

    private static String styleBtn(String bg, String text, String border) {
        return  "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + text + ";" +
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 28 10 28;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1.5;" +
                "-fx-cursor: hand;";
    }

    private static String styleBtnGhost(String bg, String text, String border) {
        return  "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: " + text + ";" +
                "-fx-font-family: " + FONT_FAMILY + ";" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-padding: 10 28 10 28;" +
                "-fx-background-radius: 10;" +
                "-fx-border-color: " + border + ";" +
                "-fx-border-radius: 10;" +
                "-fx-border-width: 1.5;" +
                "-fx-cursor: hand;";
    }
}
