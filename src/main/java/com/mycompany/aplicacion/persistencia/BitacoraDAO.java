package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Bitacora;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BitacoraDAO {

    public static List<Bitacora> listarPorClinica(int idClinica) {
        List<Bitacora> lista = new ArrayList<>();
        String sql = """
            SELECT b.id, b.fecha_hora, b.id_usuario_web, b.modulo, b.detalle,
                   u.nombre, u.apellidos, u.usuario
            FROM tb_bitacora b
            INNER JOIN tb_usuario_web u ON b.id_usuario_web = u.id
            WHERE u.id_clinica = ?
            ORDER BY b.fecha_hora DESC
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    LocalDateTime fechaHora = rs.getTimestamp("fecha_hora") != null ? 
                        rs.getTimestamp("fecha_hora").toLocalDateTime() : null;
                    int idUsuarioWeb = rs.getInt("id_usuario_web");
                    String modulo = rs.getString("modulo");
                    String detalle = rs.getString("detalle");
                    
                    String nombre = rs.getString("nombre");
                    String apellidos = rs.getString("apellidos");
                    String usuario = rs.getString("usuario");
                    
                    // Formatear nombre de usuario como "Nombre Apellidos (usuario)"
                    String usuarioNombre = nombre;
                    if (apellidos != null && !apellidos.isBlank()) {
                        usuarioNombre += " " + apellidos;
                    }
                    if (usuario != null && !usuario.isBlank()) {
                        usuarioNombre += " (" + usuario + ")";
                    }
                    
                    lista.add(new Bitacora(id, fechaHora, idUsuarioWeb, usuarioNombre, modulo, detalle));
                }
            }
        } catch (Exception e) {
            System.err.println("[BitacoraDAO] Error al listar bitacora: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
