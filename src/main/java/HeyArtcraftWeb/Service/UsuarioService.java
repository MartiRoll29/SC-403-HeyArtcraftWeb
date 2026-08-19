package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

/**
 * Módulo 7 - Perfil del Cliente (HU-16 y HU-17).
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // HU-16: datos de la cuenta del cliente conectado
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Usuario conectado en la sesión actual. Lo usan el perfil (HU-16 a HU-19)
     * y la confirmación de compra (HU-15), para que todo pedido quede con dueño.
     */
    @Transactional(readOnly = true)
    public Usuario getUsuarioAutenticado() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        if (autenticacion == null || !autenticacion.isAuthenticated()) {
            throw new IllegalStateException("No hay un usuario autenticado en la sesión");
        }
        return usuarioRepository.findByUsername(autenticacion.getName())
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado no encontrado en BD: " + autenticacion.getName()));
    }

    /**
     * HU-17: cambia la contraseña del usuario validando que la actual sea
     * correcta, que la nueva coincida con su confirmación y que tenga la
     * longitud mínima. Los mensajes de error son CLAVES de messages.properties
     * para que el controlador las traduzca.
     */
    @Transactional
    public void cambiarPassword(String username, String actual, String nueva, String confirmacion) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("usuario.error01"));

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new IllegalArgumentException("perfil.error.passwordActual");
        }
        if (nueva == null || !nueva.equals(confirmacion)) {
            throw new IllegalArgumentException("perfil.error.passwordNoCoincide");
        }
        if (nueva.length() < 6) {
            throw new IllegalArgumentException("perfil.error.passwordCorta");
        }

        // La contraseña se guarda SIEMPRE cifrada.
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }
}
