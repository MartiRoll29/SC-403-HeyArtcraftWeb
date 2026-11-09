package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Bloqueado hasta recibir las firmas del módulo Catálogo (Fase 4).
 * Ver docs/PROCEDIMIENTOS_PENDIENTES.md.
 */
@Repository
public class ProductoRepository {

    private static final String MODULO = "Catálogo";

    public Optional<Producto> findById(Integer id) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Producto> findAll() {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Producto> findByDestacadoTrue() {
        throw new ModuloPendienteException(MODULO);
    }

    public Producto saveAndFlush(Producto producto) {
        throw new ModuloPendienteException(MODULO);
    }
}
