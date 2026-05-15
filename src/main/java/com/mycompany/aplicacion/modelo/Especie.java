package com.mycompany.aplicacion.modelo;

public class Especie {
    private int id;
    private String nombre;
    private String descripcion;

    public Especie(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
