package com.HeyArtCrafts.HeyArtcraftWeb.controller;

import com.HeyArtCrafts.HeyArtcraftWeb.carrito.CarritoItem;
import com.HeyArtCrafts.HeyArtcraftWeb.carrito.CarritoSesion;
import com.HeyArtCrafts.HeyArtcraftWeb.model.Pedido;
import com.HeyArtCrafts.HeyArtcraftWeb.model.Producto;
import com.HeyArtCrafts.HeyArtcraftWeb.service.PedidoService;
import com.HeyArtCrafts.HeyArtcraftWeb.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Módulo 6: Carrito de Compras (HU-13, HU-14 y HU-15).
 */
@Controller
public class CarritoController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final CarritoSesion carritoSesion;

    @Autowired
    public CarritoController(ProductoService productoService, PedidoService pedidoService, CarritoSesion carritoSesion) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.carritoSesion = carritoSesion;
    }

    // HU-13: Añadir producto al carrito
    @PostMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId,
                                    @RequestParam(required = false) String textoPersonalizado,
                                    @RequestParam(required = false) String tamanoSeleccionado,
                                    @RequestParam(required = false) String especificaciones) {
        Producto producto = productoService.buscarPorId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));

        CarritoItem item = new CarritoItem(
                producto.getId(),
                producto.getNombre(),
                producto.getImagenUrl(),
                textoPersonalizado,
                tamanoSeleccionado,
                especificaciones,
                producto.getPrecio());

        carritoSesion.agregar(item);
        return "redirect:/carrito";
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        model.addAttribute("items", carritoSesion.getItems());
        model.addAttribute("subtotal", carritoSesion.calcularSubtotal());
        model.addAttribute("costoEnvio", PedidoService.COSTO_ENVIO);
        return "carrito/ver";
    }

    // HU-14: Eliminar producto del carrito
    @PostMapping("/carrito/eliminar/{indice}")
    public String eliminarDelCarrito(@PathVariable int indice) {
        carritoSesion.eliminar(indice);
        return "redirect:/carrito";
    }

    // HU-15: Confirmar compra
    @PostMapping("/carrito/confirmar")
    public String confirmarCompra(@RequestParam(name = "incluyeEnvio", required = false) Boolean incluyeEnvio,
                                   Model model) {
        if (carritoSesion.estaVacio()) {
            return "redirect:/carrito";
        }

        Pedido pedido = pedidoService.confirmarCompra(carritoSesion, Boolean.TRUE.equals(incluyeEnvio));

        model.addAttribute("items", carritoSesion.getItems());
        model.addAttribute("subtotal", carritoSesion.calcularSubtotal());
        model.addAttribute("costoEnvio", PedidoService.COSTO_ENVIO);
        model.addAttribute("pedidoConfirmado", pedido);
        return "carrito/ver";
    }
}
