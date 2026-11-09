package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Usuario;
import java.math.BigDecimal;
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
public class UsuarioRepository {

    private static final String NO_ENCONTRADO = "N/A";

    private static final RowMapper<Usuario> MAPEO = (rs, fila) -> {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setFechaRegistro(LlamadaBd.fecha(rs.getTimestamp("fecha_registro")));
        return usuario;
    };

    private final SimpleJdbcCall fnExisteCorreo;
    private final SimpleJdbcCall spInsertar;
    private final SimpleJdbcCall spActualizar;
    private final SimpleJdbcCall spCambiarContrasena;
    private final SimpleJdbcCall spEliminar;
    private final SimpleJdbcCall spListar;
    private final SimpleJdbcCall spObtener;
    private final SimpleJdbcCall spObtenerCorreo;

    public UsuarioRepository(JdbcTemplate jdbc) {
        fnExisteCorreo = LlamadaBd.funcionNumerica(jdbc, "fn_usuario_existe_correo",
                new SqlParameter("p_correo", Types.VARCHAR));

        spInsertar = LlamadaBd.procedimiento(jdbc, "sp_usuario_insertar",
                new SqlParameter("p_nombre", Types.VARCHAR),
                new SqlParameter("p_correo", Types.VARCHAR),
                new SqlParameter("p_contrasena", Types.VARCHAR),
                new SqlOutParameter("p_id_usuario", Types.NUMERIC));

        spActualizar = LlamadaBd.procedimiento(jdbc, "sp_usuario_actualizar",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_nombre", Types.VARCHAR),
                new SqlParameter("p_correo", Types.VARCHAR));

        spCambiarContrasena = LlamadaBd.procedimiento(jdbc, "sp_usuario_cambiar_contrasena",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlParameter("p_contrasena", Types.VARCHAR));

        spEliminar = LlamadaBd.procedimiento(jdbc, "sp_usuario_eliminar",
                new SqlParameter("p_id_usuario", Types.NUMERIC));

        spListar = LlamadaBd.procedimiento(jdbc, "sp_usuario_listar",
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO));

        spObtener = LlamadaBd.procedimiento(jdbc, "sp_usuario_obtener",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_nombre", Types.VARCHAR),
                new SqlOutParameter("p_correo", Types.VARCHAR),
                new SqlOutParameter("p_fecha_registro", Types.TIMESTAMP));

        spObtenerCorreo = LlamadaBd.procedimiento(jdbc, "sp_usuario_obtener_correo",
                new SqlParameter("p_correo", Types.VARCHAR),
                new SqlOutParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_nombre", Types.VARCHAR),
                new SqlOutParameter("p_contrasena", Types.VARCHAR));
    }

    public boolean existeCorreo(String correo) {
        BigDecimal resultado = fnExisteCorreo.executeFunction(BigDecimal.class,
                new MapSqlParameterSource("p_correo", correo));
        return LlamadaBd.esVerdadero(resultado);
    }

    /** Recibe la contraseña ya cifrada y devuelve el id del usuario nuevo. */
    public Integer insertar(String nombre, String correo, String contrasenaCifrada) {
        Map<String, Object> salida = spInsertar.execute(new MapSqlParameterSource()
                .addValue("p_nombre", nombre)
                .addValue("p_correo", correo)
                .addValue("p_contrasena", contrasenaCifrada));
        return LlamadaBd.entero(salida.get("p_id_usuario"));
    }

    public void actualizar(Integer idUsuario, String nombre, String correo) {
        spActualizar.execute(new MapSqlParameterSource()
                .addValue("p_id_usuario", idUsuario)
                .addValue("p_nombre", nombre)
                .addValue("p_correo", correo));
    }

    public void cambiarContrasena(Integer idUsuario, String contrasenaCifrada) {
        spCambiarContrasena.execute(new MapSqlParameterSource()
                .addValue("p_id_usuario", idUsuario)
                .addValue("p_contrasena", contrasenaCifrada));
    }

    public void eliminar(Integer idUsuario) {
        spEliminar.execute(new MapSqlParameterSource("p_id_usuario", idUsuario));
    }

    public List<Usuario> listar() {
        Map<String, Object> salida = spListar.execute();
        return LlamadaBd.lista(salida);
    }

    /** Sin la contraseña. Vacío si la BD devolvió 'N/A'. */
    public Optional<Usuario> obtener(Integer idUsuario) {
        Map<String, Object> salida = spObtener.execute(
                new MapSqlParameterSource("p_id_usuario", idUsuario));

        String correo = (String) salida.get("p_correo");
        if (correo == null || NO_ENCONTRADO.equals(correo)) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre((String) salida.get("p_nombre"));
        usuario.setCorreo(correo);
        usuario.setFechaRegistro(LlamadaBd.fecha(salida.get("p_fecha_registro")));
        return Optional.of(usuario);
    }

    /** Incluye el hash de la contraseña. Vacío si p_id_usuario llegó NULL. */
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        Map<String, Object> salida = spObtenerCorreo.execute(
                new MapSqlParameterSource("p_correo", correo));

        Integer idUsuario = LlamadaBd.entero(salida.get("p_id_usuario"));
        if (idUsuario == null) {
            return Optional.empty();
        }

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre((String) salida.get("p_nombre"));
        usuario.setCorreo(correo);
        usuario.setPassword((String) salida.get("p_contrasena"));
        return Optional.of(usuario);
    }
}
