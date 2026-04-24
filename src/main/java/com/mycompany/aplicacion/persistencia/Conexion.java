package com.mycompany.aplicacion.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    private Connection conectar;

    private String usuario = "root";
    private String contrasenia = "";
    private String bd = "pawtastic";
    private String ip = "localhost";
    private String puerto = "3306";

    public Connection estableceConexion(String user, String pass) {
        String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conectar = DriverManager.getConnection(cadena, user, pass);
        } catch (Exception e) {
            this.conectar = null;
            e.printStackTrace();
        }
        return this.conectar;
    }

    public Connection estableceConexion() {
        String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
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
