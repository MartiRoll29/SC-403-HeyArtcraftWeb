package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Perfil del usuario. El nombre de usuario de Spring Security es el correo.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene al usuario conectado a partir del correo de la sesión.
     */
    public Usuario getUsuarioAutenticado() {

        Authentication autenticacion
                = SecurityContextHolder.getContext().getAuthentication();

        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || "anonymousUser".equals(autenticacion.getName())) {

            throw new IllegalStateException(
                    "No hay un usuario autenticado en la sesión"
            );
        }

        String correo = autenticacion.getName();

        return usuarioRepository.obtenerPorCorreo(correo)
                .orElseThrow(() -> new IllegalStateException(
                "Usuario autenticado no encontrado en la base de datos: "
                + correo
        ));
    }

    /**
     * Cambia la contraseña de un usuario local.
     */
    public void cambiarPassword(
            String correo,
            String actual,
            String nueva,
            String confirmacion) {

        Usuario usuario = usuarioRepository.obtenerPorCorreo(correo)
                .orElseThrow(() ->
                new IllegalArgumentException("usuario.error01"));

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new IllegalArgumentException(
                    "perfil.error.passwordActual"
            );
        }

        if (nueva == null || !nueva.equals(confirmacion)) {
            throw new IllegalArgumentException(
                    "perfil.error.passwordNoCoincide"
            );
        }

        if (nueva.length() < 6) {
            throw new IllegalArgumentException(
                    "perfil.error.passwordCorta"
            );
        }

        // La contraseña se guarda SIEMPRE cifrada.
        usuarioRepository.cambiarContrasena(
                usuario.getIdUsuario(), passwordEncoder.encode(nueva));
    }
}
