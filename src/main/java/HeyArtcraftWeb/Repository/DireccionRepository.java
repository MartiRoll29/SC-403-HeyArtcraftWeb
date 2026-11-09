package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Direccion;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class DireccionRepository {

    private static final RowMapper<Direccion> MAPEO = (rs, fila) -> {
        Direccion direccion = new Direccion();
        direccion.setIdDireccion(rs.getInt("id_direccion"));
        direccion.setProvincia(rs.getString("provincia"));
        direccion.setCanton(rs.getString("canton"));
        direccion.setDistrito(rs.getString("distrito"));
        direccion.setSenasExactas(rs.getString("senas_exactas"));
        direccion.setCodigoPostal(rs.getString("codigo_postal"));
        return direccion;
    };

    private final SimpleJdbcCall spInsertar;
    private final SimpleJdbcCall spActualizar;
    private final SimpleJdbcCall spEliminar;
    private final SimpleJdbcCall spObtener;
    private final SimpleJdbcCall spListarUsuario;

    public DireccionRepository(JdbcTemplate jdbc) {
        spInsertar = LlamadaBd.procedimiento(jdbc, "sp_direccion_insertar",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_provincia", Types.VARCHAR),
                new SqlParameter("p_canton", Types.VARCHAR),
                new SqlParameter("p_distrito", Types.VARCHAR),
                new SqlParameter("p_senas_exactas", Types.VARCHAR),
                new SqlParameter("p_codigo_postal", Types.VARCHAR),
                new SqlOutParameter("p_id_direccion", Types.NUMERIC));

        spActualizar = LlamadaBd.procedimiento(jdbc, "sp_direccion_actualizar",
                new SqlParameter("p_id_direccion", Types.NUMERIC),
                new SqlParameter("p_provincia", Types.VARCHAR),
                new SqlParameter("p_canton", Types.VARCHAR),
                new SqlParameter("p_distrito", Types.VARCHAR),
                new SqlParameter("p_senas_exactas", Types.VARCHAR),
                new SqlParameter("p_codigo_postal", Types.VARCHAR));

        spEliminar = LlamadaBd.procedimiento(jdbc, "sp_direccion_eliminar",
                new SqlParameter("p_id_direccion", Types.NUMERIC));

        spObtener = LlamadaBd.procedimiento(jdbc, "sp_direccion_obtener",
                new SqlParameter("p_id_direccion", Types.NUMERIC),
                new SqlOutParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_provincia", Types.VARCHAR),
                new SqlOutParameter("p_canton", Types.VARCHAR),
                new SqlOutParameter("p_distrito", Types.VARCHAR),
                new SqlOutParameter("p_senas_exactas", Types.VARCHAR),
                new SqlOutParameter("p_codigo_postal", Types.VARCHAR));

        spListarUsuario = LlamadaBd.procedimiento(jdbc, "sp_direccion_listar_usuario",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO));
    }

    public Integer insertar(Direccion direccion) {
        Map<String, Object> salida = spInsertar.execute(new MapSqlParameterSource()
                .addValue("p_id_usuario", direccion.getIdUsuario())
                .addValue("p_provincia", direccion.getProvincia())
                .addValue("p_canton", direccion.getCanton())
                .addValue("p_distrito", direccion.getDistrito())
                .addValue("p_senas_exactas", direccion.getSenasExactas())
                .addValue("p_codigo_postal", direccion.getCodigoPostal()));
        return LlamadaBd.entero(salida.get("p_id_direccion"));
    }

    public void actualizar(Direccion direccion) {
        spActualizar.execute(new MapSqlParameterSource()
                .addValue("p_id_direccion", direccion.getIdDireccion())
                .addValue("p_provincia", direccion.getProvincia())
                .addValue("p_canton", direccion.getCanton())
                .addValue("p_distrito", direccion.getDistrito())
                .addValue("p_senas_exactas", direccion.getSenasExactas())
                .addValue("p_codigo_postal", direccion.getCodigoPostal()));
    }

    public void eliminar(Integer idDireccion) {
        spEliminar.execute(new MapSqlParameterSource("p_id_direccion", idDireccion));
    }

    /** Vacío si p_id_usuario llegó NULL (la dirección no existe). */
    public Optional<Direccion> obtener(Integer idDireccion) {
        Map<String, Object> salida = spObtener.execute(
                new MapSqlParameterSource("p_id_direccion", idDireccion));

        Integer idUsuario = LlamadaBd.entero(salida.get("p_id_usuario"));
        if (idUsuario == null) {
            return Optional.empty();
        }

        Direccion direccion = new Direccion();
        direccion.setIdDireccion(idDireccion);
        direccion.setIdUsuario(idUsuario);
        direccion.setProvincia((String) salida.get("p_provincia"));
        direccion.setCanton((String) salida.get("p_canton"));
        direccion.setDistrito((String) salida.get("p_distrito"));
        direccion.setSenasExactas((String) salida.get("p_senas_exactas"));
        direccion.setCodigoPostal((String) salida.get("p_codigo_postal"));
        return Optional.of(direccion);
    }

    public List<Direccion> listarPorUsuario(Integer idUsuario) {
        Map<String, Object> salida = spListarUsuario.execute(
                new MapSqlParameterSource("p_id_usuario", idUsuario));
        return LlamadaBd.lista(salida);
    }
}
