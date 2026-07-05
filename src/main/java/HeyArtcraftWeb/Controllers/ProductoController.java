package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Service.CategoriaService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import HeyArtcraftWeb.Service.ProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    public ProductoController(ProductoService productoService, CategoriaService categoriaService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        var productos = productoService.getProductos();
        var categorias = categoriaService.getCategoriasConProductos();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", new Producto()); // 👈 necesario para el formulario
        return "producto/listado";
    }
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto, @RequestParam("categoriaId") Integer categoriaId) {
        productoService.save(producto, categoriaId);
        return "redirect:/producto/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Integer id) {
        productoService.delete(id);
        return "redirect:/producto/listado";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model) {
        Optional<Producto> productoOpt = Optional.ofNullable(productoService.getProducto(id));
        if (productoOpt.isEmpty()) {
            return "redirect:/producto/listado";
        }
        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categoriaService.getCategoriasConProductos());
        return "producto/modifica";
    }
}