package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Service.CategoriaService;
import HeyArtcraftWeb.Service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {
    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    public CatalogoController(CategoriaService categoriaService, ProductoService productoService) {
        this.categoriaService = categoriaService;
        this.productoService = productoService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var categorias = categoriaService.getCategoriasConProductos();
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", new Producto());
        model.addAttribute("categoria", new Categoria());
        return "catalogo/listado";
    }

    // HU-10: Visualizar catálogo de productos (vista de cliente)
    @GetMapping
    public String catalogoCliente(Model model) {
        model.addAttribute("productos", productoService.getProductos());
        return "catalogo/lista";
    }

    // HU-11: Visualizar producto específico
    @GetMapping("/{id}")
    public String detalleProducto(@PathVariable Integer id, Model model) {
        Producto producto = productoService.getProducto(id);
        if (producto == null) {
            return "redirect:/catalogo";
        }
        model.addAttribute("producto", producto);
        return "catalogo/detalle";
    }
}