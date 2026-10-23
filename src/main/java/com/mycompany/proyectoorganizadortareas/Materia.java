package com.mycompany.proyectoorganizadortareas;

import java.util.ArrayList;
import java.util.List;

//Representa una materia académica del estudiante
public class Materia {

    private final int id;
    private String nombre;
    private final List<Tarea> tareas;

    //Construye una nueva materia.
    public Materia(int id, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre de la materia es obligatorio.");
        }

        this.id = id;
        this.nombre = nombre.trim();
        this.tareas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    //Modifica el nombre de la materia.
    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre no puede estar vacío.");
        }

        this.nombre = nombre.trim();
    }

    //Obtiene una copia de la lista de tareas.
    public List<Tarea> getTareas() {
        return new ArrayList<>(tareas);
    }

    //Agrega una tarea a la materia.
    public void agregarTarea(Tarea tarea) {
        if (tarea == null || tarea.getMateria() != this) {
            throw new IllegalArgumentException(
                    "La tarea debe pertenecer a esta materia.");
        }

        if (!tareas.contains(tarea)) {
            tareas.add(tarea);
        }
    }

    //Elimina una tarea de la lista interna.
    public void eliminarTarea(Tarea tarea) {
        tareas.remove(tarea);
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}


