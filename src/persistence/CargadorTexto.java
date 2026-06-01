package persistence;

import java.io.*;
import java.util.*;
import model.*;

/**
 * Carga equipos y usuarios desde archivos de texto delimitados por
 * asterisco. Cada linea representa una entidad y no se usan encabezados.
 */
public class CargadorTexto {

    /**
     * Lee los equipos desde un archivo de texto. Cada linea tiene el
     * formato codigo*nombre*tipo*laboratorio*estado*valor*usos.
     *
     * @param ruta ruta del archivo de equipos
     * @return lista de equipos leidos del archivo
     * @throws IOException si el archivo no se puede leer
     */
    public ArrayList<Equipo> cargarEquipos(String ruta) throws IOException {
        ArrayList<Equipo> equipos = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = lector.readLine()) != null) {
            if (linea.isBlank()) {
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(linea, "*");
            String codigo = tokenizer.nextToken();
            String nombre = tokenizer.nextToken();
            TipoEquipo tipo = TipoEquipo.valueOf(tokenizer.nextToken());
            String laboratorio = tokenizer.nextToken();
            EstadoEquipo estado = EstadoEquipo.valueOf(tokenizer.nextToken());
            double valor = Double.parseDouble(tokenizer.nextToken());
            int usos = Integer.parseInt(tokenizer.nextToken());
            Equipo equipo = new Equipo(codigo, nombre, tipo, laboratorio, estado, valor, usos);
            equipos.add(equipo);
        }
        lector.close();
        return equipos;
    }

    /**
     * Lee los usuarios desde un archivo de texto. El formato depende del
     * rol, que es el tercer campo de cada linea:
     * ESTUDIANTE: codigo*nombre*rol*clave*programa
     * MONITOR: codigo*nombre*rol*clave*area
     * ADMINISTRADOR: codigo*nombre*rol*clave
     *
     * @param ruta ruta del archivo de usuarios
     * @return lista de usuarios leidos del archivo
     * @throws IOException si el archivo no se puede leer
     */
    public ArrayList<Usuario> cargarUsuarios(String ruta) throws IOException {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new FileReader(ruta));
        String linea;
        while ((linea = lector.readLine()) != null) {
            if (linea.isBlank()) {
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(linea, "*");
            String codigo = tokenizer.nextToken();
            String nombre = tokenizer.nextToken();
            Rol rol = Rol.valueOf(tokenizer.nextToken());
            String clave = tokenizer.nextToken();

            Usuario usuario;
            if (rol == Rol.ESTUDIANTE) {
                String programa = tokenizer.nextToken();
                usuario = new Estudiante(codigo, nombre, clave, programa);
            } else if (rol == Rol.MONITOR) {
                String area = tokenizer.nextToken();
                usuario = new Monitor(codigo, nombre, clave, area);
            } else {
                usuario = new Administrador(codigo, nombre, clave);
            }
            usuarios.add(usuario);
        }
        lector.close();
        return usuarios;
    }
}