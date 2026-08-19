package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Service.RegistroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    private final RegistroService registroService;

    public RegistroController(RegistroService registroService) {
        this.registroService = registroService;
    }

    // Mostrar formulario
    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro"; 
    }

    // Procesar registro
    @PostMapping
    public String registrar(@ModelAttribute Usuario usuario) {
        registroService.registrarNuevoUsuario(usuario);
        return "redirect:/login?registrado";
    }
}