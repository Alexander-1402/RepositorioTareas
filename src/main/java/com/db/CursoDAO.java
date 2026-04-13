package com.db;

import com.model.Alumno;
import com.model.Curso;
import com.model.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CursoDAO {

    // ── Crear curso ───────────────────────────
    public static Curso crear(String nombre, String codigo) {
        String sql = "INSERT INTO cursos (nombre, codigo) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, codigo);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Curso c = new Curso(nombre, null, codigo);
                c.setId(rs.getInt(1));
                return c;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Listar cursos simple (sin JOIN) ───────
    public static List<Curso> listar() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM cursos";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Curso c = new Curso(rs.getString("nombre"), null, rs.getString("codigo"));
                c.setId(rs.getInt("id"));
                cursos.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cursos;
    }

    // ── Listar cursos con tareas y alumnos (JOIN) ──
    // Solo 2 consultas totales sin importar cuántos cursos haya
    public static List<Curso> listarConTareasYAlumnos() {

        // Mapa para no duplicar cursos — clave es el id del curso
        Map<Integer, Curso> mapaC = new LinkedHashMap<>();

        // CONSULTA 1 — cursos + tareas con LEFT JOIN
        String sqlTareas = """
            SELECT c.id, c.nombre, c.codigo,
                   t.id as tarea_id, t.titulo, t.dificultad
            FROM cursos c
            LEFT JOIN tareas t ON t.curso_id = c.id
            ORDER BY c.id, t.id
        """;

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sqlTareas);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cursoId = rs.getInt("id");

                // Si el curso no está en el mapa lo agrega
                if (!mapaC.containsKey(cursoId)) {
                    Curso c = new Curso(rs.getString("nombre"), null, rs.getString("codigo"));
                    c.setId(cursoId);
                    mapaC.put(cursoId, c);
                }

                // Si tiene tarea la agrega al curso
                int tareaId = rs.getInt("tarea_id");
                if (tareaId != 0) {
                    Tarea t = new Tarea(rs.getString("titulo"), null);
                    t.setId(tareaId);
                    t.setCursoId(cursoId);
                    t.setDificultad(Tarea.Dificultad.valueOf(rs.getString("dificultad")));
                    mapaC.get(cursoId).agregarTarea(t);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // CONSULTA 2 — cursos + alumnos con JOIN
        String sqlAlumnos = """
            SELECT ac.curso_id, a.id, a.nombre, a.username
            FROM alumno_curso ac
            JOIN alumnos a ON a.id = ac.alumno_id
            ORDER BY ac.curso_id
        """;

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sqlAlumnos);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int cursoId = rs.getInt("curso_id");
                if (mapaC.containsKey(cursoId)) {
                    Alumno a = new Alumno(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("username")
                    );
                    mapaC.get(cursoId).agregarAlumno(a);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        return new ArrayList<>(mapaC.values());
    }

    // ── Buscar curso por código ───────────────
    public static Curso buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM cursos WHERE codigo = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Curso c = new Curso(rs.getString("nombre"), null, rs.getString("codigo"));
                c.setId(rs.getInt("id"));
                return c;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Inscribir alumno en curso ─────────────
    public static void inscribirAlumno(int alumnoId, int cursoId) {
        String sql = "INSERT IGNORE INTO alumno_curso (alumno_id, curso_id) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, cursoId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Obtener cursos de un alumno ───────────
    public static List<Curso> obtenerCursosDeAlumno(int alumnoId) {
        List<Curso> cursos = new ArrayList<>();
        String sql = """
            SELECT c.* FROM cursos c
            JOIN alumno_curso ac ON c.id = ac.curso_id
            WHERE ac.alumno_id = ?
        """;
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Curso c = new Curso(rs.getString("nombre"), null, rs.getString("codigo"));
                c.setId(rs.getInt("id"));
                cursos.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return cursos;
    }

    // ── Expulsar alumno de curso ──────────────
    public static void expulsarAlumno(int alumnoId, int cursoId) {
        String sql = "DELETE FROM alumno_curso WHERE alumno_id = ? AND curso_id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, cursoId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Curso> listarConDetalles() {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT * FROM cursos";

        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Curso c = new Curso(rs.getInt("id"), rs.getString("nombre"), rs.getString("codigo"));
                c.setTareas(TareaDAO.listarPorCurso(c.getId()));

                // Cargamos los recursos (enlaces) usando tu RecursoDAO
                c.setRecursos(RecursoDAO.obtenerPorCurso(c.getId()));

                cursos.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cursos;
    }

    // ── Obtener alumnos de un curso ───────────
    public static List<Alumno> obtenerAlumnosDeCurso(int cursoId) {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = """
            SELECT a.* FROM alumnos a
            JOIN alumno_curso ac ON a.id = ac.alumno_id
            WHERE ac.curso_id = ?
        """;
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Alumno a = new Alumno(rs.getInt("id"), rs.getString("nombre"), rs.getString("username"));
                alumnos.add(a);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return alumnos;
    }
}