error id: file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/CConexion.java:java/lang/Class#forName().
file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/CConexion.java
empty definition using pc, found symbol in pc: java/lang/Class#forName().
semanticdb not found
empty definition using fallback
non-local guesses:

offset: 752
uri: file://<WORKSPACE>/src/main/java/com/mycompany/aplicacion/CConexion.java
text:
```scala
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion;
import java.sql.Connection;
import java.sql.DriverManager;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
/**
 *
 * @author USUARIO
 */
public class CConexion {
    Connection conectar;
    
    String usuario = "root";
    String contrasenia = "juanrc06";
    String bd = "pawtastic";
    String ip = "localhost";
    String puerto = "3306";

    String cadena = "jdbc:mysql://"+ip+":"+puerto+"/"+bd;
    
    public Connection estableceConexion (){
        try{
            Class.for@@Name("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection(cadena,usuario,contrasenia);
        } catch (Exception e) {
            conectar = null;
            e.printStackTrace();
        }
        return conectar;
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: java/lang/Class#forName().