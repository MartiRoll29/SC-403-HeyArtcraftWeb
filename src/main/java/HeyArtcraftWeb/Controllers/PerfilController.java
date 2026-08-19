package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Service.FacturaPdfService;
import HeyArtcraftWeb.Service.PedidoService;
import HeyArtcraftWeb.Service.UsuarioService;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;

/**
 * Módulo 7 - Perfil del Cliente (HU-16, HU-17, HU-18, HU-19 y HU-20).
 * Todas estas rutas exigen sesión iniciada; ver CLIENTE_URLS en SecurityConfig.
 */
@Controller
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final FacturaPdfService facturaPdfService;
    private final MessageSource messageSource;

    public PerfilController(UsuarioService usuarioService, PedidoService pedidoService,
            FacturaPdfService facturaPdfService, MessageSource messageSource) {
        this.usuarioService = usuarioService;
        this.pedidoService = pedidoService;
        this.facturaPdfService = facturaPdfService;
        this.messageSource = messageSource;
    }

    // HU-16: consultar la información de la cuenta
    @GetMapping({"", "/"})
    public String perfil(Model model) {
        model.addAttribute("usuario", usuarioService.getUsuarioAutenticado());
        return "perfil/perfil";
    }

    // HU-17: formulario de cambio de contraseña
    @GetMapping("/password")
    public String formPassword() {
        return "perfil/password";
    }

    // HU-17: procesa el cambio (Post/Redirect/Get)
    @PostMapping("/password")
    public String cambiarPassword(@RequestParam String passwordActual,
            @RequestParam String passwordNueva,
            @RequestParam String passwordConfirmacion,
            RedirectAttributes redirectAttributes) {

        Usuario usuario = usuarioService.getUsuarioAutenticado();
        try {
            usuarioService.cambiarPassword(usuario.getUsername(),
                    passwordActual, passwordNueva, passwordConfirmacion);

            redirectAttributes.addFlashAttribute("todoOk", traducir("perfil.password.ok"));
            return "redirect:/perfil";

        } catch (IllegalArgumentException e) {
            // e.getMessage() trae la clave i18n del error
            redirectAttributes.addFlashAttribute("error", traducir(e.getMessage()));
            return "redirect:/perfil/password";
        }
    }

    // HU-18: historial de pedidos del cliente
    @GetMapping("/pedidos")
    public String historial(Model model) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        model.addAttribute("pedidos", pedidoService.getPedidosDeUsuario(usuario));
        return "perfil/pedidos";
    }

    /**
     * HU-19: descarga la factura del pedido. getPedidoDeUsuario valida que el
     * pedido sea del cliente conectado, así que cambiar el id en la URL para
     * ver la factura de otro termina en acceso denegado.
     */
    @GetMapping("/pedidos/{id}/factura")
    public ResponseEntity<byte[]> descargarFactura(@PathVariable Integer id) {
        Usuario usuario = usuarioService.getUsuarioAutenticado();
        Pedido pedido = pedidoService.getPedidoDeUsuario(id, usuario);
        byte[] pdf = facturaPdfService.generarFactura(pedido);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"factura-" + pedido.getId() + ".pdf\"")
                .body(pdf);
    }

    private String traducir(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.getDefault());
    }
}
