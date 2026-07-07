package com.HeyArtCrafts.HeyArtcraftWeb.controller;

import com.HeyArtCrafts.HeyArtcraftWeb.model.Producto;
import com.HeyArtCrafts.HeyArtcraftWeb.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Módulo 4: Catálogo (HU-10 y HU-11).
 */
@Controller
public class CatalogoController {

    private final ProductoService productoService;

    @Autowired
    public CatalogoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/catalogo";
    }

    // HU-10: Visualizar catálogo de productos
    @GetMapping("/catalogo")
    public String verCatalogo(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "catalogo/lista";
    }

    // HU-11: Visualizar producto específico
    @GetMapping("/catalogo/{id}")
    public String verProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
        model.addAttribute("producto", producto);
        return "catalogo/detalle";
    }
}
