package com.mycompany.aplicacion.modelo;

public class Citas {

    private int id;
    private String fecha;
    private String hora;
    private String nombreMascota;
    private String nombrePropietario;
    private String veterinario;
    private String motivo;
    private String estado;

    public Citas(int id, String fecha, String hora, String nombreMascota, String nombrePropietario, String veterinario,
            String motivo, String estado) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.nombreMascota = nombreMascota;
        this.nombrePropietario = nombrePropietario;
        this.veterinario = veterinario;
        this.motivo = motivo;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombrePropietario() {
        return nombrePropietario;
    }

    public void setNombrePropietario(String nombrePropietario) {
        this.nombrePropietario = nombrePropietario;
    }

    public String getVeterinario() {
        return veterinario;
    }

    public void setVeterinario(String veterinario) {
        this.veterinario = veterinario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return fecha + " " + hora + " - " + nombreMascota + " (" + estado + ")";
    }
}
