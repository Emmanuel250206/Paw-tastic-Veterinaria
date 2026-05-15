/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import com.mycompany.aplicacion.App;
import com.mycompany.aplicacion.persistencia.CitasDAO;
import com.mycompany.aplicacion.persistencia.MascotaDAO;
import com.mycompany.aplicacion.persistencia.InventarioDAO;
import com.mycompany.aplicacion.modelo.UserSession;

/**
 *
 * @author emmanuel
 */
public class DashboardController {
    private VeterinarioController padre;

    // Contenedores directos FXML
    @FXML
    private VBox cardCitas;
    @FXML
    private VBox cardMascotas;
    @FXML
    private VBox cardInventario;
    @FXML
    private VBox cardStaff;
    @FXML
    private VBox cardReportes;

    @FXML
    private ImageView imgPerfilHeader;

    @FXML
    private Label lblNombreHeader;

    @FXML
    private Label lblRolHeader;

    @FXML
    private HBox hboxPerfil;

    @FXML
    private Label lblSaludo;

    /** Instancia persistente para poder hacer toggle show/hide. */
    private ContextMenu menuPerfil;

    @FXML
    private void initialize() {
        // --- Perfil de usuario en el header ---
        UserSession.loadProfileImage(imgPerfilHeader);
        lblNombreHeader.setText(UserSession.getInstance().getUserName());
        lblRolHeader.setText(UserSession.getInstance().getUserRole());

        generarSaludoDinamico();

        // Construir el ContextMenu una sola vez
        construirMenuPerfil();

        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            // Solo ocultar tarjeta Staff
            if (cardStaff != null) {
                cardStaff.setVisible(false);
                cardStaff.setManaged(false);
            }
        } else if ("Veterinario".equalsIgnoreCase(App.getRolUsuario())) {
            // Staff is for admins, veterinario usually shouldn't see it either, 
            // but we follow existing logic. User only asked to NOT hide Reportes.
        }

        renderizarTarjetas();

        // Aplicar efectos de micro-interacción (Hover)
        aplicarEfectoHover(cardCitas);
        aplicarEfectoHover(cardMascotas);
        aplicarEfectoHover(cardInventario);
        aplicarEfectoHover(cardReportes);
        aplicarEfectoHover(cardStaff);

