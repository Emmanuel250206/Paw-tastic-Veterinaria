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
 * CORRECCIONES APLICADAS:
 *  - Eliminadas columnas inexistentes: temperatura, frecuencia_cardiaca, frecuencia_respiratoria
 *  - insertarBasico() corregido: usa id_tipo_cita en vez de columna 'tipo'
 *  - cambiarEstado() corregido: usa id_state (FK) en vez de columna 'estado' (char)
 */
public class CitasDAO {

    // IDs de estado en tb_state (tipo = 'cita')
    public static final int STATE_PENDIENTE     = 13;
    public static final int STATE_CONFIRMADA    = 14;
    public static final int STATE_EN_CURSO      = 15;
    public static final int STATE_COMPLETADA    = 16;
    public static final int STATE_REPROGRAMADA  = 17;
    public static final int STATE_CANCELADA     = 18;

    /**
     * Registra los signos vitales del triaje como nota en el expediente de la mascota.
     * NOTA: tb_citas no tiene columnas de signos vitales en el esquema actual.
     *       Los valores se guardan en tb_expediente.historial como texto de triaje.
     *
     * @param idCita               ID de la cita a actualizar
     * @param temperatura          Temperatura corporal (°C)
     * @param frecuenciaCardiaca   Frecuencia cardíaca (lpm)
     * @param frecuenciaRespiratoria Frecuencia respiratoria (rpm)
     * @return true si se guardó exitosamente
     */
    public static boolean registrarTriage(int idCita, double temperatura,
                                          int frecuenciaCardiaca, int frecuenciaRespiratoria) {
        int idExpediente = obtenerOCrearExpediente(idCita);
        if (idExpediente <= 0) return false;

        String notaTriage = String.format(
            "\n--- Triaje registrado ---\n" +
            "  Temperatura: %.1f °C\n" +
            "  Frec. Cardíaca: %d lpm\n" +
            "  Frec. Respiratoria: %d rpm\n",
            temperatura, frecuenciaCardiaca, frecuenciaRespiratoria
        );

        String sql = "UPDATE tb_expediente SET historial = CONCAT(COALESCE(historial,''), ?) WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, notaTriage);
            ps.setInt(2, idExpediente);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al registrar triaje: " + e.getMessage());
            return false;
        }
    }

    // ─── Obtener citas del día actual ────────────────────────────────────────
    public static ObservableList<Citas> getCitasHoy() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();

        String sql = """
            SELECT c.id, c.fecha, tc.nombre AS tipo, c.motivo, s.nombre AS estado,
                   m.nombre    AS mascota,
                   COALESCE(p.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            JOIN  tb_tipo_cita   tc  ON tc.id  = c.id_tipo_cita
            JOIN  tb_state       s   ON s.id   = c.id_state
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
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
            System.err.println("[CitasDAO] Error al cargar citas hoy: " + e.getMessage());
        }
        return lista;
    }


    // ─── Obtener todas las citas (para historial) ────────────────────────────
    public static ObservableList<Citas> getTodas() {
        ObservableList<Citas> lista = FXCollections.observableArrayList();
        String sql = """
            SELECT c.id, c.fecha, tc.nombre AS tipo, c.motivo, s.nombre AS estado,
                   m.nombre AS mascota,
                   COALESCE(p.nombre, 'Sin propietario') AS propietario,
                   COALESCE(p.apellidos, '') AS ape_prop,
                   COALESCE(p.telefono, '') AS telefono,
                   COALESCE(uw2.nombre, '') AS veterinario
            FROM tb_citas c
            JOIN  tb_mascotas    m   ON m.id   = c.id_mascota
            JOIN  tb_tipo_cita   tc  ON tc.id  = c.id_tipo_cita
            JOIN  tb_state       s   ON s.id   = c.id_state
            LEFT JOIN tb_propietarios p   ON p.id   = m.id_propietario
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

    // ─── Cambiar estado de cita por ID de estado relacional ──────────────────
    /**
     * Actualiza el estado de una cita usando la FK id_state (tabla tb_state).
     * Usar las constantes STATE_* de esta clase para el parámetro idEstado.
     */
    public static boolean cambiarEstadoCita(int idCita, int idEstado) {
        String sql = "UPDATE tb_citas SET id_state = ? WHERE id = ?";
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

    /**
     * Reprograma una cita: actualiza fecha y estado a Reprogramada.
     */
    public static boolean reprogramarCita(int idCita, String nuevaFechaHora) {
        String sql = "UPDATE tb_citas SET fecha = ?, id_state = ? WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaFechaHora);
            ps.setInt(2, STATE_REPROGRAMADA);
            ps.setInt(3, idCita);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al reprogramar cita: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancela una cita (id_state = 18 = Cancelada).
     */
    public static boolean cancelarCita(int idCita) {
        return cambiarEstadoCita(idCita, STATE_CANCELADA);
    }

    // ─── Guardar diagnóstico al finalizar consulta ───────────────────────────
    public static boolean guardarDiagnostico(int idExpediente, int idCita,
                                              String descripcion, String tratamiento) {
        // Si no existe expediente, se crea automáticamente buscando la mascota de la cita
        if (idExpediente <= 0) {
            idExpediente = obtenerOCrearExpediente(idCita);
        }
        if (idExpediente <= 0) {
            System.err.println("[CitasDAO] No se pudo obtener expediente para cita " + idCita);
            return false;
        }

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

    /** Obtiene el id de expediente de una cita; si no existe, lo crea. */
    private static int obtenerOCrearExpediente(int idCita) {
        // 1. Obtener id_mascota de la cita
        String sqlMasc = "SELECT id_mascota FROM tb_citas WHERE id = ?";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sqlMasc)) {
            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return -1;
            int idMascota = rs.getInt("id_mascota");

            // 2. Buscar expediente existente
            String sqlExp = "SELECT id FROM tb_expediente WHERE id_mascota = ? LIMIT 1";
            try (PreparedStatement ps2 = con.prepareStatement(sqlExp)) {
                ps2.setInt(1, idMascota);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) return rs2.getInt("id");
            }

            // 3. Crear expediente nuevo
            String sqlIns = "INSERT INTO tb_expediente (id_mascota, historial, fecha_apertura) VALUES (?, '', NOW())";
            try (PreparedStatement ps3 = con.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                ps3.setInt(1, idMascota);
                ps3.executeUpdate();
                ResultSet rsKeys = ps3.getGeneratedKeys();
                if (rsKeys.next()) return rsKeys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al obtener/crear expediente: " + e.getMessage());
        }
        return -1;
    }

    // ─── Insertar nueva cita (mascota ya registrada) ─────────────────────────
    public static boolean insertarCitaMascotaRegistrada(
            int idMascota, int idTipoCita, String motivo, String fechaHora, int idUsuarioWeb) {

        String sql = "INSERT INTO tb_citas (id_mascota, id_usuario_web, motivo, fecha, fecha_reg, id_tipo_cita, id_state) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
        try (Connection con = new Conexion().estableceConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ps.setInt(2, idUsuarioWeb);
            ps.setString(3, motivo);
            ps.setString(4, fechaHora);
            ps.setInt(5, idTipoCita);
            ps.setInt(6, STATE_PENDIENTE);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[CitasDAO] Error al insertar cita registrada: " + e.getMessage());
            return false;
        }
    }

    // ─── Insertar cita con mascota NUEVA ────────────────────────────────────
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
                psProp.setString(2, pApellidos != null ? pApellidos : "");
                psProp.setString(3, pTelefono);
                psProp.setString(4, pDireccion == null || pDireccion.trim().isEmpty() ? "Sin dirección" : pDireccion);
                psProp.executeUpdate();
                try (ResultSet rs = psProp.getGeneratedKeys()) {
                    if (rs.next()) idPropietario = rs.getInt(1);
                }
            }

            if (idPropietario == -1) { con.rollback(); return false; }

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
                    if (rs.next()) idMascota = rs.getInt(1);
                }
            }

            if (idMascota == -1) { con.rollback(); return false; }

            // 3. Insertar Cita
            String sqlCita = "INSERT INTO tb_citas (id_mascota, id_usuario_web, motivo, fecha, fecha_reg, id_tipo_cita, id_state) VALUES (?, ?, ?, ?, NOW(), ?, ?)";
            try (PreparedStatement psCita = con.prepareStatement(sqlCita)) {
                psCita.setInt(1, idMascota);
                psCita.setInt(2, idUsuarioWeb);
                psCita.setString(3, motivo);
                psCita.setString(4, fechaHora);
                psCita.setInt(5, idTipoCita);
                psCita.setInt(6, STATE_PENDIENTE);
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

    // ─── Listar citas por fecha ──────────────────────────────────────────────
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
                s.nombre AS estado_nombre
            FROM tb_citas c
            JOIN tb_mascotas m ON c.id_mascota = m.id
            LEFT JOIN tb_propietarios p ON m.id_propietario = p.id
            JOIN tb_tipo_cita tc ON c.id_tipo_cita = tc.id
            JOIN tb_state s ON c.id_state = s.id
            WHERE DATE(c.fecha) = ?
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

    // ─── Listar tipos de cita ────────────────────────────────────────────────
    public static List<TipoCita> listarTiposCita() {
        List<TipoCita> lista = new ArrayList<>();
        String sql = "SELECT id, nombre, descripcion FROM tb_tipo_cita ORDER BY id";
        Connection con = new Conexion().estableceConexion();
        if (con == null) return lista;

        try {
            boolean tieneRegistros = false;
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tb_tipo_cita")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    tieneRegistros = true;
                }
            }

            if (!tieneRegistros) {
                String sqlIns = "INSERT INTO tb_tipo_cita (nombre, descripcion) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(sqlIns)) {
                    String[][] defaults = {
                        {"Revisión",       "Chequeo médico preventivo general"},
                        {"Vacunación",     "Aplicación de vacunas y refuerzos"},
                        {"Estética",       "Servicio de baño, corte y peinado"},
                        {"Urgente",        "Atención médica inmediata de emergencia"},
                        {"Desparasitación","Control antiparasitario"},
                        {"Otro",           "Consulta o servicio especial no listado"}
                    };
                    for (String[] def : defaults) {
                        ps.setString(1, def[0]);
                        ps.setString(2, def[1]);
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
            estadoDB != null ? estadoDB : "Pendiente",
            prioridad
        );
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
        String propietarioNombre   = rs.getString("propietario_nombre");
        String propietarioApellidos = rs.getString("propietario_apellidos");
        String propietarioCompleto = (propietarioNombre != null ? propietarioNombre : "") +
                                     (propietarioApellidos != null && !propietarioApellidos.isBlank()
                                      ? " " + propietarioApellidos : "");
        propietarioCompleto = propietarioCompleto.trim();

        String tipoCitaNombre = rs.getString("tipo_cita_nombre");
        String estadoNombre   = rs.getString("estado_nombre");
        String motivo         = rs.getString("motivo");

        Prioridad prioridad = (tipoCitaNombre != null && tipoCitaNombre.toLowerCase().contains("urgente"))
            ? Prioridad.URGENTE : Prioridad.NORMAL;

        return new Citas(
            rs.getInt("id"),
            fechaStr,
            horaStr,
            mascotaNombre,
            propietarioCompleto,
            "", // telefonoDueno
            "", // veterinario
            motivo,
            estadoNombre != null ? estadoNombre : "Pendiente",
            prioridad,
            tipoCitaNombre
        );
    }
}
