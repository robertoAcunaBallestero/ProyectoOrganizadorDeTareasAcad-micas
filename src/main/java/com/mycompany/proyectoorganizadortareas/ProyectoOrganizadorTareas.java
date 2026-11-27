package com.mycompany.proyectoorganizadortareas;

import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/** Interfaz gráfica Swing para el organizador académico. Requiere Java 17+. */
public class ProyectoOrganizadorTareas extends JFrame {
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final Color FONDO = new Color(246, 248, 252);
    private static final Color AZUL = new Color(33, 53, 91);
    private static final Color ACENTO = new Color(67, 97, 238);
    private static final Color TEXTO = new Color(37, 49, 68);
    private static final Color GRIS = new Color(105, 116, 135);
    private static final Color MENU_BOTON = new Color(228, 234, 245);
    private static final Color MENU_HOVER = new Color(198, 215, 242);
    private static final Color SECUNDARIO = new Color(221, 229, 245);
    private static final Color SECUNDARIO_HOVER = new Color(204, 216, 240);
    private static final Color PELIGRO = new Color(214, 74, 74);
    private static final Color PELIGRO_HOVER = new Color(190, 58, 58);
    private final Organizador organizador;
    private boolean guardadoHabilitado = true;
    private final JLabel avisoEntregas = new JLabel();
    private final JPanel panelAvisos = new JPanel(new BorderLayout(8, 0));
    private final ModeloTareas modelo = new ModeloTareas();
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<Object> filtroMateria = new JComboBox<>();
    private final JComboBox<String> filtroEstado = new JComboBox<>(new String[]{"Todos los estados", "Pendiente", "Completada"});
    private final JComboBox<String> filtroPrioridad = new JComboBox<>(new String[]{"Todas las prioridades", "Alta", "Media", "Baja"});
    private final JComboBox<String> orden = new JComboBox<>(new String[]{"Orden original", "Fecha de entrega", "Prioridad"});
    private final JTextField buscar = new JTextField();
    private final JLabel resumen = new JLabel();
    private final JLabel totalMaterias = new JLabel("0");
    private final JLabel totalTareas = new JLabel("0");
    private final JLabel pendientes = new JLabel("0");
    private final JLabel completadas = new JLabel("0");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new ProyectoOrganizadorTareas().setVisible(true);
        });
    }

    public ProyectoOrganizadorTareas() {
        super("ACADEMIA 2.0 | Organizador de Tareas Académicas");
        Organizador recuperado;
        String errorCarga = null;
        try {
            recuperado = RepositorioSQLServer.cargar();
        } catch (SQLException e) {
            recuperado = new Organizador();
            guardadoHabilitado = false; // Evita sobrescribir datos en caso de fallo de conexión.
            errorCarga = e.getMessage();
        }
        organizador = recuperado;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1130, 730);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO);
        add(crearBarraLateral(), BorderLayout.WEST);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
        if (errorCarga != null) {
            final String detalle = errorCarga;
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this,
                    "No se pudieron recuperar los datos desde SQL Server: " + detalle
                    + "\nPor seguridad, el guardado automático está desactivado en esta sesión."
                    + "\nBase de datos: OrganizadorTareasDB",
                    "Atención: error de conexión", JOptionPane.ERROR_MESSAGE));
        } else if (organizador.consultarMaterias().isEmpty()) {
            SwingUtilities.invokeLater(this::mostrarBienvenida);
        }
    }

    private JPanel crearBarraLateral() {
        JPanel lateral = new JPanel();
        lateral.setBackground(AZUL);
        lateral.setPreferredSize(new Dimension(220, 0));
        lateral.setBorder(new EmptyBorder(32, 16, 20, 16));
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        JLabel marca = new JLabel("  ACADEMIA 2.0");
        marca.setForeground(Color.WHITE);
        marca.setFont(new Font("SansSerif", Font.BOLD, 20));
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(marca);
        JLabel subtitulo = new JLabel("  Organizador personal");
        subtitulo.setForeground(new Color(184, 201, 234));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(subtitulo);
        lateral.add(Box.createVerticalStrut(50));
        lateral.add(botonMenu("  Inicio / tareas", () -> limpiarFiltros()));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(botonMenu("  Mis materias", this::mostrarMaterias));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(botonMenu("  Agregar materia", this::registrarMateria));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(botonMenu("  Nueva tarea", this::registrarTarea));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(botonMenu("  ¿Cómo usar?", this::mostrarAyuda));
        lateral.add(Box.createVerticalGlue());
        JLabel pie = new JLabel("  SC-250  |  Java + Swing");
        pie.setForeground(new Color(184, 201, 234));
        pie.setAlignmentX(Component.LEFT_ALIGNMENT);
        lateral.add(pie);
        return lateral;
    }

    private JButton botonMenu(String titulo, Runnable accion) {
        JButton boton = new JButton(titulo);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 43));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(new Font("SansSerif", Font.BOLD, 13));
        configurarBoton(boton, MENU_BOTON, Color.BLACK, MENU_HOVER,
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(156, 174, 204)),
                        new EmptyBorder(10, 12, 10, 8)));
        boton.addActionListener(e -> accion.run());
        return boton;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout(0, 19));
        contenido.setBackground(FONDO);
        contenido.setBorder(new EmptyBorder(28, 28, 26, 28));
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);
        JPanel titulo = new JPanel(new GridLayout(2, 1, 0, 3));
        titulo.setOpaque(false);
        JLabel h1 = new JLabel("Mis tareas académicas");
        h1.setFont(new Font("SansSerif", Font.BOLD, 27));
        h1.setForeground(TEXTO);
        JLabel h2 = new JLabel("Organizá tus materias, fechas de entrega y prioridades en un solo lugar.");
        h2.setForeground(GRIS);
        titulo.add(h1); titulo.add(h2);
        cabecera.add(titulo, BorderLayout.WEST);
        JButton nueva = botonAccion("+ Nueva tarea", new Color(204, 220, 250), Color.BLACK);
        nueva.addActionListener(e -> registrarTarea());
        cabecera.add(nueva, BorderLayout.EAST);
        contenido.add(cabecera, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 18));
        centro.setOpaque(false);
        JPanel tarjetas = new JPanel(new GridLayout(1, 4, 13, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(tarjeta("Materias", totalMaterias));
        tarjetas.add(tarjeta("Total de tareas", totalTareas));
        tarjetas.add(tarjeta("Pendientes", pendientes));
        tarjetas.add(tarjeta("Completadas", completadas));
        JPanel resumenSuperior = new JPanel(new BorderLayout(0, 10));
        resumenSuperior.setOpaque(false);
        resumenSuperior.add(tarjetas, BorderLayout.CENTER);
        avisoEntregas.setOpaque(true);
        avisoEntregas.setBackground(new Color(233, 240, 255));
        avisoEntregas.setForeground(AZUL);
        avisoEntregas.setFont(new Font("SansSerif", Font.BOLD, 12));
        avisoEntregas.setBorder(new EmptyBorder(10, 13, 10, 13));
        panelAvisos.setBackground(new Color(233, 240, 255));
        panelAvisos.add(avisoEntregas, BorderLayout.CENTER);
        JButton verAvisos = botonAccion("Ver entregas", SECUNDARIO, Color.BLACK);
        verAvisos.addActionListener(e -> mostrarAvisos());
        panelAvisos.add(verAvisos, BorderLayout.EAST);
        resumenSuperior.add(panelAvisos, BorderLayout.SOUTH);
        centro.add(resumenSuperior, BorderLayout.NORTH);

        JPanel bloque = new JPanel(new BorderLayout(0, 15));
        bloque.setBackground(Color.WHITE);
        bloque.setBorder(new EmptyBorder(18, 18, 18, 18));
        JPanel herramientas = new JPanel(new BorderLayout(0, 12));
        herramientas.setOpaque(false);
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        buscar.setToolTipText("Escribí para buscar por título, descripción o materia");
        buscar.putClientProperty("JTextField.placeholderText", "Buscar tareas...");
        buscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { aplicarFiltros(); }
        });
        JPanel cajaBusqueda = new JPanel(new BorderLayout(8, 0));
        cajaBusqueda.setOpaque(false);
        JLabel buscarEtiqueta = new JLabel("Buscar tarea:");
        buscarEtiqueta.setForeground(TEXTO);
        cajaBusqueda.add(buscarEtiqueta, BorderLayout.WEST);
        cajaBusqueda.add(buscar, BorderLayout.CENTER);
        fila.add(cajaBusqueda, BorderLayout.CENTER);
        JButton limpiar = botonAccion("Limpiar filtros", SECUNDARIO, AZUL);
        limpiar.addActionListener(e -> limpiarFiltros());
        fila.add(limpiar, BorderLayout.EAST);
        herramientas.add(fila, BorderLayout.NORTH);
        JPanel combos = new JPanel(new GridLayout(1, 4, 10, 0));
        combos.setOpaque(false);
        combos.add(filtroMateria); combos.add(filtroEstado); combos.add(filtroPrioridad); combos.add(orden);
        herramientas.add(combos, BorderLayout.SOUTH);
        bloque.add(herramientas, BorderLayout.NORTH);

        tabla.setRowHeight(37);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(236, 239, 245));
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(239, 243, 250));
        tabla.getTableHeader().setForeground(TEXTO);
        tabla.setAutoCreateRowSorter(false);
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 8));
                if (!selected) setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 253));
                if (column == 4 && "Pendiente".equals(value)) setForeground(new Color(153, 98, 15));
                else if (column == 4 && "Completada".equals(value)) setForeground(new Color(35, 128, 82));
                else if (column == 3 && "Alta".equals(value)) setForeground(new Color(173, 54, 54));
                else setForeground(TEXTO);
                return this;
            }
        });
        tabla.setToolTipText("Seleccioná una fila para editar, cambiar estado o eliminar la tarea.");
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(232, 236, 244)));
        bloque.add(scroll, BorderLayout.CENTER);
        JPanel pie = new JPanel(new BorderLayout(10, 0));
        pie.setOpaque(false);
        resumen.setForeground(GRIS);
        pie.add(resumen, BorderLayout.WEST);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton editar = botonAccion("Editar", SECUNDARIO, AZUL);
        JButton estado = botonAccion("Cambiar estado", SECUNDARIO, AZUL);
        JButton eliminar = botonAccion("Eliminar", new Color(251, 218, 218), Color.BLACK);
        editar.addActionListener(e -> modificarTarea());
        estado.addActionListener(e -> cambiarEstado());
        eliminar.addActionListener(e -> eliminarTarea());
        acciones.add(editar); acciones.add(estado); acciones.add(eliminar);
        pie.add(acciones, BorderLayout.EAST);
        bloque.add(pie, BorderLayout.SOUTH);
        centro.add(bloque, BorderLayout.CENTER);
        contenido.add(centro, BorderLayout.CENTER);
        filtroMateria.addActionListener(e -> aplicarFiltros());
        filtroEstado.addActionListener(e -> aplicarFiltros());
        filtroPrioridad.addActionListener(e -> aplicarFiltros());
        orden.addActionListener(e -> aplicarFiltros());
        return contenido;
    }

    private JPanel tarjeta(String texto, JLabel valor) {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1, 0, 5));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(new EmptyBorder(17, 20, 17, 20));
        JLabel nombre = new JLabel(texto);
        nombre.setForeground(GRIS);
        nombre.setFont(new Font("SansSerif", Font.PLAIN, 13));
        valor.setForeground(AZUL);
        valor.setFont(new Font("SansSerif", Font.BOLD, 30));
        tarjeta.add(nombre); tarjeta.add(valor);
        return tarjeta;
    }

    private JButton botonAccion(String texto, Color fondo, Color frente) {
        JButton b = new JButton(texto);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));

        Color hover = fondo.equals(new Color(251, 218, 218)) ? new Color(244, 191, 191)
                : fondo.equals(new Color(204, 220, 250)) ? new Color(179, 202, 246)
                : SECUNDARIO_HOVER;

        Color borde = fondo.equals(new Color(251, 218, 218)) ? new Color(201, 137, 137)
                : new Color(160, 178, 214);

        configurarBoton(b, fondo, frente, hover,
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borde),
                        new EmptyBorder(11, 15, 11, 15)));
        return b;
    }

    private void configurarBoton(JButton boton, Color fondo, Color frente,
            Color hover, javax.swing.border.Border borde) {
        boton.setBackground(fondo);
        boton.setForeground(frente);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorder(borde);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setContentAreaFilled(true);
        boton.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                boton.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                boton.setBackground(fondo);
            }
        });
    }

    private void mensaje(String texto) { JOptionPane.showMessageDialog(this, texto); }

    private void mostrarBienvenida() {
        JOptionPane.showMessageDialog(this,
                "¡Bienvenido a ACADEMIA 2.0!\n\n"
                + "1. Primero hacé clic en 'Agregar materia'.\n"
                + "2. Después elegí 'Nueva tarea' y completá los datos.\n"
                + "3. Para cambiar una tarea, seleccionala en la tabla.\n\n"
                + "Las tareas se guardan automáticamente en SQL Server.\n"
                + "Podés volver a ver esta guía en '¿Cómo usar?'.",
                "¡Comencemos!", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAyuda() {
        JTextArea ayuda = new JTextArea(
                "GUÍA RÁPIDA DE ACADEMIA 2.0\n\n"
                + "1. AGREGAR MATERIA: registrá la materia que estás cursando.\n\n"
                + "2. NUEVA TAREA: escribí el título, la descripción, elegí una\n"
                + "   materia, la prioridad y una fecha desde el calendario.\n\n"
                + "3. EDITAR: seleccioná una tarea de la tabla y presioná Editar.\n\n"
                + "4. CAMBIAR ESTADO: marcá la tarea como Pendiente o Completada.\n\n"
                + "5. ELIMINAR: seleccioná una tarea y confirmá su eliminación.\n\n"
                + "6. BUSCAR Y FILTRAR: escribí un texto, o elegí materia, estado,\n"
                + "   prioridad y orden para encontrar tareas fácilmente.\n\n"
                + "7. AVISOS: arriba de la tabla podés ver cuántas tareas están\n"
                + "   vencidas, vencen hoy o vencen dentro de los próximos 3 días.\n\n"
                + "8. GUARDADO: cada cambio se guarda automáticamente en SQL Server.\n\n"
                + "Consejo: si no ves una tarea, presioná 'Limpiar filtros'.");
        ayuda.setEditable(false);
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ayuda.setBackground(new Color(247, 249, 253));
        ayuda.setBorder(new EmptyBorder(10, 10, 10, 10));
        JOptionPane.showMessageDialog(this, new JScrollPane(ayuda),
                "¿Cómo usar ACADEMIA 2.0?", JOptionPane.INFORMATION_MESSAGE);
    }

    private void confirmarCambio(String confirmacion) {
        refrescar();
        if (!guardadoHabilitado) {
            JOptionPane.showMessageDialog(this,
                    "El cambio se hizo en esta sesión, pero el guardado está desactivado "
                    + "porque no se pudieron recuperar los datos anteriores desde SQL Server.",
                    "Atención: cambio no guardado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            RepositorioSQLServer.guardar(organizador);
            mensaje(confirmacion);
        } catch (SQLException error) {
            JOptionPane.showMessageDialog(this,
                    "El cambio se aplicó en esta sesión, pero NO se pudo guardar en SQL Server.\n"
                    + "Detalle: " + error.getMessage(),
                    "Atención: cambio no guardado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescar() {
        List<Materia> materias = organizador.consultarMaterias();
        Object seleccion = filtroMateria.getSelectedItem();
        Integer idSeleccionado = seleccion instanceof Materia ? ((Materia) seleccion).getId() : null;
        filtroMateria.removeAllItems();
        filtroMateria.addItem("Todas las materias");
        for (Materia m : materias) filtroMateria.addItem(m);
        if (idSeleccionado != null) for (int i = 1; i < filtroMateria.getItemCount(); i++) {
            Materia m = (Materia) filtroMateria.getItemAt(i);
            if (m.getId() == idSeleccionado) filtroMateria.setSelectedIndex(i);
        }
        List<Tarea> tareas = organizador.consultarTareas();
        totalMaterias.setText(String.valueOf(materias.size()));
        totalTareas.setText(String.valueOf(tareas.size()));
        pendientes.setText(String.valueOf(tareas.stream().filter(t -> t.getEstado() == EstadoTarea.PENDIENTE).count()));
        completadas.setText(String.valueOf(tareas.stream().filter(t -> t.getEstado() == EstadoTarea.COMPLETADA).count()));
        aplicarFiltros();
        actualizarAvisos();
    }

    private void actualizarAvisos() {
        LocalDate hoy = LocalDate.now();
        long vencidas = 0;
        long hoyMismo = 0;
        long proximas = 0;
        for (Tarea t : organizador.consultarTareas()) {
            if (t.getEstado() == EstadoTarea.COMPLETADA) continue;
            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, t.getFechaEntrega());
            if (dias < 0) vencidas++;
            else if (dias == 0) hoyMismo++;
            else if (dias <= 3) proximas++;
        }
        if (organizador.consultarMaterias().isEmpty()) {
            avisoEntregas.setText("Empezá por 'Agregar materia' y después creá tu primera tarea.");
        } else if (vencidas + hoyMismo + proximas == 0) {
            avisoEntregas.setText("Todo al día: no tenés tareas pendientes que venzan en los próximos 3 días.");
        } else {
            avisoEntregas.setText("Avisos: " + vencidas + " vencida(s)  |  " + hoyMismo
                    + " para hoy  |  " + proximas + " en los próximos 3 días");
        }
        Color fondoAviso = vencidas + hoyMismo > 0
                ? new Color(255, 237, 226) : new Color(233, 240, 255);
        avisoEntregas.setBackground(fondoAviso);
        panelAvisos.setBackground(fondoAviso);
    }

    private void mostrarAvisos() {
        List<Tarea> pendientes = organizador.consultarTareas().stream()
                .filter(t -> t.getEstado() == EstadoTarea.PENDIENTE)
                .sorted(java.util.Comparator.comparing(Tarea::getFechaEntrega))
                .toList();
        if (pendientes.isEmpty()) {
            mensaje("No tenés tareas pendientes. ¡Muy bien!");
            return;
        }
        StringBuilder detalle = new StringBuilder("PRÓXIMAS ENTREGAS Y TAREAS PENDIENTES\n\n");
        LocalDate hoy = LocalDate.now();
        for (Tarea t : pendientes) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(hoy, t.getFechaEntrega());
            String situacion = dias < 0 ? "Vencida hace " + Math.abs(dias) + " día(s)"
                    : dias == 0 ? "Vence hoy"
                    : dias == 1 ? "Vence mañana"
                    : "Faltan " + dias + " días";
            detalle.append("• ").append(t.getTitulo()).append(" — ").append(t.getMateria().getNombre())
                    .append("\n   ").append(t.getFechaEntrega().format(FECHA_CORTA))
                    .append("  |  ").append(situacion).append("\n\n");
        }
        JTextArea area = new JTextArea(detalle.toString(), 19, 53);
        area.setEditable(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 13));
        area.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
                "Avisos de entregas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void limpiarFiltros() {
        buscar.setText(""); filtroMateria.setSelectedIndex(0);
        filtroEstado.setSelectedIndex(0); filtroPrioridad.setSelectedIndex(0);
        orden.setSelectedIndex(0); aplicarFiltros();
    }

    private void aplicarFiltros() {
        if (modelo == null) return;
        Materia materia = filtroMateria.getSelectedItem() instanceof Materia ? (Materia) filtroMateria.getSelectedItem() : null;
        EstadoTarea estado = filtroEstado.getSelectedIndex() == 1 ? EstadoTarea.PENDIENTE :
                filtroEstado.getSelectedIndex() == 2 ? EstadoTarea.COMPLETADA : null;
        Prioridad prioridad = switch (filtroPrioridad.getSelectedIndex()) {
            case 1 -> Prioridad.ALTA; case 2 -> Prioridad.MEDIA; case 3 -> Prioridad.BAJA; default -> null;
        };
        List<Tarea> resultado = new ArrayList<>(organizador.consultarConFiltros(estado, materia, prioridad, false));
        if (orden.getSelectedIndex() == 1) resultado = OperacionesFuncionales.ordenarPorFecha(resultado);
        if (orden.getSelectedIndex() == 2) resultado = OperacionesFuncionales.ordenarPorPrioridad(resultado);
        String termino = buscar.getText().trim().toLowerCase();
        if (!termino.isEmpty()) resultado = resultado.stream().filter(t ->
                t.getTitulo().toLowerCase().contains(termino) ||
                t.getDescripcion().toLowerCase().contains(termino) ||
                t.getMateria().getNombre().toLowerCase().contains(termino)).toList();
        modelo.actualizar(resultado);
        resumen.setText("Mostrando " + resultado.size() + " de " + organizador.consultarTareas().size() + " tareas");
    }

    private void registrarMateria() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre de la nueva materia:", "Registrar materia", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null) return;
        if (!organizador.registrarMateria(nombre)) mensaje("La materia ya existe o el nombre es inválido.");
        else confirmarCambio("Materia registrada correctamente.");
    }

    private void mostrarMaterias() {
        List<Materia> materias = organizador.consultarMaterias();
        if (materias.isEmpty()) { mensaje("Todavía no hay materias registradas."); return; }
        StringBuilder texto = new StringBuilder("MATERIAS REGISTRADAS\n\n");
        for (Materia m : materias) texto.append(m).append("  (Tareas: ").append(m.getTareas().size()).append(")\n");
        JTextArea area = new JTextArea(texto.toString(), 14, 33);
        area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Mis materias", JOptionPane.INFORMATION_MESSAGE);
    }

    private Tarea tareaSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { mensaje("Seleccioná una tarea de la tabla primero."); return null; }
        return modelo.tareaEn(fila);
    }

    private void registrarTarea() {
        if (organizador.consultarMaterias().isEmpty()) { mensaje("Primero tenés que registrar una materia."); return; }
        FormularioTarea formulario = new FormularioTarea(null);
        if (!formulario.mostrar("Registrar nueva tarea")) return;
        boolean ok = organizador.registrarTarea(formulario.titulo(), formulario.descripcion(),
                formulario.materia(), formulario.prioridad(), formulario.fecha());
        if (ok) confirmarCambio("Tarea registrada correctamente.");
        else mensaje("No fue posible registrar la tarea.");
    }

    private void modificarTarea() {
        Tarea tarea = tareaSeleccionada();
        if (tarea == null) return;
        FormularioTarea formulario = new FormularioTarea(tarea);
        if (!formulario.mostrar("Modificar tarea")) return;
        boolean ok = organizador.modificarTarea(tarea.getId(), formulario.titulo(), formulario.descripcion(),
                formulario.materia(), formulario.prioridad(), formulario.fecha());
        if (ok) confirmarCambio("Tarea modificada correctamente.");
        else mensaje("No fue posible modificar la tarea.");
    }

    private void eliminarTarea() {
        Tarea tarea = tareaSeleccionada();
        if (tarea == null) return;
        int confirmar = JOptionPane.showConfirmDialog(this, "¿Eliminar la tarea '" + tarea.getTitulo() + "'?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirmar == JOptionPane.YES_OPTION && organizador.eliminarTarea(tarea.getId())) {
            confirmarCambio("Tarea eliminada correctamente.");
        }
    }

    private void cambiarEstado() {
        Tarea tarea = tareaSeleccionada();
        if (tarea == null) return;
        EstadoTarea estado = (EstadoTarea) JOptionPane.showInputDialog(this, "Nuevo estado:", "Cambiar estado",
                JOptionPane.QUESTION_MESSAGE, null, EstadoTarea.values(), tarea.getEstado());
        if (estado != null && organizador.cambiarEstado(tarea.getId(), estado)) {
            confirmarCambio("Estado actualizado correctamente.");
        }
    }

    private final class FormularioTarea {
        private final JTextField titulo = new JTextField(24);
        private final JTextArea descripcion = new JTextArea(3, 24);
        private final JComboBox<Materia> materia = new JComboBox<>();
        private final JComboBox<Prioridad> prioridad = new JComboBox<>(Prioridad.values());
        private final JTextField fecha = new JTextField(11);
        private LocalDate fechaValidada;
        private final JButton botonCalendario = new JButton("Elegir fecha...");

        FormularioTarea(Tarea tarea) {
            for (Materia m : organizador.consultarMaterias()) materia.addItem(m);
            fechaValidada = LocalDate.now().plusDays(7);
            fecha.setEditable(false);
            fecha.setBackground(Color.WHITE);
            fecha.setFont(new Font("SansSerif", Font.BOLD, 13));
            botonCalendario.setForeground(Color.BLACK);
            botonCalendario.setToolTipText("Abrir un calendario para elegir el día de entrega");
            botonCalendario.addActionListener(e -> {
                LocalDate elegida = SelectorFecha.seleccionar(ProyectoOrganizadorTareas.this, fechaValidada);
                if (elegida != null) {
                    fechaValidada = elegida;
                    fecha.setText(FECHA_CORTA.format(elegida));
                }
            });
            descripcion.setLineWrap(true); descripcion.setWrapStyleWord(true);
            if (tarea != null) {
                titulo.setText(tarea.getTitulo()); descripcion.setText(tarea.getDescripcion());
                materia.setSelectedItem(tarea.getMateria()); prioridad.setSelectedItem(tarea.getPrioridad());
                fechaValidada = tarea.getFechaEntrega();
            }
            fecha.setText(FECHA_CORTA.format(fechaValidada));
        }

        boolean mostrar(String nombre) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(new EmptyBorder(10, 8, 10, 8));
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(6, 6, 6, 6); g.fill = GridBagConstraints.HORIZONTAL;
            agregarCampo(panel, g, 0, "Título *", titulo);
            agregarCampo(panel, g, 1, "Descripción", new JScrollPane(descripcion));
            agregarCampo(panel, g, 2, "Materia *", materia);
            agregarCampo(panel, g, 3, "Prioridad *", prioridad);
            JPanel selector = new JPanel(new BorderLayout(7, 0));
            selector.add(fecha, BorderLayout.CENTER);
            selector.add(botonCalendario, BorderLayout.EAST);
            agregarCampo(panel, g, 4, "Fecha de entrega *", selector);
            while (true) {
                int opcion = JOptionPane.showConfirmDialog(ProyectoOrganizadorTareas.this, panel, nombre,
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (opcion != JOptionPane.OK_OPTION) return false;
                if (titulo.getText().isBlank()) { mensaje("El título de la tarea es obligatorio."); continue; }
                if (materia.getSelectedItem() == null) { mensaje("Seleccioná una materia."); continue; }
                if (fechaValidada == null) { mensaje("Seleccioná una fecha en el calendario."); continue; }
                return true;
            }
        }

        private void agregarCampo(JPanel panel, GridBagConstraints g, int y, String etiqueta, Component campo) {
            g.gridx = 0; g.gridy = y; g.weightx = 0;
            panel.add(new JLabel(etiqueta), g);
            g.gridx = 1; g.weightx = 1;
            panel.add(campo, g);
        }
        String titulo() { return titulo.getText().trim(); }
        String descripcion() { return descripcion.getText().trim(); }
        Materia materia() { return (Materia) materia.getSelectedItem(); }
        Prioridad prioridad() { return (Prioridad) prioridad.getSelectedItem(); }
        LocalDate fecha() { return fechaValidada; }
    }

    private static final class ModeloTareas extends AbstractTableModel {
        private static final String[] COLUMNAS = {"Título", "Materia", "Entrega", "Prioridad", "Estado"};
        private List<Tarea> filas = new ArrayList<>();

        void actualizar(List<Tarea> nuevasFilas) {
            this.filas = nuevasFilas == null ? new ArrayList<>() : new ArrayList<>(nuevasFilas);
            fireTableDataChanged();
        }

        Tarea tareaEn(int fila) {
            if (fila < 0 || fila >= filas.size()) return null;
            return filas.get(fila);
        }

        @Override public int getRowCount() { return filas.size(); }
        @Override public int getColumnCount() { return COLUMNAS.length; }
        @Override public String getColumnName(int column) { return COLUMNAS[column]; }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            Tarea t = filas.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> t.getTitulo();
                case 1 -> t.getMateria().getNombre();
                case 2 -> t.getFechaEntrega().format(FECHA_CORTA);
                case 3 -> t.getPrioridad().toString();
                case 4 -> t.getEstado().toString();
                default -> "";
            };
        }
    }
}