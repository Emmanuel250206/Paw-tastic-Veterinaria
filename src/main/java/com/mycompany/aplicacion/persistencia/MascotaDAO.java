package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Mascota;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO de Mascotas — todas las operaciones contra tb_mascotas + tb_propietarios.
 */
public class MascotaDAO {

    // ─── Obtener todas las mascotas activas ──────────────────────────────────
    public static ObservableList<Mascota> getTodas() {
        ObservableList<Mascota> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT m.id, m.nombre,
                   e.especie,
                   r.nombre   AS raza,
                   m.fecha_nacimiento,
                   m.descripcion,
                   COALESCE(p.nombre, uw.nombre, '') AS nom_prop,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS tel_prop,
                   COALESCE(p.direccion, '') AS dir_prop,
                   COALESCE(c.codigo_nfc, '') AS nfc,
                   COALESCE(exp.historial, '') AS historial
            FROM tb_mascotas m
            LEFT JOIN tb_especie      e   ON e.id   = m.id_especie
            LEFT JOIN tb_raza         r   ON r.id   = m.id_raza
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
            LEFT JOIN tb_usuario_web  uw  ON uw.id  = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id  = p.id_usuario_movil
            LEFT JOIN tb_collar       c   ON c.id_mascota = m.id AND c.estado = '1'
            LEFT JOIN tb_expediente   exp ON exp.id_mascota = m.id
            WHERE m.estado = '1'
            ORDER BY m.nombre
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Calcular edad a partir de fecha_nacimiento si existe
                String edadStr = calcularEdad(rs.getString("fecha_nacimiento"));
                String propietario = formatearNombre(rs.getString("nom_prop"), rs.getString("ape_prop"));

                Mascota m = new Mascota(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("especie") != null ? rs.getString("especie") : "—",
                    rs.getString("raza")    != null ? rs.getString("raza")    : "—",
                    0,                        // edad como int (legado) — no se usa si hay fecha_nacimiento
                    rs.getString("nfc"),
                    propietario,
                    rs.getString("tel_prop"),
                    rs.getString("dir_prop"),
                    rs.getString("historial")
                );
                // Guardar edad formateada en el nombre (reutilizamos el campo int como 0)
                lista.add(m);
            }
        } catch (Exception e) {
            System.err.println("[MascotaDAO] Error al cargar mascotas: " + e.getMessage());
        }
        return lista;
    }

    // ─── Buscar por nombre (coincidencia parcial) ────────────────────────────
    public static ObservableList<Mascota> buscarPorNombre(String texto) {
        if (texto == null || texto.isBlank()) return getTodas();

        ObservableList<Mascota> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT m.id, m.nombre,
                   e.especie, r.nombre AS raza, m.fecha_nacimiento, m.descripcion,
                   COALESCE(p.nombre, uw.nombre, '') AS nom_prop,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS tel_prop,
                   COALESCE(p.direccion, '') AS dir_prop,
                   COALESCE(c.codigo_nfc, '') AS nfc,
                   COALESCE(exp.historial, '') AS historial
            FROM tb_mascotas m
            LEFT JOIN tb_especie      e   ON e.id  = m.id_especie
            LEFT JOIN tb_raza         r   ON r.id  = m.id_raza
            LEFT JOIN tb_propietarios p   ON p.id  = m.id_propietario
            LEFT JOIN tb_usuario_web  uw  ON uw.id = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id = p.id_usuario_movil
            LEFT JOIN tb_collar       c   ON c.id_mascota = m.id AND c.estado = '1'
            LEFT JOIN tb_expediente   exp ON exp.id_mascota = m.id
            WHERE m.estado = '1' AND LOWER(m.nombre) LIKE ?
            ORDER BY m.nombre
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + texto.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String propietario = formatearNombre(rs.getString("nom_prop"), rs.getString("ape_prop"));
                lista.add(new Mascota(
                    rs.getInt("id"), rs.getString("nombre"),
                    rs.getString("especie") != null ? rs.getString("especie") : "—",
                    rs.getString("raza")    != null ? rs.getString("raza")    : "—",
                    0, rs.getString("nfc"), propietario,
                    rs.getString("tel_prop"), rs.getString("dir_prop"),
                    rs.getString("historial")
                ));
            }
        } catch (Exception e) {
            System.err.println("[MascotaDAO] Error al buscar mascotas: " + e.getMessage());
        }
        return lista;
    }

    // ─── Buscar por NFC del collar ───────────────────────────────────────────
    public static Mascota buscarPorCollar(String nfc) {
        if (nfc == null || nfc.isBlank()) return null;

        String sql = """
            SELECT m.id, m.nombre,
                   e.especie, r.nombre AS raza,
                   COALESCE(p.nombre, uw.nombre, '') AS nom_prop,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS tel_prop,
                   COALESCE(p.direccion, '') AS dir_prop,
                   c.codigo_nfc AS nfc,
                   COALESCE(exp.historial, '') AS historial
            FROM tb_collar c
            JOIN tb_mascotas m ON m.id = c.id_mascota
            LEFT JOIN tb_especie e ON e.id = m.id_especie
            LEFT JOIN tb_raza r ON r.id = m.id_raza
            LEFT JOIN tb_propietarios p ON p.id = m.id_propietario
            LEFT JOIN tb_usuario_web uw ON uw.id = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id = p.id_usuario_movil
            LEFT JOIN tb_expediente exp ON exp.id_mascota = m.id
            WHERE c.codigo_nfc = ? AND m.estado = '1'
            LIMIT 1
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nfc);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String propietario = formatearNombre(rs.getString("nom_prop"), rs.getString("ape_prop"));
                return new Mascota(
                    rs.getInt("id"), rs.getString("nombre"),
                    rs.getString("especie") != null ? rs.getString("especie") : "—",
                    rs.getString("raza")    != null ? rs.getString("raza")    : "—",
                    0, rs.getString("nfc"), propietario,
                    rs.getString("tel_prop"), rs.getString("dir_prop"),
                    rs.getString("historial")
                );
            }
        } catch (Exception e) {
            System.err.println("[MascotaDAO] Error al buscar por collar: " + e.getMessage());
        }
        return null;
    }

    // ─── Guardar historial clínico (expediente) ───────────────────────────────
    public static boolean guardarHistorial(int idMascota, String historial) {
        // Verificar si ya tiene expediente
        String sqlCheck = "SELECT id FROM tb_expediente WHERE id_mascota = ? LIMIT 1";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sqlCheck)) {

            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Actualizar expediente existente
                int idExp = rs.getInt("id");
                String sqlUpd = "UPDATE tb_expediente SET historial = ? WHERE id = ?";
                PreparedStatement psUpd = con.prepareStatement(sqlUpd);
                psUpd.setString(1, historial);
                psUpd.setInt(2, idExp);
                psUpd.executeUpdate();
            } else {
                // Crear nuevo expediente
                String sqlIns = "INSERT INTO tb_expediente (id_mascota, historial, fecha_apertura) VALUES (?, ?, NOW())";
                PreparedStatement psIns = con.prepareStatement(sqlIns);
                psIns.setInt(1, idMascota);
                psIns.setString(2, historial);
                psIns.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            System.err.println("[MascotaDAO] Error al guardar historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inserta una mascota y su propietario de forma rápida (Staff).
     * @return El ID de la mascota creada o -1 si falla.
     */
    public static int insertarBasico(String nombre, String dueno, String tel) {
        String sqlProp = "INSERT INTO tb_propietarios (nombre, telefono, estado, created_at, direccion) VALUES (?, ?, '1', NOW(), 'Sin dirección')";
        String sqlMasc = "INSERT INTO tb_mascotas (id_propietario, nombre, id_especie, id_raza, estado, created_at, descripcion) VALUES (?, ?, ?, ?, '1', NOW(), 'Ingresado por recepción')";

        try (Connection con = new Conexion().estableceConexion()) {
            con.setAutoCommit(false);
            int idProp = -1;
            try (PreparedStatement psP = con.prepareStatement(sqlProp, Statement.RETURN_GENERATED_KEYS)) {
                psP.setString(1, dueno);
                psP.setString(2, tel);
                psP.executeUpdate();
                ResultSet rsP = psP.getGeneratedKeys();
                if (rsP.next()) idProp = rsP.getInt(1);
            }

            if (idProp == -1) { con.rollback(); return -1; }

            // Asegurar que existan especie y raza por defecto (ID 1)
            // Si las tablas están vacías, esto fallaría. Insertamos si no existen.
            verificarDefaults(con);

            try (PreparedStatement psM = con.prepareStatement(sqlMasc, Statement.RETURN_GENERATED_KEYS)) {
                psM.setInt(1, idProp);
                psM.setString(2, nombre);
                psM.setInt(3, 1); // Default Especie
                psM.setInt(4, 1); // Default Raza
                psM.executeUpdate();
                ResultSet rsM = psM.getGeneratedKeys();
                if (rsM.next()) {
                    int idM = rsM.getInt(1);
                    con.commit();
                    return idM;
                }
            }
            con.rollback();
        } catch (Exception e) {
            System.err.println("[MascotaDAO] Error al insertar basico: " + e.getMessage());
        }
        return -1;
    }

    private static void verificarDefaults(Connection con) throws SQLException {
        String checkEsp = "SELECT id FROM tb_especie WHERE id = 1";
        String checkRaz = "SELECT id FROM tb_raza WHERE id = 1";
        
        try (Statement st = con.createStatement()) {
            ResultSet rsE = st.executeQuery(checkEsp);
            if (!rsE.next()) {
                st.executeUpdate("INSERT INTO tb_especie (id, especie) VALUES (1, 'General')");
            }
            ResultSet rsR = st.executeQuery(checkRaz);
            if (!rsR.next()) {
                st.executeUpdate("INSERT INTO tb_raza (id, id_especie, nombre) VALUES (1, 1, 'General')");
            }
        }
    }


    // ─── Helpers privados ────────────────────────────────────────────────────
    private static String formatearNombre(String nombre, String apellidos) {
        String n = nombre    != null ? nombre.trim()    : "";
        String a = apellidos != null ? apellidos.trim() : "";
        return (n + " " + a).trim().isEmpty() ? "Sin propietario" : (n + " " + a).trim();
    }

    private static String calcularEdad(String fechaNacStr) {
        if (fechaNacStr == null || fechaNacStr.isBlank()) return "—";
        try {
            java.time.LocalDate fn  = java.time.LocalDate.parse(fechaNacStr);
            java.time.LocalDate hoy = java.time.LocalDate.now();
            long años = java.time.temporal.ChronoUnit.YEARS.between(fn, hoy);
            if (años == 0) {
                long meses = java.time.temporal.ChronoUnit.MONTHS.between(fn, hoy);
                return meses + " meses";
            }
            return años + " años";
        } catch (Exception e) { return "—"; }
    }
}
