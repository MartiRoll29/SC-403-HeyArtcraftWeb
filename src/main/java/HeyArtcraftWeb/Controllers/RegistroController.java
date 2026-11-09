package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult resultado) {
        // La contraseña solo es obligatoria aquí, no al editar el perfil.
        if (usuario.getPassword() == null) {
            resultado.rejectValue("password", "perfil.error.passwordCorta");
        }

        if (resultado.hasErrors()) {
            return "registro";
        }

        try {
            registroService.registrarNuevoUsuario(usuario);
        } catch (IllegalArgumentException e) {
            resultado.reject(e.getMessage());
            return "registro";
        }

        return "redirect:/login?registrado";
    }
}
