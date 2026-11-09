package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.MensajeContacto;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class MensajeContactoRepository {

    private static final RowMapper<MensajeContacto> MAPEO = (rs, fila) -> {
        MensajeContacto mensaje = new MensajeContacto();
        mensaje.setIdMensaje(rs.getInt("id_mensaje"));
        mensaje.setNombreRemitente(rs.getString("nombre_remitente"));
        mensaje.setCorreoRemitente(rs.getString("correo_remitente"));
        mensaje.setAsunto(rs.getString("asunto"));
        mensaje.setMensaje(rs.getString("mensaje"));
        mensaje.setFechaEnvio(LlamadaBd.fecha(rs.getTimestamp("fecha_envio")));
        return mensaje;
    };

    private final SimpleJdbcCall spInsertar;
    private final SimpleJdbcCall spListar;

    public MensajeContactoRepository(JdbcTemplate jdbc) {
        spInsertar = LlamadaBd.procedimiento(jdbc, "sp_mensaje_insertar",
                new SqlParameter("p_nombre_remitente", Types.VARCHAR),
                new SqlParameter("p_correo_remitente", Types.VARCHAR),
                new SqlParameter("p_asunto", Types.VARCHAR),
                new SqlParameter("p_mensaje", Types.VARCHAR),
                new SqlOutParameter("p_id_mensaje", Types.NUMERIC));

        spListar = LlamadaBd.procedimiento(jdbc, "sp_mensaje_listar",
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO));
    }

    public Integer insertar(MensajeContacto mensaje) {
        Map<String, Object> salida = spInsertar.execute(new MapSqlParameterSource()
                .addValue("p_nombre_remitente", mensaje.getNombreRemitente())
                .addValue("p_correo_remitente", mensaje.getCorreoRemitente())
                .addValue("p_asunto", mensaje.getAsunto())
                .addValue("p_mensaje", mensaje.getMensaje()));
        return LlamadaBd.entero(salida.get("p_id_mensaje"));
    }

    public List<MensajeContacto> listar() {
        Map<String, Object> salida = spListar.execute();
        return LlamadaBd.lista(salida);
    }
}
