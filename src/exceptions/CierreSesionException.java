package exceptions;
// se activa cuando ocurre un error al cerrar una sesion de uso
public class CierreSesionException extends Exception {
    public CierreSesionException(String mensaje) {
        super(mensaje);
    }
}