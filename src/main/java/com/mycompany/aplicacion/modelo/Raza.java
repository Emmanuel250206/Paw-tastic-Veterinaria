package com.mycompany.aplicacion.modelo;

public class Raza {
    private int id;
    private int idEspecie;
    private String nombre;
    private String descripcion;

    public Raza(int id, int idEspecie, String nombre, String descripcion) {
        this.id = id;
        this.idEspecie = idEspecie;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public int getIdEspecie() { return idEspecie; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return nombre;
    }
}
