package model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Clase de sesiones que registra el equipo y el usuario responsable, las fechas de inicio yfin, y la penalizacion calculada si el cierre ocurre fuera del tiempo
// permitido
public class Sesion implements Serializable {

    private static final long serialVersionUID = 1L;

    // constante de tiempo maximo permitido de uso en horas, una vez pasado este limite cada hora de retraso iniciada genera penalizacion
    private static final long HORAS_PERMITIDAS = 2;

    //porcentaje del valor del equipo que se cobra por cada hora de retraso iniciada
    private static final double PORCENTAJE_PENALIZACION = 0.01;
    private String codigo;
    private Equipo equipo;
    private Usuario usuario;
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private double penalizacion;

    public Sesion(String codigo, Equipo equipo, Usuario usuario, LocalDateTime inicio) {
        this.codigo = codigo;
        this.equipo = equipo;
        this.usuario = usuario;
        this.inicio = inicio;
        this.fin = null;
        this.penalizacion = 0.0;
    }

    public String getCodigo() {
        return codigo;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public double getPenalizacion() {
        return penalizacion;
    }

    // Indica si la sesion ya fue cerrada
    public boolean estaCerrada() {
        return fin != null;
    }

    // este metodo cierra la sesion en la fecha indicada y calcula la penalizacion si el cierre ocurre despues del tiempo permitido, cualquier fraccion
    //de hora de retraso cuenta como una hora completa
    public double cerrar(LocalDateTime fechaCierre) {
        this.fin = fechaCierre;

        long minutosUsados = ChronoUnit.MINUTES.between(inicio, fin);
        long minutosPermitidos = HORAS_PERMITIDAS * 60;
        if (minutosUsados > minutosPermitidos) {
            long minutosRetraso = minutosUsados - minutosPermitidos;

            // aqui hacemos redondeo hacia arriba, cualquier fraccion de hora iniciada cuenta como una hora completa de retraso
            long horasRetraso = (minutosRetraso + 59) / 60;
            this.penalizacion = horasRetraso * PORCENTAJE_PENALIZACION * equipo.getValor();
        } else {
            this.penalizacion = 0.0;
        }
        return this.penalizacion;
    }

    @Override
    public String toString() {
        String estado;
        if (estaCerrada()) {
            estado = "Cerrada | Fin: " + fin + " | Penalizacion: " + penalizacion;
        } else {
            estado = "Abierta";
        }
        return codigo + " | Equipo: " + equipo.getCodigo() + " | Usuario: " + usuario.getCodigo() + " | Inicio: " + inicio + " | " + estado;
    }
}