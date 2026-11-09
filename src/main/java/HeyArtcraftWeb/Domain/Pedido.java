package HeyArtcraftWeb.Domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Compra confirmada desde el carrito. Sus campos se ajustarán a la BD en la
 * Fase 4 (id_estado e id_direccion obligatorios).
 */
@Data
@NoArgsConstructor
public class Pedido {

    public static final String ESTADO_PENDIENTE = "CONFIRMADO";
    public static final String ESTADO_COMPLETADO = "COMPLETADO";
    public static final String ESTADO_CANCELADO = "CANCELADO";

    private Integer id;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime fechaEntrega;
    private BigDecimal subtotal;
    private boolean incluyeEnvio;
    private BigDecimal costoEnvio = BigDecimal.ZERO;
    private BigDecimal total;
    private String estado = ESTADO_PENDIENTE;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<DetallePedido> detalles = new ArrayList<>();

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }
}
