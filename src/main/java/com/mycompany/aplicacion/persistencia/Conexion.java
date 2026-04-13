package com.mycompany.aplicacion.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    private Connection conectar;

    private String usuario = "root";
    private String contrasenia = "TuPasswordSegura123!";
    private String bd = "pawtastic";
    private String ip = "localhost";
    private String puerto = "3306";

    private String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public Connection estableceConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contrasenia);
        } catch (Exception e) {
            conectar = null;
            e.printStackTrace();
        }
        return conectar;
    }
}
