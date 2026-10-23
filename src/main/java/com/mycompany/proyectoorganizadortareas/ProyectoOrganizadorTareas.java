package com.mycompany.proyectoorganizadortareas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Clase principal del organizador de tareas académicas.
 * Maneja el menú y la interacción con el usuario.
 */
public class ProyectoOrganizadorTareas {

    private static final Organizador organizador =
            new Organizador();

    public static void main(String[] args) {

        while (true) {

            String menu =
                "ORGANIZADOR DE TAREAS ACADÉMICAS\n\n"
                + "1. Registrar materia\n"
                + "2. Consultar materias\n"
                + "3. Registrar tarea\n"
                + "4. Consultar tareas\n"
                + "5. Modificar tarea\n"
                + "6. Eliminar tarea\n"
                + "7. Cambiar estado\n"
                + "8. Filtrar tareas\n"
                + "9. Ordenar tareas\n"
                + "10. Salir\n\n"
                + "Seleccione una opción:";

            Integer opcion = pedirNumero(menu);

            if (opcion == null || opcion == 10) {
                mensaje("Hasta luego.");
                return;
            }

            switch (opcion) {
                case 1 -> registrarMateria();
                case 2 -> consultarMaterias();
                case 3 -> registrarTarea();
                case 4 -> mostrarTareas(
                    organizador.consultarTareas());
                case 5 -> modificarTarea();
                case 6 -> eliminarTarea();
                case 7 -> cambiarEstado();
                case 8 -> filtrarTareas();
                case 9 -> ordenarTareas();
                default -> mensaje("Opción incorrecta.");
            }
        }
    }

    // Métodos auxiliares para capturar y mostrar información.

    private static void mensaje(String texto) {
        JOptionPane.showMessageDialog(null, texto);
    }

    private static String pedirTexto(String pregunta) {

        String texto = JOptionPane.showInputDialog(pregunta);

        if (texto == null) {
            return null;
        }

        if (texto.isBlank()) {
            mensaje("El dato es obligatorio.");
            return null;
        }

        return texto.trim();
    }

