package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Carrito.CarritoItem;
import HeyArtcraftWeb.Carrito.CarritoSesion;
import HeyArtcraftWeb.Domain.DetallePedido;
import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Repository.PedidoRepository;
import HeyArtcraftWeb.Repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PedidoService {

    public static final BigDecimal COSTO_ENVIO = new BigDecimal("2000.00");

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

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
