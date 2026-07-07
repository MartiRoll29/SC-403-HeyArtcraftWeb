package com.HeyArtCrafts.HeyArtcraftWeb.service;

import com.HeyArtCrafts.HeyArtcraftWeb.carrito.CarritoItem;
import com.HeyArtCrafts.HeyArtcraftWeb.carrito.CarritoSesion;
import com.HeyArtCrafts.HeyArtcraftWeb.model.DetallePedido;
import com.HeyArtCrafts.HeyArtcraftWeb.model.Pedido;
import com.HeyArtCrafts.HeyArtcraftWeb.repository.PedidoRepository;
import com.HeyArtCrafts.HeyArtcraftWeb.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PedidoService {

    public static final BigDecimal COSTO_ENVIO = new BigDecimal("2000.00");

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Autowired
    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Pedido confirmarCompra(CarritoSesion carrito, boolean incluyeEnvio) {
        BigDecimal subtotal = carrito.calcularSubtotal();
        BigDecimal costoEnvio = incluyeEnvio ? COSTO_ENVIO : BigDecimal.ZERO;

        Pedido pedido = new Pedido();
        pedido.setSubtotal(subtotal);
        pedido.setIncluyeEnvio(incluyeEnvio);
        pedido.setCostoEnvio(costoEnvio);
        pedido.setTotal(subtotal.add(costoEnvio));

        for (CarritoItem item : carrito.getItems()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(productoRepository.findById(item.getProductoId()).orElse(null));
            detalle.setNombreProducto(item.getNombreProducto());
            detalle.setTextoPersonalizado(item.getTextoPersonalizado());
            detalle.setTamanoSeleccionado(item.getTamanoSeleccionado());
            detalle.setEspecificaciones(item.getEspecificaciones());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            pedido.agregarDetalle(detalle);
        }

        Pedido guardado = pedidoRepository.save(pedido);
        carrito.vaciar();
        return guardado;
    }
}
