package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.UsuarioRolRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Puente entre la BD y Spring Security. El inicio de sesión se hace con el
 * correo y la contraseña se compara con el PasswordEncoder de SecurityConfig.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.obtenerPorCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + correo));

        // El prefijo ROLE_ es obligatorio para que hasRole() funcione.
        List<GrantedAuthority> autoridades = usuarioRolRepository
                .listarPorUsuario(usuario.getIdUsuario()).stream()
                .map(rol -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()))
                .toList();

        return new User(usuario.getCorreo(), usuario.getPassword(), autoridades);
    }
}
