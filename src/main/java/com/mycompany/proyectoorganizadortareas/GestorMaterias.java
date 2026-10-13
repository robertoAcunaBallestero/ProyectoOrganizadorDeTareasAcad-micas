
package com.mycompany.proyectoorganizadortareas;

import java.util.ArrayList;

public class GestorMaterias {

    private ArrayList<Materia> materias;
    private int contador;

    public GestorMaterias() {
        materias = new ArrayList<>();
        contador = 1;
    }

    // Registrar una materia nueva
    public boolean registrarMateria(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }

        for (Materia materia : materias) {
            if (materia.getNombre().equalsIgnoreCase(nombre.trim())) {
                return false;
            }
        }

        Materia nueva = new Materia(contador, nombre.trim());
        materias.add(nueva);
        contador++;

        return true;
    }

    // Consultar las materias registradas
    public ArrayList<Materia> consultarMaterias() {
        return new ArrayList<>(materias);
    }

    // Buscar una materia por ID
    public Materia buscarMateria(int id) {

        for (Materia materia : materias) {
            if (materia.getId() == id) {
                return materia;
            }
        }

        return null;
    }
}

