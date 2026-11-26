package com.mycompany.proyectoorganizadortareas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

/** Suite independiente de 44 casos; ejecutar su propio método main. */
public final class PruebasOrganizador {
    private static int total, aprobadas, fallidas;
    private static final LocalDate FECHA = LocalDate.of(2026, 12, 1);

    @FunctionalInterface
    private interface Caso { boolean ejecutar() throws Exception; }

    private static void probar(String nombre, Caso caso) {
        total++;
        try {
            if (!caso.ejecutar()) throw new AssertionError("Resultado inesperado");
            aprobadas++;
            System.out.printf("CP-%02d | OK    | %s%n", total, nombre);
        } catch (Exception | AssertionError e) {
            fallidas++;
            System.out.printf("CP-%02d | FALLO | %s: %s%n", total, nombre, e);
        }
    }

    private static Organizador base() {
        Organizador o = new Organizador();
        o.registrarMateria("Programación");
        o.registrarMateria("Álgebra");
        o.registrarTarea("Proyecto", "Trabajo final", o.buscarMateria(1), Prioridad.MEDIA, FECHA.plusDays(2));
        o.registrarTarea("Ejercicios", "Práctica", o.buscarMateria(2), Prioridad.ALTA, FECHA);
        o.registrarTarea("Lectura", "", o.buscarMateria(1), Prioridad.BAJA, FECHA.plusDays(1));
        o.cambiarEstado(3, EstadoTarea.COMPLETADA);
        return o;
    }

    private static List<Integer> ids(List<Tarea> tareas) {
        return tareas.stream().map(Tarea::getId).toList();
    }

