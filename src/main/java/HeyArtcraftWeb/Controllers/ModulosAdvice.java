package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Repository.ModuloPendienteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Publica en las vistas qué módulos están activos y convierte las llamadas a
 * un módulo aún no entregado en una página de aviso.
 */
@ControllerAdvice
public class ModulosAdvice {

    private final boolean catalogoActivo;
    private final boolean compraActivo;

    public ModulosAdvice(
            @Value("${app.modulos.catalogo.enabled:false}") boolean catalogoActivo,
            @Value("${app.modulos.compra.enabled:false}") boolean compraActivo) {
        this.catalogoActivo = catalogoActivo;
        this.compraActivo = compraActivo;
    }

    @ModelAttribute("catalogoActivo")
    public boolean catalogoActivo() {
        return catalogoActivo;
    }

    @ModelAttribute("compraActivo")
    public boolean compraActivo() {
        return compraActivo;
    }

    @ExceptionHandler(ModuloPendienteException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String moduloPendiente(ModuloPendienteException excepcion, Model model) {
        model.addAttribute("modulo", excepcion.getModulo());
        return "general/modulo_pendiente";
    }
}
