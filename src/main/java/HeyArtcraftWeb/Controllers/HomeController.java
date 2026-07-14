package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoService productoService;

    public HomeController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // HU-06: Visualizar Hero principal de productos personalizados
    // HU-09: Visualizar productos destacados
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("productosDestacados", productoService.getProductosDestacados());
        return "index";
    }
}
