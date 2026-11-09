package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Factura;
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
public class FacturaRepository {

    private static final RowMapper<Factura> MAPEO = (rs, fila) -> new Factura(
            rs.getInt("id_factura"),
            rs.getInt("id_pedido"),
            LlamadaBd.fecha(rs.getTimestamp("fecha_emision")),
            rs.getBigDecimal("subtotal"),
            rs.getBigDecimal("impuesto"),
            rs.getBigDecimal("monto_total"));

    private final SimpleJdbcCall spListarUsuario;

    public FacturaRepository(JdbcTemplate jdbc) {
        spListarUsuario = LlamadaBd.procedimiento(jdbc, "sp_factura_listar_usuario",
                new SqlParameter("p_id_usuario", Types.NUMERIC),
                new SqlOutParameter("p_cursor", OracleTypes.CURSOR, MAPEO));
    }

    public List<Factura> listarPorUsuario(Integer idUsuario) {
        Map<String, Object> salida = spListarUsuario.execute(
                new MapSqlParameterSource("p_id_usuario", idUsuario));
        return LlamadaBd.lista(salida);
    }
}
