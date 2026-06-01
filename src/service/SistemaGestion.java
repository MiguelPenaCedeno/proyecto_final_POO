package service;

import java.time.*;
import java.util.*;
import java.io.*;
import model.*;
import exceptions.*;

/**
 * Servicio de gestion del sistema. Mantiene las colecciones de usuarios,
 * equipos y sesiones, controla la autenticacion y concentra la logica de
 * negocio del sistema de gestion de equipos de laboratorio.
 */
public class SistemaGestion {
    // Usuarios y equipos se guardan en mapas con su codigo como llave porque la operacion mas comun es buscarlos por codigo
    private HashMap<String, Usuario> usuarios;
    private HashMap<String, Equipo> equipos;

    // Las sesiones se recorren mas de lo que se buscan por codigo, por eso una lista que conserva el orden de creacion
    private ArrayList<Sesion> sesiones;
    // usuario actualmente autenticado, es null mientras nadie ha entrado
    private Usuario usuarioActual;

    /**
     * Inicializa las colecciones vacias y siembra un administrador por
     * defecto para permitir el primer ingreso al sistema.
     */
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

    /**
     * Devuelve el usuario autenticado actualmente, o null si nadie ha
     * iniciado sesion.
     *
     * @return el usuario actual o null
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Valida las credenciales contra los usuarios registrados y, si son
     * correctas, deja al usuario como autenticado.
     *
     * @param codigo codigo del usuario que intenta ingresar
     * @param clave clave ingresada
     * @throws CredencialInvalidaException si el codigo no existe o la
     *         clave no coincide
     */
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

    /**
     * Cierra la sesion del usuario autenticado.
     */
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

    /**
     * Registra un equipo nuevo en el sistema. Operacion exclusiva de
     * administradores.
     *
     * @param equipo equipo a registrar
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public void registrarEquipo(Equipo equipo) throws AccesoDenegadoException {
        validarAdministrador();
        if (equipos.containsKey(equipo.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un equipo con el codigo " + equipo.getCodigo());
        }
        equipos.put(equipo.getCodigo(), equipo);
    }

    /**
     * Registra un usuario nuevo en el sistema. Operacion exclusiva de
     * administradores.
     *
     * @param usuario usuario a registrar
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public void registrarUsuario(Usuario usuario) throws AccesoDenegadoException {
        validarAdministrador();
        if (usuarios.containsKey(usuario.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un usuario con el codigo " + usuario.getCodigo());
        }
        usuarios.put(usuario.getCodigo(), usuario);
    }

    /**
     * Programa una sesion de uso de un equipo. El equipo debe estar
     * disponible y el responsable debe ser estudiante o monitor. Al
     * programarse, el equipo pasa a estado en uso y se incrementa su
     * contador de usos.
     *
     * @param codigoSesion codigo de la nueva sesion
     * @param codigoEquipo codigo del equipo a usar
     * @param codigoResponsable codigo del usuario responsable
     * @param inicio fecha y hora de inicio de la sesion
     * @return la sesion creada
     * @throws EntidadNoEncontradaException si el equipo o el usuario no existen
     * @throws EquipoNoDisponibleException si el equipo no esta disponible
     */
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

    /**
     * Indica si el usuario autenticado puede programar sesiones a nombre
     * de otros usuarios. Solo el administrador puede hacerlo.
     *
     * @return true si el usuario actual es administrador
     */
    public boolean puedeProgramarParaOtros() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    /**
     * Cierra una sesion de uso abierta, calcula la penalizacion segun el
     * tiempo de uso y devuelve el equipo a estado disponible. Operacion
     * exclusiva de administradores.
     *
     * @param codigoSesion codigo de la sesion a cerrar
     * @param fechaCierre fecha y hora de cierre
     * @return la penalizacion calculada
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     * @throws EntidadNoEncontradaException si la sesion no existe
     * @throws CierreSesionException si la sesion ya estaba cerrada o la
     *         fecha de cierre es anterior a la de inicio
     */
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

    /**
     * Organiza el inventario de equipos agrupado por laboratorio. Para
     * cada laboratorio devuelve la lista de sus equipos.
     *
     * @return mapa de laboratorio a la lista de sus equipos
     */
    public HashMap<String, ArrayList<Equipo>> inventarioPorLaboratorio() {
        HashMap<String, ArrayList<Equipo>> inventario = new HashMap<>();
        for (Equipo equipo : equipos.values()) {
            String laboratorio = equipo.getLaboratorio();
            if (!inventario.containsKey(laboratorio)) {
                inventario.put(laboratorio, new ArrayList<>());
            }
            inventario.get(laboratorio).add(equipo);
        }
        return inventario;
    }

    /**
     * Por cada laboratorio determina el equipo con mayor numero de usos.
     *
     * @return mapa de laboratorio a su equipo mas usado
     */
    public HashMap<String, Equipo> equipoMasUsadoPorLaboratorio() {
        HashMap<String, Equipo> resultado = new HashMap<>();
        for (Equipo equipo : equipos.values()) {
            String laboratorio = equipo.getLaboratorio();
            Equipo actual = resultado.get(laboratorio);
            if (actual == null || equipo.getUsos() > actual.getUsos()) {
                resultado.put(laboratorio, equipo);
            }
        }
        return resultado;
    }

