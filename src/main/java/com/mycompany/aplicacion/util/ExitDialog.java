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
    private static final String COLOR_BG        = "#1A3A2F";   // Professional Pine Green
    private static final String COLOR_BORDER    = "#142D24";   // Sutil border
    private static final String COLOR_BTN_SI    = "#4e9364ff";   // Emerald Green
    private static final String COLOR_BTN_SI_HV = "#2ECC71";   // Lighter Emerald
    private static final String COLOR_SUBTITLE  = "#A0A0A0";   // Light gray
    private static final String FONT_FAMILY     = "'Segoe UI', 'Inter', sans-serif";

    /**
     * Muestra el modal de confirmación de salida.
     *
     * @param ownerStage La Stage propietaria (para centrado y bloqueo modal).
     * @param onConfirm  Acción a ejecutar si el usuario pulsa "Sí".
     */
    public static void mostrar(Stage ownerStage, Runnable onConfirm) {
        mostrar(ownerStage, "¿Estás seguro de que\nquieres salir?", "Perderás la sesión activa.", "Sí, salir", "No, quedarme", onConfirm);
    }

    /**
     * Versión genérica del modal de confirmación.
     */
    public static void mostrar(Stage ownerStage, String title, String subtext, String btnSiText, String btnNoText, Runnable onConfirm) {

        Stage modal = new Stage();
        modal.initOwner(ownerStage);
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initStyle(StageStyle.TRANSPARENT);
        modal.setResizable(false);

        // ── Logo oficial Paw-Tastic ───────────────────────────────────────────
        ImageView imgLogo = new ImageView();
        imgLogo.setFitWidth(90);
        imgLogo.setPreserveRatio(true);
        imgLogo.setSmooth(true);
        InputStream logoStream = ExitDialog.class.getResourceAsStream("/images/logo_paw.png");
        if (logoStream != null) {
            imgLogo.setImage(new Image(logoStream));
            DropShadow logoGlow = new DropShadow();
            logoGlow.setColor(Color.rgb(255, 255, 255, 0.85));
            logoGlow.setRadius(25);
            logoGlow.setSpread(0.2);
            imgLogo.setEffect(logoGlow);
        }

        // ── Pregunta principal ────────────────────────────────────────────────
        Label lblPregunta = new Label(title);
        lblPregunta.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;" +
            "-fx-text-alignment: center;"
        );
        lblPregunta.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // ── Subtexto ─────────────────────────────────────────────────────────
        Label lblSub = new Label(subtext);
        lblSub.setStyle(
            "-fx-font-family: " + FONT_FAMILY + ";" +
            "-fx-font-size: 14px;" +
            "-fx-text-fill: #A0A0A0;" +
            "-fx-text-alignment: center;"
        );
        lblSub.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        lblSub.setWrapText(true);
        lblSub.setMaxWidth(300);

        // ── Botones ──────────────────────────────────────────────────────────
        Button btnSi = new Button(btnSiText);
        btnSi.setPrefWidth(120);
        btnSi.setPrefHeight(45);
        String siBase = styleBtn("#27AE60", "white", "transparent");
        String siHover = styleBtn("#2ECC71", "white", "transparent");
        btnSi.setStyle(siBase + "-fx-background-radius: 25;");
        btnSi.setCursor(javafx.scene.Cursor.HAND);
        
        btnSi.setOnMouseEntered(e -> {
            btnSi.setStyle(siHover + "-fx-background-radius: 25;");
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btnSi);
            st.setToX(1.05); st.setToY(1.05); st.play();
        });
        btnSi.setOnMouseExited(e  -> {
            btnSi.setStyle(siBase + "-fx-background-radius: 25;");
            ScaleTransition st = new ScaleTransition(Duration.millis(150), btnSi);
            st.setToX(1.0); st.setToY(1.0); st.play();
        });
        btnSi.setOnAction(e -> {
            modal.close();
            if (onConfirm != null) onConfirm.run();
        });

        Button btnNo = new Button(btnNoText);
        btnNo.setPrefWidth(120);
        btnNo.setPrefHeight(45);
        String noBase  = "-fx-background-color: #FDFEFE; -fx-text-fill: #2C3E50; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 25;";
        String noHover = "-fx-background-color: #F4F6F6; -fx-text-fill: #2C3E50; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 25;";
        btnNo.setStyle(noBase);
        btnNo.setCursor(javafx.scene.Cursor.HAND);
        btnNo.setOnMouseEntered(e -> btnNo.setStyle(noHover));
        btnNo.setOnMouseExited(e  -> btnNo.setStyle(noBase));

        HBox hboxBotones = new HBox(15, btnNo, btnSi);
        hboxBotones.setAlignment(Pos.CENTER);
        hboxBotones.setPadding(new Insets(8, 0, 0, 0));
        VBox root = new VBox(16, imgLogo, lblPregunta, lblSub, hboxBotones);
        
        btnNo.setOnAction(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), root);
            ft.setFromValue(1.0); ft.setToValue(0.0);
            ft.setOnFinished(evt -> modal.close());
            ft.play();
        });

        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 40, 32, 40));
        root.setStyle(
            "-fx-background-color: " + COLOR_BG + ";" +
            "-fx-background-radius: 25;" +
            "-fx-border-radius: 25;" +
            "-fx-border-color: " + COLOR_BORDER + ";" +
            "-fx-border-width: 1.5;"
        );
        root.setEffect(new DropShadow(60, Color.rgb(0, 0, 0, 0.60)));

        final double[] dragOffset = new double[2];
        root.setOnMousePressed(e -> {
            dragOffset[0] = modal.getX() - e.getScreenX();
            dragOffset[1] = modal.getY() - e.getScreenY();
        });
        root.setOnMouseDragged(e -> {
            modal.setX(e.getScreenX() + dragOffset[0]);
            modal.setY(e.getScreenY() + dragOffset[1]);
        });

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);

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
