package com.db;

import com.model.Recurso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecursoDAO {

    public static void guardar(Recurso recurso) {
        String sql = "INSERT INTO recursos (titulo, tipo, enlace, curso_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recurso.getTitulo());
            ps.setString(2, recurso.getTipo().name());
            ps.setString(3, recurso.getEnlace());
            ps.setInt(4, recurso.getCursoId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Recurso> obtenerPorCurso(int cursoId) {
        List<Recurso> recursos = new ArrayList<>();
        String sql = "SELECT * FROM recursos WHERE curso_id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cursoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recurso.TipoRecurso tipo = Recurso.TipoRecurso.valueOf(rs.getString("tipo"));
                Recurso r = new Recurso(
                        rs.getString("titulo"),
                        tipo,
                        rs.getString("enlace"),
                        cursoId
                );

                r.setId(rs.getInt("id"));
                recursos.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recursos;
    }
}
