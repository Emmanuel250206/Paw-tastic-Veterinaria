package com.mycompany.aplicacion.util;

import com.mycompany.aplicacion.App;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Toast {

    /**
     * Muestra un Toast con la duración predeterminada (6 segundos).
     * Ideal para mensajes de alta importancia donde el usuario necesita leer
     * información generada (ej. nombres de usuario asignados automáticamente).
     */
    public static void showToast(String message) {
        showToast(message, 6);
    }

    /**
     * Muestra un Toast con una duración personalizada en segundos.
     * La barra de progreso (verde → amarillo → rojo) y el fade-out se sincronizan
     * automáticamente con el valor proporcionado.
     *
     * @param message          Texto a mostrar en el Toast.
     * @param durationInSeconds Tiempo en segundos que el Toast permanece visible.
     */
    public static void showToast(String message, int durationInSeconds) {
        // Garantizamos un mínimo razonable para que la animación sea legible
        final double d = Math.max(1, durationInSeconds);

        Platform.runLater(() -> {
            Stage owner = App.getStage();
            if (owner == null) return;

            Stage toastStage = new Stage();
            toastStage.initOwner(owner);
            toastStage.initStyle(StageStyle.TRANSPARENT);

            Label lblMessage = new Label(message);
            // Estilo alineado al diseño
            lblMessage.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-alignment: center;");
            lblMessage.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

            HBox content = new HBox(lblMessage);
            content.setAlignment(Pos.CENTER);
            content.setPadding(new Insets(20));

            // Barra de progreso dinámica
            Rectangle progressBar = new Rectangle(0, 4);
            progressBar.setArcWidth(4);
            progressBar.setArcHeight(4);
            progressBar.setFill(Color.web("#4CAF50")); // Inicia en verde

            HBox barContainer = new HBox(progressBar);
            barContainer.setAlignment(Pos.CENTER_LEFT);
            barContainer.setPadding(new Insets(0, 20, 15, 20)); // Espaciado inferior y lateral

            // VBox principal
            VBox root = new VBox(content, barContainer);
            root.setAlignment(Pos.CENTER);
            // Dark green background (#2D5A4C), 15px rounded corners
            root.setStyle("-fx-background-color: #2D5A4C; -fx-background-radius: 15; -fx-border-radius: 15;");
            
            // Sombra para un look moderno
            root.setEffect(new javafx.scene.effect.DropShadow(15, Color.rgb(0,0,0,0.25)));

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            toastStage.setScene(scene);

            toastStage.setOnShown(event -> {
                double ownerX = owner.getX();
                double ownerY = owner.getY();
                double ownerW = owner.getWidth();
                double ownerH = owner.getHeight();

                double toastW = toastStage.getWidth();
                double toastH = toastStage.getHeight();

                toastStage.setX(ownerX + (ownerW - toastW) / 2);

                double finalY = ownerY + (ownerH - toastH) / 2;
                double startY = ownerY - toastH; // Aparece desde arriba del área visible
                
                toastStage.setY(startY);

                // 1. Animación de Slide-in manual de la ventana
                Transition slideIn = new Transition() {
                    { 
                        setCycleDuration(Duration.millis(500)); 
                        setInterpolator(Interpolator.EASE_OUT);
                    }
                    @Override
                    protected void interpolate(double frac) {
                        toastStage.setY(startY + (finalY - startY) * frac);
                    }
                };

                // 2. Animación de barra de progreso (Ancho y Color) — sincronizada con 'd'
                // Proporciones: Verde hasta 50%, Amarillo hasta 83%, Rojo hasta 100%
                double maxW = root.getWidth() - 40; // restando el padding
                progressBar.setWidth(maxW);
                
                Timeline progressTimeline = new Timeline(
                    new KeyFrame(Duration.ZERO,
                        new KeyValue(progressBar.widthProperty(), maxW),
                        new KeyValue(progressBar.fillProperty(), Color.web("#4CAF50"))
                    ),
                    new KeyFrame(Duration.seconds(d * 0.50),
                        new KeyValue(progressBar.fillProperty(), Color.web("#FFC107"))
                    ),
                    new KeyFrame(Duration.seconds(d * 0.83),
                        new KeyValue(progressBar.fillProperty(), Color.web("#F44336"))
                    ),
                    new KeyFrame(Duration.seconds(d),
                        new KeyValue(progressBar.widthProperty(), 0)
                    )
                );

                // 3. Fade Out Final — se dispara exactamente cuando la barra llega a 0
                Timeline fadeOutTimeline = new Timeline(new KeyFrame(Duration.seconds(d), evt -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(400), root);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e -> toastStage.close());
                    fadeOut.play();
                }));

                slideIn.play();
                progressTimeline.play();
                fadeOutTimeline.play();
            });

            // Inicia transparente y hace Fade-In junto al Slide
            root.setOpacity(0);
            toastStage.show();

            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
    }
}
