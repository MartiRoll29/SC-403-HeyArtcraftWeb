package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.UsuarioRol;
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
public class UsuarioRolRepository {

    private static final RowMapper<UsuarioRol> MAPEO_ASIGNACION = (rs, fila) -> new UsuarioRol(
            rs.getInt("id_usuario"),
            rs.getString("nombre"),
            rs.getInt("id_rol"),
            rs.getString("nombre_rol"));

    private static final RowMapper<Rol> MAPEO_ROL = (rs, fila)
            -> new Rol(rs.getInt("id_rol"), rs.getString("nombre_rol"));

    private final SimpleJdbcCall spInsertar;
    private final SimpleJdbcCall spListar;
    private final SimpleJdbcCall spListarUsuario;

    public UsuarioRolRepository(JdbcTemplate jdbc) {
        spInsertar = LlamadaBd.procedimiento(jdbc, "sp_usuario_rol_insertar",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_id_rol", Types.NUMERIC));

        spListar = LlamadaBd.procedimiento(jdbc, "sp_usuario_rol_listar",
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO_ASIGNACION));

        spListarUsuario = LlamadaBd.procedimiento(jdbc, "sp_usuario_rol_listar_usuario",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO_ROL));
    }

    public void insertar(Integer idUsuario, Integer idRol) {
        spInsertar.execute(new MapSqlParameterSource()
                .addValue("p_id_usuario", idUsuario)
                .addValue("p_id_rol", idRol));
    }

    public List<UsuarioRol> listar() {
        Map<String, Object> salida = spListar.execute();
        return LlamadaBd.lista(salida);
    }

    public List<Rol> listarPorUsuario(Integer idUsuario) {
        Map<String, Object> salida = spListarUsuario.execute(
                new MapSqlParameterSource("p_id_usuario", idUsuario));
        return LlamadaBd.lista(salida);
    }
}
