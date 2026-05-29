package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.VentaDTO;
import com.mycompany.aplicacion.modelo.UserSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DAO de Ventas — operaciones contra tb_venta y tb_detalle_venta.
 * CORRECCIONES APLICADAS:
 *  - registrarVenta() ahora usa el esquema real de tb_venta (sin columnas 'nota' ni 'tipo_atencion')
 *  - Añadida inserción de tb_detalle_venta por línea de carrito
 *  - Añadido descuento de stock en tb_producto tras venta
 *  - Ventas sin cita usan id_cita = NULL (requiere que la BD permita NULL; si no, se debe crear una cita genérica)
 */
public class VentasDAO {

    /**
     * Registra una venta completa con sus líneas de detalle y descuenta stock.
     *
     * @param idMascota    ID de mascota asociada (puede ser 0 si no aplica)
     * @param metodoPago   'E' = Efectivo, 'T' = Tarjeta
     * @param total        Total de la venta
     * @param carrito      Mapa de idProducto → cantidad
     * @param precios      Mapa de idProducto → precio unitario
     * @return true si la transacción fue exitosa
     */
    public static boolean registrarVentaCompleta(int idMascota, char metodoPago,
                                                  double total,
                                                  Map<Integer, Integer> carrito,
                                                  Map<Integer, Double> precios) {
        // Buscar id_State para 'Activa' tipo 'venta'
        int idStateActiva = buscarIdState("Activa", "venta");
        if (idStateActiva == -1) idStateActiva = 1; // fallback

        Connection con = new Conexion().estableceConexion();
        if (con == null) return false;

        try {
            con.setAutoCommit(false);

            // Buscar id_cita si idMascota > 0
            Integer idCita = null;
            if (idMascota > 0) {
                String sqlCita = "SELECT id FROM tb_citas WHERE id_mascota = ? ORDER BY fecha DESC LIMIT 1";
                try (PreparedStatement psCita = con.prepareStatement(sqlCita)) {
                    psCita.setInt(1, idMascota);
                    try (ResultSet rs = psCita.executeQuery()) {
                        if (rs.next()) {
                            idCita = rs.getInt("id");
                        }
                    }
                }
            }

            String sqlVenta = """
                INSERT INTO tb_venta
                    (id_cita, id_usuario_web, metodo_pago, total, fecha, fecha_reg, id_State)
                VALUES (?, ?, ?, ?, NOW(), NOW(), ?)
                """;

            // 1. Insertar encabezado de venta
            int idVenta = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                if (idCita != null) {
                    ps.setInt(1, idCita);
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setInt(2, UserSession.getInstance().getUserId());
                ps.setString(3, String.valueOf(metodoPago));
                ps.setDouble(4, total);
                ps.setInt(5, idStateActiva);
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) idVenta = rs.getInt(1);
            }

            if (idVenta == -1) { con.rollback(); return false; }

            // 2. Insertar líneas de detalle y descontar stock
            String sqlDetalle = """
                INSERT INTO tb_detalle_venta
                    (id_venta, id_producto, cantidad, precio_unitario, subtotal)
                VALUES (?, ?, ?, ?, ?)
                """;
            String sqlStock = "UPDATE tb_producto SET existencia = existencia - ? WHERE id = ? AND existencia >= ?";

            for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                int idProducto = entry.getKey();
                int cantidad   = entry.getValue();
                double precio  = precios.getOrDefault(idProducto, 0.0);
                double subtotal = precio * cantidad;

                // Insertar detalle
                try (PreparedStatement psDet = con.prepareStatement(sqlDetalle)) {
                    psDet.setInt(1, idVenta);
                    psDet.setInt(2, idProducto);
                    psDet.setInt(3, cantidad);
                    psDet.setDouble(4, precio);
                    psDet.setDouble(5, subtotal);
                    psDet.executeUpdate();
                }

                // Descontar stock
                try (PreparedStatement psStock = con.prepareStatement(sqlStock)) {
                    psStock.setInt(1, cantidad);
                    psStock.setInt(2, idProducto);
                    psStock.setInt(3, cantidad);
                    int rows = psStock.executeUpdate();
                    if (rows == 0) {
                        System.err.println("[VentasDAO] Stock insuficiente para producto " + idProducto);
                        con.rollback();
                        return false;
                    }
                }
            }

            con.commit();
            return true;

        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ignored) {}
            System.err.println("[VentasDAO] Error al registrar venta: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (Exception ignored) {}
        }
    }

    /**
     * Versión simplificada para mantener compatibilidad con código anterior.
     * Registra una venta de un solo concepto sin detalle de productos.
     */
    public static boolean registrarVenta(double total, int idMascota, String concepto) {
        int idStateActiva = buscarIdState("Activa", "venta");
        if (idStateActiva == -1) idStateActiva = 1;

        // id_cita = NULL para ventas directas
        String sql = """
            INSERT INTO tb_venta
                (id_cita, id_usuario_web, metodo_pago, total, fecha, fecha_reg, id_State)
            VALUES (NULL, ?, 'E', ?, NOW(), NOW(), ?)
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getUserId());
            ps.setDouble(2, total);
            ps.setInt(3, idStateActiva);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al registrar venta simple: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene las ventas recientes con su mascota asociada (vía cita).
     */
    public static List<VentaDTO> obtenerVentasRecientes() {
        List<VentaDTO> lista = new ArrayList<>();
        String sql = """
            SELECT v.total, v.fecha,
                   COALESCE(m.nombre, 'Venta directa') AS mascota_nombre,
                   s.nombre AS estado,
                   COALESCE(
                       (SELECT GROUP_CONCAT(CONCAT(dv.cantidad, 'x ', p.nombre) SEPARATOR ', ')
                        FROM tb_detalle_venta dv
                        JOIN tb_producto p ON dv.id_producto = p.id
                        WHERE dv.id_venta = v.id),
                       'Venta / Servicio'
                   ) AS concepto
            FROM tb_venta v
            LEFT JOIN tb_citas c ON v.id_cita = c.id
            LEFT JOIN tb_mascotas m ON c.id_mascota = m.id
            LEFT JOIN tb_state s ON v.id_State = s.id
            ORDER BY v.fecha DESC
            LIMIT 50
            """;

        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String fecha   = rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString() : "—";
                String mascota = rs.getString("mascota_nombre");
                double monto   = rs.getDouble("total");
                String concepto = rs.getString("concepto") != null ? rs.getString("concepto") : "Venta / Servicio";
                lista.add(new VentaDTO(fecha, mascota, concepto, monto));
            }
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al obtener ventas: " + e.getMessage());
        }
        return lista;
    }

    /** Total de ventas del mes actual. */
    public static double totalVentasMes() {
        String sql = """
            SELECT COALESCE(SUM(total), 0)
            FROM tb_venta
            WHERE MONTH(fecha) = MONTH(NOW()) AND YEAR(fecha) = YEAR(NOW())
            """;
        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al calcular total mes: " + e.getMessage());
        }
        return 0;
    }

    /** Número de ventas del día actual. */
    public static int ventasHoy() {
        String sql = "SELECT COUNT(*) FROM tb_venta WHERE DATE(fecha) = CURDATE()";
        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[VentasDAO] Error al contar ventas hoy: " + e.getMessage());
        }
        return 0;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private static int buscarIdState(String nombre, String tipo) {
        String sql = "SELECT id FROM tb_state WHERE nombre = ? AND tipo = ? LIMIT 1";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, tipo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { /* silencioso */ }
        return -1;
    }
}
