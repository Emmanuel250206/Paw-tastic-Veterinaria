package com.mycompany.aplicacion.modelo;

public class Staff {

    private int id;
    private String nombre;
    private String apellidos;
    private String rol;
    private String especialidad;
    private String telefono;
    private String email;
    private String turno;
    private String cedula;

    public Staff(int id, String nombre, String apellidos, String rol, String especialidad, String telefono,
            String email, String turno, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.rol = rol;
        this.especialidad = especialidad;
        this.telefono = telefono;
        this.email = email;
        this.turno = turno;
        this.cedula = cedula;
    }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + rol + ")";
    }
}
