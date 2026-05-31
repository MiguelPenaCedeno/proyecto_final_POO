package exceptions;

// sale cuando el login falla porque el codigo no existe o la clave es incorrecta
public class CredencialInvalidaException extends Exception {

    public CredencialInvalidaException(String mensaje) {
        super(mensaje);
    }
}