package com.db;

import com.model.Alumno;

import java.sql.*;

public class AlumnoDAO {

    public static Alumno registrarORecuperar(String nombre, String username) {
      
        Alumno existente = buscarPorUsername(username);
        if (existente != null) return existente;

        String sql = "INSERT INTO alumnos (nombre, username) VALUES (?, ?)";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre);
            ps.setString(2, username);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return new Alumno(rs.getInt(1), nombre, username);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Alumno buscarPorUsername(String username) {
        String sql = "SELECT * FROM alumnos WHERE username = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Alumno(rs.getInt("id"), rs.getString("nombre"), rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Alumno buscarPorId(int id) {
        String sql = "SELECT * FROM alumnos WHERE id = ?";
        try (Connection conn = ConexionDB.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Alumno(rs.getInt("id"), rs.getString("nombre"), rs.getString("username"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
