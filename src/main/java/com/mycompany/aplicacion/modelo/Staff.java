package com.mycompany.aplicacion.modelo;

public class Staff {

    private int     id;
    private String  usuario;
    private String  nombre;
    private String  apellidos;
    private String  rol;
    private String  especialidad;
    private String  cedula;
    private String  telefono;
    private String  email;
    private String  contrasenia;
    private boolean activo;

    // ─── Constructor completo (con activo) ──────────────────────────────────
    public Staff(int id, String usuario, String nombre, String apellidos,
                 String rol, String especialidad, String cedula,
                 String telefono, String email, boolean activo) {
        this.id          = id;
        this.usuario     = usuario;
        this.nombre      = nombre;
        this.apellidos   = apellidos;
        this.rol         = rol;
        this.especialidad = especialidad;
        this.cedula      = cedula;
        this.telefono    = telefono;
        this.email       = email;
        this.activo      = activo;
        this.contrasenia = "";
    }

    // ─── Constructor legado (sin activo) ────────────────────────────────────
    public Staff(int id, String nombre, String apellidos, String rol, String especialidad,
                 String telefono, String email, String contrasenia,
                 String cedula, String usuario) {
        this.id          = id;
        this.nombre      = nombre;
        this.apellidos   = apellidos;
        this.rol         = rol;
        this.especialidad = especialidad;
        this.telefono    = telefono;
        this.email       = email;
        this.contrasenia = contrasenia;
        this.cedula      = cedula;
        this.usuario     = usuario;
        this.activo      = true;
    }

    // ─── Getters y Setters ───────────────────────────────────────────────────
    public int     getId()              { return id; }
    public void    setId(int id)        { this.id = id; }

    public String  getUsuario()         { return usuario; }
    public void    setUsuario(String u) { this.usuario = u; }

    public String  getNombre()          { return nombre; }
    public void    setNombre(String n)  { this.nombre = n; }

    public String  getApellidos()               { return apellidos; }
    public void    setApellidos(String apellidos){ this.apellidos = apellidos; }

    public String  getRol()             { return rol; }
    public void    setRol(String rol)   { this.rol = rol; }

    public String  getEspecialidad()              { return especialidad; }
    public void    setEspecialidad(String e)      { this.especialidad = e; }

    public String  getCedula()          { return cedula; }
    public void    setCedula(String c)  { this.cedula = c; }

    public String  getTelefono()        { return telefono; }
    public void    setTelefono(String t){ this.telefono = t; }

    public String  getEmail()           { return email; }
    public void    setEmail(String e)   { this.email = e; }

    public String  getContrasenia()              { return contrasenia; }
    public void    setContrasenia(String contra) { this.contrasenia = contra; }

    public boolean isActivo()           { return activo; }
    public void    setActivo(boolean a) { this.activo = a; }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + rol + ")";
    }
}
