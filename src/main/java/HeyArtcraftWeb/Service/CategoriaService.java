package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public Optional<Categoria> getCategoria(Integer id) {
        return categoriaRepository.findById(id);
    }

    public List<Categoria> getCategorias() {
        return categoriaRepository.findAll();
    }

    public List<Categoria> getCategoriasConProductos() {
        return categoriaRepository.findAllWithProductos();
    }

    public void save(Categoria categoria) {
        categoriaRepository.save(categoria);
    }

    public void delete(Integer id) {
        categoriaRepository.deleteById(id);
    }
}