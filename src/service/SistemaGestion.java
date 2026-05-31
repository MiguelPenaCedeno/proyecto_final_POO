package service;

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
}