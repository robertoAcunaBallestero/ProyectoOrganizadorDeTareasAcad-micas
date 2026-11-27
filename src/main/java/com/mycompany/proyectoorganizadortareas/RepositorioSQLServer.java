package com.mycompany.proyectoorganizadortareas;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Carga y guarda materias y tareas en SQL Server (OrganizadorTareasDB)
 * filtrando por el usuario activo.
 */
public final class RepositorioSQLServer {

    private RepositorioSQLServer() {
    }

    public static Organizador cargar() throws SQLException {
        return cargarPorUsuario(ConexionBD.getUsuarioIdActivo());
    }

    public static Organizador cargarPorUsuario(int usuarioId) throws SQLException {
        Organizador organizador = new Organizador();
        Map<Integer, Materia> materiasCargadas = new HashMap<>();

        String sqlMaterias =
                "SELECT MateriaId, Nombre "
                + "FROM dbo.Materias "
                + "WHERE UsuarioId = ? "
                + "ORDER BY MateriaId ASC";

        String sqlTareas =
                "SELECT t.TareaId, t.MateriaId, t.Titulo, t.Descripcion, "
                + "       t.FechaEntrega, t.Prioridad, t.Estado "
                + "FROM dbo.Tareas t "
                + "INNER JOIN dbo.Materias m ON m.MateriaId = t.MateriaId "
                + "WHERE m.UsuarioId = ? "
                + "ORDER BY t.TareaId ASC";

        try (Connection cn = ConexionBD.obtenerConexion()) {
            try (PreparedStatement psMaterias = cn.prepareStatement(sqlMaterias)) {
                psMaterias.setInt(1, usuarioId);
                try (ResultSet rs = psMaterias.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("MateriaId");
                        String nombre = rs.getString("Nombre");
                        Materia materia = organizador.restaurarMateria(id, nombre);
                        materiasCargadas.put(id, materia);
                    }
                }
            }

            try (PreparedStatement psTareas = cn.prepareStatement(sqlTareas)) {
                psTareas.setInt(1, usuarioId);
                try (ResultSet rs = psTareas.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("TareaId");
                        int materiaId = rs.getInt("MateriaId");
                        Materia materia = materiasCargadas.get(materiaId);
                        if (materia == null) {
                            continue;
                        }
                        String titulo = rs.getString("Titulo");
                        String descripcion = rs.getString("Descripcion");
                        LocalDate fechaEntrega = rs.getDate("FechaEntrega").toLocalDate();
                        Prioridad prioridad = Prioridad.valueOf(rs.getString("Prioridad").trim());
                        EstadoTarea estado = EstadoTarea.valueOf(rs.getString("Estado").trim());

                        organizador.restaurarTarea(
                                id, titulo, descripcion, materia, prioridad, fechaEntrega, estado);
                    }
                }
            }
        }

        return organizador;
    }

    public static void guardar(Organizador organizador) throws SQLException {
        guardarPorUsuario(organizador, ConexionBD.getUsuarioIdActivo());
    }

    public static void guardarPorUsuario(Organizador organizador, int usuarioId) throws SQLException {
        String sqlSelectMaterias =
                "SELECT MateriaId, Nombre FROM dbo.Materias WHERE UsuarioId = ?";
        String sqlInsertMateria =
                "INSERT INTO dbo.Materias (UsuarioId, Nombre) VALUES (?, ?)";
        String sqlUpdateMateria =
                "UPDATE dbo.Materias SET Nombre = ? WHERE MateriaId = ? AND UsuarioId = ?";
        String sqlDeleteMateria =
                "DELETE FROM dbo.Materias WHERE MateriaId = ? AND UsuarioId = ?";

        String sqlSelectTareas =
                "SELECT t.TareaId "
                + "FROM dbo.Tareas t "
                + "INNER JOIN dbo.Materias m ON m.MateriaId = t.MateriaId "
                + "WHERE m.UsuarioId = ?";
        String sqlInsertTarea =
                "INSERT INTO dbo.Tareas (MateriaId, Titulo, Descripcion, FechaEntrega, Prioridad, Estado) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        String sqlUpdateTarea =
                "UPDATE dbo.Tareas "
                + "SET MateriaId = ?, Titulo = ?, Descripcion = ?, FechaEntrega = ?, Prioridad = ?, Estado = ? "
                + "WHERE TareaId = ?";
        String sqlDeleteTarea =
                "DELETE FROM dbo.Tareas WHERE TareaId = ?";

        try (Connection cn = ConexionBD.obtenerConexion()) {
            cn.setAutoCommit(false);
            try {
                Map<Integer, String> materiasEnBD = new HashMap<>();
                try (PreparedStatement ps = cn.prepareStatement(sqlSelectMaterias)) {
                    ps.setInt(1, usuarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            materiasEnBD.put(rs.getInt("MateriaId"), rs.getString("Nombre"));
                        }
                    }
                }

                List<Materia> materiasMemoria = organizador.consultarMaterias();
                Map<Integer, Integer> mapaIdMateriaMemoriaA_BD = new HashMap<>();

                for (Materia m : materiasMemoria) {
                    if (materiasEnBD.containsKey(m.getId())) {
                        mapaIdMateriaMemoriaA_BD.put(m.getId(), m.getId());
                        if (!materiasEnBD.get(m.getId()).equals(m.getNombre())) {
                            try (PreparedStatement ps = cn.prepareStatement(sqlUpdateMateria)) {
                                ps.setString(1, m.getNombre());
                                ps.setInt(2, m.getId());
                                ps.setInt(3, usuarioId);
                                ps.executeUpdate();
                            }
                        }
                    } else {
                        try (PreparedStatement ps = cn.prepareStatement(
                                sqlInsertMateria, Statement.RETURN_GENERATED_KEYS)) {
                            ps.setInt(1, usuarioId);
                            ps.setString(2, m.getNombre());
                            ps.executeUpdate();
                            try (ResultSet keys = ps.getGeneratedKeys()) {
                                if (keys.next()) {
                                    mapaIdMateriaMemoriaA_BD.put(m.getId(), keys.getInt(1));
                                }
                            }
                        }
                    }
                }

                List<Integer> idsMemoria = materiasMemoria.stream().map(Materia::getId).toList();
                for (Integer idBD : materiasEnBD.keySet()) {
                    if (!idsMemoria.contains(idBD)) {
                        try (PreparedStatement ps = cn.prepareStatement(sqlDeleteMateria)) {
                            ps.setInt(1, idBD);
                            ps.setInt(2, usuarioId);
                            ps.executeUpdate();
                        }
                    }
                }

                List<Integer> tareasEnBD = new ArrayList<>();
                try (PreparedStatement ps = cn.prepareStatement(sqlSelectTareas)) {
                    ps.setInt(1, usuarioId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            tareasEnBD.add(rs.getInt("TareaId"));
                        }
                    }
                }

                List<Tarea> tareasMemoria = organizador.consultarTareas();
                List<Integer> idsTareasMemoria = new ArrayList<>();

                for (Tarea t : tareasMemoria) {
                    idsTareasMemoria.add(t.getId());
                    Integer materiaIdBD = mapaIdMateriaMemoriaA_BD.get(t.getMateria().getId());
                    if (materiaIdBD == null) {
                        materiaIdBD = t.getMateria().getId();
                    }

                    if (tareasEnBD.contains(t.getId())) {
                        try (PreparedStatement ps = cn.prepareStatement(sqlUpdateTarea)) {
                            ps.setInt(1, materiaIdBD);
                            ps.setString(2, t.getTitulo());
                            ps.setString(3, t.getDescripcion());
                            ps.setDate(4, Date.valueOf(t.getFechaEntrega()));
                            ps.setString(5, t.getPrioridad().name());
                            ps.setString(6, t.getEstado().name());
                            ps.setInt(7, t.getId());
                            ps.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement ps = cn.prepareStatement(sqlInsertTarea)) {
                            ps.setInt(1, materiaIdBD);
                            ps.setString(2, t.getTitulo());
                            ps.setString(3, t.getDescripcion());
                            ps.setDate(4, Date.valueOf(t.getFechaEntrega()));
                            ps.setString(5, t.getPrioridad().name());
                            ps.setString(6, t.getEstado().name());
                            ps.executeUpdate();
                        }
                    }
                }

                for (Integer tareaIdBD : tareasEnBD) {
                    if (!idsTareasMemoria.contains(tareaIdBD)) {
                        try (PreparedStatement ps = cn.prepareStatement(sqlDeleteTarea)) {
                            ps.setInt(1, tareaIdBD);
                            ps.executeUpdate();
                        }
                    }
                }

                cn.commit();
            } catch (SQLException ex) {
                cn.rollback();
                throw ex;
            } finally {
                cn.setAutoCommit(true);
            }
        }
    }
}