    private static Integer pedirNumero(String pregunta) {

        String texto = JOptionPane.showInputDialog(pregunta);

        if (texto == null) {
            return null;
        }

        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            mensaje("Ingrese un número válido.");
            return null;
        }
    }

    private static LocalDate pedirFecha() {

        String texto = pedirTexto(
            "Fecha de entrega (AAAA-MM-DD):");

        if (texto == null) {
            return null;
        }

        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            mensaje("Fecha incorrecta.");
            return null;
        }
    }

    private static <T> T seleccionar(
            String titulo, T[] opciones) {

        if (opciones.length == 0) {
            return null;
        }

        int indice = JOptionPane.showOptionDialog(
            null,
            "Seleccione una opción:",
            titulo,
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        return indice < 0 ? null : opciones[indice];
    }

    private static Materia seleccionarMateria() {

        List<Materia> materias =
            organizador.consultarMaterias();

        if (materias.isEmpty()) {
            mensaje("Primero debe registrar una materia.");
            return null;
        }

        return seleccionar(
            "Materias", materias.toArray(new Materia[0]));
    }

    private static Tarea seleccionarTarea() {

        List<Tarea> tareas = organizador.consultarTareas();

        if (tareas.isEmpty()) {
            mensaje("No hay tareas registradas.");
            return null;
        }

        StringBuilder listado = new StringBuilder();

        for (Tarea tarea : tareas) {
            listado.append(tarea.getId())
                   .append(" - ")
                   .append(tarea.getTitulo())
                   .append("\n");
        }

        Integer id = pedirNumero(
            listado + "\nDigite el ID de la tarea:");

        if (id == null) {
            return null;
        }

        Tarea tarea = organizador.buscarTarea(id);

        if (tarea == null) {
            mensaje("La tarea no existe.");
        }

        return tarea;
    }

    private static void mostrarTexto(
            String titulo, String contenido) {

        JTextArea area = new JTextArea(contenido, 18, 45);
        area.setEditable(false);
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(
            null,
            new JScrollPane(area),
            titulo,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarTareas(List<Tarea> tareas) {

        if (tareas.isEmpty()) {
            mensaje("No hay tareas para mostrar.");
            return;
        }

        StringBuilder texto = new StringBuilder(
            "TAREAS ACADÉMICAS\n\n");

        for (Tarea tarea : tareas) {
            texto.append(tarea)
                 .append("\n----------------------\n");
        }

        mostrarTexto("Consulta de tareas", texto.toString());
    }

    // Registro y consulta de materias.

    private static void registrarMateria() {

        String nombre = pedirTexto("Nombre de la materia:");
        if (nombre == null) return;

        boolean registrado =
            organizador.registrarMateria(nombre);

        mensaje(registrado
            ? "Materia registrada correctamente."
            : "La materia ya existe o el nombre es inválido.");
    }

    private static void consultarMaterias() {

        List<Materia> materias =
            organizador.consultarMaterias();

        if (materias.isEmpty()) {
            mensaje("No hay materias registradas.");
            return;
        }

        StringBuilder texto = new StringBuilder();

        for (Materia materia : materias) {
            texto.append(materia).append("\n");
        }

        mostrarTexto("Materias", texto.toString());
    }

    // Registra una tarea y la asocia con una materia.

    private static void registrarTarea() {

        if (organizador.consultarMaterias().isEmpty()) {
            mensaje("Primero debe registrar una materia.");
            return;
        }

        String titulo = pedirTexto("Título de la tarea:");
        if (titulo == null) return;

        String descripcion = pedirTexto("Descripción:");
        if (descripcion == null) return;

        Materia materia = seleccionarMateria();
        if (materia == null) return;

        Prioridad prioridad = seleccionar(
            "Prioridad", Prioridad.values());
        if (prioridad == null) return;

        LocalDate fecha = pedirFecha();
        if (fecha == null) return;

        boolean resultado = organizador.registrarTarea(
            titulo, descripcion, materia, prioridad, fecha);

        mensaje(resultado
            ? "Tarea registrada correctamente."
            : "No fue posible registrar la tarea.");
    }

    // Modifica los datos de una tarea existente.

    private static void modificarTarea() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        String titulo = pedirTexto("Nuevo título:");
        if (titulo == null) return;

        String descripcion = pedirTexto("Nueva descripción:");
        if (descripcion == null) return;

        Materia materia = seleccionarMateria();
        if (materia == null) return;

        Prioridad prioridad = seleccionar(
            "Prioridad", Prioridad.values());
        if (prioridad == null) return;

        LocalDate fecha = pedirFecha();
        if (fecha == null) return;

        boolean resultado = organizador.modificarTarea(
            tarea.getId(), titulo, descripcion,
            materia, prioridad, fecha
        );

        mensaje(resultado
            ? "Tarea modificada correctamente."
            : "No fue posible modificar la tarea.");
    }

    // Elimina una tarea después de confirmar la operación.

    private static void eliminarTarea() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        int respuesta = JOptionPane.showConfirmDialog(
            null,
            "¿Desea eliminar " + tarea.getTitulo() + "?",
            "Eliminar tarea",
            JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            boolean resultado =
                organizador.eliminarTarea(tarea.getId());

            mensaje(resultado
                ? "Tarea eliminada correctamente."
                : "No fue posible eliminar la tarea.");
        }
    }

    // Actualiza el estado de una tarea.

    private static void cambiarEstado() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        EstadoTarea estado = seleccionar(
            "Estado de la tarea", EstadoTarea.values());

        if (estado == null) return;

        boolean resultado = organizador.cambiarEstado(
            tarea.getId(), estado);

        mensaje(resultado
            ? "Estado actualizado correctamente."
            : "No fue posible actualizar el estado.");
    }

    // Permite aplicar filtros individuales o combinados.

    private static void filtrarTareas() {

        String filtro = seleccionar(
            "Filtrar tareas",
            new String[]{
                "Materia", "Estado", "Prioridad",
                "Combinar filtros"
            });

        if (filtro == null) return;

        List<Tarea> resultado;

        switch (filtro) {

            case "Materia" -> {
                Materia materia = seleccionarMateria();
                if (materia == null) return;

                resultado = organizador.filtrarPorMateria(materia);
            }

            case "Estado" -> {
                EstadoTarea estado = seleccionar(
                    "Estado", EstadoTarea.values());
                if (estado == null) return;

                resultado = organizador.filtrarPorEstado(estado);
            }

            case "Prioridad" -> {
                Prioridad prioridad = seleccionar(
                    "Prioridad", Prioridad.values());
                if (prioridad == null) return;

                resultado = organizador.filtrarPorPrioridad(prioridad);
            }

            case "Combinar filtros" -> {
                String[] criterios = {"Todos", "Seleccionar"};

                String usarMateria = seleccionar(
                    "Filtro de materia", criterios);
                if (usarMateria == null) return;

                Materia materia = null;
                if (usarMateria.equals("Seleccionar")) {
                    materia = seleccionarMateria();
                    if (materia == null) return;
                }

                String usarEstado = seleccionar(
                    "Filtro de estado", criterios);
                if (usarEstado == null) return;

                EstadoTarea estado = null;
                if (usarEstado.equals("Seleccionar")) {
                    estado = seleccionar(
                        "Estado", EstadoTarea.values());
                    if (estado == null) return;
                }

                String usarPrioridad = seleccionar(
                    "Filtro de prioridad", criterios);
                if (usarPrioridad == null) return;

                Prioridad prioridad = null;
                if (usarPrioridad.equals("Seleccionar")) {
                    prioridad = seleccionar(
                        "Prioridad", Prioridad.values());
                    if (prioridad == null) return;
                }

                resultado = organizador.consultarConFiltros(
                    estado, materia, prioridad, false);
            }

            default -> {
                return;
            }
        }

        mostrarTareas(resultado);
    }

    // Ordena las tareas sin modificar su colección original.

    private static void ordenarTareas() {

        String opcion = seleccionar(
            "Ordenar tareas",
            new String[]{"Fecha de entrega", "Prioridad"});

        if (opcion == null) return;

        if (opcion.equals("Fecha de entrega")) {
            mostrarTareas(organizador.ordenarPorFecha());
        } else {
            mostrarTareas(organizador.ordenarPorPrioridad());
        }
    }
}


