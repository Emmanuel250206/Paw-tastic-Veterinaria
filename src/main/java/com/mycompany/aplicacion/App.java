package com.mycompany.aplicacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static String rolUsuario = ""; // Puede ser "Veterinario" o "Staff"

    public static String getRolUsuario() {
        return rolUsuario;
    }

    public static void setRolUsuario(String rolUsuario) {
        App.rolUsuario = rolUsuario;
    }

    @Override
    public void start(Stage stage) throws IOException {
        //prueba de conexion con la base de datos
        
        Parent root = loadFXML("fxml/VeterinariaP1");
        scene = new Scene(root);
        stage.setScene(scene);

        // El Login no debe estirarse
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        // Usamos el cargador de clases del sistema para buscar el recurso
        java.net.URL url = App.class.getResource("/" + fxml + ".fxml");

        if (url == null) {
            // Esto nos dirá exactamente qué ruta está fallando en la consola
            throw new IOException("¡No encontré el archivo! Busqué en: /com/mycompany/aplicacion/" + fxml + ".fxml");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        return fxmlLoader.load();
    }

    public static Scene getScene() {
        return scene;
    }

    public static Stage getStage() {
        return (Stage) scene.getWindow();
    }

    public static void main(String[] args) {
        launch();
    }
} // Fin de la clase
