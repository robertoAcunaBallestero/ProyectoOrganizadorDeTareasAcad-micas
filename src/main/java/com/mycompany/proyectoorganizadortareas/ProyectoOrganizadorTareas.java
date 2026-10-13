
package com.mycompany.proyectoorganizadortareas;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProyectoOrganizadorTareas {

    static GestorMaterias gestorMaterias = new GestorMaterias();
    static GestorTareas gestorTareas = new GestorTareas();

    public static void main(String[] args) {

        int opcion = 0;

        while (opcion != 10) {

            String menu = "ORGANIZADOR DE TAREAS ACADEMICAS\n\n"
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
                    + "Digite una opcion:";

            Integer numero = pedirNumero(menu);
            if (numero == null) {
                break;
            }

            opcion = numero;

            switch (opcion) {
                case 1:
                    registrarMateria();
                    break;
                case 2:
                    consultarMaterias();
                    break;
                case 3:
                    registrarTarea();
                    break;
                case 4:
                    mostrarTareas(gestorTareas.consultarTareas());
                    break;
                case 5:
                    modificarTarea();
                    break;
                case 6:
                    eliminarTarea();
                    break;
                case 7:
                    cambiarEstado();
                    break;
                case 8:
                    filtrarTareas();
                    break;
                case 9:
                    ordenarTareas();
                    break;
                case 10:
                    JOptionPane.showMessageDialog(null, "Hasta luego.");
                    break;
                default:
                    mensaje("Opcion incorrecta.");
            }
        }
    }

    // METODOS AUXILIARES

    public static void mensaje(String texto) {
        JOptionPane.showMessageDialog(null, texto);
    }

    public static String pedirTexto(String pregunta) {

        String texto = JOptionPane.showInputDialog(pregunta);

        if (texto == null) {
            return null;
        }

        if (texto.trim().isEmpty()) {
            mensaje("Debe ingresar un dato.");
            return null;
        }

        return texto.trim();
    }

    public static Integer pedirNumero(String pregunta) {

        String texto = JOptionPane.showInputDialog(pregunta);

        if (texto == null) {
            return null;
        }

        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            mensaje("Debe ingresar un numero valido.");
            return null;
        }
    }

    public static LocalDate pedirFecha() {

        String texto = pedirTexto(
                "Fecha de entrega (AAAA-MM-DD):");

        if (texto == null) {
            return null;
        }

        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException e) {
            mensaje("La fecha no es valida.");
            return null;
        }
    }

    public static String seleccionar(String titulo, String[] opciones) {

        return (String) JOptionPane.showInputDialog(
                null,
                "Seleccione una opcion:",
                titulo,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
    }

    public static Materia seleccionarMateria() {

        List<Materia> materias = gestorMaterias.consultarMaterias();

        if (materias.isEmpty()) {
            mensaje("Primero debe registrar una materia.");
            return null;
        }

        Materia[] opciones = materias.toArray(new Materia[0]);

        return (Materia) JOptionPane.showInputDialog(
                null,
                "Seleccione la materia:",
                "Materias",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
    }

    public static Tarea seleccionarTarea() {

        List<Tarea> tareas = gestorTareas.consultarTareas();

        if (tareas.isEmpty()) {
            mensaje("No existen tareas registradas.");
            return null;
        }

        String lista = "TAREAS REGISTRADAS\n\n";

        for (Tarea tarea : tareas) {
            lista += tarea.getId() + " - "
                    + tarea.getTitulo() + "\n";
        }

        Integer id = pedirNumero(lista + "\nDigite el ID:");

        if (id == null) {
            return null;
        }

        Tarea tarea = gestorTareas.buscarTarea(id);

        if (tarea == null) {
            mensaje("La tarea no existe.");
        }

        return tarea;
    }

    // REGISTRAR MATERIA

    public static void registrarMateria() {

        String nombre = pedirTexto("Nombre de la materia:");

        if (nombre == null) {
            return;
        }

        boolean resultado = gestorMaterias.registrarMateria(nombre);

        if (resultado) {
            mensaje("Materia registrada correctamente.");
        } else {
            mensaje("La materia ya existe o el nombre no es valido.");
        }
    }

    // CONSULTAR MATERIAS

    public static void consultarMaterias() {

        List<Materia> materias = gestorMaterias.consultarMaterias();

        if (materias.isEmpty()) {
            mensaje("No hay materias registradas.");
            return;
        }

        String lista = "MATERIAS REGISTRADAS\n\n";

        for (Materia materia : materias) {
            lista += materia.toString() + "\n";
        }

        mostrarTexto("Materias", lista);
    }

    // REGISTRAR TAREA

    public static void registrarTarea() {

        if (gestorMaterias.consultarMaterias().isEmpty()) {
            mensaje("Primero debe registrar una materia.");
            return;
        }

        String titulo = pedirTexto("Titulo de la tarea:");
        if (titulo == null) return;

        String descripcion = pedirTexto("Descripcion:");
        if (descripcion == null) return;

        Materia materia = seleccionarMateria();
        if (materia == null) return;

        String prioridad = seleccionar("Prioridad",
                new String[]{"Alta", "Media", "Baja"});
        if (prioridad == null) return;

        LocalDate fecha = pedirFecha();
        if (fecha == null) return;

        gestorTareas.registrarTarea(
                titulo, descripcion, materia, prioridad, fecha);

        mensaje("Tarea registrada correctamente.");
    }

    // MOSTRAR TAREAS

    public static void mostrarTareas(List<Tarea> tareas) {

        if (tareas.isEmpty()) {
            mensaje("No hay tareas para mostrar.");
            return;
        }

        String lista = "TAREAS ACADEMICAS\n\n";

        for (Tarea tarea : tareas) {
            lista += tarea.toString()
                    + "\n-------------------------\n";
        }

        mostrarTexto("Consulta de tareas", lista);
    }

    public static void mostrarTexto(String titulo, String texto) {

        JTextArea area = new JTextArea(texto, 15, 40);
        area.setEditable(false);
        area.setCaretPosition(0);

        JOptionPane.showMessageDialog(
                null,
                new JScrollPane(area),
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    // MODIFICAR TAREA

    public static void modificarTarea() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        String titulo = pedirTexto("Nuevo titulo:");
        if (titulo == null) return;

        String descripcion = pedirTexto("Nueva descripcion:");
        if (descripcion == null) return;

        Materia materia = seleccionarMateria();
        if (materia == null) return;

        String prioridad = seleccionar("Prioridad",
                new String[]{"Alta", "Media", "Baja"});
        if (prioridad == null) return;

        LocalDate fecha = pedirFecha();
        if (fecha == null) return;

        boolean resultado = gestorTareas.modificarTarea(
                tarea.getId(), titulo, descripcion,
                materia, prioridad, fecha);

        if (resultado) {
            mensaje("Tarea modificada correctamente.");
        } else {
            mensaje("No fue posible modificar la tarea.");
        }
    }

    // ELIMINAR TAREA

    public static void eliminarTarea() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        int confirmar = JOptionPane.showConfirmDialog(
                null,
                "Desea eliminar la tarea: " + tarea.getTitulo() + "?",
                "Eliminar tarea",
                JOptionPane.YES_NO_OPTION);

        if (confirmar == JOptionPane.YES_OPTION) {

            gestorTareas.eliminarTarea(tarea.getId());
            mensaje("Tarea eliminada correctamente.");
        }
    }

    // CAMBIAR ESTADO

    public static void cambiarEstado() {

        Tarea tarea = seleccionarTarea();
        if (tarea == null) return;

        String estado = seleccionar("Estado",
                new String[]{"Pendiente", "En progreso", "Completada"});

        if (estado == null) return;

        gestorTareas.cambiarEstado(tarea.getId(), estado);

        mensaje("Estado actualizado correctamente.");
    }

    // FILTRAR TAREAS

    public static void filtrarTareas() {

        String filtro = seleccionar("Filtrar tareas",
                new String[]{"Materia", "Estado", "Prioridad"});

        if (filtro == null) return;

        List<Tarea> resultado;

        switch (filtro) {

            case "Materia":
                Materia materia = seleccionarMateria();
                if (materia == null) return;

                resultado = gestorTareas.filtrarPorMateria(
                        materia.getId());
                break;

            case "Estado":
                String estado = seleccionar("Estado",
                        new String[]{"Pendiente", "En progreso", "Completada"});
                if (estado == null) return;

                resultado = gestorTareas.filtrarPorEstado(estado);
                break;

            case "Prioridad":
                String prioridad = seleccionar("Prioridad",
                        new String[]{"Alta", "Media", "Baja"});
                if (prioridad == null) return;

                resultado = gestorTareas.filtrarPorPrioridad(prioridad);
                break;

            default:
                return;
        }

        mostrarTareas(resultado);
    }

    // ORDENAR TAREAS

    public static void ordenarTareas() {

        String opcion = seleccionar("Ordenar tareas",
                new String[]{"Fecha de entrega", "Prioridad"});

        if (opcion == null) return;

        if (opcion.equals("Fecha de entrega")) {
            mostrarTareas(gestorTareas.ordenarPorFecha());
        } else {
            mostrarTareas(gestorTareas.ordenarPorPrioridad());
        }
    }
}




