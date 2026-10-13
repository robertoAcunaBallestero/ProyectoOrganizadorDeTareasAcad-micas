package com.mycompany.proyectoorganizadortareas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GestorTareas {

    private ArrayList<Tarea> tareas;
    private int contador;

    public GestorTareas() {
        tareas = new ArrayList<>();
        contador = 1;
    }

    // Registrar tarea
    public void registrarTarea(String titulo, String descripcion,
            Materia materia, String prioridad,
            LocalDate fecha) {

        Tarea nueva = new Tarea(contador, titulo,
                descripcion, materia, prioridad, fecha);

        tareas.add(nueva);
        contador++;
    }

    // Consultar todas las tareas
    public List<Tarea> consultarTareas() {
        return new ArrayList<>(tareas);
    }

    // Buscar tarea por ID
    public Tarea buscarTarea(int id) {

        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return tarea;
            }
        }

        return null;
    }

    // Modificar una tarea
    public boolean modificarTarea(int id, String titulo,
            String descripcion, Materia materia,
            String prioridad, LocalDate fecha) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null) {
            return false;
        }

        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setMateria(materia);
        tarea.setPrioridad(prioridad);
        tarea.setFechaEntrega(fecha);

        return true;
    }

    // Eliminar tarea
    public boolean eliminarTarea(int id) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null) {
            return false;
        }

        tareas.remove(tarea);
        return true;
    }

    // Cambiar estado de una tarea
    public boolean cambiarEstado(int id, String estado) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null) {
            return false;
        }

        tarea.setEstado(estado);
        return true;
    }

    // PROGRAMACION FUNCIONAL

    // Filtrar tareas por materia
    public List<Tarea> filtrarPorMateria(int idMateria) {

        return tareas.stream()
                .filter(t -> t.getMateria().getId() == idMateria)
                .collect(Collectors.toList());
    }

    // Filtrar tareas por estado
    public List<Tarea> filtrarPorEstado(String estado) {

        return tareas.stream()
                .filter(t -> t.getEstado().equalsIgnoreCase(estado))
                .collect(Collectors.toList());
    }

    // Filtrar tareas por prioridad
    public List<Tarea> filtrarPorPrioridad(String prioridad) {

        return tareas.stream()
                .filter(t -> t.getPrioridad().equalsIgnoreCase(prioridad))
                .collect(Collectors.toList());
    }

    // Ordenar por fecha de entrega
    public List<Tarea> ordenarPorFecha() {

        return tareas.stream()
                .sorted(Comparator.comparing(Tarea::getFechaEntrega))
                .collect(Collectors.toList());
    }

    // Ordenar por prioridad: Alta, Media, Baja
    public List<Tarea> ordenarPorPrioridad() {

        return tareas.stream()
                .sorted(Comparator.comparingInt(
                        t -> valorPrioridad(t.getPrioridad())))
                .collect(Collectors.toList());
    }

    private int valorPrioridad(String prioridad) {

        if (prioridad.equalsIgnoreCase("Alta")) {
            return 1;
        } else if (prioridad.equalsIgnoreCase("Media")) {
            return 2;
        } else {
            return 3;
        }
    }
}