    /**
     * Cuenta, por usuario, cuantas de sus sesiones fueron cerradas con
     * penalizacion. Ese conteo es el indice de uso indebido. Solo incluye
     * usuarios con al menos una sesion penalizada.
     *
     * @return mapa de usuario a su numero de sesiones penalizadas
     */
    public HashMap<Usuario, Integer> usuariosPorUsoIndebido() {
        HashMap<Usuario, Integer> conteo = new HashMap<>();
        for (Sesion sesion : sesiones) {
            if (sesion.estaCerrada() && sesion.getPenalizacion() > 0) {
                Usuario responsable = sesion.getUsuario();
                if (!conteo.containsKey(responsable)) {
                    conteo.put(responsable, 1);
                } else {
                    conteo.put(responsable, conteo.get(responsable) + 1);
                }
            }
        }
        return conteo;
    }

    /**
     * Registra en el sistema una lista de equipos cargada desde archivo.
     * Los equipos cuyo codigo ya existe se omiten. Operacion exclusiva de
     * administradores.
     *
     * @param equiposCargados lista de equipos leidos del archivo
     * @return cuantos equipos se registraron efectivamente
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public int cargarEquipos(ArrayList<Equipo> equiposCargados) throws AccesoDenegadoException {
        validarAdministrador();
        int registrados = 0;
        for (Equipo equipo : equiposCargados) {
            if (!equipos.containsKey(equipo.getCodigo())) {
                equipos.put(equipo.getCodigo(), equipo);
                registrados++;
            }
        }
        return registrados;
    }

    /**
     * Registra en el sistema una lista de usuarios cargada desde archivo.
     * Los usuarios cuyo codigo ya existe se omiten. Operacion exclusiva de
     * administradores.
     *
     * @param usuariosCargados lista de usuarios leidos del archivo
     * @return cuantos usuarios se registraron efectivamente
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public int cargarUsuarios(ArrayList<Usuario> usuariosCargados) throws AccesoDenegadoException {
        validarAdministrador();
        int registrados = 0;
        for (Usuario usuario : usuariosCargados) {
            if (!usuarios.containsKey(usuario.getCodigo())) {
                usuarios.put(usuario.getCodigo(), usuario);
                registrados++;
            }
        }
        return registrados;
    }

    /**
     * Guarda el estado completo del sistema en un archivo binario,
     * escribiendo las tres colecciones en orden: usuarios, equipos y
     * sesiones. Operacion exclusiva de administradores.
     *
     * @param ruta ruta del archivo binario de destino
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     * @throws IOException si ocurre un error al escribir el archivo
     */
    public void guardarEstado(String ruta) throws AccesoDenegadoException, IOException {
        validarAdministrador();
        ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ruta));
        salida.writeObject(usuarios);
        salida.writeObject(equipos);
        salida.writeObject(sesiones);
        salida.close();
    }

    /**
     * Recupera el estado completo del sistema desde un archivo binario,
     * leyendo las tres colecciones en el mismo orden en que se
     * escribieron. Reemplaza el estado actual por el recuperado.
     * Operacion exclusiva de administradores.
     *
     * @param ruta ruta del archivo binario a leer
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     * @throws IOException si ocurre un error al leer el archivo
     * @throws ClassNotFoundException si no se encuentra la clase de algun
     *         objeto al deserializar
     */
    @SuppressWarnings("unchecked")
    public void recuperarEstado(String ruta)
            throws AccesoDenegadoException, IOException, ClassNotFoundException {
        validarAdministrador();
        ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta));
        this.usuarios = (HashMap<String, Usuario>) entrada.readObject();
        this.equipos = (HashMap<String, Equipo>) entrada.readObject();
        this.sesiones = (ArrayList<Sesion>) entrada.readObject();
        entrada.close();
    }

    /**
     * Crea y registra un nuevo administrador en el sistema. Operacion
     * exclusiva de administradores.
     *
     * @param codigo codigo del nuevo administrador
     * @param nombre nombre del nuevo administrador
     * @param clave clave del nuevo administrador
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public void crearAdministrador(String codigo, String nombre, String clave) throws AccesoDenegadoException {
        validarAdministrador();
        if (usuarios.containsKey(codigo)) {
            throw new IllegalArgumentException("Ya existe un usuario con el codigo " + codigo);
        }
        Administrador admin = new Administrador(codigo, nombre, clave);
        usuarios.put(codigo, admin);
    }

    /**
     * Devuelve la lista de los administradores registrados en el sistema.
     * Operacion exclusiva de administradores.
     *
     * @return lista de administradores registrados
     * @throws AccesoDenegadoException si el usuario actual no es administrador
     */
    public ArrayList<Administrador> listarAdministradores() throws AccesoDenegadoException {
        validarAdministrador();
        ArrayList<Administrador> resultado = new ArrayList<>();
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getRol() == Rol.ADMINISTRADOR) {
                resultado.add((Administrador) usuario);
            }
        }
        return resultado;
    }
}