package com.mycompany.proyectoorganizadortareas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la conexión JDBC hacia SQL Server (OrganizadorTareasDB)
 * y el usuario de sesión activo para el futuro módulo de login.
 */
public final class ConexionBD {

    // Ajusta servidor, instancia o puerto según tu SQL Server local
    // Ejemplos: localhost:1433 o localhost\SQLEXPRESS
    private static final String SERVIDOR = "localhost:1433";
    private static final String BASE_DATOS = "OrganizadorTareasDB";

    // Credenciales de autenticación SQL Server
    private static final String USUARIO = "academia_app";
    private static final String CONTRASENA = "Academia2026#";

    // Desactivada para conectarse mediante la cuenta SQL creada
    private static final boolean USAR_AUTENTICACION_WINDOWS = false;

    // Usuario por defecto (admin -> UsuarioId = 1) hasta que se integre el login
    private static int usuarioIdActivo = 1;

    private ConexionBD() {
    }

    public static String construirUrl() {
        String baseUrl = "jdbc:sqlserver://" + SERVIDOR + ";"
                + "databaseName=" + BASE_DATOS + ";"
                + "encrypt=true;"
                + "trustServerCertificate=true;";

        if (USAR_AUTENTICACION_WINDOWS) {
            return baseUrl + "integratedSecurity=true;";
        }
        return baseUrl;
    }

    public static Connection obtenerConexion() throws SQLException {
        if (USAR_AUTENTICACION_WINDOWS) {
            return DriverManager.getConnection(construirUrl());
        }
        return DriverManager.getConnection(construirUrl(), USUARIO, CONTRASENA);
    }

    public static int getUsuarioIdActivo() {
        return usuarioIdActivo;
    }

    public static void setUsuarioIdActivo(int usuarioId) {
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor que cero.");
        }
        usuarioIdActivo = usuarioId;
    }
}