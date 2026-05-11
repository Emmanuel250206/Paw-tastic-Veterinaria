package com.mycompany.aplicacion.modelo;

public class VentaDTO {
    private String fecha;
    private String mascota;
    private String concepto;
    private double monto;

    public VentaDTO(String fecha, String mascota, String concepto, double monto) {
        this.fecha = fecha;
        this.mascota = mascota;
        this.concepto = concepto;
        this.monto = monto;
    }

    public String getFecha() { return fecha; }
    public String getMascota() { return mascota; }
    public String getConcepto() { return concepto; }
    public double getMonto() { return monto; }
}
