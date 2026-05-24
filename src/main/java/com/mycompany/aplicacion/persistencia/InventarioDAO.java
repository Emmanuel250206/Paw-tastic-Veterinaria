package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Inventario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO de Inventario — operaciones contra tb_producto.
 * CORRECCIONES APLICADAS:
 *  - Eliminada columna id_clinica (no existe en tb_producto del esquema actual)
 *  - Eliminada función buscarDisponibilidadGlobal (requería id_clinica)
 *  - Añadida creación de tb_alerta_stock al insertar/actualizar producto con bajo stock
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

    // ─── Buscar productos por nombre ────────────────────────────────────────
    public static ObservableList<Inventario> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) return getTodos();

        ObservableList<Inventario> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT p.id, p.nombre, p.categoria, p.descripcion,
                   p.existencia AS stock_actual, p.stock_minimo,
                   p.unidad_medida, p.costo_unitario AS precio_compra,
                   p.precio AS precio_venta, p.fecha_caducidad
            FROM tb_producto p
            INNER JOIN tb_state s ON s.id = p.id_State AND s.tipo = 'producto'
            WHERE s.nombre = 'Activo' AND LOWER(p.nombre) LIKE ?
            ORDER BY p.nombre
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + texto.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error en búsqueda: " + e.getMessage());
        }
        return lista;
    }

    // ─── Insertar nuevo producto ────────────────────────────────────────────
    public static boolean insertar(Inventario inv) {
        int idState = buscarIdState("Activo", "producto");
        if (idState == -1) {
            System.err.println("[InventarioDAO] No se encontró state 'Activo'");
            return false;
        }

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
            if (inv.getFecha_caducidad() != null && !inv.getFecha_caducidad().isBlank()) {
                ps.setString(10, inv.getFecha_caducidad());
            } else {
                ps.setNull(10, Types.DATE);
            }
            ps.setInt(11, idState);
            ps.executeUpdate();

            ResultSet generados = ps.getGeneratedKeys();
            if (generados.next()) {
                inv.setId(generados.getInt(1));
                // Verificar alerta de stock tras inserción
                crearAlertaStockSiNecesario(con, inv.getId(),
                        inv.getStock_actual(), inv.getStock_minimo());
            }
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
            if (inv.getFecha_caducidad() != null && !inv.getFecha_caducidad().isBlank()) {
                ps.setString(9, inv.getFecha_caducidad());
            } else {
                ps.setNull(9, Types.DATE);
            }
            ps.setInt(10, inv.getId());
            boolean ok = ps.executeUpdate() > 0;

            if (ok) {
                // Verificar alerta de stock tras actualización
                crearAlertaStockSiNecesario(con, inv.getId(),
                        inv.getStock_actual(), inv.getStock_minimo());
            }
            return ok;

        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al actualizar: " + e.getMessage());
            return false;
        }
    }

    // ─── Descontar stock tras venta ─────────────────────────────────────────
    /**
     * Descuenta la cantidad vendida del stock del producto.
     * Si el stock resultante es menor o igual al mínimo, crea una alerta.
     */
    public static boolean descontarStock(int idProducto, int cantidad) {
        String sqlUpd = "UPDATE tb_producto SET existencia = existencia - ? WHERE id = ? AND existencia >= ?";
        String sqlSel = "SELECT existencia, stock_minimo FROM tb_producto WHERE id = ?";

        try (Connection con = new Conexion().estableceConexion()) {
            try (PreparedStatement ps = con.prepareStatement(sqlUpd)) {
                ps.setInt(1, cantidad);
                ps.setInt(2, idProducto);
                ps.setInt(3, cantidad);
                if (ps.executeUpdate() == 0) {
                    System.err.println("[InventarioDAO] Stock insuficiente para producto " + idProducto);
                    return false;
                }
            }
            // Verificar si se debe generar alerta
            try (PreparedStatement ps2 = con.prepareStatement(sqlSel)) {
                ps2.setInt(1, idProducto);
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) {
                    int existencia  = rs.getInt("existencia");
                    int stockMinimo = rs.getInt("stock_minimo");
                    crearAlertaStockSiNecesario(con, idProducto, existencia, stockMinimo);
                }
            }
            return true;

        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al descontar stock: " + e.getMessage());
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

    // ─── Alertas de stock ────────────────────────────────────────────────────
    /**
     * Crea una alerta en tb_alerta_stock si existencia <= stock_minimo.
     * Solo crea la alerta si no existe una alerta sin resolver para ese producto.
     */
    private static void crearAlertaStockSiNecesario(Connection con, int idProducto,
                                                     int existencia, int stockMinimo) {
        if (existencia > stockMinimo) return;

        try {
            // Verificar si ya existe alerta sin resolver
            String sqlCheck = "SELECT id FROM tb_alerta_stock WHERE id_producto = ? AND resuelta = 0 LIMIT 1";
            try (PreparedStatement psCheck = con.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idProducto);
                if (psCheck.executeQuery().next()) return; // Ya existe alerta activa
            }

            String sqlIns = "INSERT INTO tb_alerta_stock (id_producto, existencia, stock_minimo, resuelta) VALUES (?, ?, ?, 0)";
            try (PreparedStatement psIns = con.prepareStatement(sqlIns)) {
                psIns.setInt(1, idProducto);
                psIns.setInt(2, existencia);
                psIns.setInt(3, stockMinimo);
                psIns.executeUpdate();
                System.out.println("[InventarioDAO] Alerta de stock creada para producto " + idProducto);
            }
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al crear alerta de stock: " + e.getMessage());
        }
    }

    /** Obtiene el número de alertas de stock activas (sin resolver). */
    public static int contarAlertasActivas() {
        String sql = "SELECT COUNT(*) FROM tb_alerta_stock WHERE resuelta = 0";
        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al contar alertas: " + e.getMessage());
        }
        return 0;
    }

    /** Obtiene el número de productos con stock bajo (existencia <= stock_minimo). */
    public static int contarProductosBajoStock() {
        String sql = "SELECT COUNT(*) FROM tb_producto WHERE existencia <= stock_minimo AND id_State IN (SELECT id FROM tb_state WHERE nombre='Activo' AND tipo='producto')";
        try (Connection con = new Conexion().estableceConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error al contar bajo stock: " + e.getMessage());
        }
        return 0;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private static Inventario mapear(ResultSet rs) throws SQLException {
        return new Inventario(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("categoria")    != null ? rs.getString("categoria")    : "",
            rs.getString("descripcion")  != null ? rs.getString("descripcion")  : "",
            rs.getInt("stock_actual"),
            rs.getInt("stock_minimo"),
            rs.getString("unidad_medida") != null ? rs.getString("unidad_medida") : "",
            rs.getDouble("precio_compra"),
            rs.getDouble("precio_venta"),
            rs.getString("fecha_caducidad") != null ? rs.getString("fecha_caducidad") : "",
            0  // proveedor_id — campo legado
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
        String base = nombre.replaceAll("[^A-Za-z]", "").toUpperCase();
        base = base.length() >= 3 ? base.substring(0, 3) : base;
        return base + System.currentTimeMillis() % 100000;
    }

    // ─── Buscar disponibilidad global (todos los productos que coincidan) ─────
    /**
     * Busca productos con stock disponible cuyo nombre coincida con el texto dado.
     * NOTA: El esquema actual no tiene id_clinica en tb_producto, por lo que
     *       muestra todos los productos activos del sistema.
     */
    public static ObservableList<String> buscarDisponibilidadGlobal(String nombreProducto) {
        ObservableList<String> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT p.nombre, p.existencia, p.categoria
            FROM tb_producto p
            INNER JOIN tb_state s ON s.id = p.id_State AND s.tipo = 'producto'
            WHERE s.nombre = 'Activo'
              AND p.existencia > 0
              AND LOWER(p.nombre) LIKE LOWER(?)
            ORDER BY p.nombre
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + nombreProducto + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombre   = rs.getString("nombre");
                    int    stock    = rs.getInt("existencia");
                    String categoria = rs.getString("categoria");
                    lista.add(nombre + " | Stock: " + stock +
                              (categoria != null ? " | " + categoria : ""));
                }
            }
        } catch (Exception e) {
            System.err.println("[InventarioDAO] Error en búsqueda global: " + e.getMessage());
        }
        return lista;
    }
}
