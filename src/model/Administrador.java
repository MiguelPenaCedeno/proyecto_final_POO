package model;
// usuario de tipo administrador, en este caso es el unico autorizado para las operaciones criticas del sistemaNo agrega atributos propios
public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador(String codigo, String nombre, String clave) {
        super(codigo, nombre, Rol.ADMINISTRADOR, clave);
    }
    @Override
    public String getInformacionAdicional() {
        return "Administrador del sistema";
    }
}