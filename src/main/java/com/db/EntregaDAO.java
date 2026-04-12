package com.db;

import com.model.Alumno;
import com.model.Entrega;
import com.model.Tarea;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntregaDAO {

 
    public static void registrar(int alumnoId, int tareaId, String rutaArchivo) {
        String sql = "INSERT INTO entregas (alumno_id, tarea_id, archivo) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, tareaId);
            ps.setString(3, rutaArchivo);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Entrega> obtenerPorTarea(int tareaId) {
        List<Entrega> entregas = new ArrayList<>();
        String sql = """
            SELECT e.*, a.nombre as alumno_nombre, a.username as alumno_username
            FROM entregas e
            JOIN alumnos a ON e.alumno_id = a.id
            WHERE e.tarea_id = ?
            ORDER BY e.fecha DESC
        """;
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tareaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("alumno_id"),
                        rs.getString("alumno_nombre"),
                        rs.getString("alumno_username")
                );
                Tarea tarea = TareaDAO.buscarPorId(tareaId);
                java.io.File archivo = new java.io.File(rs.getString("archivo"));
                Entrega e = new Entrega(alumno, tarea, archivo);
                entregas.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entregas;
    }

    public static List<Entrega> obtenerTodas() {
        List<Entrega> entregas = new ArrayList<>();
        String sql = """
            SELECT e.*, a.nombre as alumno_nombre, a.username as alumno_username
            FROM entregas e
            JOIN alumnos a ON e.alumno_id = a.id
            ORDER BY e.fecha DESC
        """;
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Alumno alumno = new Alumno(
                        rs.getInt("alumno_id"),
                        rs.getString("alumno_nombre"),
                        rs.getString("alumno_username")
                );
                Tarea tarea = TareaDAO.buscarPorId(rs.getInt("tarea_id"));
                java.io.File archivo = new java.io.File(rs.getString("archivo"));
                Entrega e = new Entrega(alumno, tarea, archivo);
                entregas.add(e);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return entregas;
    }
}
