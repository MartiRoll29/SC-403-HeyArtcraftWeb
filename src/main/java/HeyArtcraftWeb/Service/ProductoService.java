package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Categoria;
import HeyArtcraftWeb.Domain.Producto;
import HeyArtcraftWeb.Repository.CategoriaRepository;
import HeyArtcraftWeb.Repository.ProductoRepository;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public void save(Producto producto, MultipartFile imagenFile) {
        if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
            throw new IllegalArgumentException("El id de la categoría no fue enviado correctamente");
        }

        Integer categoriaId = producto.getCategoria().getId();
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        producto.setCategoria(categoria);

        // Validar y guardar imagen
        if (!imagenFile.isEmpty()) {
            String contentType = imagenFile.getContentType();
            if (contentType.equals("image/jpeg") || contentType.equals("image/png")) {
                try {
                    // Ruta absoluta a la carpeta static/img
                    String rutaBase = new File("src/main/resources/static/img").getAbsolutePath();
                    File destino = new File(rutaBase, imagenFile.getOriginalFilename());

                    // Crear carpeta si no existe
                    destino.getParentFile().mkdirs();

                    // Guardar archivo
                    imagenFile.transferTo(destino);

                    // Guardar solo el nombre del archivo en BD
                    producto.setImagen("/img/" + imagenFile.getOriginalFilename());
                } catch (IOException e) {
                    throw new RuntimeException("Error al guardar la imagen", e);
                }
            } else {
                throw new IllegalArgumentException("Solo se permiten imágenes JPG o PNG");
            }
        }
        productoRepository.save(producto);
    }

    public void delete(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        // ✅ Eliminar el producto completo de la BD
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public Producto getProducto(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Producto> getProductos() {
        return productoRepository.findAll();
    }
}