package com.mycompany.aplicacion.persistencia;

import com.mycompany.aplicacion.modelo.Citas;
import com.mycompany.aplicacion.modelo.Citas.Prioridad;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import com.mycompany.aplicacion.modelo.TipoCita;

/**
 * DAO de Citas — operaciones contra tb_citas + relaciones.
 */
public class CitasDAO {

    // ─── Obtener citas del día actual (activas) ──────────────────────────────
    public static ObservableList<Citas> getCitasHoy() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id, c.fecha, tc.nombre AS tipo, c.motivo, s.nombre AS estado,
                   m.nombre    AS mascota,
                   COALESCE(p.nombre, uw.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario,
                   c.temperatura, c.frecuencia_cardiaca, c.frecuencia_respiratoria
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            JOIN  tb_tipo_cita   tc  ON tc.id  = c.id_tipo_cita
            JOIN  tb_state       s   ON s.id   = c.id_State
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
            LEFT JOIN tb_usuario_web  uw  ON uw.id  = p.id_usuario_web
            LEFT JOIN tb_usuario_movil um ON um.id  = p.id_usuario_movil
            LEFT JOIN tb_usuario_web  uw2 ON uw2.id = c.id_usuario_web
            WHERE DATE(c.fecha) = CURDATE()
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
            SELECT c.id, c.fecha, tc.nombre AS tipo, c.motivo, s.nombre AS estado,
                   m.nombre AS mascota,
                   COALESCE(p.nombre, uw.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, uw.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, um.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario,
                   c.temperatura, c.frecuencia_cardiaca, c.frecuencia_respiratoria
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            JOIN  tb_tipo_cita   tc  ON tc.id  = c.id_tipo_cita
            JOIN  tb_state       s   ON s.id   = c.id_State
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

    // ─── Cambiar estado de cita por ID de estado relacional ──────────────────
    public static boolean cambiarEstadoCita(int idCita, int idEstado) {
        String sql = "UPDATE tb_citas SET id_State = ? WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstado);
            ps.setInt(2, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al cambiar estado de cita: " + e.getMessage());
            return false;
        }
    }

    // ─── Registrar triage y signos vitales en cita ──────────────────────────
    public static boolean registrarTriage(int idCita, double temp, int fc, int fr) {
        String sql = "UPDATE tb_citas SET temperatura = ?, frecuencia_cardiaca = ?, frecuencia_respiratoria = ? WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (temp == 0.0) ps.setNull(1, java.sql.Types.DECIMAL);
            else ps.setDouble(1, temp);

            if (fc == 0) ps.setNull(2, java.sql.Types.INTEGER);
            else ps.setInt(2, fc);

            if (fr == 0) ps.setNull(3, java.sql.Types.INTEGER);
            else ps.setInt(3, fr);

            ps.setInt(4, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al registrar triaje: " + e.getMessage());
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

        Citas cita = new Citas(
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
        cita.setTemperatura(rs.getObject("temperatura") != null ? rs.getDouble("temperatura") : null);
        cita.setFrecuenciaCardiaca(rs.getObject("frecuencia_cardiaca") != null ? rs.getInt("frecuencia_cardiaca") : null);
        cita.setFrecuenciaRespiratoria(rs.getObject("frecuencia_respiratoria") != null ? rs.getInt("frecuencia_respiratoria") : null);
        return cita;
    }

    // ─── Listar citas por fecha con JOINs completos ──────────────────────────
    public static ObservableList<Citas> listarCitasPorFecha(LocalDate fecha) {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT 
                c.id, 
                c.motivo, 
                c.fecha, 
                m.nombre AS mascota_nombre, 
                p.nombre AS propietario_nombre, 
                p.apellidos AS propietario_apellidos,
                tc.nombre AS tipo_cita_nombre, 
                s.nombre AS estado_nombre,
                c.temperatura,
                c.frecuencia_cardiaca,
                c.frecuencia_respiratoria
            FROM tb_citas c
            JOIN tb_mascotas m ON c.id_mascota = m.id
            JOIN tb_propietarios p ON m.id_propietario = p.id
            JOIN tb_tipo_cita tc ON c.id_tipo_cita = tc.id
            JOIN tb_state s ON c.id_State = s.id
            WHERE DATE(c.fecha) = ? AND s.tipo = 'cita'
            ORDER BY c.fecha ASC
            """;

        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearRelacional(rs));
                }
            }
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al listar citas por fecha: " + e.getMessage());
        }
        return lista;
    }

    private static Citas mapearRelacional(ResultSet rs) throws SQLException {
        String fechaStr = "";
        String horaStr  = "";
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) {
            java.time.LocalDateTime ldt = ts.toLocalDateTime();
            fechaStr = ldt.toLocalDate().toString();
            horaStr  = String.format("%02d:%02d", ldt.getHour(), ldt.getMinute());
        }

        String mascotaNombre = rs.getString("mascota_nombre");
        String propietarioNombre = rs.getString("propietario_nombre");
        String propietarioApellidos = rs.getString("propietario_apellidos");
        String propietarioCompleto = (propietarioNombre != null ? propietarioNombre : "") + 
                                     (propietarioApellidos != null && !propietarioApellidos.isBlank() ? " " + propietarioApellidos : "");
        propietarioCompleto = propietarioCompleto.trim();

        String tipoCitaNombre = rs.getString("tipo_cita_nombre");
        String estadoNombre = rs.getString("estado_nombre");
        String motivo = rs.getString("motivo");

        Prioridad prioridad = (tipoCitaNombre != null && tipoCitaNombre.toLowerCase().contains("urgente"))
            ? Prioridad.URGENTE : Prioridad.NORMAL;

        Citas cita = new Citas(
            rs.getInt("id"),
            fechaStr,
            horaStr,
            mascotaNombre,
            propietarioCompleto,
            "", // telefonoDueno
            "", // veterinario
            motivo,
            estadoNombre,
            prioridad,
            tipoCitaNombre
        );
        cita.setTemperatura(rs.getObject("temperatura") != null ? rs.getDouble("temperatura") : null);
        cita.setFrecuenciaCardiaca(rs.getObject("frecuencia_cardiaca") != null ? rs.getInt("frecuencia_cardiaca") : null);
        cita.setFrecuenciaRespiratoria(rs.getObject("frecuencia_respiratoria") != null ? rs.getInt("frecuencia_respiratoria") : null);
        return cita;
    }

    public static List<TipoCita> listarTiposCita() {
        List<TipoCita> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM tb_tipo_cita ORDER BY id";
        Connection con = new Conexion().estableceConexion();
        if (con == null) return lista;

        try {
            // Verificar si hay registros
            boolean tieneRegistros = false;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tb_tipo_cita")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    tieneRegistros = true;
                }
            }

            if (!tieneRegistros) {
                // Insertar tipos por defecto
                String sqlIns = "INSERT INTO tb_tipo_cita (id, nombre, descripcion) VALUES (?, ?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                    String[][] defaults = {
                        {"1", "Revisión", "Chequeo médico preventivo general"},
                        {"2", "Vacunación", "Aplicación de vacunas y refuerzos"},
                        {"3", "Estética", "Servicio de baño, corte y peinado"},
                        {"4", "Urgente", "Atención médica inmediata de emergencia"},
                        {"5", "Otro", "Consulta o servicio especial no listado"}
                    };
                    for (String[] def : defaults) {
                        ps.setInt(1, Integer.parseInt(def[0]));
                        ps.setString(2, def[1]);
                        ps.setString(3, def[2]);
                        ps.executeUpdate();
                    }
                }
            }

            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    lista.add(new TipoCita(rs.getInt("id"), rs.getString("nombre"), rs.getString("descripcion")));
                }
            }
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al listar tipos de cita: " + e.getMessage());
        } finally {
            try { con.close(); } catch (Exception ignored) {}
        }
        return lista;
    }

    public static boolean insertarCitaMascotaNueva(
            String mNombre, String mFechaNac, int idEsp, int idRaz, 
            String pNombre, String pApellidos, String pTelefono, String pDireccion, 
            int idTipoCita, String motivo, String fechaHora, int idUsuarioWeb) {
        
        Connection con = new Conexion().estableceConexion();
        if (con == null) return false;

        try {
            con.setAutoCommit(false);

            // 1. Insertar Propietario
            String sqlProp = "INSERT INTO tb_propietarios (nombre, apellidos, telefono, direccion, estado, created_at) VALUES (?, ?, ?, ?, '1', NOW())";
            int idPropietario = -1;
            try (PreparedStatement psProp = con.prepareStatement(sqlProp, Statement.RETURN_GENERATED_KEYS)) {
                psProp.setString(1, pNombre);
                psProp.setString(2, pApellidos);
                psProp.setString(3, pTelefono);
                psProp.setString(4, pDireccion == null || pDireccion.trim().isEmpty() ? "Sin dirección" : pDireccion);
                psProp.executeUpdate();
                try (ResultSet rs = psProp.getGeneratedKeys()) {
                    if (rs.next()) {
                        idPropietario = rs.getInt(1);
                    }
                }
            }

            if (idPropietario == -1) {
                con.rollback();
                return false;
            }

            // 2. Insertar Mascota
            String sqlMascota = "INSERT INTO tb_mascotas (id_propietario, nombre, id_especie, id_raza, fecha_nacimiento, estado, created_at, descripcion) VALUES (?, ?, ?, ?, ?, '1', NOW(), 'Registrado desde recepción')";
            int idMascota = -1;
            try (PreparedStatement psMascota = con.prepareStatement(sqlMascota, Statement.RETURN_GENERATED_KEYS)) {
                psMascota.setInt(1, idPropietario);
                psMascota.setString(2, mNombre);
                psMascota.setInt(3, idEsp);
                psMascota.setInt(4, idRaz);
                if (mFechaNac != null && !mFechaNac.trim().isEmpty()) {
                    psMascota.setDate(5, java.sql.Date.valueOf(mFechaNac));
                } else {
                    psMascota.setNull(5, Types.DATE);
                }
                psMascota.executeUpdate();
                try (ResultSet rs = psMascota.getGeneratedKeys()) {
                    if (rs.next()) {
                        idMascota = rs.getInt(1);
                    }
                }
            }

            if (idMascota == -1) {
                con.rollback();
                return false;
            }

            // 3. Insertar Cita
            String sqlCita = "INSERT INTO tb_citas (id_mascota, id_usuario_web, motivo, fecha, fecha_reg, id_tipo_cita, id_State) VALUES (?, ?, ?, ?, NOW(), ?, 13)";
            try (PreparedStatement psCita = con.prepareStatement(sqlCita)) {
                psCita.setInt(1, idMascota);
                psCita.setInt(2, idUsuarioWeb);
                psCita.setString(3, motivo);
                psCita.setString(4, fechaHora);
                psCita.setInt(5, idTipoCita);
                psCita.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            try { con.rollback(); } catch (Exception ignored) {}
            System.err.println("[CitasDAO] Error en transacción insertarCitaMascotaNueva: " + e.getMessage());
            return false;
        } finally {
            try { con.close(); } catch (Exception ignored) {}
        }
    }

    public static boolean insertarCitaMascotaRegistrada(
            int idMascota, int idTipoCita, String motivo, String fechaHora, int idUsuarioWeb) {
        
        String sql = "INSERT INTO tb_citas (id_mascota, id_usuario_web, motivo, fecha, fecha_reg, id_tipo_cita, id_State) VALUES (?, ?, ?, ?, NOW(), ?, 13)";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ps.setInt(2, idUsuarioWeb);
            ps.setString(3, motivo);
            ps.setString(4, fechaHora);
            ps.setInt(5, idTipoCita);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al insertar cita registrada: " + e.getMessage());
            return false;
        }
    }
}
