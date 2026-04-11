package com.db;

import com.model.Alumno;
import com.model.Curso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                return new Curso(nombre, null, codigo);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Listar todos los cursos ───────────────
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
