package com.mycompany.aplicacion.modelo;

import java.time.LocalDateTime;

public class Bitacora {
    private int id;
    private LocalDateTime fechaHora;
    private int idUsuarioWeb;
    private String usuarioNombre;
    private String modulo;
    private String detalle;

    public Bitacora(int id, LocalDateTime fechaHora, int idUsuarioWeb, String usuarioNombre, String modulo, String detalle) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.idUsuarioWeb = idUsuarioWeb;
        this.usuarioNombre = usuarioNombre;
        this.modulo = modulo;
        this.detalle = detalle;
    }

    public int getId() { return id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getIdUsuarioWeb() { return idUsuarioWeb; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public String getModulo() { return modulo; }
    public String getDetalle() { return detalle; }
}
