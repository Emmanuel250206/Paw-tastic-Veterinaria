package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.Citas.Prioridad;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

/**
 * DAO de Citas — operaciones contra tb_citas + relaciones.
 */
public class CitasDAO {

    // ─── Obtener citas del día actual (activas) ──────────────────────────────
    public static ObservableList<Citas> getCitasHoy() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id, c.fecha, c.tipo, c.motivo, c.estado,
                   m.nombre    AS mascota,
                   COALESCE(p.nombre, uw.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
            LEFT JOIN tb_usuario_web  uw  ON uw.id  = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id  = p.id_usuario_movil
            LEFT JOIN tb_usuario_web  uw2 ON uw2.id = c.id_usuario_web
            WHERE DATE(c.fecha) = CURDATE()
              AND c.estado NOT IN ('C', 'F')
            ORDER BY c.fecha ASC
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al cargar citas: " + e.getMessage());
        }
        return lista;
    }

    // ─── Obtener todas las citas (para historial) ────────────────────────────
    public static ObservableList<Citas> getTodas() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id, c.fecha, c.tipo, c.motivo, c.estado,
                   m.nombre AS mascota,
                   COALESCE(p.nombre, uw.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
            LEFT JOIN tb_usuario_web  uw  ON uw.id  = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id  = p.id_usuario_movil
            LEFT JOIN tb_usuario_web  uw2 ON uw2.id = c.id_usuario_web
            ORDER BY c.fecha DESC
            LIMIT 100
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al cargar historial: " + e.getMessage());
        }
        return lista;
    }

    // ─── Cambiar estado de una cita ──────────────────────────────────────────
    public static boolean cambiarEstado(int idCita, String nuevoEstado) {
        // estado: 'P'=Programada 'E'=En Consulta 'F'=Finalizada 'C'=Cancelada
        String sql = "UPDATE tb_citas SET estado = ? WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al cambiar estado: " + e.getMessage());
            return false;
        }
    }

    // ─── Guardar diagnóstico al finalizar consulta ───────────────────────────
    public static boolean guardarDiagnostico(int idExpediente, int idCita,
                                              String descripcion, String tratamiento) {
        String sql = """
            INSERT INTO tb_diagnosticos (id_expediente, id_cita, descripcion, tratamiento, fecha_registro)
            VALUES (?, ?, ?, ?, NOW())
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt   (1, idExpediente);
            ps.setInt   (2, idCita);
            ps.setString(3, descripcion);
            ps.setString(4, tratamiento);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al guardar diagnóstico: " + e.getMessage());
            return false;
        }
    }

    // ─── Insertar nueva cita ──────────────────────────────────────────
    public static boolean insertarBasico(Citas cita, int idMascota, int idUsuarioWeb) {
        String sql = """
            INSERT INTO tb_citas (id_mascota, id_usuario_web, tipo, motivo, estado, fecha, fecha_reg)
            VALUES (?, ?, ?, ?, 'P', ?, NOW())
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, idMascota);
            ps.setInt(2, idUsuarioWeb);
            ps.setString(3, cita.getPrioridad() == Prioridad.URGENTE ? "Urgente" : "Revisión");
            ps.setString(4, cita.getMotivo());
            // Formatear la fecha: YYYY-MM-DD HH:MM:00
            String fechaStr = cita.getFecha() + " " + cita.getHora() + ":00";
            ps.setString(5, fechaStr);
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        cita.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al insertar cita: " + e.getMessage());
        }
        return false;
    }


    // ─── Mapear ResultSet → objeto Citas ────────────────────────────────────
    private static Citas mapear(ResultSet rs) throws SQLException {
        String fechaStr = "";
        String horaStr  = "";
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            java.time.LocalDateTime ldt = ts.toLocalDateTime();
            fechaStr = ldt.toLocalDate().toString();
            horaStr  = String.format("%02d:%02d", ldt.getHour(), ldt.getMinute());
        }

        String estadoDB = rs.getString("estado");
        String estadoLegible = switch (estadoDB != null ? estadoDB : "P") {
            case "P" -> "Programada";
            case "E" -> "En Consulta";
            case "F" -> "Finalizada";
            case "C" -> "Cancelada";
            default  -> estadoDB;
        };

        // Urgente: si el tipo empieza con 'U' o contiene 'urgente' (configurable)
        String tipo = rs.getString("tipo");
        Prioridad prioridad = (tipo != null && tipo.toLowerCase().contains("urgente"))
            ? Prioridad.URGENTE : Prioridad.NORMAL;

        String propietario = rs.getString("propietario");
        String ape = rs.getString("ape_prop");
        if (ape != null && !ape.isBlank()) propietario += " " + ape;

        return new Citas(
            rs.getInt("id"),
            fechaStr,
            horaStr,
            rs.getString("mascota"),
            propietario.trim(),
            rs.getString("telefono"),
            rs.getString("veterinario"),
            rs.getString("motivo") != null ? rs.getString("motivo") : tipo,
            estadoLegible,
            prioridad
        );
    }
}
