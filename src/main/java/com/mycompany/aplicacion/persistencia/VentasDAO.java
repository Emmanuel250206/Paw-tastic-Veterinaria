package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.VentaDTO;
import com.mycompany.aplicacion.modelo.UserSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentasDAO {

    public static boolean registrarVenta(double total, int idMascota, String concepto) {
        String sql = "INSERT INTO tb_venta (id_usuario_web, total, fecha, fecha_reg, id_State, nota, metodo_pago, tipo_atencion) " +
                     "VALUES (?, ?, NOW(), NOW(), 1, ?, 'E', 'sin_cita')";
        
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getUserId());
            ps.setDouble(2, total);
            ps.setString(3, concepto);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al registrar venta: " + e.getMessage());
            return false;
        }
    }

    public static List<VentaDTO> obtenerVentasRecientes() {
        List<VentaDTO> lista = new ArrayList<>();
        String sql = "SELECT v.total, v.fecha, m.nombre as mascota_nombre " +
                     "FROM tb_venta v " +
                     "LEFT JOIN tb_citas c ON v.id_cita = c.id " +
                     "LEFT JOIN tb_mascotas m ON c.id_mascota = m.id " +
                     "ORDER BY v.fecha DESC LIMIT 50";

        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String fecha = rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString() : "—";
                String mascota = rs.getString("mascota_nombre");
                if (mascota == null) mascota = "N/A";
                double monto = rs.getDouble("total");
                lista.add(new VentaDTO(fecha, mascota, "Venta POS", monto));
            }
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al obtener ventas: " + e.getMessage());
        }
        return lista;
    }
}
