package HeyArtcraftWeb.Domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Módulo 6 - Carrito de Compras (HU-15): registro transaccional de cada
 * compra confirmada desde el carrito.
 */
@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
public class Pedido {

    /** Módulo 10 (HU-26): estado inicial, a la espera de ser atendido. */
    public static final String ESTADO_PENDIENTE = "CONFIRMADO";
    /** Módulo 10 (HU-27): el administrador ya atendió/entregó el pedido. */
    public static final String ESTADO_COMPLETADO = "COMPLETADO";
    /** Módulo 10 (HU-28): el administrador no procesará el pedido. */
    public static final String ESTADO_CANCELADO = "CANCELADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Módulo 7 (HU-18): dueño del pedido. Es nullable para no romper los
     * pedidos que ya existían antes de que hubiera cuentas de usuario.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    /** Módulo 7 (HU-18): null mientras el pedido aún no se entrega. */
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "incluye_envio", nullable = false)
    private boolean incluyeEnvio;

    @Column(name = "costo_envio", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, length = 30)
    private String estado = ESTADO_PENDIENTE;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetallePedido> detalles = new ArrayList<>();

    public void agregarDetalle(DetallePedido detalle) {
        detalle.setPedido(this);
        this.detalles.add(detalle);
    }
}