    public static void main(String[] args) throws Exception {
        // Aislar el repositorio antes de inicializar RepositorioDatos.
        String hogarOriginal = System.getProperty("user.home");
        Path temporal = Files.createTempDirectory("academia-pruebas-");
        System.setProperty("user.home", temporal.toString());
        try {
            if (!RepositorioDatos.rutaArchivo().normalize().startsWith(temporal.normalize())) {
                throw new IllegalStateException("La ruta de pruebas no está aislada. No se ejecutará la suite.");
            }
            ejecutar();
        } finally {
            System.setProperty("user.home", hogarOriginal);
            try (var rutas = Files.walk(temporal)) {
                for (Path ruta : rutas.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(ruta);
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("TOTAL: " + total);
        System.out.println("APROBADAS: " + aprobadas);
        System.out.println("FALLIDAS: " + fallidas);
        if (fallidas != 0) throw new AssertionError("Hay pruebas fallidas");
    }

    private static void ejecutar() {
        System.out.println("ACADEMIA 2.0 | PRUEBAS AUTOMATIZADAS");
        probar("Registrar materia válida", () -> { var o = new Organizador(); return o.registrarMateria("Redes") && o.consultarMaterias().size() == 1; });
        probar("Rechazar materia vacía", () -> !new Organizador().registrarMateria(""));
        probar("Rechazar materia con espacios", () -> !new Organizador().registrarMateria("   "));
        probar("Rechazar materia nula", () -> !new Organizador().registrarMateria(null));
        probar("Rechazar materia duplicada", () -> { var o = base(); return !o.registrarMateria("Programación") && o.consultarMaterias().size() == 2; });
        probar("Rechazar duplicado con mayúsculas y espacios", () -> !base().registrarMateria("  PROGRAMACIÓN  "));
        probar("Consultar materias", () -> base().consultarMaterias().size() == 2);
        probar("Buscar materia existente", () -> "Álgebra".equals(base().buscarMateria(2).getNombre()));
        probar("Buscar materia inexistente", () -> base().buscarMateria(999) == null);
        probar("Consultar materias sin exponer la lista interna", () -> { var o = base(); o.consultarMaterias().clear(); return o.consultarMaterias().size() == 2; });
        probar("Registrar tarea válida", () -> { var o = base(); return o.registrarTarea("Informe", "", o.buscarMateria(1), Prioridad.ALTA, FECHA) && o.consultarTareas().size() == 4; });
        probar("Estado inicial pendiente", () -> base().buscarTarea(1).getEstado() == EstadoTarea.PENDIENTE);
        probar("Asociar tarea con materia", () -> { var o = base(); return o.buscarTarea(1).getMateria() == o.buscarMateria(1) && ids(o.buscarMateria(1).getTareas()).equals(List.of(1,3)); });
        probar("Rechazar título vacío", () -> { var o = base(); return !o.registrarTarea(" ", "", o.buscarMateria(1), Prioridad.ALTA, FECHA); });
        probar("Rechazar título nulo", () -> { var o = base(); return !o.registrarTarea(null, "", o.buscarMateria(1), Prioridad.ALTA, FECHA); });
        probar("Rechazar tarea sin materia", () -> !base().registrarTarea("Informe", "", null, Prioridad.ALTA, FECHA));
        probar("Rechazar materia ajena al organizador", () -> !base().registrarTarea("Informe", "", new Materia(1,"Otra"), Prioridad.ALTA, FECHA));
        probar("Rechazar tarea sin prioridad", () -> { var o = base(); return !o.registrarTarea("Informe", "", o.buscarMateria(1), null, FECHA); });
        probar("Rechazar tarea sin fecha", () -> { var o = base(); return !o.registrarTarea("Informe", "", o.buscarMateria(1), Prioridad.ALTA, null); });
        probar("Aceptar descripción opcional", () -> { var o = base(); return o.registrarTarea("Informe", null, o.buscarMateria(1), Prioridad.ALTA, FECHA) && o.buscarTarea(4).getDescripcion().isEmpty(); });
        probar("Consultar y buscar tareas", () -> { var o = base(); return ids(o.consultarTareas()).equals(List.of(1,2,3)) && o.buscarTarea(999) == null; });
        probar("Modificar título, descripción, prioridad y fecha", () -> { var o = base(); var m = o.buscarMateria(1); boolean ok = o.modificarTarea(1,"Nuevo","Detalle",m,Prioridad.ALTA,FECHA); var t = o.buscarTarea(1); return ok && t.getTitulo().equals("Nuevo") && t.getDescripcion().equals("Detalle") && t.getPrioridad()==Prioridad.ALTA && t.getFechaEntrega().equals(FECHA); });
        probar("Reasignar tarea y actualizar ambas materias", () -> { var o = base(); return o.modificarTarea(1,"Proyecto","",o.buscarMateria(2),Prioridad.MEDIA,FECHA) && ids(o.buscarMateria(1).getTareas()).equals(List.of(3)) && ids(o.buscarMateria(2).getTareas()).equals(List.of(2,1)); });
        probar("Rechazar modificación de tarea inexistente", () -> { var o = base(); return !o.modificarTarea(999,"Nuevo","",o.buscarMateria(1),Prioridad.ALTA,FECHA); });
        probar("Rechazar modificación inválida sin cambiar datos", () -> { var o = base(); return !o.modificarTarea(1,"","",o.buscarMateria(2),Prioridad.ALTA,FECHA) && o.buscarTarea(1).getTitulo().equals("Proyecto") && o.buscarTarea(1).getMateria()==o.buscarMateria(1); });
        probar("Eliminar tarea y desvincularla de materia", () -> { var o = base(); return o.eliminarTarea(1) && o.buscarTarea(1)==null && ids(o.buscarMateria(1).getTareas()).equals(List.of(3)); });
        probar("Rechazar eliminación inexistente", () -> !base().eliminarTarea(999));
        probar("Cambiar a completada", () -> { var o = base(); return o.cambiarEstado(1,EstadoTarea.COMPLETADA) && o.buscarTarea(1).getEstado()==EstadoTarea.COMPLETADA; });
        probar("Cambiar de completada a pendiente", () -> { var o = base(); return o.cambiarEstado(3,EstadoTarea.PENDIENTE) && o.buscarTarea(3).getEstado()==EstadoTarea.PENDIENTE; });
        probar("Rechazar estado nulo", () -> !base().cambiarEstado(1,null));
        probar("Rechazar cambio de estado de tarea inexistente", () -> !base().cambiarEstado(999,EstadoTarea.PENDIENTE));
        probar("Filtrar por materia", () -> { var o = base(); return ids(o.filtrarPorMateria(o.buscarMateria(1))).equals(List.of(1,3)); });
        probar("Filtrar tareas pendientes", () -> ids(base().filtrarPorEstado(EstadoTarea.PENDIENTE)).equals(List.of(1,2)));
        probar("Filtrar tareas completadas", () -> ids(base().filtrarPorEstado(EstadoTarea.COMPLETADA)).equals(List.of(3)));
        probar("Filtrar por prioridad", () -> ids(base().filtrarPorPrioridad(Prioridad.ALTA)).equals(List.of(2)));
        probar("Ordenar por fecha ascendente", () -> ids(base().ordenarPorFecha()).equals(List.of(2,3,1)));
        probar("Ordenar prioridad alta, media y baja", () -> ids(base().ordenarPorPrioridad()).equals(List.of(2,1,3)));
        probar("Combinar filtros y ordenamiento", () -> { var o = base(); return ids(o.consultarConFiltros(EstadoTarea.PENDIENTE,o.buscarMateria(2),Prioridad.ALTA,true)).equals(List.of(2)) && ids(o.consultarConFiltros(null,o.buscarMateria(1),null,true)).equals(List.of(3,1)); });
        probar("Consulta sin coincidencias", () -> { var o = base(); return o.consultarConFiltros(EstadoTarea.COMPLETADA,o.buscarMateria(2),Prioridad.ALTA,false).isEmpty(); });
        probar("Filtrar y ordenar sin modificar lista ni atributos originales", () -> { var o = base(); var lista = o.consultarTareas(); var antes = lista.stream().map(Tarea::toString).toList(); OperacionesFuncionales.ordenarPorFecha(lista); OperacionesFuncionales.ordenarPorPrioridad(lista); OperacionesFuncionales.filtrarPorEstado(lista,EstadoTarea.COMPLETADA); return ids(lista).equals(List.of(1,2,3)) && antes.equals(o.consultarTareas().stream().map(Tarea::toString).toList()); });
        probar("Cargar sin archivo devuelve organizador vacío", () -> { Files.deleteIfExists(RepositorioDatos.rutaArchivo()); var o = RepositorioDatos.cargar(); return o.consultarMaterias().isEmpty() && o.consultarTareas().isEmpty(); });
        probar("Guardar y recuperar materias y tareas completas", () -> { var original = base(); RepositorioDatos.guardar(original); var o = RepositorioDatos.cargar(); return Files.exists(RepositorioDatos.rutaArchivo()) && o.consultarMaterias().stream().map(Materia::getNombre).toList().equals(List.of("Programación","Álgebra")) && o.consultarTareas().stream().map(Tarea::toString).toList().equals(original.consultarTareas().stream().map(Tarea::toString).toList()) && ids(o.buscarMateria(1).getTareas()).equals(List.of(1,3)); });
        probar("Conservar consecutivos tras eliminar y recuperar", () -> { var o = base(); o.eliminarTarea(3); RepositorioDatos.guardar(o); var r = RepositorioDatos.cargar(); return r.registrarMateria("Redes") && r.buscarMateria(3)!=null && r.registrarTarea("Informe","",r.buscarMateria(3),Prioridad.ALTA,FECHA) && r.buscarTarea(4)!=null && r.buscarTarea(3)==null; });
        probar("Rechazar archivo de datos inválido", () -> { Files.createDirectories(RepositorioDatos.rutaArchivo().getParent()); Files.writeString(RepositorioDatos.rutaArchivo(),"materias.total=abc\n"); try { RepositorioDatos.cargar(); return false; } catch (IOException esperado) { return true; } });
    }
}