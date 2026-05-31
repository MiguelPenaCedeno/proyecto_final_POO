package model;
// usuario de tipo estudiante, aqui agregamoas como tributo el programa academico al que pertenece
public class Estudiante extends Usuario {
    private static final long serialVersionUID = 1L;
    private String programa;

    public Estudiante(String codigo, String nombre, String clave, String programa) {
        super(codigo, nombre, Rol.ESTUDIANTE, clave);
        this.programa = programa;
    }

    public String getPrograma() {
        return programa;
    }

    @Override
    public String getInformacionAdicional() {
        return "Programa: " + programa;
    }
}