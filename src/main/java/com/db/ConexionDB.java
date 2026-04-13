package com.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionDB {

    private static final String HOST     = "metro.proxy.rlwy.net";
    private static final String PORT     = "24515";
    private static final String DATABASE = "taskrepo";
    private static final String USER     = "root";
    private static final String PASSWORD = "ymicOrKQGDZagdxhJEHXCWSeeuCuyuSN";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    // Carga el driver al arrancar
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✓ Driver MySQL cargado");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver MySQL no encontrado: " + e.getMessage());
        }
    }

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void inicializarTablas() {
        String sqlCursos = """
            CREATE TABLE IF NOT EXISTS cursos (
                id       INT AUTO_INCREMENT PRIMARY KEY,
                nombre   VARCHAR(100) NOT NULL,
                codigo   VARCHAR(10)  NOT NULL UNIQUE
            );
        """;

        String sqlTareas = """
            CREATE TABLE IF NOT EXISTS tareas (
                id          INT AUTO_INCREMENT PRIMARY KEY,
                titulo      VARCHAR(200) NOT NULL,
                dificultad  ENUM('FACIL','MEDIO','DIFICIL') NOT NULL DEFAULT 'MEDIO',
                curso_id    INT NOT NULL,
                FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE
            );
        """;

        String sqlAlumnos = """
            CREATE TABLE IF NOT EXISTS alumnos (
                id       INT AUTO_INCREMENT PRIMARY KEY,
                nombre   VARCHAR(100) NOT NULL,
                username VARCHAR(100) NOT NULL UNIQUE
            );
        """;

        String sqlAlumnoCurso = """
            CREATE TABLE IF NOT EXISTS alumno_curso (
                alumno_id INT NOT NULL,
                curso_id  INT NOT NULL,
                PRIMARY KEY (alumno_id, curso_id),
                FOREIGN KEY (alumno_id) REFERENCES alumnos(id)  ON DELETE CASCADE,
                FOREIGN KEY (curso_id)  REFERENCES cursos(id)   ON DELETE CASCADE
            );
        """;

        String sqlEntregas = """
            CREATE TABLE IF NOT EXISTS entregas (
                id          INT AUTO_INCREMENT PRIMARY KEY,
                alumno_id   INT NOT NULL,
                tarea_id    INT NOT NULL,
                archivo     VARCHAR(300) NOT NULL,
                fecha       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (alumno_id) REFERENCES alumnos(id)  ON DELETE CASCADE,
                FOREIGN KEY (tarea_id)  REFERENCES tareas(id)   ON DELETE CASCADE
            );
        """;

        String sqlRecursos = """
            CREATE TABLE IF NOT EXISTS recursos (
                id          INT AUTO_INCREMENT PRIMARY KEY,
                titulo      VARCHAR(200) NOT NULL,
                tipo        ENUM('VIDEO', 'ARTICULO', 'REPOSITORIO', 'PDF') NOT NULL,
                enlace      VARCHAR(500) NOT NULL, -- Se vuelve obligatorio ya que no hay ruta física
                curso_id    INT NOT NULL,
                FOREIGN KEY (curso_id) REFERENCES cursos(id) ON DELETE CASCADE
                );
         """;

        String sqlMensajes = """
            CREATE TABLE IF NOT EXISTS mensajes (
                id         INT AUTO_INCREMENT PRIMARY KEY,
                alumno_id  INT NOT NULL,
                curso_id   INT NOT NULL,
                contenido  TEXT NOT NULL,
                fecha      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                leido      BOOLEAN DEFAULT FALSE,
                FOREIGN KEY (alumno_id) REFERENCES alumnos(id) ON DELETE CASCADE,
                FOREIGN KEY (curso_id)  REFERENCES cursos(id)  ON DELETE CASCADE
            );
        """;

        try (Connection conn = getConexion(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlCursos);
            stmt.execute(sqlTareas);
            stmt.execute(sqlAlumnos);
            stmt.execute(sqlAlumnoCurso);
            stmt.execute(sqlEntregas);
            stmt.execute(sqlRecursos);
            stmt.execute(sqlMensajes);
            System.out.println("✓ Tablas verificadas/creadas correctamente");
        } catch (SQLException e) {
            System.err.println("✗ Error al inicializar tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }
}