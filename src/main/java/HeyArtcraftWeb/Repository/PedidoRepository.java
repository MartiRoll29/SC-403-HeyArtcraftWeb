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
}
