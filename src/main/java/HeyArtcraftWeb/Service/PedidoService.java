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
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PedidoService {

    public static final BigDecimal COSTO_ENVIO = new BigDecimal("2000.00");

    // Módulo 10 y 11 (HU-29, HU-30): estados que ya fueron procesados por el administrador
    private static final List<String> ESTADOS_HISTORIAL = List.of(Pedido.ESTADO_COMPLETADO, Pedido.ESTADO_CANCELADO);

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

    // HU-26: pedidos aún no atendidos, del más antiguo al más reciente
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosPendientes() {
        return pedidoRepository.findByEstadoOrderByFechaCreacionAsc(Pedido.ESTADO_PENDIENTE);
    }

    /**
     * HU-27: marca el pedido como completado. También registra la fecha de
     * entrega, que hasta ahora quedaba siempre en null (Módulo 7 ya la
     * mostraba en el perfil del cliente como "Pendiente" mientras no tuviera
     * valor).
     */
    @Transactional
    public Pedido completarPedido(Integer idPedido) {
        Pedido pedido = obtenerPedido(idPedido);
        pedido.setEstado(Pedido.ESTADO_COMPLETADO);
        pedido.setFechaEntrega(LocalDateTime.now());
        return pedidoRepository.save(pedido);
    }

    // HU-28: cancela un pedido que no podrá ser procesado
    @Transactional
    public Pedido cancelarPedido(Integer idPedido) {
        Pedido pedido = obtenerPedido(idPedido);
        pedido.setEstado(Pedido.ESTADO_CANCELADO);
        return pedidoRepository.save(pedido);
    }

    // HU-29: historial de pedidos procesados (completados y cancelados), ascendente
    @Transactional(readOnly = true)
    public List<Pedido> getHistorialPedidos() {
        return pedidoRepository.findByEstadoInOrderByFechaCreacionAsc(ESTADOS_HISTORIAL);
    }

    // HU-30: busca dentro del historial por cliente o categoría; sin texto, devuelve todo el historial
    @Transactional(readOnly = true)
    public List<Pedido> buscarEnHistorial(String texto) {
        if (texto == null || texto.isBlank()) {
            return getHistorialPedidos();
        }
        return pedidoRepository.buscarEnHistorial(ESTADOS_HISTORIAL, texto.trim());
    }

    private Pedido obtenerPedido(Integer idPedido) {
        return pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado: " + idPedido));
    }
}
