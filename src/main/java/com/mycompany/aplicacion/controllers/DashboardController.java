/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author emmanuel
 */
public class DashboardController {
    private VeterinarioController padre;

    // Método para que el VeterinarioController se "presente"
    public void setPadre(VeterinarioController padre) {
        this.padre = padre;
    }

    @FXML
    private void irAMascotas(MouseEvent event) {
        if (padre != null) {
            padre.mostrarVistaMascotas(null); 
        }
    }
    
    @FXML
    private void irACitas(MouseEvent event) {
        if (padre != null) {
            padre.navegar(padre.getbCitas(), "SeccionCitas");
        }
    }

    @FXML
    private void irAInventario(MouseEvent event) {
        if (padre != null) {
            padre.navegar(padre.getbInventario(), "SeccionInventario");
        }
    }

    @FXML
    private void irAReportes(MouseEvent event) {
        if (padre != null) {
            padre.navegar(padre.getbReportes(), "SeccionReportes");
        }
    }
    
    
}
