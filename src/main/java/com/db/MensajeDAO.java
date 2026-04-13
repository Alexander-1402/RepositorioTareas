package com.db;

import com.model.Mensaje;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {

    public static Mensaje enviar(int alumnoId, int cursoId, String contenido) {
        String sql = "INSERT INTO mensajes (alumno_id, curso_id, contenido) VALUES (?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alumnoId);
            ps.setInt(2, cursoId);
            ps.setString(3, contenido);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new Mensaje(rs.getInt(1), alumnoId, cursoId, contenido, LocalDateTime.now(), false);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static List<Mensaje> listarPorCurso(int cursoId) {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT * FROM mensajes WHERE curso_id = ? ORDER BY fecha DESC";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Mensaje(
                        rs.getInt("id"),
                        rs.getInt("alumno_id"),
                        rs.getInt("curso_id"),
                        rs.getString("contenido"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getBoolean("leido")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public static void marcarLeido(int mensajeId) {
        String sql = "UPDATE mensajes SET leido = TRUE WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mensajeId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
