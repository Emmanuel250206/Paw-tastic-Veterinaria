package com.mycompany.aplicacion.modelo;

public class Inventario {

    private int id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private int stock_actual;
    private int stock_minimo;
    private String unidad_medida;
    private double precio_compra;
    private double precio_venta;
    private String fecha_caducidad;
    private int proveedor_id;

    public Inventario(int id, String nombre, String categoria, String descripcion, int stock_actual, int stock_minimo, String unidad_medida, double precio_compra, double precio_venta, String fecha_caducidad, int proveedor_id) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.stock_actual = stock_actual;
        this.stock_minimo = stock_minimo;
        this.unidad_medida = unidad_medida;
        this.precio_compra = precio_compra;
        this.precio_venta = precio_venta;
        this.fecha_caducidad = fecha_caducidad;
        this.proveedor_id = proveedor_id;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getStock_actual() { return stock_actual; }
    public void setStock_actual(int stock_actual) { this.stock_actual = stock_actual; }

    public int getStock_minimo() { return stock_minimo; }
    public void setStock_minimo(int stock_minimo) { this.stock_minimo = stock_minimo; }

    public String getUnidad_medida() { return unidad_medida; }
    public void setUnidad_medida(String unidad_medida) { this.unidad_medida = unidad_medida; }

    public double getPrecio_compra() { return precio_compra; }
    public void setPrecio_compra(double precio_compra) { this.precio_compra = precio_compra; }

    public double getPrecio_venta() { return precio_venta; }
    public void setPrecio_venta(double precio_venta) { this.precio_venta = precio_venta; }

    public String getFecha_caducidad() { return fecha_caducidad; }
    public void setFecha_caducidad(String fecha_caducidad) { this.fecha_caducidad = fecha_caducidad; }

    public int getProveedor_id() { return proveedor_id; }
    public void setProveedor_id(int proveedor_id) { this.proveedor_id = proveedor_id; }

    @Override
    public String toString() {
        return nombre + " (" + stock_actual + " en stock)";
    }
}
