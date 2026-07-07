package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Service.CategoriaService;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategoriasConProductos();
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoria", new Categoria());
        return "catalogo/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria) {
        categoriaService.save(categoria);
        return "redirect:/catalogo/listado";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Integer id) {
        categoriaService.delete(id);
        return "redirect:/catalogo/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model) {
        Optional<Categoria> categoriaOpt = categoriaService.getCategoria(id);
        if (categoriaOpt.isEmpty()) {
            return "redirect:/catalogo/listado";
        }
        model.addAttribute("categoria", categoriaOpt.get());
        return "catalogo/modifica";
    }
}