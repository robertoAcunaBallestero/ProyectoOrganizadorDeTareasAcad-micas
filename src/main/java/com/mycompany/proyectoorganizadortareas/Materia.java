package com.mycompany.proyectoorganizadortareas;

public class Materia {

    private int id;
    private String nombre;

    // Constructor
    public Materia(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Metodos para consultar los atributos
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Metodo para mostrar la materia
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}



