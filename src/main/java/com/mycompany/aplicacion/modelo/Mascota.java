/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.modelo;

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

    // Descripciones clínicas de catálogo
    private String descEspecie;
    private String descRaza;

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

    public Mascota(int id, String nombre, String especie, String raza, int edad,
            String idCollar, String nombrePropietario, String telefonoPropietario,
            String direccionPropietario, String historialClinico, String descEsp, String descRaz) {
        this(id, nombre, especie, raza, edad, idCollar, nombrePropietario, telefonoPropietario, direccionPropietario, historialClinico);
        this.descEspecie = descEsp;
        this.descRaza = descRaz;
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

    public String getDescEspecie() { return descEspecie; }
    public String getDescRaza() { return descRaza; }

    // Setters
    public void setHistorialClinico(String historialClinico) {
        this.historialClinico = historialClinico;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getPropietario() {
        return getNombrePropietario();
    }

    @Override
    public String toString() {
        return nombre + " (" + especie + " - " + raza + ")";
    }
}
