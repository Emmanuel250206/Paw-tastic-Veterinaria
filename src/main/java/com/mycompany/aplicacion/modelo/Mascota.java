/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Mascota {

    // Datos de ficha completa
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private int edad;
    private String idCollar;
    private String nombrePropietario;
    private String telefonoPropietario;
    private String direccionPropietario;

    // Expediente clínico (como texto para mostrar)
    private String historialClinico;

    // Constructor completo
    public Mascota(int id, String nombre, String especie, String raza, int edad,
            String idCollar, String nombrePropietario, String telefonoPropietario,
            String direccionPropietario, String historialClinico) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.idCollar = idCollar;
        this.nombrePropietario = nombrePropietario;
        this.telefonoPropietario = telefonoPropietario;
        this.direccionPropietario = direccionPropietario;
        this.historialClinico = historialClinico;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecie() {
        return especie;
    }

    public String getRaza() {
        return raza;
    }

    public int getEdad() {
        return edad;
    }

    public String getIdCollar() {
        return idCollar;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public String getTelefonoPropietario() {
        return telefonoPropietario;
    }

    public String getDireccionPropietario() {
        return direccionPropietario;
    }

    public String getHistorialClinico() {
        return historialClinico;
    }

    // Setters
    public void setHistorialClinico(String historialClinico) {
        this.historialClinico = historialClinico;
    }

    @Override
    public String toString() {
        return nombre + " (" + especie + " - " + raza + ")";
    }
}
