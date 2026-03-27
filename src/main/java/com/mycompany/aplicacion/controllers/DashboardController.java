/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.controllers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import com.mycompany.aplicacion.App;

/**
 *
 * @author emmanuel
 */
public class DashboardController {
    private VeterinarioController padre;

    @FXML private VBox cardInventario;
    @FXML private VBox cardReportes;

    @FXML
    private void initialize() {
        if ("Staff".equals(App.getRolUsuario())) {
            if (cardInventario != null) {
                cardInventario.setVisible(false);
                cardInventario.setManaged(false);
            }
            if (cardReportes != null) {
                cardReportes.setVisible(false);
                cardReportes.setManaged(false);
            }
        }
    }

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
