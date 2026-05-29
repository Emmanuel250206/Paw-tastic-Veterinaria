/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.persistencia.MascotaDAO;
import com.mycompany.aplicacion.modelo.Mascota;
import com.mycompany.aplicacion.modelo.UserSession;
import com.mycompany.aplicacion.modelo.Especie;
import com.mycompany.aplicacion.modelo.Raza;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import com.mycompany.aplicacion.util.ExitDialog;
import com.mycompany.aplicacion.util.Toast;
import java.io.IOException;
import java.io.InputStream;
import javafx.event.ActionEvent;
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
    @FXML private TextField  txtNombreMascota;
    @FXML private ComboBox<Especie> comboEspecie;
    @FXML private ComboBox<Raza> comboRaza;
    @FXML private TextField  txtEdad;
    @FXML private TextField  txtPropietario;
    @FXML private TextField  txtIdMascota;

    @FXML private TextArea   txtHistorial;
    @FXML private TextField  txtBuscar;
    @FXML private Button     btnGuardar;
    @FXML private Button     btnCancelar;
    @FXML private Button     btnModificarMascota;
    @FXML private Button     btnGestionRazas;
    @FXML private HBox       containerBotones;

    private ObservableList<Mascota> masterData;
    private FilteredList<Mascota> listaFiltrada;

    // Perfil header
    @FXML private ImageView imgPerfilMascotas;
    @FXML private Label     lblNombreMascotas;
    @FXML private Label     lblRolMascotas;
    @FXML private HBox      hboxPerfil;
    private ContextMenu menuPerfil;

    // Ficha avatar + badge de estado
    @FXML private ImageView imgMascotaFicha;
    @FXML private Label     lblEstadoFicha;

    // Mascota actualmente seleccionada (para guardar en BD)
    private Mascota mascotaSeleccionada;

    @FXML
    public void initialize() {
        try {
            // Perfil de usuario en el header
            if (imgPerfilMascotas != null) {
                UserSession.loadProfileImage(imgPerfilMascotas);
            }
            if (lblNombreMascotas != null) {
                lblNombreMascotas.setText(UserSession.getInstance().getUserName());
            }
            if (lblRolMascotas != null) {
                lblRolMascotas.setText(UserSession.getInstance().getUserRole());
            }
            construirMenuPerfil();

            // Cargar especies en combo
            if (comboEspecie != null) {
                comboEspecie.setItems(MascotaDAO.listarEspecies());
                
                // Listener de cambio de especie (cascada)
                comboEspecie.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        ObservableList<Raza> razas = MascotaDAO.listarRazasPorEspecie(newVal.getId());
                        if (comboRaza != null) {
                            comboRaza.setItems(razas);
                            if (!razas.isEmpty()) {
                                comboRaza.setValue(razas.get(0));
                            } else {
                                comboRaza.setValue(null);
                            }
                        }
                    } else {
                        if (comboRaza != null) {
                            comboRaza.setItems(FXCollections.observableArrayList());
                            comboRaza.setValue(null);
                        }
                    }
                });
            }

            // Cargar datos desde BD y configurar celdas en memoria con FilteredList
            if (listaMascotas != null) {
                listaMascotas.setFocusTraversable(false);
                masterData = MascotaDAO.getTodas();
                listaFiltrada = new FilteredList<>(masterData, p -> true);
                listaMascotas.setItems(listaFiltrada);
                
                // Seleccion -> panel de detalles
                listaMascotas.getSelectionModel().selectedItemProperty().addListener(
                        (obs, oldSel, mascota) -> {
                            if (mascota != null) {
                                mostrarMascota(mascota);
                            }
                        }
                );

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
                            if (nombre != null) nombre.setText(mascota.getNombre());
                            if (especie != null) especie.setText(mascota.getEspecie() + " · " + mascota.getRaza());
                            if (estado != null) estado.setText("Activo");

                            // Avatar segun especie
                            if (avatar != null) {
                                String ruta = resolverRutaAvatar(mascota.getEspecie());
                                InputStream stream = getClass().getResourceAsStream(ruta);
                                if (stream != null) {
                                    avatar.setImage(new Image(stream));
                                }
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
            }

            if (txtBuscar != null) {
                // Búsqueda en tiempo real (por nombre de mascota o propietario)
                txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
                    if (listaFiltrada != null) {
                        listaFiltrada.setPredicate(mascota -> {
                            if (newValue == null || newValue.isEmpty()) return true;
                            String lowerCaseFilter = newValue.toLowerCase().trim();

                            boolean matchNombre = mascota.getNombre().toLowerCase().contains(lowerCaseFilter);
                            boolean matchPropietario = mascota.getPropietario() != null && 
                                                       mascota.getPropietario().toLowerCase().contains(lowerCaseFilter);

                            return matchNombre || matchPropietario;
                        });
                    }
                });
            }

            // 3. Role Enforcement (Visibility)
            String role = UserSession.getInstance().getUserRole();
            if ("Staff".equals(role)) {
                setFormulariosBloqueados(true);
                if (btnModificarMascota != null) {
                    btnModificarMascota.setVisible(false);
                    btnModificarMascota.setManaged(false);
                }
                if (btnGestionRazas != null) {
                    btnGestionRazas.setVisible(false);
                    btnGestionRazas.setManaged(false);
                }
                if (containerBotones != null) {
                    containerBotones.setVisible(false);
                    containerBotones.setManaged(false);
                }
            } else {
                // Veterinarians can edit, but locked by default
                setFormulariosBloqueados(true);
                if (containerBotones != null) {
                    containerBotones.setVisible(true);
                    containerBotones.setManaged(true);
                }
                
                // Estilos base (Professional Palette)
                String styleGuardar = "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-font-family: 'Segoe UI Bold'; -fx-font-size: 15px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;";
                String styleGuardarHover = "-fx-background-color: #2ECC71; -fx-text-fill: white; -fx-font-family: 'Segoe UI Bold'; -fx-font-size: 15px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;";
                
                String styleCancelar = "-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;";
                String styleCancelarHover = "-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;";

                // Aplicar Hover Effects y Actions de manera segura
                if (btnGuardar != null) {
                    btnGuardar.setOnMouseEntered(e -> {
                        if (!btnGuardar.isDisable()) {
                            btnGuardar.setStyle(styleGuardarHover);
                        }
                    });
                    btnGuardar.setOnMouseExited(e -> {
                        btnGuardar.setStyle(styleGuardar);
                    });
                    btnGuardar.setOnAction(e -> manejarGuardar());
                }
                
                if (btnCancelar != null) {
                    btnCancelar.setOnMouseEntered(e -> {
                        btnCancelar.setStyle(styleCancelarHover);
                    });
                    btnCancelar.setOnMouseExited(e -> {
                        btnCancelar.setStyle(styleCancelar);
                    });
                    btnCancelar.setOnAction(e -> manejarCancelar());
                }
            }
        } catch (Throwable t) {
            System.err.println("⚠️ ERROR al inicializar MascotasController: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void manejarCancelar() {
        limpiarFicha();
    }

    private void refrescarLista() {
        if (masterData != null) {
            masterData.setAll(MascotaDAO.getTodas());
        }
    }

    /**
     * Lógica de guardado seguro con Modal de Confirmación (Estilo Dribbble).
     */
    private void manejarGuardar() {
        if (mascotaSeleccionada == null) {
            Toast.showToast("No hay mascota seleccionada", 2);
            return;
        }
        javafx.stage.Stage stage = (javafx.stage.Stage) btnGuardar.getScene().getWindow();

        ExitDialog.mostrar(
            stage,
            "¿Guardar cambios?",
            "Esta acción actualizará el registro médico y la información de la mascota.",
            "SÍ",
            "NO",
            () -> {
                String nombre = txtNombreMascota.getText().trim();
                String edadStr = txtEdad.getText().trim();
                String propietario = txtPropietario.getText().trim();
                Especie esp = comboEspecie.getValue();
                Raza raz = comboRaza.getValue();
                String historial = txtHistorial.getText();

                int edad = 0;
                try {
                    String cleanEdad = edadStr.replaceAll("[^0-9]", "");
                    if (!cleanEdad.isEmpty()) {
                        edad = Integer.parseInt(cleanEdad);
                    }
                } catch (Exception ignored) {}

                String fechaNac = java.time.LocalDate.now().minusYears(edad).toString();
                int idEsp = esp != null ? esp.getId() : 1;
                int idRaz = raz != null ? raz.getId() : 1;

                boolean okInfo = MascotaDAO.actualizarMascota(mascotaSeleccionada.getId(), nombre, idEsp, idRaz, fechaNac, propietario);
                boolean okHist = MascotaDAO.guardarHistorial(mascotaSeleccionada.getId(), historial);

                if (okInfo && okHist) {
                    Toast.showToast("Información y expediente actualizados 🐾", 2);
                    mascotaSeleccionada.setHistorialClinico(historial);
                    setFormulariosBloqueados(true);

                    // Sincronizar la lista y re-seleccionar la mascota para que se visualicen los cambios
                    int selectedIndex = listaMascotas.getSelectionModel().getSelectedIndex();
                    refrescarLista();
                    if (selectedIndex >= 0 && selectedIndex < listaMascotas.getItems().size()) {
                        listaMascotas.getSelectionModel().select(selectedIndex);
                    } else {
                        limpiarFicha();
                    }
                } else {
                    Toast.showToast("⚠ Error al guardar. Verifica la conexión con la BD.", 3);
                }
            }
        );
    }

    private void limpiarFicha() {
        if (txtNombreMascota != null) txtNombreMascota.setText("-");
        if (comboEspecie != null) comboEspecie.setValue(null);
        if (comboRaza != null) comboRaza.setValue(null);
        if (txtEdad != null) txtEdad.setText("-");
        if (txtPropietario != null) txtPropietario.setText("-");
        if (txtIdMascota != null) txtIdMascota.setText("-");
        txtHistorial.setText("");
        
        if (imgMascotaFicha != null) {
            imgMascotaFicha.setImage(null);
        }
        if (lblEstadoFicha != null) {
            lblEstadoFicha.setText("-");
            lblEstadoFicha.getStyleClass().removeAll("status-healthy", "status-treatment");
        }
        
        listaMascotas.getSelectionModel().clearSelection();
        setFormulariosBloqueados(true);
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

    @FXML
    private void abrirGestionRazas(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionRazas.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Razas");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Sincronizar combo boxes al cerrar el modal
            if (comboEspecie != null) {
                Especie selectedEsp = comboEspecie.getValue();
                comboEspecie.setItems(MascotaDAO.listarEspecies());
                if (selectedEsp != null) {
                    for (Especie esp : comboEspecie.getItems()) {
                        if (esp.getId() == selectedEsp.getId()) {
                            comboEspecie.setValue(esp);
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[MascotasController] Error al abrir gestión de razas: " + e.getMessage());
        }
    }

    private void setCamposEditables(boolean editable) {
        if (txtNombreMascota != null) txtNombreMascota.setEditable(editable);
        if (txtEdad != null) txtEdad.setEditable(editable);
        if (txtPropietario != null) txtPropietario.setEditable(editable);
        if (comboEspecie != null) comboEspecie.setDisable(!editable);
        if (comboRaza != null) comboRaza.setDisable(!editable);
        if (txtIdMascota != null) txtIdMascota.setEditable(false); 
    }

    private void setFormulariosBloqueados(boolean status) {
        if (txtHistorial != null) {
            txtHistorial.setEditable(!status);
        }
        if (btnGuardar != null) {
            btnGuardar.setDisable(status);
        }
        setCamposEditables(!status);
        if (btnModificarMascota != null) {
            if (status) {
                btnModificarMascota.setText("Modificar Información");
                btnModificarMascota.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;");
            } else {
                btnModificarMascota.setText("Modificando...");
                btnModificarMascota.setStyle("-fx-background-color: #D35400; -fx-text-fill: white; -fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 14px; -fx-background-radius: 15; -fx-pref-height: 40; -fx-cursor: hand;");
            }
        }
    }

    @FXML
    private void handleModificarClick(ActionEvent event) {
        String role = UserSession.getInstance().getUserRole();
        if ("Staff".equals(role)) {
            Toast.showToast("No tiene permisos para modificar la información.", 2);
            return;
        }

        // Unlock all input fields so the Veterinario can edit values
        setFormulariosBloqueados(false);

        // Enable the save button to register changes
        btnGuardar.setDisable(false);

        Toast.show(com.mycompany.aplicacion.App.getStage(), "Campos desbloqueados. Puede editar la información de la mascota.");
    }

    // Metodo clave: rellena la ficha de detalle (Punto 1)
    private void mostrarMascota(Mascota m) {
        this.mascotaSeleccionada = m;
        // Direct FXML injections
        if (txtNombreMascota != null) txtNombreMascota.setText(m.getNombre());
        if (txtEdad != null) txtEdad.setText(m.getEdad() + " años");
        if (txtPropietario != null) txtPropietario.setText(m.getNombrePropietario());
        if (txtIdMascota != null) txtIdMascota.setText(String.valueOf(m.getId()));

        // Seleccionar Especie y Raza (cascada)
        if (comboEspecie != null) {
            Especie matchingEspecie = null;
            for (Especie esp : comboEspecie.getItems()) {
                if (esp.getNombre().equalsIgnoreCase(m.getEspecie())) {
                    matchingEspecie = esp;
                    break;
                }
            }
            comboEspecie.setValue(matchingEspecie);

            if (matchingEspecie != null && comboRaza != null) {
                Raza matchingRaza = null;
                for (Raza r : comboRaza.getItems()) {
                    if (r.getNombre().equalsIgnoreCase(m.getRaza())) {
                        matchingRaza = r;
                        break;
                    }
                }
                comboRaza.setValue(matchingRaza);
            }
        }

        if (m.getDescEspecie() != null && !m.getDescEspecie().isEmpty()) {
            Tooltip espTip = new Tooltip("Contexto: " + m.getDescEspecie());
            espTip.setStyle("-fx-font-size: 13px; -fx-background-color: #3D8D7A;");
            if (comboEspecie != null) comboEspecie.setTooltip(espTip);
        } else {
            if (comboEspecie != null) comboEspecie.setTooltip(null);
        }
        
        if (m.getDescRaza() != null && !m.getDescRaza().isEmpty()) {
            Tooltip razTip = new Tooltip("ALERTA CLÍNICA: " + m.getDescRaza());
            razTip.setStyle("-fx-font-size: 13px; -fx-background-color: #E74C3C; -fx-font-weight: bold;");
            if (comboRaza != null) {
                comboRaza.setTooltip(razTip);
                comboRaza.setStyle("-fx-background-color: white; -fx-border-color: #E74C3C; -fx-border-radius: 5;");
            }
        } else {
            if (comboRaza != null) {
                comboRaza.setTooltip(null);
                comboRaza.setStyle("-fx-background-color: white; -fx-border-color: #3D8D7A; -fx-border-radius: 5;");
            }
        }
        
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
        
        setFormulariosBloqueados(true);
    }
}
