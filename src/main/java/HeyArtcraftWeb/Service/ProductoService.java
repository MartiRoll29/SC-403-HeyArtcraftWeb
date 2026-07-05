package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaService categoriaService;

    public ProductoService(ProductoRepository productoRepository, CategoriaService categoriaService) {
        this.productoRepository = productoRepository;
        this.categoriaService = categoriaService;
    }

    @Transactional
    public void save(Producto producto, Integer categoriaId) {
        // Asignar la categoría al producto antes de guardar
        categoriaService.getCategoria(categoriaId).ifPresent(producto::setCategoria);
        productoRepository.save(producto);
    }

    @Transactional
    public void delete(Integer id) {
        productoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Producto getProducto(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public java.util.List<Producto> getProductos() {
        return productoRepository.findAll();
    }
}