package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Carrito.CarritoItem;
import HeyArtcraftWeb.Carrito.CarritoSesion;
import HeyArtcraftWeb.Domain.DetallePedido;
import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.PedidoRepository;
import HeyArtcraftWeb.Repository.ProductoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

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
    public Pedido confirmarCompra(CarritoSesion carrito, boolean incluyeEnvio, Usuario usuario) {
        BigDecimal subtotal = carrito.calcularSubtotal();
        BigDecimal costoEnvio = incluyeEnvio ? COSTO_ENVIO : BigDecimal.ZERO;

        Pedido pedido = new Pedido();
        // HU-18: el pedido queda asociado al cliente que lo confirmó
        pedido.setUsuario(usuario);
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

    // HU-18: pedidos del cliente conectado
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosDeUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario);
    }

    /**
     * HU-18 y HU-19: un pedido concreto del cliente. Verifica la pertenencia
     * para que nadie pueda ver ni descargar la factura de otro cliente solo
     * cambiando el número en la URL.
     */
    @Transactional(readOnly = true)
    public Pedido getPedidoDeUsuario(Integer idPedido, Usuario usuario) {
        Pedido pedido = pedidoRepository.findByIdConDetalle(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + idPedido));

        if (pedido.getUsuario() == null
                || !pedido.getUsuario().getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new AccessDeniedException("El pedido " + idPedido + " no pertenece al usuario autenticado");
        }
        return pedido;
    }
}
