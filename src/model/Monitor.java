package model;
//usuario de tipo monito, aqui se agrega el area academica que tiene a cargo como atributo de la clase en especifico
public class Monitor extends Usuario {

    private static final long serialVersionUID = 1L;
    private String area;

    public Monitor(String codigo, String nombre, String clave, String area) {
        super(codigo, nombre, Rol.MONITOR, clave);
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    @Override
    public String getInformacionAdicional() {
        return "Area: " + area;
    }
}