package HeyArtcraftWeb.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Página amigable cuando falla una consulta a la BD. Las operaciones de
 * guardado capturan el error en su servicio y muestran el mensaje en el
 * formulario; aquí llegan los listados.
 */
@ControllerAdvice
public class ErrorBdAdvice {

    private static final Logger log = LoggerFactory.getLogger(ErrorBdAdvice.class);

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String errorBd(DataAccessException excepcion) {
        log.error("Error al consultar la base de datos", excepcion);
        return "general/error_bd";
    }
}
