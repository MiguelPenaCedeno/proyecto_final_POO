
package model;

import java.io.*;

// esta clase representa un equipo de laboratorio. Lleva su identificacion, el laboratorio al que pertenece, su estado actual, el valor estimado y
// un contador de cuantas veces ha sido usado
public class Equipo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private TipoEquipo tipo;
    private String laboratorio;
    private EstadoEquipo estado;
    private double valor;
    private int usos;

    public Equipo(String codigo, String nombre, TipoEquipo tipo, String laboratorio,
                  EstadoEquipo estado, double valor) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.laboratorio = laboratorio;
        this.estado = estado;
        this.valor = valor;
        this.usos = 0;
    }

    // Constructor usado al cargar desde archivo, cuando el equipo ya trae
    // un historico de usos previo.
    public Equipo(String codigo, String nombre, TipoEquipo tipo, String laboratorio,
                  EstadoEquipo estado, double valor, int usos) {
        this(codigo, nombre, tipo, laboratorio, estado, valor);
        this.usos = usos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public double getValor() {
        return valor;
    }

    public int getUsos() {
        return usos;
    }

    // Incrementa en uno el contador de usos del equipo.
    public void incrementarUsos() {
        this.usos++;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " | " + tipo + " | Lab: " + laboratorio + " | Estado: " + estado + " | Valor: " + valor + " | Usos: " + usos;
    }
}