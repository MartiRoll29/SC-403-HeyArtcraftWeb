package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.productos")
    List<Categoria> findAllWithProductos();
}