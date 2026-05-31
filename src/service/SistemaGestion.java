package service;

import java.time.*;
import java.util.*;
import model.*;
import exceptions.*;

//esta clase sirve de servicio de gestion del sistema, mantiene las colecciones de usuarios, equipos y sesiones

public class SistemaGestion {
    // Usuarios y equipos se guardan en mapas con su codigo como llave porque la operacion mas comun es buscarlos por codigo
    private HashMap<String, Usuario> usuarios;
    private HashMap<String, Equipo> equipos;

    // Las sesiones se recorren mas de lo que se buscan por codigo, por eso una lista que conserva el orden de creacion
    private ArrayList<Sesion> sesiones;
    // usuario actualmente autenticado, es null mientras nadie ha entrado
    private Usuario usuarioActual;

    public SistemaGestion() {
        this.usuarios = new HashMap<>();
        this.equipos = new HashMap<>();
        this.sesiones = new ArrayList<>();
        this.usuarioActual = null;
        sembrarAdministrador();
    }

    // creamos un administrador inicial para permitir el primer ingreso al sistema cuando todavia no se han cargado usuarios desde archivo
    private void sembrarAdministrador() {
        Administrador admin = new Administrador("admin", "Administrador", "admin123");
        usuarios.put(admin.getCodigo(), admin);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    //Validamos las credenciales contra los usuarios registrados y, si son correctas, deja al usuario como autenticado. Tambien lanza excepcion si el
    //codigo no existe o la clave no coincide
    public void login(String codigo, String clave) throws CredencialInvalidaException {
        Usuario usuario = usuarios.get(codigo);
        if (usuario == null) {
            throw new CredencialInvalidaException("No existe un usuario con el codigo " + codigo);
        }
        if (!usuario.validarClave(clave)) {
            throw new CredencialInvalidaException("La clave ingresada no es correcta");
        }
        this.usuarioActual = usuario;
    }

    // cierra la sesion del usuario autenticado
    public void logout() {
        this.usuarioActual = null;
    }

    //Verificamos que haya un usuario autenticado y que sea administrador
    private void validarAdministrador() throws AccesoDenegadoException {
        if (usuarioActual == null) {
            throw new AccesoDenegadoException("Debe iniciar sesion para ejecutar esta operacion");
        }
        if (usuarioActual.getRol() != Rol.ADMINISTRADOR) {
            throw new AccesoDenegadoException("Solo un administrador puede ejecutar esta operacion");
        }
    }

    //registramosun equipo nuevo en el sistema, esta operacion es exclusiva de administradores
    public void registrarEquipo(Equipo equipo) throws AccesoDenegadoException {
        validarAdministrador();
        if (equipos.containsKey(equipo.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un equipo con el codigo " + equipo.getCodigo());
        }
        equipos.put(equipo.getCodigo(), equipo);
    }

    // registramos un usuario nuevo en el sistema, esta operacion tambien es exclusiva de administradores

    public void registrarUsuario(Usuario usuario) throws AccesoDenegadoException {
        validarAdministrador();
        if (usuarios.containsKey(usuario.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el codigo " + usuario.getCodigo());
        }
        usuarios.put(usuario.getCodigo(), usuario);
    }

    // este metodo programa una sesion de uso de un equipo, el equipo debe estar disponible y el responsable debe ser estudiante o monitor

    public Sesion programarSesion(String codigoSesion, String codigoEquipo, String codigoResponsable,LocalDateTime inicio) throws EntidadNoEncontradaException, EquipoNoDisponibleException {

        Equipo equipo = equipos.get(codigoEquipo);
        if (equipo == null) {
            throw new EntidadNoEncontradaException("No existe un equipo con el codigo " + codigoEquipo);
        }
        Usuario responsable = usuarios.get(codigoResponsable);
        if (responsable == null) {
            throw new EntidadNoEncontradaException("No existe un usuario con el codigo " + codigoResponsable);
        }

        if (responsable.getRol() == Rol.ADMINISTRADOR) {
            throw new IllegalArgumentException("Un administrador no puede ser responsable de una sesion de uso");
        }

        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw new EquipoNoDisponibleException("El equipo " + codigoEquipo + " no esta disponible, estado actual: " + equipo.getEstado());
        }

        Sesion sesion = new Sesion(codigoSesion, equipo, responsable, inicio);
        sesiones.add(sesion);
        equipo.setEstado(EstadoEquipo.EN_USO);
        equipo.incrementarUsos();
        return sesion;
    }

    //indicamos si el usuario autenticado puede programar sesiones a nombre de otros
    public boolean puedeProgramarParaOtros() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    // en este metodo se cierra una sesion de uso abierta, calcula la penalizacion segun el tiempo de uso y devuelve el equipo a estado disponible
    public double cerrarSesion(String codigoSesion, LocalDateTime fechaCierre) throws AccesoDenegadoException, EntidadNoEncontradaException, CierreSesionException {

        validarAdministrador();
        Sesion sesion = buscarSesion(codigoSesion);
        if (sesion == null) {
            throw new EntidadNoEncontradaException("No existe una sesion con el codigo " + codigoSesion);
        }
        if (sesion.estaCerrada()) {
            throw new CierreSesionException("La sesion " + codigoSesion + " ya fue cerrada");
        }
        if (fechaCierre.isBefore(sesion.getInicio())) {
            throw new CierreSesionException("La fecha de cierre no puede ser anterior a la de inicio");
        }

        double penalizacion = sesion.cerrar(fechaCierre);
        sesion.getEquipo().setEstado(EstadoEquipo.DISPONIBLE);
        return penalizacion;
    }

    //aqui buscamos una sesion por su codigo recorriendo la lista y devuelve null si no existe
    // lo declaramos private pq solo lo llama un metodo dentro de esta misma clase q es el anterior de cerrarSesion
    private Sesion buscarSesion(String codigoSesion) {
        for (Sesion sesion : sesiones) {
            if (sesion.getCodigo().equals(codigoSesion)) {
                return sesion;
            }
        }
        return null;
    }
}