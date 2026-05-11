module com.mycompany.aplicacion {
    requires java.sql;
    requires mysql.connector.j;
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires transitive javafx.graphics;

    // Permite que el FXMLLoader acceda a los controladores y modelos
    opens com.mycompany.aplicacion to javafx.fxml;
    opens com.mycompany.aplicacion.controllers to javafx.fxml;
    opens com.mycompany.aplicacion.modelo to javafx.base;

    exports com.mycompany.aplicacion;
}