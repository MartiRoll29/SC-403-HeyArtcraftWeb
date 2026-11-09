package HeyArtcraftWeb.Domain;

import java.math.BigDecimal;
import lombok.Data;

/**
 * Producto del catálogo. Sus campos se ajustarán a la BD en la Fase 4
 * (producto_tamano y producto_material).
 */
@Data
public class Producto {

    private Integer id;
    private String nombre;
    private BigDecimal precio;
    private String imagen;
    private String descripcion;
    private String tamanosDisponibles;
    private boolean personalizable = true;
    private boolean destacado = false;
    private Categoria categoria;
}
