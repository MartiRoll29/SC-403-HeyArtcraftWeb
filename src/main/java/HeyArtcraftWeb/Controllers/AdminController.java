package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Service.AdminService;
import HeyArtcraftWeb.Service.ContactoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Panel de administración. Solo rol ADMIN; ver ADMIN_URLS en SecurityConfig.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final ContactoService contactoService;

    public AdminController(AdminService adminService, ContactoService contactoService) {
        this.adminService = adminService;
        this.contactoService = contactoService;
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", adminService.usuariosConRoles());
        return "admin/usuarios";
    }

    @GetMapping("/mensajes")
    public String mensajes(Model model) {
        model.addAttribute("mensajes", contactoService.listar());
        return "admin/mensajes";
    }
}
