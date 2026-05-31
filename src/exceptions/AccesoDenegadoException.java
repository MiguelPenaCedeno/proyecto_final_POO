package exceptions;

// se activa cuando un usuario sin rol de administrador intenta una accion no permitida
public class AccesoDenegadoException extends Exception {
    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}