package com.mycompany.proyectoorganizadortareas;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Guarda y recupera las materias y las tareas en un archivo local.
 * No requiere base de datos, Internet ni librerías externas.
 */
public final class RepositorioDatos {

    private static final Path CARPETA = Path.of(System.getProperty("user.home"), ".academia-organizador");
    private static final Path ARCHIVO = CARPETA.resolve("datos.properties");

    private RepositorioDatos() {
    }

    public static Path rutaArchivo() {
        return ARCHIVO;
    }

    public static Organizador cargar() throws IOException {
        Organizador organizador = new Organizador();
        if (!Files.exists(ARCHIVO)) {
            return organizador;
        }

        Properties datos = new Properties();
        try (Reader lector = Files.newBufferedReader(ARCHIVO, StandardCharsets.UTF_8)) {
            datos.load(lector);
        }

        try {
            int totalMaterias = Integer.parseInt(datos.getProperty("materias.total", "0"));
            int totalTareas = Integer.parseInt(datos.getProperty("tareas.total", "0"));
            if (totalMaterias < 0 || totalTareas < 0 || totalMaterias > 100000 || totalTareas > 1000000) {
                throw new IllegalArgumentException("Cantidad de registros inválida");
            }

            Map<Integer, Materia> materias = new HashMap<>();
            for (int i = 0; i < totalMaterias; i++) {
                String base = "materia." + i + ".";
                int id = Integer.parseInt(obligatorio(datos, base + "id"));
                String nombre = obligatorio(datos, base + "nombre");
                Materia materia = organizador.restaurarMateria(id, nombre);
                materias.put(id, materia);
            }

            for (int i = 0; i < totalTareas; i++) {
                String base = "tarea." + i + ".";
                int id = Integer.parseInt(obligatorio(datos, base + "id"));
                int materiaId = Integer.parseInt(obligatorio(datos, base + "materiaId"));
                Materia materia = materias.get(materiaId);
                if (materia == null) {
                    throw new IllegalArgumentException("Materia de la tarea no encontrada");
                }
                Tarea tarea = organizador.restaurarTarea(
                        id,
                        obligatorio(datos, base + "titulo"),
                        datos.getProperty(base + "descripcion", ""),
                        materia,
                        Prioridad.valueOf(obligatorio(datos, base + "prioridad")),
                        LocalDate.parse(obligatorio(datos, base + "fecha")),
                        EstadoTarea.valueOf(obligatorio(datos, base + "estado")));
                if (tarea == null) {
                    throw new IllegalArgumentException("No fue posible recuperar una tarea");
                }
            }
            organizador.restaurarConsecutivos(
                    Integer.parseInt(datos.getProperty("secuencia.materia", Integer.toString(organizador.siguienteIdMateria()))),
                    Integer.parseInt(datos.getProperty("secuencia.tarea", Integer.toString(organizador.siguienteIdTarea()))));
            return organizador;
        } catch (RuntimeException error) {
            throw new IOException("El archivo de datos está incompleto o contiene información inválida.", error);
        }
    }

    public static void guardar(Organizador organizador) throws IOException {
        Properties datos = new Properties();
        List<Materia> materias = organizador.consultarMaterias();
        List<Tarea> tareas = organizador.consultarTareas();
        datos.setProperty("version", "1");
        datos.setProperty("secuencia.materia", Integer.toString(organizador.siguienteIdMateria()));
        datos.setProperty("secuencia.tarea", Integer.toString(organizador.siguienteIdTarea()));
        datos.setProperty("materias.total", Integer.toString(materias.size()));
        datos.setProperty("tareas.total", Integer.toString(tareas.size()));

        for (int i = 0; i < materias.size(); i++) {
            Materia materia = materias.get(i);
            String base = "materia." + i + ".";
            datos.setProperty(base + "id", Integer.toString(materia.getId()));
            datos.setProperty(base + "nombre", materia.getNombre());
        }
        for (int i = 0; i < tareas.size(); i++) {
            Tarea tarea = tareas.get(i);
            String base = "tarea." + i + ".";
            datos.setProperty(base + "id", Integer.toString(tarea.getId()));
            datos.setProperty(base + "titulo", tarea.getTitulo());
            datos.setProperty(base + "descripcion", tarea.getDescripcion());
            datos.setProperty(base + "materiaId", Integer.toString(tarea.getMateria().getId()));
            datos.setProperty(base + "prioridad", tarea.getPrioridad().name());
            datos.setProperty(base + "estado", tarea.getEstado().name());
            datos.setProperty(base + "fecha", tarea.getFechaEntrega().toString());
        }

        Files.createDirectories(CARPETA);
        Path temporal = Files.createTempFile(CARPETA, "academia-", ".tmp");
        try {
            try (Writer escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                datos.store(escritor, "ACADEMIA - Datos locales del organizador");
            }
            try {
                Files.move(temporal, ARCHIVO, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException error) {
                Files.move(temporal, ARCHIVO, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    private static String obligatorio(Properties datos, String clave) {
        String valor = datos.getProperty(clave);
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Falta el dato " + clave);
        }
        return valor;
    }
}
