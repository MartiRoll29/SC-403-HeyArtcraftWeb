package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Repository.CategoriaRepository;
import HeyArtcraftWeb.Repository.ProductoRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private static final Logger LOGGER
            = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FirebaseStorageService firebaseStorageService;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            FirebaseStorageService firebaseStorageService) {

        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.firebaseStorageService = firebaseStorageService;
    }

    @Transactional
    public void save(Producto producto, MultipartFile imagenFile) {

        if (producto.getCategoria() == null
                || producto.getCategoria().getId() == null) {
            throw new IllegalArgumentException(
                    "El id de la categoría no fue enviado correctamente");
        }

        Integer categoriaId = producto.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException(
                "Categoría no encontrada"));

        producto.setCategoria(categoria);

        String imagenAnterior = null;

        if (producto.getId() != null) {
            Producto productoExistente
                    = productoRepository.findById(producto.getId())
                            .orElseThrow(() -> new IllegalArgumentException(
                            "Producto no encontrado"));

            imagenAnterior = productoExistente.getImagen();
        }

        boolean tieneNuevaImagen
                = imagenFile != null && !imagenFile.isEmpty();

        if (!tieneNuevaImagen) {
            if (producto.getId() == null) {
                throw new IllegalArgumentException(
                        "Debe seleccionar una imagen");
            }

            producto.setImagen(imagenAnterior);
            productoRepository.saveAndFlush(producto);
            return;
        }

        String nuevaImagen
                = firebaseStorageService.subirImagen(imagenFile);

        producto.setImagen(nuevaImagen);

        try {
            productoRepository.saveAndFlush(producto);
        } catch (RuntimeException e) {
            eliminarImagenSinInterrumpir(nuevaImagen);
            throw e;
        }

        if (imagenAnterior != null
                && !imagenAnterior.equals(nuevaImagen)) {
            eliminarImagenSinInterrumpir(imagenAnterior);
        }
    }

    @Transactional
    public void delete(Integer idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElse(null);

        if (producto == null) {
            LOGGER.warn(
                    "Se intentó eliminar el producto {}, pero ya no existe",
                    idProducto);
            return;
        }
    }

    @Transactional(readOnly = true)
    public Producto getProducto(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }

    // HU-09: Visualizar productos destacados
    @Transactional(readOnly = true)
    public List<Producto> getProductosDestacados() {
        return productoRepository.findByDestacadoTrue();
    }

    private void eliminarImagenSinInterrumpir(String imagenUrl) {
        try {
            firebaseStorageService.eliminarImagen(imagenUrl);
        } catch (RuntimeException e) {
            LOGGER.warn(
                    "No se pudo eliminar la imagen anterior de Firebase",
                    e);
        }
    }
}