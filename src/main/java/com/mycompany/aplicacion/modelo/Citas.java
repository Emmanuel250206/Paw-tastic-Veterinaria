package com.mycompany.aplicacion.modelo;

public class Citas {

    /** Nivel de prioridad de la cita. URGENTE sube al inicio de la cola. */
    public enum Prioridad { URGENTE, NORMAL }

    private int id;
    private String fecha;
    private String hora;
    private String nombreMascota;
    private String nombrePropietario;
    private String veterinario;
    private String motivo;
    private String estado;
    private Prioridad prioridad;

    private String telefonoDueno;

    /** Constructor completo con prioridad explícita. */
    public Citas(int id, String fecha, String hora, String nombreMascota, String nombrePropietario, String telefonoDueno,
            String veterinario, String motivo, String estado, Prioridad prioridad) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.nombreMascota = nombreMascota;
        this.nombrePropietario = nombrePropietario;
        this.telefonoDueno = telefonoDueno;
        this.veterinario = veterinario;
        this.motivo = motivo;
        this.estado = estado;
        this.prioridad = prioridad;
    }

    /** Constructor legado: prioridad por defecto NORMAL. */
    public Citas(int id, String fecha, String hora, String nombreMascota, String nombrePropietario, String telefonoDueno,
            String veterinario, String motivo, String estado) {
        this(id, fecha, hora, nombreMascota, nombrePropietario, telefonoDueno, veterinario, motivo, estado, Prioridad.NORMAL);
    }

    // ── Getters / Setters ───────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getNombreMascota() { return nombreMascota; }
    public void setNombreMascota(String nombreMascota) { this.nombreMascota = nombreMascota; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public String getTelefonoDueno() { return telefonoDueno; }
    public void setTelefonoDueno(String telefonoDueno) { this.telefonoDueno = telefonoDueno; }

    public String getVeterinario() { return veterinario; }
    public void setVeterinario(String veterinario) { this.veterinario = veterinario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public boolean esUrgente() {
        return Prioridad.URGENTE.equals(prioridad);
    }

    @Override
    public String toString() {
        return hora + " - " + nombreMascota + " [" + prioridad + "]";
    }
}

