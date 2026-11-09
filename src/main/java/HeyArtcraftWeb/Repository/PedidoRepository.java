package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Bloqueado hasta recibir las firmas del módulo Compra (Fase 4).
 * Ver docs/PROCEDIMIENTOS_PENDIENTES.md.
 */
@Repository
public class PedidoRepository {

    private static final String MODULO = "Compra";

    public Optional<Pedido> findById(Integer id) {
        throw new ModuloPendienteException(MODULO);
    }

    public Pedido save(Pedido pedido) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario) {
        throw new ModuloPendienteException(MODULO);
    }

    public Optional<Pedido> findByIdConDetalle(Integer id) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Pedido> findByEstadoOrderByFechaCreacionAsc(String estado) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<String> estados) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Pedido> buscarEnHistorial(List<String> estados, String texto) {
        throw new ModuloPendienteException(MODULO);
    }
}
