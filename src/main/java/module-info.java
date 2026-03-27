module com.mycompany.aplicacion {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    // Esto permite que el FXMLLoader lea tus archivos FXML
    opens fxml to javafx.fxml;
    opens com.mycompany.aplicacion to javafx.fxml;

    opens com.mycompany.aplicacion.controllers to javafx.fxml;
    opens com.mycompany.aplicacion.modelo to javafx.base;

    exports com.mycompany.aplicacion;
}