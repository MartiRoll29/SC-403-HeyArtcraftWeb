package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Pedido;
import HeyArtcraftWeb.Domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // HU-18: historial del cliente, del pedido más reciente al más antiguo
    List<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    /**
     * HU-19: trae el pedido con su cliente y su detalle en una sola consulta.
     * El fetch join evita LazyInitializationException al armar el PDF fuera
     * del renderizado de la vista.
     */
    @Query("SELECT DISTINCT p FROM Pedido p "
            + "LEFT JOIN FETCH p.usuario u "
            + "LEFT JOIN FETCH p.detalles d "
            + "LEFT JOIN FETCH d.producto pr "
            + "WHERE p.id = :id")
    Optional<Pedido> findByIdConDetalle(@Param("id") Integer id);

    // HU-26: pedidos pendientes, del más antiguo al más reciente
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(String estado);

    // HU-29: historial (completados y cancelados), en orden ascendente
    List<Pedido> findByEstadoInOrderByFechaCreacionAsc(List<String> estados);

    /**
     * HU-30: busca dentro del historial por nombre/apellidos del cliente o
     * por nombre de la categoría de alguno de los productos del pedido. El
     * join de categoría solo se usa para filtrar (no lleva FETCH), así que
     * los detalles completos del pedido igual se cargan enteros gracias a
     * que la colección está mapeada como EAGER en Pedido.
     */
    @Query("SELECT DISTINCT p FROM Pedido p "
            + "LEFT JOIN p.usuario u "
            + "LEFT JOIN p.detalles d "
            + "LEFT JOIN d.producto pr "
            + "LEFT JOIN pr.categoria cat "
            + "WHERE p.estado IN :estados AND ("
            + "LOWER(CONCAT(u.nombre, ' ', u.apellidos)) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "OR LOWER(cat.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))) "
            + "ORDER BY p.fechaCreacion ASC")
    List<Pedido> buscarEnHistorial(@Param("estados") List<String> estados, @Param("texto") String texto);
}
