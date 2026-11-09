package HeyArtcraftWeb.Domain;

import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Línea de un pedido. Sus campos se ajustarán a la BD en la Fase 4
 * (cantidad, id_tamano e id_material).
 */
@Data
@NoArgsConstructor
public class DetallePedido {

    private Integer id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Pedido pedido;

    private Producto producto;
    private String nombreProducto;
    private String textoPersonalizado;
    private String tamanoSeleccionado;
    private String especificaciones;
    private BigDecimal precioUnitario;
}
