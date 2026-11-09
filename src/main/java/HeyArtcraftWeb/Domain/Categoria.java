package HeyArtcraftWeb.Domain;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

/**
 * Categoría del catálogo. Sus campos se ajustarán a la BD en la Fase 4.
 */
@Data
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String nombre;
    private List<Producto> productos;
}
