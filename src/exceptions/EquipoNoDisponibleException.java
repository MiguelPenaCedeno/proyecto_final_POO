package exceptions;
// sale cuando intentas programar una sesion sobre un equipo que no esta disponible
public class EquipoNoDisponibleException extends Exception {
    public EquipoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}