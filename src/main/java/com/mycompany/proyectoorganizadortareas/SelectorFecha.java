package com.mycompany.proyectoorganizadortareas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/** Calendario sencillo hecho en Swing, sin librerías adicionales. */
public final class SelectorFecha extends JDialog {

    private static final Color AZUL = new Color(33, 53, 91);
    private static final Locale ESPANOL = Locale.forLanguageTag("es-CR");
    private final JLabel mesTitulo = new JLabel("", SwingConstants.CENTER);
    private final JPanel celdas = new JPanel(new GridLayout(7, 7, 3, 3));
    private final LocalDate fechaInicial;
    private YearMonth mesMostrado;
    private LocalDate seleccion;

    private SelectorFecha(Window propietario, LocalDate inicial) {
        super(propietario, "Seleccionar fecha de entrega", ModalityType.APPLICATION_MODAL);
        fechaInicial = inicial == null ? LocalDate.now() : inicial;
        mesMostrado = YearMonth.from(fechaInicial);
        setSize(400, 390);
        setResizable(false);
        setLocationRelativeTo(propietario);
        setLayout(new BorderLayout(8, 8));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBorder(new EmptyBorder(14, 14, 8, 14));
        JButton anterior = new JButton("<");
        JButton siguiente = new JButton(">");
        anterior.setForeground(Color.BLACK);
        siguiente.setForeground(Color.BLACK);
        mesTitulo.setFont(new Font("SansSerif", Font.BOLD, 17));
        encabezado.add(anterior, BorderLayout.WEST);
        encabezado.add(mesTitulo, BorderLayout.CENTER);
        encabezado.add(siguiente, BorderLayout.EAST);
        anterior.addActionListener(e -> { mesMostrado = mesMostrado.minusMonths(1); mostrarMes(); });
        siguiente.addActionListener(e -> { mesMostrado = mesMostrado.plusMonths(1); mostrarMes(); });
        add(encabezado, BorderLayout.NORTH);

        celdas.setBorder(new EmptyBorder(2, 13, 2, 13));
        add(celdas, BorderLayout.CENTER);

        JPanel pie = new JPanel();
        JButton hoy = new JButton("Hoy");
        JButton cancelar = new JButton("Cancelar");
        hoy.setForeground(Color.BLACK);
        cancelar.setForeground(Color.BLACK);
        hoy.addActionListener(e -> { seleccion = LocalDate.now(); dispose(); });
        cancelar.addActionListener(e -> dispose());
        pie.add(hoy);
        pie.add(cancelar);
        pie.setBorder(new EmptyBorder(6, 0, 12, 0));
        add(pie, BorderLayout.SOUTH);
        mostrarMes();
    }

    private void mostrarMes() {
        mesTitulo.setText(capitalizar(mesMostrado.getMonth().getDisplayName(TextStyle.FULL, ESPANOL))
                + " " + mesMostrado.getYear());
        celdas.removeAll();
        String[] dias = {"L", "M", "M", "J", "V", "S", "D"};
        for (String dia : dias) {
            JLabel etiqueta = new JLabel(dia, SwingConstants.CENTER);
            etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD));
            etiqueta.setForeground(AZUL);
            celdas.add(etiqueta);
        }
        int espacios = mesMostrado.atDay(1).getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue();
        for (int indice = 0; indice < 42; indice++) {
            int dia = indice - espacios + 1;
            if (dia < 1 || dia > mesMostrado.lengthOfMonth()) {
                celdas.add(new JLabel());
                continue;
            }
            LocalDate fecha = mesMostrado.atDay(dia);
            JButton boton = new JButton(Integer.toString(dia));
            boton.setFocusPainted(false);
            boton.setForeground(Color.BLACK);
            if (fecha.equals(fechaInicial)) {
                boton.setBackground(new Color(196, 217, 253));
                boton.setBorder(BorderFactory.createLineBorder(new Color(67, 97, 238), 2));
            } else if (fecha.equals(LocalDate.now())) {
                boton.setBackground(new Color(227, 238, 252));
            } else {
                boton.setBackground(Color.WHITE);
            }
            boton.setOpaque(true);
            boton.addActionListener(e -> { seleccion = fecha; dispose(); });
            celdas.add(boton);
        }
        celdas.revalidate();
        celdas.repaint();
    }

    private String capitalizar(String texto) {
        return texto.substring(0, 1).toUpperCase(ESPANOL) + texto.substring(1);
    }

    public static LocalDate seleccionar(Component padre, LocalDate inicial) {
        Window ventana = SwingUtilities.getWindowAncestor(padre);
        SelectorFecha calendario = new SelectorFecha(ventana, inicial);
        calendario.setVisible(true);
        return calendario.seleccion;
    }
}
