package HeyArtcraftWeb.Controllers;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Service.CategoriaService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import HeyArtcraftWeb.Service.ProductoService;
import org.springframework.web.multipart.MultipartFile;

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
        Producto producto = new Producto();
        producto.setCategoria(new Categoria());
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("producto", producto);
        return "/catalogo/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
            @RequestParam("imagenFile") MultipartFile imagenFile) {
        productoService.save(producto, imagenFile);
        return "redirect:/catalogo/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("id") Integer id) {
        productoService.delete(id);
        return "redirect:/catalogo";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable("id") Integer id, Model model) {
        Optional<Producto> productoOpt = Optional.ofNullable(productoService.getProducto(id));
        if (productoOpt.isEmpty()) {
            return "redirect:/catalogo";
        }
        model.addAttribute("producto", productoOpt.get());
        model.addAttribute("categorias", categoriaService.getCategoriasConProductos());
        return "catalogo/modifica";
    }
}