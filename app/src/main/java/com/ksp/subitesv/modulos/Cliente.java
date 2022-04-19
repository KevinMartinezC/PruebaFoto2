package com.ksp.subitesv.modulos;

public class Cliente {
    String id;
    String nombre;
    String correo;
    String imagen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Cliente(String id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
    }

    public Cliente() {
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Cliente(String id, String nombre, String correo, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.imagen = imagen;
    }
}
