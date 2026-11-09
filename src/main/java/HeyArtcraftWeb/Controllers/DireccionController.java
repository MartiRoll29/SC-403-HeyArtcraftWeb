package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Direccion;
import HeyArtcraftWeb.Service.DireccionService;
import HeyArtcraftWeb.Service.UsuarioService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * "Mis direcciones". El dueño siempre sale de la sesión, nunca del formulario.
 */
@Controller
@RequestMapping("/perfil/direcciones")
public class DireccionController {

    private static final String LISTADO = "redirect:/perfil/direcciones";
    private static final String FORMULARIO = "perfil/direccion_form";

    private final DireccionService direccionService;
    private final UsuarioService usuarioService;
    private final MessageSource messageSource;

    public DireccionController(DireccionService direccionService,
            UsuarioService usuarioService, MessageSource messageSource) {
        this.direccionService = direccionService;
        this.usuarioService = usuarioService;
        this.messageSource = messageSource;
    }

    @GetMapping({"", "/"})
    public String listar(Model model) {
        model.addAttribute("direcciones",
                direccionService.listarDelUsuario(usuarioService.getIdUsuarioAutenticado()));
        return "perfil/direcciones";
    }

    @GetMapping("/nueva")
    public String formNueva(Model model) {
        model.addAttribute("direccion", new Direccion());
        return FORMULARIO;
    }

    @PostMapping("/nueva")
    public String crear(@Valid @ModelAttribute("direccion") Direccion direccion,
            BindingResult resultado, RedirectAttributes redirectAttributes) {

        direccion.setIdDireccion(null);
        if (resultado.hasErrors()) {
            return FORMULARIO;
        }

        try {
            direccionService.crear(direccion, usuarioService.getIdUsuarioAutenticado());
        } catch (IllegalArgumentException e) {
            resultado.reject(e.getMessage());
            return FORMULARIO;
        }

        redirectAttributes.addFlashAttribute("todoOk", traducir("direccion.ok.creada"));
        return LISTADO;
    }

    @GetMapping("/{id}/editar")
    public String formEditar(@PathVariable Integer id, Model model) {
        model.addAttribute("direccion",
                direccionService.obtenerPropia(id, usuarioService.getIdUsuarioAutenticado()));
        return FORMULARIO;
    }

    @PostMapping("/{id}/editar")
    public String editar(@PathVariable Integer id,
            @Valid @ModelAttribute("direccion") Direccion direccion,
            BindingResult resultado, RedirectAttributes redirectAttributes) {

        direccion.setIdDireccion(id);
        if (resultado.hasErrors()) {
            return FORMULARIO;
        }

        try {
            direccionService.actualizar(direccion, usuarioService.getIdUsuarioAutenticado());
        } catch (IllegalArgumentException e) {
            resultado.reject(e.getMessage());
            return FORMULARIO;
        }

        redirectAttributes.addFlashAttribute("todoOk", traducir("direccion.ok.actualizada"));
        return LISTADO;
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            direccionService.eliminar(id, usuarioService.getIdUsuarioAutenticado());
            redirectAttributes.addFlashAttribute("todoOk", traducir("direccion.ok.eliminada"));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", traducir(e.getMessage()));
        }
        return LISTADO;
    }

    private String traducir(String clave) {
        return messageSource.getMessage(clave, null, clave, Locale.getDefault());
    }
}
