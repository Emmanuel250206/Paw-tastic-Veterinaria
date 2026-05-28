package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Staff;
import com.mycompany.aplicacion.modelo.UserSession;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO centralizado para gestión de usuarios web (tb_usuario_web).
 * Provee operaciones de listado, creación, edición y desactivación de staff.
 */
public class StaffDAO {

    // ─── Listar personal de la clínica activa ───────────────────────────────
    public static List<Staff> listarPorClinica() {
        List<Staff> lista = new ArrayList<>();
        String sql = """
            SELECT id, usuario, nombre, apellidos, tipo_rol,
                   especialidad, cedula, telefono, email, activo
            FROM tb_usuario_web
            WHERE id_clinica = ?
            ORDER BY nombre
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getClinicId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al listar staff: " + e.getMessage());
        }
        return lista;
    }

    // ─── Listar solo staff activo ────────────────────────────────────────────
    public static List<Staff> listarActivosPorClinica() {
        List<Staff> lista = new ArrayList<>();
        String sql = """
            SELECT id, usuario, nombre, apellidos, tipo_rol,
                   especialidad, cedula, telefono, email, activo
            FROM tb_usuario_web
            WHERE id_clinica = ? AND activo = 1
            ORDER BY nombre
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getClinicId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs));
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al listar staff activo: " + e.getMessage());
        }
        return lista;
    }

    // ─── Insertar nuevo usuario ──────────────────────────────────────────────
    public static boolean insertar(String usuario, String nombre, String apellidos,
                                   String tipoRol, String especialidad, String cedula,
                                   String telefono, String email, String contrasena) {
        // Verificar que usuario y email no estén en uso
        if (existeUsuario(usuario)) {
            System.err.println("[StaffDAO] Usuario ya existe: " + usuario);
            return false;
        }

        String sql = """
            INSERT INTO tb_usuario_web
                (id_clinica, usuario, nombre, apellidos, tipo_rol,
                 especialidad, cedula, telefono, email, contrasenia, activo)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getClinicId());
            ps.setString(2, usuario.trim());
            ps.setString(3, nombre.trim());
            ps.setString(4, apellidos != null ? apellidos.trim() : "");
            ps.setString(5, tipoRol.trim().toLowerCase());
            ps.setString(6, especialidad != null ? especialidad.trim() : null);
            ps.setString(7, cedula != null ? cedula.trim() : null);
            ps.setString(8, telefono != null ? telefono.trim() : "");
            ps.setString(9, email.trim());
            ps.setString(10, contrasena.trim());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // ─── Actualizar datos de un usuario ─────────────────────────────────────
    public static boolean actualizar(int idUsuario, String nombre, String apellidos,
                                     String tipoRol, String especialidad, String cedula,
                                     String telefono, String email) {
        String sql = """
            UPDATE tb_usuario_web SET
                nombre = ?, apellidos = ?, tipo_rol = ?,
                especialidad = ?, cedula = ?, telefono = ?, email = ?
            WHERE id = ?
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, apellidos != null ? apellidos.trim() : "");
            ps.setString(3, tipoRol.trim().toLowerCase());
            ps.setString(4, especialidad != null ? especialidad.trim() : null);
            ps.setString(5, cedula != null ? cedula.trim() : null);
            ps.setString(6, telefono != null ? telefono.trim() : "");
            ps.setString(7, email.trim());
            ps.setInt(8, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // ─── Actualizar perfil propio (nombre, alias, cedula, especialidad, telefono, email) ─
    public static boolean actualizarPerfil(int idUsuario, String nombre, String alias,
                                            String especialidad, String cedula,
                                            String telefono, String email) {
        String sql = """
            UPDATE tb_usuario_web SET
                nombre = ?, usuario = ?,
                especialidad = ?, cedula = ?,
                telefono = ?, email = ?
            WHERE id = ?
            """;
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ps.setString(2, alias.trim());
            ps.setString(3, especialidad != null ? especialidad.trim() : null);
            ps.setString(4, cedula != null ? cedula.trim() : null);
            ps.setString(5, telefono != null ? telefono.trim() : "");
            ps.setString(6, email != null ? email.trim() : "");
            ps.setInt(7, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al actualizar perfil: " + e.getMessage());
            return false;
        }
    }

    // ─── Desactivar usuario (soft delete) ───────────────────────────────────
    public static boolean desactivar(int idUsuario) {
        String sql = "UPDATE tb_usuario_web SET activo = 0 WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ─── Reactivar usuario ───────────────────────────────────────────────────
    public static boolean reactivar(int idUsuario) {
        String sql = "UPDATE tb_usuario_web SET activo = 1 WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al reactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ─── Contar personal activo de la clínica ───────────────────────────────
    public static int contarActivosPorClinica() {
        String sql = "SELECT COUNT(*) FROM tb_usuario_web WHERE id_clinica = ? AND activo = 1";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, UserSession.getInstance().getClinicId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[StaffDAO] Error al contar staff: " + e.getMessage());
        }
        return 0;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────
    private static boolean existeUsuario(String usuario) {
        String sql = "SELECT id FROM tb_usuario_web WHERE usuario = ? LIMIT 1";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, usuario.trim());
            return ps.executeQuery().next();
        } catch (Exception e) {
            return false;
        }
    }

    // ─── Relational Login ────────────────────────────────────────────────────
    public static boolean login(String usuario, String contrasenia) {
        String sql = "SELECT u.*, c.nombre AS clinica_nombre " +
                     "FROM tb_usuario_web u " +
                     "INNER JOIN tb_clinicas c ON u.id_clinica = c.id " +
                     "WHERE u.usuario = ? AND u.contrasenia = ?";
                     
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario);
            ps.setString(2, contrasenia);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    UserSession sesion = UserSession.getInstance();
                    sesion.setNombre(rs.getString("nombre"));
                    sesion.setRol(rs.getString("tipo_rol"));
                    sesion.setIdClinica(rs.getInt("id_clinica"));
                    
                    // Extraer los datos adicionales necesarios para evitar scope vacíos
                    sesion.setUserId(rs.getInt("id"));
                    sesion.setUserAlias(rs.getString("usuario"));
                    sesion.setUserCedula(rs.getString("cedula"));
                    sesion.setUserEmail(rs.getString("email"));
                    sesion.setUserTelefono(rs.getString("telefono"));
                    sesion.setUserEspecialidad(rs.getString("especialidad"));
                    sesion.setUsernameChanged(false);

                    // Extract the alias 'clinica_nombre' from the INNER JOIN
                    sesion.setNombreClinica(rs.getString("clinica_nombre"));
                    
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en el login: " + e.getMessage());
        }
        return false;
    }

    private static Staff mapear(ResultSet rs) throws SQLException {
        return new Staff(
            rs.getInt("id"),
            rs.getString("usuario"),
            rs.getString("nombre"),
            rs.getString("apellidos") != null ? rs.getString("apellidos") : "",
            rs.getString("tipo_rol"),
            rs.getString("especialidad") != null ? rs.getString("especialidad") : "",
            rs.getString("cedula")       != null ? rs.getString("cedula")       : "",
            rs.getString("telefono")     != null ? rs.getString("telefono")     : "",
            rs.getString("email")        != null ? rs.getString("email")        : "",
            rs.getInt("activo") == 1
        );
    }
}
