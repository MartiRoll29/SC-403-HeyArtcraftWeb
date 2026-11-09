package HeyArtcraftWeb.Domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Factura de un pedido (tabla factura).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    private Integer idFactura;
    private Integer idPedido;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal montoTotal;
}
