package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Inventario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO de Inventario — operaciones contra tb_producto.
 */
public class InventarioDAO {

    // ─── Obtener todos los productos activos ────────────────────────────────
    public static ObservableList<Inventario> getTodos() {
        ObservableList<Inventario> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id, p.nombre, p.categoria, p.descripcion,
                   p.existencia AS stock_actual, p.stock_minimo,
                   p.unidad_medida, p.costo_unitario AS precio_compra,
                   p.precio AS precio_venta, p.fecha_caducidad
            FROM tb_producto p
            INNER JOIN tb_state s ON s.id = p.id_State AND s.tipo = 'producto'
            WHERE s.nombre = 'Activo'
            ORDER BY p.nombre
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al cargar inventario: " + e.getMessage());
        }
        return lista;
    }

    // ─── Insertar nuevo producto ────────────────────────────────────────────
    public static boolean insertar(Inventario inv) {
        // Buscar id_State para 'Activo' tipo 'producto'
        int idState = buscarIdState("Activo", "producto");
        if (idState == -1) { System.err.println("[InventarioDAO] No se encontró state 'Activo'"); return false; }

        String sql = """
            INSERT INTO tb_producto
                (nombre, categoria, descripcion, codigo, precio, costo_unitario,
                 existencia, stock_minimo, unidad_medida, fecha_caducidad, fecha, id_State)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), ?)
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, inv.getNombre());
            ps.setString(2, inv.getCategoria());
            ps.setString(3, inv.getDescripcion());
            ps.setString(4, generarCodigo(inv.getNombre()));
            ps.setDouble(5, inv.getPrecio_venta());
            ps.setDouble(6, inv.getPrecio_compra());
            ps.setInt   (7, inv.getStock_actual());
            ps.setInt   (8, inv.getStock_minimo());
            ps.setString(9, inv.getUnidad_medida());
            ps.setString(10, inv.getFecha_caducidad());
            ps.setInt   (11, idState);
            ps.executeUpdate();

            ResultSet generados = ps.getGeneratedKeys();
            if (generados.next()) inv.setId(generados.getInt(1));
            return true;

        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // ─── Actualizar producto existente ──────────────────────────────────────
    public static boolean actualizar(Inventario inv) {
        String sql = """
            UPDATE tb_producto SET
                nombre = ?, categoria = ?, descripcion = ?,
                precio = ?, costo_unitario = ?, existencia = ?,
                stock_minimo = ?, unidad_medida = ?, fecha_caducidad = ?
            WHERE id = ?
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, inv.getNombre());
            ps.setString(2, inv.getCategoria());
            ps.setString(3, inv.getDescripcion());
            ps.setDouble(4, inv.getPrecio_venta());
            ps.setDouble(5, inv.getPrecio_compra());
            ps.setInt   (6, inv.getStock_actual());
            ps.setInt   (7, inv.getStock_minimo());
            ps.setString(8, inv.getUnidad_medida());
            ps.setString(9, inv.getFecha_caducidad());
            ps.setInt   (10, inv.getId());
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ─── Desactivar producto (soft delete) ──────────────────────────────────
    public static boolean desactivar(int id) {
        int idState = buscarIdState("Inactivo", "producto");
        if (idState == -1) return false;

        String sql = "UPDATE tb_producto SET id_State = ? WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idState);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al desactivar: " + e.getMessage());
            return false;
        }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private static Inventario mapear(ResultSet rs) throws SQLException {
        return new Inventario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("categoria") != null ? rs.getString("categoria") : "",
            rs.getString("descripcion") != null ? rs.getString("descripcion") : "",
            rs.getInt("stock_actual"),
            rs.getInt("stock_minimo"),
            rs.getString("unidad_medida") != null ? rs.getString("unidad_medida") : "",
            rs.getDouble("precio_compra"),
            rs.getDouble("precio_venta"),
            rs.getString("fecha_caducidad") != null ? rs.getString("fecha_caducidad") : "",
            0  // proveedor_id — campo legado, no requerido en la BD nueva
        );
    }

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

    private static String generarCodigo(String nombre) {
        // Genera código simple: primeras 3 letras + timestamp corto
        String base = nombre.replaceAll("[^A-Za-z]", "").toUpperCase();
        base = base.length() >= 3 ? base.substring(0, 3) : base;
        return base + System.currentTimeMillis() % 100000;
    }
}
