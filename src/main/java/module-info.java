module com.mycompany.aplicacion {
    requires javafx.controls;
    requires javafx.fxml;

    // Esto permite que el FXMLLoader lea tus archivos FXML
    opens fxml to javafx.fxml; 
    opens com.mycompany.aplicacion to javafx.fxml;

    exports com.mycompany.aplicacion;
}