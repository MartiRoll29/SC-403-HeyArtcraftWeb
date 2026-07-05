package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {
    private final CategoriaService categoriaService;

    public CatalogoController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategoriasConProductos();
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", new Producto());
        model.addAttribute("categoria", new Categoria());
        return "catalogo/listado";
    }
}