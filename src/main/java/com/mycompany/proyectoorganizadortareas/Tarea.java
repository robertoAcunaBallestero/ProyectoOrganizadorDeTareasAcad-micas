package com.mycompany.proyectoorganizadortareas;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una tarea académica con sus datos y estado.
 */
public class Tarea {

    private final int id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaEntrega;
    private Prioridad prioridad;
    private EstadoTarea estado;
    private Materia materia;

    public Tarea(int id, String titulo, String descripcion,
            Materia materia, Prioridad prioridad,
            LocalDate fechaEntrega) {

        this.id = id;
        setTitulo(titulo);
        setDescripcion(descripcion);
        setMateria(materia);
        setPrioridad(prioridad);
        setFechaEntrega(fechaEntrega);

        // Toda tarea nueva inicia pendiente.
        this.estado = EstadoTarea.PENDIENTE;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                "El título es obligatorio.");
        }
        this.titulo = titulo.trim();
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = Objects.requireNonNullElse(
            descripcion, "").trim();
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = Objects.requireNonNull(
            fechaEntrega, "La fecha es obligatoria.");
    }

    public void setPrioridad(Prioridad prioridad) {
        this.prioridad = Objects.requireNonNull(
            prioridad, "La prioridad es obligatoria.");
    }

    public void setMateria(Materia materia) {
        this.materia = Objects.requireNonNull(
            materia, "La materia es obligatoria.");
    }

    // Cambia el estado utilizando únicamente valores válidos.
    public void actualizarEstado(EstadoTarea nuevoEstado) {
        this.estado = Objects.requireNonNull(
            nuevoEstado, "El estado no es válido.");
    }

    @Override
    public String toString() {
        return "ID: " + id
            + "\nTítulo: " + titulo
            + "\nDescripción: " + descripcion
            + "\nMateria: " + materia.getNombre()
            + "\nPrioridad: " + prioridad
            + "\nFecha de entrega: " + fechaEntrega
            + "\nEstado: " + estado;
    }
}