        // --- SECUENCIA DE ENTRADA STAGGERED ---
        animarEntradaEscalonada();
    }

    private void animarEntradaEscalonada() {
        VBox[] tarjetas = {cardCitas, cardMascotas, cardInventario, cardReportes, cardStaff};
        int delay = 100;
        for (VBox t : tarjetas) {
            if (t != null && t.isVisible()) {
                t.setOpacity(0);
                t.setTranslateY(20);
                
                javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(javafx.util.Duration.millis(600), t);
                fade.setToValue(1);
                fade.setDelay(javafx.util.Duration.millis(delay));
                
                javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(600), t);
                slide.setToY(0);
                slide.setDelay(javafx.util.Duration.millis(delay));
                
                fade.play();
                slide.play();
                delay += 120;
            }
        }
    }

    private void animarConteo(Label label, int valorFinal) {
        if (label == null || valorFinal <= 0) return;
        
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        int pasos = Math.min(valorFinal, 20); // Máximo 20 pasos para suavidad
        for (int i = 0; i <= pasos; i++) {
            final int currentVal = (int) ((double) valorFinal * i / pasos);
            javafx.animation.KeyFrame frame = new javafx.animation.KeyFrame(
                javafx.util.Duration.millis(800.0 * i / pasos),
                e -> label.setText(String.valueOf(currentVal))
            );
            timeline.getKeyFrames().add(frame);
        }
        timeline.play();
    }

    /**
     * Aplica efectos de micro-interacción usando la técnica 'Ghost Layer'.
     * Los eventos se vinculan a una Región estática (triggerArea) que está al frente,
     * mientras que la animación se aplica al contenedor de la tarjeta detrás.
     */
    private void aplicarEfectoHover(VBox card) {
        if (card == null || !(card.getParent() instanceof javafx.scene.layout.StackPane)) return;

        javafx.scene.layout.StackPane parent = (javafx.scene.layout.StackPane) card.getParent();
        
        // El triggerArea es la Región transparente al frente (último hijo en FXML)
        javafx.scene.Node triggerNode = parent.getChildren().get(parent.getChildren().size() - 1);
        if (!(triggerNode instanceof javafx.scene.layout.Region)) return;
        
        javafx.scene.layout.Region trigger = (javafx.scene.layout.Region) triggerNode;
        trigger.setCursor(javafx.scene.Cursor.HAND);

        // Sombra base (Normal: Radius 10, Opacity 0.1)
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow(10, javafx.scene.paint.Color.rgb(0,0,0,0.1));
        shadow.setOffsetY(4);
        card.setEffect(shadow);

        // Buscar el icono para la animación de rotación
        javafx.scene.image.ImageView icono = buscarIcono(card);

        // Animación de Desplazamiento (Lift: -8px en Y)
        javafx.animation.TranslateTransition tt = new javafx.animation.TranslateTransition(javafx.util.Duration.millis(200), card);
        tt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);

        javafx.animation.RotateTransition rt = null;
        if (icono != null) {
            rt = new javafx.animation.RotateTransition(javafx.util.Duration.millis(200), icono);
            rt.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        }

        final javafx.animation.RotateTransition rtFinal = rt;

        // EVENTOS EN EL TRIGGER (Área estática que no se mueve)
        trigger.setOnMouseEntered(e -> {
            tt.stop();
            tt.setToY(-8);
            tt.play();
            
            if (rtFinal != null) {
                rtFinal.stop();
                rtFinal.setToAngle(15);
                rtFinal.play();
            }
            
            // Animación de sombra (Hover: Radius 20, Opacity 0.25)
            javafx.animation.Timeline shadowIn = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(200),
                    new javafx.animation.KeyValue(shadow.radiusProperty(), 20, javafx.animation.Interpolator.EASE_OUT),
                    new javafx.animation.KeyValue(shadow.colorProperty(), javafx.scene.paint.Color.rgb(0,0,0,0.25), javafx.animation.Interpolator.EASE_OUT),
                    new javafx.animation.KeyValue(shadow.offsetYProperty(), 10, javafx.animation.Interpolator.EASE_OUT)
                )
            );
            shadowIn.play();
        });

        trigger.setOnMouseExited(e -> {
            tt.stop();
            tt.setToY(0);
            tt.play();

            if (rtFinal != null) {
                rtFinal.stop();
                rtFinal.setToAngle(0);
                rtFinal.play();
            }

            // Retorno de sombra a estado normal
            javafx.animation.Timeline shadowOut = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(200),
                    new javafx.animation.KeyValue(shadow.radiusProperty(), 10, javafx.animation.Interpolator.EASE_OUT),
                    new javafx.animation.KeyValue(shadow.colorProperty(), javafx.scene.paint.Color.rgb(0,0,0,0.1), javafx.animation.Interpolator.EASE_OUT),
                    new javafx.animation.KeyValue(shadow.offsetYProperty(), 4, javafx.animation.Interpolator.EASE_OUT)
                )
            );
            shadowOut.play();
        });
    }

    private javafx.scene.image.ImageView buscarIcono(VBox card) {
        // Buscamos el ImageView en la jerarquía (VBox -> HBox -> Region/StackPane -> ImageView)
        for (javafx.scene.Node n : card.getChildren()) {
            if (n instanceof HBox) {
                for (javafx.scene.Node m : ((HBox)n).getChildren()) {
                    if (m instanceof javafx.scene.layout.StackPane) {
                        for (javafx.scene.Node o : ((javafx.scene.layout.StackPane)m).getChildren()) {
                            if (o instanceof ImageView) return (ImageView) o;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void generarSaludoDinamico() {
        if (lblSaludo == null) return;
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int hour = now.getHour();
        java.time.DayOfWeek dayOfWeek = now.getDayOfWeek();
        
        String nombreCompleto = "";
        int currentUserId = UserSession.getInstance().getUserId();
        
        // Ejecutar SELECT usando el ID único para garantizar sincronización perfecta con la DB
        com.mycompany.aplicacion.persistencia.Conexion cx = new com.mycompany.aplicacion.persistencia.Conexion();
        try (java.sql.Connection conn = cx.estableceConexion()) {
            if (conn != null && currentUserId > 0) {
                String sql = "SELECT nombre, apellidos FROM tb_usuario_web WHERE id = ?";
                try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, currentUserId);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String n = rs.getString("nombre");
                            String a = rs.getString("apellidos");
                            nombreCompleto = (n != null ? n : "") + " " + (a != null ? a : "");
                            nombreCompleto = nombreCompleto.trim();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail or use a logger
        }

        // Fallback a UserSession si falla la BD
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            nombreCompleto = UserSession.getInstance().getUserName();
        }

        String[] dias = {"lunes", "martes", "miércoles", "jueves", "viernes", "sábado", "domingo"};
        String diaStr = dias[dayOfWeek.getValue() - 1];

        String saludo;
        if (hour >= 5 && hour < 12) {
            saludo = "¡Buenos días! Excelente " + diaStr + ", " + nombreCompleto + " ☀️";
        } else if (hour >= 12 && hour < 19) {
            saludo = "¡Buenas tardes! Es un gusto verte, " + nombreCompleto + " ⛅";
        } else {
            saludo = "¡Buenas noches! Excelente " + diaStr + ", " + nombreCompleto + " 🌙";
        }

        lblSaludo.setText(saludo);
    }

    private void construirMenuPerfil() {
        menuPerfil = new ContextMenu();

        // Estilo inline — no depende de ningún recurso externo
        menuPerfil.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #3D8D7A;" +
            "-fx-border-radius: 10;" +
            "-fx-border-width: 1.2;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.14), 14, 0, 0, 5);" +
            "-fx-padding: 4 0 4 0;"
        );

        // Label es un Node real → soporta setOnMouseEntered / setOnMouseExited
        Label lblConfigurar = new Label("⚙  Configurar Perfil");
        lblConfigurar.setMaxWidth(Double.MAX_VALUE);
        lblConfigurar.setPrefWidth(185);

        String estiloBase =
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #2C3E50;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-background-color: transparent;" +
            "-fx-background-radius: 7;" +
            "-fx-cursor: hand;";

        String estiloHover =
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #2E7D6B;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-font-family: 'Segoe UI';" +
            "-fx-background-color: #E9F5F2;" +
            "-fx-background-radius: 7;" +
            "-fx-cursor: hand;";

        lblConfigurar.setStyle(estiloBase);

        // Mouse entra → verde suave
        lblConfigurar.setOnMouseEntered(e -> lblConfigurar.setStyle(estiloHover));

        // Mouse sale → vuelve a transparente (evita el color pegado)
        lblConfigurar.setOnMouseExited(e -> lblConfigurar.setStyle(estiloBase));

        // Clic → acción + cierra el menú
        lblConfigurar.setOnMouseClicked(e -> {
            menuPerfil.hide();
            ConfigurarPerfilController.abrir(hboxPerfil, () -> {
                UserSession.loadProfileImage(imgPerfilHeader);
                lblNombreHeader.setText(UserSession.getInstance().getUserName());
                lblRolHeader.setText(UserSession.getInstance().getUserRole());
                generarSaludoDinamico();
            });
        });

        // hideOnClick=true para que el CustomMenuItem cierre el popup al hacer clic
        CustomMenuItem itemConfigurar = new CustomMenuItem(lblConfigurar, true);
        itemConfigurar.setMnemonicParsing(false);

        menuPerfil.getItems().add(itemConfigurar);
    }

    @FXML
    private void manejarClickPerfil(MouseEvent event) {
        if (menuPerfil == null) return;

        if (menuPerfil.isShowing()) {
            menuPerfil.hide();
            return;
        }

        // Alinear el borde derecho del menú con el del HBox de perfil
        double offsetX = hboxPerfil.getWidth() - 185;
        menuPerfil.show(hboxPerfil, Side.BOTTOM, offsetX, 4);
    }


    private void renderizarTarjetas() {
        // Citas (Info rica con AM/PM)
        java.util.List<String> detalleCitas = new java.util.ArrayList<>();
        var citasHoy = CitasDAO.getCitasHoy();
        int maxCitas = Math.min(3, citasHoy.size());
        for (int i = 0; i < maxCitas; i++) {
            var c = citasHoy.get(i);
            String horaStr = c.getHora();
            try {
                String[] partes = horaStr.split(":");
                int horaInt = Integer.parseInt(partes[0].trim());
                String ampm = (horaInt >= 12) ? "PM" : "AM";
                int hora12 = (horaInt > 12) ? horaInt - 12 : (horaInt == 0 ? 12 : horaInt);
                horaStr = String.format("%02d:%s %s", hora12, partes[1].trim(), ampm);
            } catch (Exception ignore) {
            }
            detalleCitas.add("• " + c.getNombreMascota() + " - " + c.getMotivo() + " (" + horaStr + ")");
        }
        crearTarjetaDinamica(cardCitas, "Citas pendientes", citasHoy.size(), "Próximas hoy", "/images/Icon_Citas.png", "#E3F2FD", detalleCitas);

        // Mascotas (Info rica)
        java.util.List<String> detalleMascotas = new java.util.ArrayList<>();
        var mascotas = MascotaDAO.getTodas();
        int maxMas = Math.min(3, mascotas.size());
        for (int i = 0; i < maxMas; i++) {
            var m = mascotas.get(i);
            detalleMascotas.add("• " + m.getNombre() + " (" + m.getRaza() + ")");
        }
        crearTarjetaDinamica(cardMascotas, "Mascotas", mascotas.size(), "Registradas", "/images/Icon_Mascotas.png", "#E8F5E9", detalleMascotas);

        // Inventario (Info rica)
        java.util.List<String> detalleInv = new java.util.ArrayList<>();
        var inventario = InventarioDAO.getTodos();
        int maxInv = Math.min(3, inventario.size());
        for (int i = 0; i < maxInv; i++) {
            var inv = inventario.get(i);
            detalleInv.add("• " + inv.getNombre() + " - Stock: " + inv.getStock_actual());
        }
        crearTarjetaDinamica(cardInventario, "Inventario", inventario.size(), "En stock", "/images/Icon_Inventario.png", "#FFF3E0", detalleInv);

        // Reportes (Datos reales de ventas)
        java.util.List<String> detalleReportes = new java.util.ArrayList<>();
        var ventas = com.mycompany.aplicacion.persistencia.VentasDAO.obtenerVentasRecientes();
        int maxVentas = Math.min(3, ventas.size());
        for (int i = 0; i < maxVentas; i++) {
            var v = ventas.get(i);
            detalleReportes.add("• " + v.getConcepto() + ": " + v.getMascota() + " ($" + v.getMonto() + ")");
        }
        crearTarjetaDinamica(cardReportes, "Reportes", ventas.size(), "Ventas recientes", "/images/Icon_Reporte.png", "#FFCC80", detalleReportes);

        // Staff (Activity List Style)
        populateStaffCard(cardStaff);
    }

    private void crearTarjetaDinamica(VBox tarjeta, String titulo, int cantidad, String subLabelText, String iconPath, String colorBg, java.util.List<String> detallesList) {
        if (tarjeta == null)
            return;

        tarjeta.getChildren().clear();
        tarjeta.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        tarjeta.setPadding(new javafx.geometry.Insets(20, 25, 20, 25));
        tarjeta.setCursor(javafx.scene.Cursor.HAND);
        tarjeta.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, " + colorBg + "); -fx-background-radius: 20;");

        // Top Title
        Label lblTitulo = new Label(titulo);
        lblTitulo.setStyle("-fx-text-fill: #3d8d7a;");

        // Middle Box (Number + Icon)
        HBox middleBox = new HBox();
        middleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox numberBox = new VBox(2);
        numberBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        numberBox.setPadding(new javafx.geometry.Insets(5, 0, 15, 0));
        
        Label lblCantidad = new Label("0");
        animarConteo(lblCantidad, cantidad);
        lblCantidad.setStyle("-fx-text-fill: #3D8D7A; -fx-font-weight: bold; -fx-font-size: 40px;");
        
        Label lblSubLabel = new Label(subLabelText.toUpperCase());
        lblSubLabel.setStyle("-fx-text-fill: #b0bec5; -fx-font-weight: bold;");
        numberBox.getChildren().addAll(lblCantidad, lblSubLabel);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        ImageView icon = new ImageView();
        try {
            icon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream(iconPath)));
        } catch(Exception e) { /* Icon not found */ }
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        
        javafx.scene.layout.StackPane iconBg = new javafx.scene.layout.StackPane(icon);
        iconBg.setStyle("-fx-background-color: white; -fx-background-radius: 50;");
        iconBg.setPadding(new javafx.geometry.Insets(8));
        iconBg.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0,0,0,0.05)));
        
        middleBox.getChildren().addAll(numberBox, spacer, iconBg);

        // Details container
        VBox detallesContainer = new VBox();
        Label lblSubtitulo = new Label(detallesList.isEmpty() ? "Sin registros recientes" : "Registros recientes:");
        lblSubtitulo.setStyle("-fx-text-fill: #7f8c8d;");

        detallesContainer.getChildren().add(lblSubtitulo);

        java.util.List<Label> labelsDetalle = new java.util.ArrayList<>();
        for (String det : detallesList) {
            Label lblDetalle = new Label(det);
            lblDetalle.setStyle("-fx-text-fill: #34495e; -fx-font-style: italic;");
            detallesContainer.getChildren().add(lblDetalle);
            labelsDetalle.add(lblDetalle);
        }

        tarjeta.getChildren().addAll(lblTitulo, middleBox, detallesContainer);

        // --- LÓGICA DE FUENTES DINÁMICAS (RESPONSIVE POR ESCALONES) ---
        // Al usar escalones (Tiers) evitamos el recálculo circular que causa el parpadeo
        tarjeta.widthProperty().addListener((obs, oldVal, newVal) -> {
            double w = newVal.doubleValue();
            double f = 1.0;

            if (w > 700) {
                f = 1.6;
            } else if (w > 500) {
                f = 1.35;
            } else if (w > 400) {
                f = 1.15;
            }
            if (tarjeta.getUserData() != null && (double) tarjeta.getUserData() == f) {
                return;
            }
            tarjeta.setUserData(f);

            tarjeta.setSpacing(8 * f);
            detallesContainer.setSpacing(4 * f);

            lblTitulo.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 18.0 * f));
            lblCantidad.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 40.0 * f));
            lblSubLabel.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 10.0 * f));
            lblSubtitulo.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 13.0 * f));

            for (Label lDet : labelsDetalle) {
                lDet.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.NORMAL, 13.0 * f));
            }
        });
    }

    private void populateStaffCard(VBox tarjeta) {
        if (tarjeta == null) return;
        tarjeta.getChildren().clear();
        tarjeta.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        tarjeta.setPadding(new javafx.geometry.Insets(20, 25, 20, 25));
        tarjeta.setCursor(javafx.scene.Cursor.HAND);
        tarjeta.setSpacing(10);

        tarjeta.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffffff, #E1BEE7); -fx-background-radius: 20;");

        // Top Title
        Label lblTitulo = new Label("Staff");
        lblTitulo.setStyle("-fx-text-fill: #3D8D7A; -fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Middle Box (Number + Icon)
        HBox middleBox = new HBox();
        middleBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        VBox numberBox = new VBox(2);
        numberBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        numberBox.setPadding(new javafx.geometry.Insets(5, 0, 15, 0));
        
        Label lblCantidad = new Label("0");
        animarConteo(lblCantidad, 3); // Valor simulado para Staff
        lblCantidad.setStyle("-fx-text-fill: #3D8D7A; -fx-font-weight: bold; -fx-font-size: 40px;");
        
        Label lblSubLabel = new Label("ACTIVOS");
        lblSubLabel.setStyle("-fx-text-fill: #b0bec5; -fx-font-weight: bold;");
        numberBox.getChildren().addAll(lblCantidad, lblSubLabel);

        javafx.scene.layout.Region spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        ImageView icon = new ImageView();
        try {
            icon.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/Icon_Staff.png")));
        } catch(Exception e) { /* Icon not found */ }
        icon.setFitWidth(24);
        icon.setFitHeight(24);
        
        javafx.scene.layout.StackPane iconBg = new javafx.scene.layout.StackPane(icon);
        iconBg.setStyle("-fx-background-color: white; -fx-background-radius: 50;");
        iconBg.setPadding(new javafx.geometry.Insets(8));
        iconBg.setEffect(new javafx.scene.effect.DropShadow(5, javafx.scene.paint.Color.rgb(0,0,0,0.05)));
        
        middleBox.getChildren().addAll(numberBox, spacer, iconBg);

        tarjeta.getChildren().addAll(lblTitulo, middleBox);

        try {
            com.mycompany.aplicacion.persistencia.Conexion cx = new com.mycompany.aplicacion.persistencia.Conexion();
            java.sql.Connection conn = cx.estableceConexion();
            if (conn != null) {
                String sqlTop = "SELECT id, nombre, apellidos, tipo_rol FROM tb_usuario_web WHERE LOWER(tipo_rol) IN ('staff', 'veterinario', 'recepcionista') LIMIT 3";
                java.sql.PreparedStatement psTop = conn.prepareStatement(sqlTop);
                java.sql.ResultSet rsTop = psTop.executeQuery();
                while(rsTop.next()) {
                    int id = rsTop.getInt("id");
                    String nom = rsTop.getString("nombre");
                    String ape = rsTop.getString("apellidos");
                    String nombreCompleto = (nom != null ? nom : "") + " " + (ape != null ? ape : "");
                    nombreCompleto = nombreCompleto.trim();
                    if (nombreCompleto.isEmpty()) nombreCompleto = "Usuario";

                    String rol = rsTop.getString("tipo_rol");
                    if (rol != null && !rol.isEmpty()) {
                        rol = rol.substring(0, 1).toUpperCase() + rol.substring(1).toLowerCase();
                    } else {
                        rol = "Staff";
                    }

                    // Create row
                    HBox row = new HBox(10);
                    row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                    ImageView avatar = new ImageView();
                    String[] avas = {"Ava_perro1.png", "Ava_perro2.png", "Ava_gato.png", "Ava_conejo.png", "Ava_perro3.png", "Ava_perro4.png"};
                    String avatarName = avas[id % avas.length];
                    try {
                        avatar.setImage(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/" + avatarName)));
                    } catch(Exception ex) {
                        // Avatar not found
                    }
                    avatar.setFitWidth(32);
                    avatar.setFitHeight(32);
                    javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(16, 16, 16);
                    avatar.setClip(clip);

                    VBox textContainer = new VBox(2);
                    textContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    Label lblNombre = new Label(nombreCompleto);
                    lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2c3e50;");
                    Label lblRol = new Label(rol);
                    lblRol.setStyle("-fx-font-size: 11px; -fx-text-fill: #95a5a6;");
                    textContainer.getChildren().addAll(lblNombre, lblRol);

                    row.getChildren().addAll(avatar, textContainer);
                    tarjeta.getChildren().add(row);
                }
            }
        } catch (Exception e) {
            // Error loading staff
        }
    }

    // Método para que el VeterinarioController se "presente"
    public void setPadre(VeterinarioController padre) {
        this.padre = padre;
    }

    @FXML
    private void irAMascotas(MouseEvent event) {
        if (padre != null && padre.getbMascotas() != null) {
            padre.navegar(padre.getbMascotas(), "SeccionMascotas");
        }
    }

    @FXML
    private void irACitas(MouseEvent event) {
        if (padre != null && padre.getbCitas() != null) {
            if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
                padre.navegar(padre.getbCitas(), "SeccionCitasStaff");
            } else {
                padre.navegar(padre.getbCitas(), "SeccionCitas");
            }
        }
    }

    @FXML
    private void irAInventario(MouseEvent event) {
        if (padre != null && padre.getbInventario() != null) {
            padre.navegar(padre.getbInventario(), "SeccionInventario");
        }
    }

    @FXML
    private void irAReportes(MouseEvent event) {
        if (padre != null && padre.getbReportes() != null) {
            padre.navegar(padre.getbReportes(), "SeccionReportes");
        }
    }

    @FXML
    private void irAStaff(MouseEvent event) {
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) return; // Candado adicional de seguridad
        if (padre != null) {
            padre.mostrarVistaStaff(null);
        }
    }

}
