package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Service.PedidoService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Módulo 10 - Gestión de Pedidos y Módulo 11 - Historial de Pedidos
 * (Administrador): HU-26 a HU-30. Todas estas rutas exigen rol ADMIN;
 * ver ADMIN_URLS en SecurityConfig.
 */
@Controller
@RequestMapping("/pedidos/admin")
public class PedidoAdminController {

    private final PedidoService pedidoService;
    private final MessageSource messageSource;

    public PedidoAdminController(PedidoService pedidoService, MessageSource messageSource) {
        this.pedidoService = pedidoService;
        this.messageSource = messageSource;
    }

    // HU-26: pedidos pendientes, del más antiguo al más reciente
    @GetMapping({"", "/"})
    public String pendientes(Model model) {
        model.addAttribute("pedidos", pedidoService.getPedidosPendientes());
        return "pedidosAdmin/pendientes";
    }

    // HU-27: marca un pedido como completado
    @PostMapping("/{id}/completar")
    public String completar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.completarPedido(id);
            redirectAttributes.addFlashAttribute("todoOk", traducir("pedido.admin.completado.ok"));
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", traducir("pedido.admin.error.noEncontrado"));
        }
        return "redirect:/pedidos/admin";
    }

    // HU-28: cancela un pedido recibido (el modal de confirmación vive en la vista)
    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(id);
            redirectAttributes.addFlashAttribute("todoOk", traducir("pedido.admin.cancelado.ok"));
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("error", traducir("pedido.admin.error.noEncontrado"));
        }
        return "redirect:/pedidos/admin";
    }

    // HU-29 y HU-30: historial de pedidos procesados, con búsqueda opcional por cliente o categoría
    @GetMapping("/historial")
    public String historial(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("pedidos", pedidoService.buscarEnHistorial(q));
        model.addAttribute("q", q);
        return "pedidosAdmin/historial";
    }

    private String traducir(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.getDefault());
    }
}
