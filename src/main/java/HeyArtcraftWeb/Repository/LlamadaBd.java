package HeyArtcraftWeb.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

/**
 * Utilidades comunes para invocar procedimientos y funciones almacenados.
 * Toda la capa de datos pasa por aquí: Java nunca escribe SQL.
 *
 * Los parámetros se declaran a mano y deben ir en el mismo orden que en el
 * procedimiento de la BD.
 */
final class LlamadaBd {

    private LlamadaBd() {
    }

    /** Prepara la llamada a un procedimiento almacenado. */
    static SimpleJdbcCall procedimiento(JdbcTemplate jdbc, String nombre, SqlParameter... parametros) {
        return new SimpleJdbcCall(jdbc)
                .withProcedureName(nombre)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(parametros);
    }

    /**
     * Prepara la llamada a una función que devuelve NUMBER. El valor de
     * retorno debe declararse antes que los parámetros de entrada.
     */
    static SimpleJdbcCall funcionNumerica(JdbcTemplate jdbc, String nombre, SqlParameter... entradas) {
        return new SimpleJdbcCall(jdbc)
                .withFunctionName(nombre)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(new SqlOutParameter("resultado", Types.NUMERIC))
                .declareParameters(entradas);
    }

    /** Interpreta el resultado de las funciones que devuelven 1 = sí, 0 = no. */
    static boolean esVerdadero(BigDecimal resultado) {
        return resultado != null && resultado.intValue() == 1;
    }

    /** NUMBER llega como BigDecimal; NULL se conserva. */
    static Integer entero(Object valor) {
        return valor == null ? null : ((Number) valor).intValue();
    }

    /** DATE llega como Timestamp; NULL se conserva. */
    static LocalDateTime fecha(Object valor) {
        return valor == null ? null : ((Timestamp) valor).toLocalDateTime();
    }

    /** Extrae la lista que produjo el SYS_REFCURSOR del parámetro p_cursor. */
    @SuppressWarnings("unchecked")
    static <T> List<T> lista(Map<String, Object> salida) {
        return (List<T>) salida.get("p_cursor");
    }
}
