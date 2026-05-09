package com.mycompany.aplicacion.util;

import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.InputStream;

public class ExitDialog {

    // Paleta centralizada
    // Paleta centralizada (Refinada para estilo High-End)
    private static final String COLOR_BG = "#2D3E3E"; // Deep Sage Green / Charcoal
    private static final String COLOR_BORDER = "#3A4D4D";
    private static final String COLOR_BTN_SI = "#A3C1AD"; // Sage Green Pastel
    private static final String COLOR_BTN_SI_HV = "#B8D2C2";
    private static final String COLOR_BTN_NO = "#FFFFFF";
    private static final String COLOR_BTN_NO_HV = "#F5F5F5";
    private static final String FONT_FAMILY = "'Segoe UI', 'Inter', sans-serif";

    private ExitDialog() {
    } 

    // ─────────────────────────────────────────────────────────────

    public static void mostrar(Stage owner, Runnable onConfirm) {
        mostrar(owner,
                "¿Estás seguro de que quieres salir?",
                "Perderás la sesión activa.",
                "Sí, Cerrar",
                "No, volver",
                onConfirm);
    }

    public static void mostrar(Stage owner,
            String title,
            String subtext,
            String btnSiText,
            String btnNoText,
            Runnable onConfirm) {

        Stage modal = createStage(owner);

        // --- CONTENIDO DEL DIÁLOGO ---
        VBox dialogBox = new VBox(20,
                createLogo(),
                createTitle(title),
                createSubtext(subtext));
        
        HBox buttons = new HBox(15,
                createNoButton(btnNoText, modal, dialogBox),
                createYesButton(btnSiText, modal, onConfirm));

        buttons.setAlignment(Pos.CENTER);
        dialogBox.getChildren().add(buttons);
        
        styleDialogBox(dialogBox);
        enableDrag(dialogBox, modal);

        // --- OVERLAY (CAPA OSCURA) ---
        StackPane overlay = new StackPane(dialogBox);
        overlay.setAlignment(Pos.CENTER);
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Fondo oscuro semi-transparente
        overlay.setPrefSize(owner.getWidth(), owner.getHeight());

        Scene scene = new Scene(overlay);
        scene.setFill(Color.TRANSPARENT);

        // Sincronizar posición y tamaño con el owner para el overlay
        modal.setX(owner.getX());
        modal.setY(owner.getY());
        modal.setWidth(owner.getWidth());
        modal.setHeight(owner.getHeight());

        modal.setScene(scene);

        playEntryAnimation(dialogBox);
        modal.show();
    }

    // ─────────────────────────────────────────────────────────────
    // COMPONENTES
    // ─────────────────────────────────────────────────────────────

    private static Stage createStage(Stage owner) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        return stage;
    }

    private static ImageView createLogo() {
        ImageView img = new ImageView();
        img.setFitWidth(80);
        img.setPreserveRatio(true);

        InputStream stream = ExitDialog.class.getResourceAsStream("/images/logo_paw.png");
        if (stream != null) {
            img.setImage(new Image(stream));
            img.setEffect(new DropShadow(15, Color.rgb(255, 255, 255, 0.4)));
        }
        return img;
    }

    private static Label createTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-family:" + FONT_FAMILY + ";" +
                        "-fx-font-size:18px;" +
                        "-fx-font-weight:bold;" +
                        "-fx-text-fill:white;");
        lbl.setWrapText(true);
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return lbl;
    }

    private static Label createSubtext(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-family:" + FONT_FAMILY + ";" +
                        "-fx-font-size:14px;" +
                        "-fx-text-fill:#BDC3C7;");
        lbl.setWrapText(true);
        lbl.setMaxWidth(320);
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return lbl;
    }

    // ─────────────────────────────────────────────────────────────
    // BOTONES
    // ─────────────────────────────────────────────────────────────

    private static Button createYesButton(String text, Stage modal, Runnable onConfirm) {
        return createButton(
                text,
                styleBtn(COLOR_BTN_SI, "#2D3E3E"), // Texto oscuro sobre pastel
                styleBtn(COLOR_BTN_SI_HV, "#2D3E3E"),
                () -> {
                    modal.close();
                    if (onConfirm != null)
                        onConfirm.run();
                });
    }

    private static Button createNoButton(String text, Stage modal, VBox root) {
        // Botón blanco con borde fino
        String baseStyle = styleBtn(COLOR_BTN_NO, "#2D3E3E") + "-fx-border-color: #BDC3C7; -fx-border-radius: 25; -fx-border-width: 1;";
        String hoverStyle = styleBtn(COLOR_BTN_NO_HV, "#2D3E3E") + "-fx-border-color: #3D8D7A; -fx-border-radius: 25; -fx-border-width: 1.5;";
        
        return createButton(
                text,
                baseStyle,
                hoverStyle,
                () -> playExitAnimation(root, modal));
    }

    private static Button createButton(String text, String baseStyle, String hoverStyle, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefSize(140, 45);
        btn.setStyle(baseStyle);
        btn.setCursor(Cursor.HAND);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            scale(btn, 1.03);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            scale(btn, 1.0);
        });

        btn.setOnAction(e -> action.run());

        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    // ESTILOS
    // ─────────────────────────────────────────────────────────────

    private static void styleDialogBox(VBox root) {
        root.setAlignment(Pos.CENTER);
        root.setMaxSize(400, 300);
        root.setPadding(new Insets(30, 40, 30, 40));
        root.setStyle(
                "-fx-background-color:" + COLOR_BG + ";" +
                        "-fx-background-radius:25;" +
                        "-fx-border-radius:25;" +
                        "-fx-border-color:" + COLOR_BORDER + ";" +
                        "-fx-border-width:1;");
        root.setEffect(new DropShadow(40, Color.rgb(0, 0, 0, 0.6)));
    }

    private static String styleBtn(String bg, String text) {
        return "-fx-background-color:" + bg + ";" +
                "-fx-text-fill:" + text + ";" +
                "-fx-font-family:" + FONT_FAMILY + ";" +
                "-fx-font-size:14px;" +
                "-fx-font-weight:bold;" +
                "-fx-background-radius:25;";
    }

    // ─────────────────────────────────────────────────────────────
    // ANIMACIONES
    // ─────────────────────────────────────────────────────────────

    private static void playEntryAnimation(Node node) {
        node.setOpacity(0);
        node.setScaleX(0.9);
        node.setScaleY(0.9);

        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(300), node);
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.EASE_OUT);

        fade.play();
        scale.play();
    }

    private static void playExitAnimation(Node node, Stage modal) {
        FadeTransition fade = new FadeTransition(Duration.millis(180), node);
        fade.setToValue(0);
        fade.setOnFinished(e -> modal.close());
        fade.play();
    }

    private static void scale(Node node, double value) {
        ScaleTransition st = new ScaleTransition(Duration.millis(120), node);
        st.setToX(value);
        st.setToY(value);
        st.play();
    }

    // ─────────────────────────────────────────────────────────────
    //  DRAG
    // ────────────────────────────────────────────────────────────

    private static void enableDrag(Node node, Stage stage) {
        final double[] offset = new double[2];

        node.setOnMousePressed(e -> {
            offset[0] = stage.getX() - e.getScreenX();
            offset[1] = stage.getY() - e.getScreenY();
        });

        node.setOnMouseDragged(e -> {
            stage.setX(e.getScreenX() + offset[0]);
            stage.setY(e.getScreenY() + offset[1]);
        });
    }
}
