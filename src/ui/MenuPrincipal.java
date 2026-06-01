package ui;

import java.time.*;
import java.util.*;
import java.io.*;

import persistence.*;
import model.*;
import service.*;
import exceptions.*;

//Esta clase es el menu de interaccion por consola con el sistema
public class MenuPrincipal {

    private SistemaGestion sistema;
    private Scanner sc = new Scanner(System.in);

    public MenuPrincipal() {
        this.sistema = new SistemaGestion();
    }

    // bucle principal del sistema donde mostramos el menu
    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            String opcion = sc.nextLine().trim();
            switch (opcion) {
                // pedimos logins al admin
                case "1": {
                    System.out.print("Codigo: ");
                    String codigo = sc.nextLine().trim();
                    System.out.print("Clave: ");
                    String clave = sc.nextLine().trim();
                    try {
                        sistema.login(codigo, clave);
                        Usuario actual = sistema.getUsuarioActual();
                        System.out.println("Bienvenido, " + actual.getNombre() + " (" + actual.getRol() + ")");
                    } catch (CredencialInvalidaException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "2": {
                    System.out.print("Codigo: ");
                    String codigo = sc.nextLine().trim();
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine().trim();

                    System.out.println("Tipo de equipo:");
                    for (TipoEquipo t : TipoEquipo.values()) {
                        System.out.println("  " + t);
                    }
                    System.out.print("Tipo: ");
                    String tipoTexto = sc.nextLine().trim().toUpperCase();

                    System.out.print("Laboratorio: ");
                    String laboratorio = sc.nextLine().trim();

                    System.out.println("Estado:");
                    for (EstadoEquipo e : EstadoEquipo.values()) {
                        System.out.println("  " + e);
                    }
                    System.out.print("Estado: ");
                    String estadoTexto = sc.nextLine().trim().toUpperCase();

                    System.out.print("Valor: ");
                    String valorTexto = sc.nextLine().trim();

                    try {
                        TipoEquipo tipo = TipoEquipo.valueOf(tipoTexto);
                        EstadoEquipo estado = EstadoEquipo.valueOf(estadoTexto);
                        double valor = Double.parseDouble(valorTexto);
                        Equipo equipo = new Equipo(codigo, nombre, tipo, laboratorio, estado, valor);
                        sistema.registrarEquipo(equipo);
                        System.out.println("Equipo registrado");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error, dato invalido " + e.getMessage());
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                //registramos el usuario
                case "3": {
                    System.out.print("Codigo: ");
                    String codigo = sc.nextLine().trim();
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine().trim();
                    System.out.print("Clave: ");
                    String clave = sc.nextLine().trim();

                    System.out.println("Rol:");
                    for (Rol r : Rol.values()) {
                        System.out.println("  " + r);
                    }
                    System.out.print("Rol: ");
                    String rolTexto = sc.nextLine().trim().toUpperCase();

                    try {
                        Rol rol = Rol.valueOf(rolTexto);
                        Usuario usuario;
                        if (rol == Rol.ESTUDIANTE) {
                            System.out.print("Programa: ");
                            String programa = sc.nextLine().trim();
                            usuario = new Estudiante(codigo, nombre, clave, programa);
                        } else if (rol == Rol.MONITOR) {
                            System.out.print("Area: ");
                            String area = sc.nextLine().trim();
                            usuario = new Monitor(codigo, nombre, clave, area);
                        } else {
                            usuario = new Administrador(codigo, nombre, clave);
                        }
                        sistema.registrarUsuario(usuario);
                        System.out.println("Usuario registrado.");
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: dato invalido. " + e.getMessage());
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "4": {
                    System.out.print("Ruta del archivo de equipos: ");
                    String ruta = sc.nextLine().trim();
                    try {
                        CargadorTexto cargador = new CargadorTexto();
                        ArrayList<Equipo> leidos = cargador.cargarEquipos(ruta);
                        int registrados = sistema.cargarEquipos(leidos);
                        System.out.println("Equipos leidos: " + leidos.size() + ", registrados: " + registrados + ", omitidos por codigo repetido: " + (leidos.size() - registrados));
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Error al leer el archivo: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error, el archivo tiene datos invalidos " + e.getMessage());
                    }
                    break;
                }
                case "5": {
                    System.out.print("Ruta del archivo de usuarios: ");
                    String ruta = sc.nextLine().trim();
                    try {
                        CargadorTexto cargador = new CargadorTexto();
                        ArrayList<Usuario> leidos = cargador.cargarUsuarios(ruta);
                        int registrados = sistema.cargarUsuarios(leidos);
                        System.out.println("Usuarios leidos: " + leidos.size() + ", registrados: " + registrados + ", omitidos por codigo repetido: " + (leidos.size() - registrados));
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Error al leer el archivo: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error, el archivo tiene datos invalidos " + e.getMessage());
                    }
                    break;
                }
                case "6": {
                    System.out.print("Ruta del archivo binario: ");
                    String ruta = sc.nextLine().trim();
                    try {
                        sistema.guardarEstado(ruta);
                        System.out.println("Estado guardado.");
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Error al guardar: " + e.getMessage());
                    }
                    break;
                }
                case "7": {
                    System.out.print("Ruta del archivo binario: ");
                    String ruta = sc.nextLine().trim();
                    try {
                        sistema.recuperarEstado(ruta);
                        System.out.println("Estado recuperado.");
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IOException e) {
                        System.out.println("Error al recuperar: " + e.getMessage());
                    } catch (ClassNotFoundException e) {
                        System.out.println("Error: clase no encontrada al recuperar. " + e.getMessage());
                    }
                    break;
                }
                case "8": {
                    HashMap<String, ArrayList<Equipo>> inventario = sistema.inventarioPorLaboratorio();
                    if (inventario.isEmpty()) {
                        System.out.println("No hay equipos registrados.");
                    } else {
                        for (String laboratorio : inventario.keySet()) {
                            System.out.println("Laboratorio: " + laboratorio);
                            for (Equipo equipo : inventario.get(laboratorio)) {
                                System.out.println("  " + equipo);
                            }
                        }
                    }
                    break;
                }
                case "9": {
                    Usuario actual = sistema.getUsuarioActual();
                    if (actual == null) {
                        System.out.println("Debe iniciar sesion para programar.");
                        break;
                    }

                    System.out.print("Codigo de la sesion: ");
                    String codigoSesion = sc.nextLine().trim();
                    System.out.print("Codigo del equipo: ");
                    String codigoEquipo = sc.nextLine().trim();

                    String codigoResponsable;
                    if (sistema.puedeProgramarParaOtros()) {
                        System.out.print("Codigo del responsable: ");
                        codigoResponsable = sc.nextLine().trim();
                    } else {
                        codigoResponsable = actual.getCodigo();
                    }

                    System.out.println("Fecha y hora de inicio");
                    try {
                        System.out.print("Año: ");
                        int anio = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Mes (1-12): ");
                        int mes = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Dia: ");
                        int dia = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Hora (0-23): ");
                        int hora = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Minuto (0-59): ");
                        int minuto = Integer.parseInt(sc.nextLine().trim());
                        LocalDateTime inicio = LocalDateTime.of(anio, mes, dia, hora, minuto);

                        Sesion sesion = sistema.programarSesion(codigoSesion, codigoEquipo, codigoResponsable, inicio);
                        System.out.println("Sesion programada: " + sesion);
                    } catch (NumberFormatException e) {
                        System.out.println("Error, la fecha debe ser numerica");
                    } catch (DateTimeException e) {
                        System.out.println("Error, fecha u hora invalida");
                    } catch (EntidadNoEncontradaException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (EquipoNoDisponibleException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "10": {
                    System.out.print("Codigo de la sesion a cerrar: ");
                    String codigoSesion = sc.nextLine().trim();

                    System.out.println("Fecha y hora de cierre");
                    try {
                        System.out.print("Anio: ");
                        int anio = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Mes (1-12): ");
                        int mes = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Dia: ");
                        int dia = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Hora (0-23): ");
                        int hora = Integer.parseInt(sc.nextLine().trim());
                        System.out.print("Minuto (0-59): ");
                        int minuto = Integer.parseInt(sc.nextLine().trim());
                        LocalDateTime cierre = LocalDateTime.of(anio, mes, dia, hora, minuto);

                        double penalizacion = sistema.cerrarSesion(codigoSesion, cierre);
                        if (penalizacion > 0) {
                            System.out.println("Sesion cerrada con penalizacion de: " + penalizacion);
                        } else {
                            System.out.println("Sesion cerrada sin penalizacion");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error, la fecha debe ser numerica");
                    } catch (DateTimeException e) {
                        System.out.println("Error, fecha u hora invalida");
                    } catch (AccesoDenegadoException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (EntidadNoEncontradaException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (CierreSesionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "11": {
                    HashMap<String, Equipo> masUsados = sistema.equipoMasUsadoPorLaboratorio();
                    if (masUsados.isEmpty()) {
                        System.out.println("No hay equipos registrados.");
                    } else {
                        for (String laboratorio : masUsados.keySet()) {
                            Equipo equipo = masUsados.get(laboratorio);
                            System.out.println("Laboratorio: " + laboratorio);
                            System.out.println("Equipo mas usado: " + equipo.getNombre() + " con " + equipo.getUsos() + " usos");
                        }
                    }
                    break;
                }
                case "12": {
                    HashMap<Usuario, Integer> usoIndebido = sistema.usuariosPorUsoIndebido();
                    if (usoIndebido.isEmpty()) {
                        System.out.println("No hay usuarios con uso indebido registrado");
                    } else {
                        for (Usuario usuario : usoIndebido.keySet()) {
                            int sesionesPenalizadas = usoIndebido.get(usuario);
                            System.out.println(usuario.getNombre() + " (" + usuario.getCodigo() + "): " + sesionesPenalizadas + " sesiones con penalizacion");
                        }
                    }
                    break;
                }
                case "13":
                    // gestion de administradores
                    break;
                case "14":
                    salir = true;
                    System.out.println("Saliendo del sistema.");
                    break;
                default:
                    System.out.println("Opcion no valida.");
            }
            System.out.println();
        }
    }

    // comprimimos el menu en un metodo
    private void mostrarMenu() {
        System.out.println("Sistema de Gestion de Equipos de Laboratorio");
        Usuario actual = sistema.getUsuarioActual();
        if (actual == null) {
            System.out.println("Sesion: ninguna");
        } else {
            System.out.println("Sesion: " + actual.getNombre() + " (" + actual.getRol() + ")");
        }
        System.out.println("1. Login de administrador");
        System.out.println("2. Registrar equipo");
        System.out.println("3. Registrar usuario");
        System.out.println("4. Cargar equipos desde archivo");
        System.out.println("5. Cargar usuarios desde archivo");
        System.out.println("6. Guardar estado del sistema");
        System.out.println("7. Recuperar estado del sistema");
        System.out.println("8. Mostrar inventario por laboratorio y estado");
        System.out.println("9. Programar sesion de uso");
        System.out.println("10. Cerrar sesion de uso");
        System.out.println("11. Equipos con mayor uso por laboratorio");
        System.out.println("12. Usuarios con mayor indice de uso indebido");
        System.out.println("13. Gestion de administradores");
        System.out.println("14. Salir");
        System.out.print("Opcion: ");
    }
}