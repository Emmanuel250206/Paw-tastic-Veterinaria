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
    private static final String COLOR_BG = "#1A3A2F";
    private static final String COLOR_BORDER = "#142D24";
    private static final String COLOR_BTN_SI = "#4e9364";
    private static final String COLOR_BTN_SI_HV = "#2ECC71";
    private static final String COLOR_BTN_NO = "#FDFEFE";
    private static final String COLOR_BTN_NO_HV = "#F4F6F6";
    private static final String FONT_FAMILY = "'Segoe UI', 'Inter', sans-serif";

    private ExitDialog() {
    } 

    // ─────────────────────────────────────────────────────────────

    public static void mostrar(Stage owner, Runnable onConfirm) {
        mostrar(owner,
                "¿Estás seguro de que\nquieres salir?",
                "Perderás la sesión activa.",
                "Sí, salir",
                "No, quedarme",
                onConfirm);
    }

    public static void mostrar(Stage owner,
            String title,
            String subtext,
            String btnSiText,
            String btnNoText,
            Runnable onConfirm) {

        Stage modal = createStage(owner);

        VBox root = new VBox(16,
                createLogo(),
                createTitle(title),
                createSubtext(subtext));

        HBox buttons = new HBox(15,
                createNoButton(btnNoText, modal, root),
                createYesButton(btnSiText, modal, onConfirm));

        buttons.setAlignment(Pos.CENTER);
        root.getChildren().add(buttons);

        styleRoot(root);
        enableDrag(root, modal);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);

        modal.setScene(scene);

        playEntryAnimation(root);
        modal.show();
        modal.centerOnScreen();
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
        img.setFitWidth(90);
        img.setPreserveRatio(true);

        InputStream stream = ExitDialog.class.getResourceAsStream("/images/logo_paw.png");
        if (stream != null) {
            img.setImage(new Image(stream));
            img.setEffect(new DropShadow(25, Color.rgb(255, 255, 255, 0.85)));
        }
        return img;
    }

    private static Label createTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle(
                "-fx-font-family:" + FONT_FAMILY + ";" +
                        "-fx-font-size:20px;" +
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
                        "-fx-text-fill:#A0A0A0;");
        lbl.setWrapText(true);
        lbl.setMaxWidth(300);
        lbl.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return lbl;
    }

    // ─────────────────────────────────────────────────────────────
    // BOTONES
    // ─────────────────────────────────────────────────────────────

    private static Button createYesButton(String text, Stage modal, Runnable onConfirm) {
        return createButton(
                text,
                styleBtn(COLOR_BTN_SI, "white"),
                styleBtn(COLOR_BTN_SI_HV, "white"),
                () -> {
                    modal.close();
                    if (onConfirm != null)
                        onConfirm.run();
                });
    }

    private static Button createNoButton(String text, Stage modal, VBox root) {
        return createButton(
                text,
                styleBtn(COLOR_BTN_NO, "#2C3E50"),
                styleBtn(COLOR_BTN_NO_HV, "#2C3E50"),
                () -> playExitAnimation(root, modal));
    }

    private static Button createButton(String text, String baseStyle, String hoverStyle, Runnable action) {
        Button btn = new Button(text);
        btn.setPrefSize(120, 45);
        btn.setStyle(baseStyle);
        btn.setCursor(Cursor.HAND);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            scale(btn, 1.05);
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

    private static void styleRoot(VBox root) {
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(36, 40, 32, 40));
        root.setStyle(
                "-fx-background-color:" + COLOR_BG + ";" +
                        "-fx-background-radius:25;" +
                        "-fx-border-radius:25;" +
                        "-fx-border-color:" + COLOR_BORDER + ";" +
                        "-fx-border-width:1.5;");
        root.setEffect(new DropShadow(60, Color.rgb(0, 0, 0, 0.6)));
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
        node.setScaleX(0.85);
        node.setScaleY(0.85);

        FadeTransition fade = new FadeTransition(Duration.millis(200), node);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(220), node);
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
