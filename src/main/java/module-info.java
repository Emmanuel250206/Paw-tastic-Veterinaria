module com.mycompany.aplicacion {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    // Esto permite que el FXMLLoader lea tus archivos FXML
    opens fxml to javafx.fxml;
    opens com.mycompany.aplicacion to javafx.fxml;

    opens com.mycompany.aplicacion.controllers to javafx.fxml;
    opens com.mycompany.aplicacion.modelo to javafx.base;

    exports com.mycompany.aplicacion;
    
    // Configuración de módulos completada
}