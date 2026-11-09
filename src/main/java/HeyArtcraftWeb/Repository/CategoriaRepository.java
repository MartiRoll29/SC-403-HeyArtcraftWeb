package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Bloqueado hasta recibir las firmas del módulo Catálogo (Fase 4).
 * Ver docs/PROCEDIMIENTOS_PENDIENTES.md.
 */
@Repository
public class CategoriaRepository {

    private static final String MODULO = "Catálogo";

    public Optional<Categoria> findById(Integer id) {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Categoria> findAll() {
        throw new ModuloPendienteException(MODULO);
    }

    public List<Categoria> findAllWithProductos() {
        throw new ModuloPendienteException(MODULO);
    }

    public Categoria save(Categoria categoria) {
        throw new ModuloPendienteException(MODULO);
    }

    public void deleteById(Integer id) {
        throw new ModuloPendienteException(MODULO);
    }
}
