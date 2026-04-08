error id: file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/controllers/VeterinarioController.java
file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/controllers/VeterinarioController.java
### com.thoughtworks.qdox.parser.ParseException: syntax error @[190,12]

error in qdox parser
file content:
```java
offset: 6206
uri: file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/controllers/VeterinarioController.java
text:
```scala
package com.mycompany.aplicacion.controllers;

import com.mycompany.aplicacion.App;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class VeterinarioController implements Initializable {

    @FXML
    private BorderPane bpPrincipal;

    // Declaración de todos los botones de la barra lateral
    @FXML
    private Button bDashboard;
    @FXML
    private Button bMascotas;
    @FXML
    private Button bCitas;
    @FXML
    private Button bInventario;
    @FXML
    private Button bStaff;
    @FXML
    private Button bReportes;
    @FXML
    private Button btnCerrarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Candado de tamaño para proteger el menú lateral
        Platform.runLater(() -> {
            Stage stage = (Stage) bpPrincipal.getScene().getWindow();
            if (stage != null) {
                // Bloqueamos la ventana para que no pueda ser más pequeña que el menú
                stage.setMinWidth(1100);
                stage.setMinHeight(760); // Se ajustó a 760 para cubrir todos los botones + márgenes reales

                // Forzamos a que la ventana se ajuste a este nuevo mínimo ahora mismo
                if (stage.getHeight() < 760) {
                    stage.setHeight(760);
                }
            }
        });
        // 2. Verificar rol de usuario (Tu lógica actual)
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) {
            // El Staff puede ver Mascotas, Citas e Inventario.
            // Ocultamos Reportes y Sección Staff (Admin)
            if (bReportes != null) {
                bReportes.setVisible(false);
                bReportes.setManaged(false);
            }
            if (bStaff != null) {
                bStaff.setVisible(false);
                bStaff.setManaged(false);
            }
        }

        navegar(bDashboard, "SeccionDashboard");
    }

    /**
     * MÉTODO MAESTRO: Gestiona el cambio de color de los botones
     * y carga el FXML en el centro del BorderPane.
     */
    public void navegar(Button botonPulsado, String nombreFXML) {
        // 1. Lista de todos los botones para resetear estilos
        Button[] botonesMenu = { bDashboard, bMascotas, bCitas, bInventario, bStaff, bReportes };

        for (Button b : botonesMenu) {
            if (b != null) {
                b.getStyleClass().remove("boton-menu-activo");
                if (!b.getStyleClass().contains("boton-menu")) {
                    b.getStyleClass().add("boton-menu");
                }
            }
        }

        // 2. Aplicar estilo activo al botón que se presionó (si existe)
        if (botonPulsado != null) {
            botonPulsado.getStyleClass().remove("boton-menu");
            botonPulsado.getStyleClass().add("boton-menu-activo");
        }

        // 3. Cargar la nueva vista usando una instancia de FXMLLoader
        try {
            String ruta = "/fxml/" + nombreFXML + ".fxml";
            URL url = getClass().getResource(ruta);

            if (url == null) {
                System.err.println("❌ ERROR: No se encuentra el archivo en: " + ruta);
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node vista = loader.load();

            if (bpPrincipal == null) {
                System.err.println("❌ ERROR: bpPrincipal es NULL. Revisa el fx:id en Scene Builder.");
                return;
            }

            if (nombreFXML.equals("SeccionDashboard")) {
                DashboardController dashCtrl = loader.getController();
                if (dashCtrl != null) {
                    dashCtrl.setPadre(this);
                } else {
                    System.err.println("⚠️ ADVERTENCIA: SeccionDashboard no tiene un controlador asignado.");
                }
            }

            bpPrincipal.setCenter(vista);
            System.out.println("✅ Vista cargada con éxito: " + nombreFXML);

        } catch (IOException e) {
            System.err.println("❌ ERROR CRÍTICO al cargar " + nombreFXML);
            e.printStackTrace();
        }
    }

    // --- MÉTODOS ON ACTION PARA LOS BOTONES ---

    @FXML
    public void mostrarVistaDashboard(ActionEvent event) {
        navegar(bDashboard, "SeccionDashboard");
    }

    @FXML
    public void mostrarVistaMascotas(ActionEvent event) {
        navegar(bMascotas, "SeccionMascotas");
    }

    @FXML
    public void mostrarVistaCitas(ActionEvent event) {
        navegar(bCitas, "SeccionCitas");
    }

    @FXML
    public void mostrarVistaInventario(ActionEvent event) {
        navegar(bInventario, "SeccionInventario");
    }

    @FXML
    public void mostrarVistaStaff(ActionEvent event) {
        if ("Staff".equalsIgnoreCase(App.getRolUsuario())) return; // Bloqueo de seguridad a nivel router
        navegar(bStaff, "SeccionStaff");
    }

    @FXML
    public void mostrarVistaReportes(ActionEvent event) {
        navegar(bReportes, "SeccionReportes");
    }

    // --- LÓGICA DE SESIÓN (CORREGIDA PARA UBUNTU/WINDOWS) ---

    @FXML
    private void onCerrarSesionClick(ActionEvent event) {
            try {
                App.setRoot("fxml/VeterinariaP1");

                Platform.runLater(() -> {
                    Stage stage = (Stage) App.getScene().getWindow();
                    stage.setMaximized(false);
                    stage.setResizable(true);
                    stage.sizeToScene();

                    // Medidas estándar para tu Login
                    stage.setWidth(852);
                    stage.setHeight(535);
                    stage.setResizable(false);

                    Platform.runLater(() -> stage.centerOnScreen());
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Getters para que el Dashboard pueda acceder a los botones del menú
    public B@@utton getbMascotas() {
        return bMascotas;
    }

    public Button getbCitas() {
        return bCitas;
    }

    public Button getbInventario() {
        return bInventario;
    }

    public Button getbReportes() {
        return bReportes;
    }

}
```

```



#### Error stacktrace:

```
com.thoughtworks.qdox.parser.impl.Parser.yyerror(Parser.java:2025)
	com.thoughtworks.qdox.parser.impl.Parser.yyparse(Parser.java:2147)
	com.thoughtworks.qdox.parser.impl.Parser.parse(Parser.java:2006)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:232)
	com.thoughtworks.qdox.library.SourceLibrary.parse(SourceLibrary.java:190)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:94)
	com.thoughtworks.qdox.library.SourceLibrary.addSource(SourceLibrary.java:89)
	com.thoughtworks.qdox.library.SortedClassLibraryBuilder.addSource(SortedClassLibraryBuilder.java:162)
	com.thoughtworks.qdox.JavaProjectBuilder.addSource(JavaProjectBuilder.java:174)
	scala.meta.internal.mtags.JavaMtags.indexRoot(JavaMtags.scala:49)
	scala.meta.internal.metals.SemanticdbDefinition$.foreachWithReturnMtags(SemanticdbDefinition.scala:99)
	scala.meta.internal.metals.Indexer.indexSourceFile(Indexer.scala:560)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3(Indexer.scala:691)
	scala.meta.internal.metals.Indexer.$anonfun$reindexWorkspaceSources$3$adapted(Indexer.scala:688)
	scala.collection.IterableOnceOps.foreach(IterableOnce.scala:630)
	scala.collection.IterableOnceOps.foreach$(IterableOnce.scala:628)
	scala.collection.AbstractIterator.foreach(Iterator.scala:1313)
	scala.meta.internal.metals.Indexer.reindexWorkspaceSources(Indexer.scala:688)
	scala.meta.internal.metals.MetalsLspService.$anonfun$onChange$2(MetalsLspService.scala:940)
	scala.runtime.java8.JFunction0$mcV$sp.apply(JFunction0$mcV$sp.scala:18)
	scala.concurrent.Future$.$anonfun$apply$1(Future.scala:691)
	scala.concurrent.impl.Promise$Transformation.run(Promise.scala:500)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

QDox parse error in file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/controllers/VeterinarioController.java