package com.mycompany.proyectoorganizadortareas;

import java.time.LocalDate;

public class Tarea {

    private int id;
    private String titulo;
    private String descripcion;
    private Materia materia;
    private String prioridad;
    private LocalDate fechaEntrega;
    private String estado;

    // Constructor
    public Tarea(int id, String titulo, String descripcion,
            Materia materia, String prioridad,
            LocalDate fechaEntrega) {

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.materia = materia;
        this.prioridad = prioridad;
        this.fechaEntrega = fechaEntrega;
        this.estado = "Pendiente";
    }

    // Metodos get
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Materia getMateria() {
        return materia;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    // Metodos set
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Mostrar informacion de la tarea
    @Override
    public String toString() {
        return "ID: " + id
                + "\nTitulo: " + titulo
                + "\nDescripcion: " + descripcion
                + "\nMateria: " + materia.getNombre()
                + "\nPrioridad: " + prioridad
                + "\nFecha de entrega: " + fechaEntrega
                + "\nEstado: " + estado;
    }
}


