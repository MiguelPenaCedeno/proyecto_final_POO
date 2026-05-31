package model;

import java.io.*;

//Clase base abstracta para los usuarios del sistema

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String codigo;
    protected String nombre;
    protected Rol rol;
    protected String clave;

    public Usuario(String codigo, String nombre, Rol rol, String clave) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.rol = rol;
        this.clave = clave;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    //comprueba si la clave ingresada coincide con la del usuario
    public boolean validarClave(String claveIngresada) {
        return this.clave.equals(claveIngresada);
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    // Cada subclase devuelve la informacion propia de su tipo de usuario.
    public abstract String getInformacionAdicional();

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (" + rol + ") " + getInformacionAdicional();
    }
}