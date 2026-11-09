package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Service.ProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductoService productoService;
    private final boolean catalogoActivo;

    public HomeController(ProductoService productoService,
            @Value("${app.modulos.catalogo.enabled:false}") boolean catalogoActivo) {
        this.productoService = productoService;
        this.catalogoActivo = catalogoActivo;
    }

    // HU-06: Visualizar Hero principal de productos personalizados
    // HU-09: Visualizar productos destacados (requiere el módulo Catálogo)
    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("productosDestacados",
                catalogoActivo ? productoService.getProductosDestacados() : List.of());
        return "index";
    }
}
