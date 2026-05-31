package service;

import java.util.*;
import model.*;

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

    // crea un administrador inicial para permitir el primer ingreso al sistema cuando todavia no se han cargado usuarios desde archivo
    private void sembrarAdministrador() {
        Administrador admin = new Administrador("admin", "Administrador", "admin123");
        usuarios.put(admin.getCodigo(), admin);
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}