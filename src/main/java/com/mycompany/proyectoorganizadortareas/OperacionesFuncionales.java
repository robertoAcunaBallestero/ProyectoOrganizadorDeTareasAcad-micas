package com.mycompany.proyectoorganizadortareas;

import java.util.Comparator;
import java.util.List;

/**
 * Filtra y ordena tareas aplicando programación funcional.
 * Las operaciones no modifican la lista original.
 */
public final class OperacionesFuncionales {

    private OperacionesFuncionales() {
    }

    public static List<Tarea> filtrarPorMateria(
            List<Tarea> tareas, Materia materia) {

        return tareas.stream()
                .filter(t -> t.getMateria() == materia)
                .toList();
    }

    public static List<Tarea> filtrarPorEstado(
            List<Tarea> tareas, EstadoTarea estado) {

        return tareas.stream()
                .filter(t -> t.getEstado() == estado)
                .toList();
    }

    public static List<Tarea> filtrarPorPrioridad(
            List<Tarea> tareas, Prioridad prioridad) {

        return tareas.stream()
                .filter(t -> t.getPrioridad() == prioridad)
                .toList();
    }

    public static List<Tarea> ordenarPorFecha(
            List<Tarea> tareas) {

        return tareas.stream()
                .sorted(Comparator.comparing(
                    Tarea::getFechaEntrega))
                .toList();
    }

    public static List<Tarea> ordenarPorPrioridad(
            List<Tarea> tareas) {

        return tareas.stream()
                .sorted(Comparator.comparing(
                    Tarea::getPrioridad))
                .toList();
    }

    // Combina filtros opcionales sin alterar los datos.
    public static List<Tarea> consultarTareas(
            List<Tarea> tareas,
            EstadoTarea estadoFiltro,
            Materia materiaFiltro,
            Prioridad prioridadFiltro,
            boolean ordenarPorFecha) {

        var resultado = tareas.stream();

        if (estadoFiltro != null) {
            resultado = resultado.filter(
                t -> t.getEstado() == estadoFiltro);
        }

        if (materiaFiltro != null) {
            resultado = resultado.filter(
                t -> t.getMateria() == materiaFiltro);
        }

        if (prioridadFiltro != null) {
            resultado = resultado.filter(
                t -> t.getPrioridad() == prioridadFiltro);
        }

        if (ordenarPorFecha) {
            resultado = resultado.sorted(
                Comparator.comparing(Tarea::getFechaEntrega));
        }

        return resultado.toList();
    }
}