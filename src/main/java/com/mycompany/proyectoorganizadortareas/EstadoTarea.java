package com.mycompany.proyectoorganizadortareas;

/** Estados posibles para una tarea académica. */
public enum EstadoTarea {
    PENDIENTE("Pendiente"),
    COMPLETADA("Completada");

    private final String etiqueta;

    EstadoTarea(String etiqueta) { this.etiqueta = etiqueta; }

    @Override
    public String toString() { return etiqueta; }
}
