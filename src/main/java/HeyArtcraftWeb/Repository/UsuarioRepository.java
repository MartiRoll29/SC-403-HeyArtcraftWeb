package HeyArtcraftWeb.Repository;

import HeyArtcraftWeb.Domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsername(String username);

    /** Solo las cuentas activas pueden iniciar sesión. */
    Optional<Usuario> findByUsernameAndActivoTrue(String username);

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByUsernameOrCorreo(String username, String correo);
}
