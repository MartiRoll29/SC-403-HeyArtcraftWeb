package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Si quieres buscar por nombre
    List<Categoria> findByNombreContaining(String nombre);
}