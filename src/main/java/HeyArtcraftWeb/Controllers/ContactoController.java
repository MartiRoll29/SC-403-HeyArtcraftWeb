package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.MensajeContacto;
import HeyArtcraftWeb.Service.ContactoService;
import jakarta.validation.Valid;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Formulario público de contacto: no requiere cuenta.
 */
@Controller
@RequestMapping("/contacto")
public class ContactoController {

    private final ContactoService contactoService;
    private final MessageSource messageSource;

    public ContactoController(ContactoService contactoService, MessageSource messageSource) {
        this.contactoService = contactoService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String formulario(Model model) {
        model.addAttribute("mensajeContacto", new MensajeContacto());
        return "contacto";
    }

    @PostMapping
    public String enviar(@Valid @ModelAttribute("mensajeContacto") MensajeContacto mensaje,
            BindingResult resultado, RedirectAttributes redirectAttributes) {

        if (resultado.hasErrors()) {
            return "contacto";
        }

        try {
            contactoService.enviar(mensaje);
        } catch (IllegalArgumentException e) {
            resultado.reject(e.getMessage());
            return "contacto";
        }

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("contacto.ok", null, Locale.getDefault()));
        return "redirect:/contacto";
    }
}
