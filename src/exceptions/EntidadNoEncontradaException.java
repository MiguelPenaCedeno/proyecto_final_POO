package exceptions;
//se activa cuando se busca un equipo, usuario o sesion que no existe en el sistema

public class EntidadNoEncontradaException extends Exception {
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}