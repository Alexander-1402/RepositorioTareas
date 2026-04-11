package com.db;

import com.model.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {

    // ── Crear tarea ───────────────────────────
    public static Tarea crear(String titulo, Tarea.Dificultad dificultad, int cursoId) {
        String sql = "INSERT INTO tareas (titulo, dificultad, curso_id) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, titulo);
            ps.setString(2, dificultad.name());
            ps.setInt(3, cursoId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Tarea t = new Tarea(titulo, null);
                t.setDificultad(dificultad);
                t.setId(rs.getInt(1));
                t.setCursoId(cursoId);
                return t;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    // ── Listar tareas de un curso ─────────────
    public static List<Tarea> listarPorCurso(int cursoId) {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas WHERE curso_id = ? ORDER BY FIELD(dificultad,'FACIL','MEDIO','DIFICIL')";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tarea t = new Tarea(rs.getString("titulo"), null);
                t.setId(rs.getInt("id"));
                t.setCursoId(cursoId);
                t.setDificultad(Tarea.Dificultad.valueOf(rs.getString("dificultad")));
                tareas.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tareas;
    }

    // ── Listar todas las tareas ───────────────
    public static List<Tarea> listarTodas() {
        List<Tarea> tareas = new ArrayList<>();
        String sql = "SELECT * FROM tareas ORDER BY FIELD(dificultad,'FACIL','MEDIO','DIFICIL')";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Tarea t = new Tarea(rs.getString("titulo"), null);
                t.setId(rs.getInt("id"));
                t.setCursoId(rs.getInt("curso_id"));
                t.setDificultad(Tarea.Dificultad.valueOf(rs.getString("dificultad")));
                tareas.add(t);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tareas;
    }

    // ── Buscar tarea por id ───────────────────
    public static Tarea buscarPorId(int id) {
        String sql = "SELECT * FROM tareas WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Tarea t = new Tarea(rs.getString("titulo"), null);
                t.setId(rs.getInt("id"));
                t.setCursoId(rs.getInt("curso_id"));
                t.setDificultad(Tarea.Dificultad.valueOf(rs.getString("dificultad")));
                return t;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
