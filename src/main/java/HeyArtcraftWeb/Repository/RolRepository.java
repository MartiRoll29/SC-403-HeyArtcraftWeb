package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Rol;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

@Repository
public class RolRepository {

    private static final RowMapper<Rol> MAPEO = (rs, fila)
            -> new Rol(rs.getInt("id_rol"), rs.getString("nombre_rol"));

    private final SimpleJdbcCall spListar;

    public RolRepository(JdbcTemplate jdbc) {
        spListar = LlamadaBd.procedimiento(jdbc, "sp_rol_listar",
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO));
    }

    public List<Rol> listar() {
        Map<String, Object> salida = spListar.execute();
        return LlamadaBd.lista(salida);
    }

    /** Busca un rol dentro de la lista que devuelve sp_rol_listar. */
    public Optional<Rol> buscarPorNombre(String nombreRol) {
        return listar().stream()
                .filter(rol -> rol.getNombreRol().equals(nombreRol))
                .findFirst();
    }
}
