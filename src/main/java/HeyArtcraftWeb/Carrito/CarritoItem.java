package HeyArtcraftWeb.Carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Producto personalizado agregado al carrito. Vive únicamente en la sesión
 * HTTP hasta que se confirma la compra (HU-15), momento en el que se
 * persiste como DetallePedido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem implements Serializable {

    private Integer productoId;
    private String nombreProducto;
    private String imagenProducto;;
    private String textoPersonalizado;
    private String tamanoSeleccionado;
    private String especificaciones;
    private BigDecimal precioUnitario;
}
