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

@Override
public void start(Stage stage) throws IOException {
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

public static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/" + fxml + ".fxml"));
    return fxmlLoader.load();
}
    public static void main(String[] args) {
        launch();
    }

public static Scene getScene() {
    return scene;
}

public static Stage getStage() {
    return (Stage) scene.getWindow();
}
    
    
}

