package com.mycompany.proyectoorganizadortareas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Coordina el registro, consulta, modificación y eliminación
 * de materias y tareas académicas.
 */
public class Organizador {

    private final List<Materia> materias;
    private final List<Tarea> tareas;
    private int siguienteIdMateria;
    private int siguienteIdTarea;

    public Organizador() {
        materias = new ArrayList<>();
        tareas = new ArrayList<>();
        siguienteIdMateria = 1;
        siguienteIdTarea = 1;
    }

    // Registra una materia evitando nombres duplicados.
    public boolean registrarMateria(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            return false;
        }

        for (Materia materia : materias) {
            if (materia.getNombre()
                    .equalsIgnoreCase(nombre.trim())) {
                return false;
            }
        }

        Materia nueva = new Materia(
            siguienteIdMateria++, nombre);

        materias.add(nueva);
        return true;
    }

    public List<Materia> consultarMaterias() {
        return new ArrayList<>(materias);
    }

    public Materia buscarMateria(int id) {
        for (Materia materia : materias) {
            if (materia.getId() == id) {
                return materia;
            }
        }
        return null;
    }

    // Registra la tarea en el organizador y su materia.
    public boolean registrarTarea(
            String titulo,
            String descripcion,
            Materia materia,
            Prioridad prioridad,
            LocalDate fechaEntrega) {

        if (titulo == null || titulo.isBlank()
                || materia == null
                || prioridad == null
                || fechaEntrega == null
                || !materias.contains(materia)) {
            return false;
        }

        Tarea nueva = new Tarea(
            siguienteIdTarea++,
            titulo,
            descripcion,
            materia,
            prioridad,
            fechaEntrega
        );

        tareas.add(nueva);
        materia.agregarTarea(nueva);

        return true;
    }

    public List<Tarea> consultarTareas() {
        return new ArrayList<>(tareas);
    }

    public Tarea buscarTarea(int id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return tarea;
            }
        }
        return null;
    }

    // Actualiza los datos y mantiene la asociación con la materia.
    public boolean modificarTarea(
            int id,
            String titulo,
            String descripcion,
            Materia nuevaMateria,
            Prioridad prioridad,
            LocalDate fechaEntrega) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null
                || titulo == null || titulo.isBlank()
                || nuevaMateria == null
                || !materias.contains(nuevaMateria)
                || prioridad == null
                || fechaEntrega == null) {
            return false;
        }

        Materia materiaAnterior = tarea.getMateria();

        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setPrioridad(prioridad);
        tarea.setFechaEntrega(fechaEntrega);

        if (materiaAnterior != nuevaMateria) {
            materiaAnterior.eliminarTarea(tarea);
            tarea.setMateria(nuevaMateria);
            nuevaMateria.agregarTarea(tarea);
        }

        return true;
    }

    public boolean eliminarTarea(int id) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null) {
            return false;
        }

        tarea.getMateria().eliminarTarea(tarea);
        tareas.remove(tarea);

        return true;
    }

    public boolean cambiarEstado(
            int id, EstadoTarea nuevoEstado) {

        Tarea tarea = buscarTarea(id);

        if (tarea == null || nuevoEstado == null) {
            return false;
        }

        tarea.actualizarEstado(nuevoEstado);
        return true;
    }

    // Delega los filtros y ordenamientos al paradigma funcional.
    public List<Tarea> filtrarPorMateria(Materia materia) {
        return OperacionesFuncionales.filtrarPorMateria(
            tareas, materia);
    }

    public List<Tarea> filtrarPorEstado(EstadoTarea estado) {
        return OperacionesFuncionales.filtrarPorEstado(
            tareas, estado);
    }

    public List<Tarea> filtrarPorPrioridad(Prioridad prioridad) {
        return OperacionesFuncionales.filtrarPorPrioridad(
            tareas, prioridad);
    }

    public List<Tarea> ordenarPorFecha() {
        return OperacionesFuncionales.ordenarPorFecha(tareas);
    }

    public List<Tarea> ordenarPorPrioridad() {
        return OperacionesFuncionales.ordenarPorPrioridad(tareas);
    }

    public List<Tarea> consultarConFiltros(
            EstadoTarea estado,
            Materia materia,
            Prioridad prioridad,
            boolean ordenarPorFecha) {

        return OperacionesFuncionales.consultarTareas(
            tareas, estado, materia, prioridad, ordenarPorFecha);
    }
}
