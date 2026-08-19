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

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Busca un usuario por username o por correo electrónico.
     */
    private Optional<Usuario> buscarPorIdentificador(String identificador) {
        return usuarioRepository.findByUsername(identificador)
                .or(() -> usuarioRepository.findByCorreo(identificador));
    }

    /**
     * Obtiene un usuario por su username o correo.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return buscarPorIdentificador(username);
    }

    /**
     * Obtiene al usuario conectado, tanto si ingresó localmente como con Google.
     */
    @Transactional(readOnly = true)
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

        String identificador = autenticacion.getName();

        return buscarPorIdentificador(identificador)
                .orElseThrow(() -> new IllegalStateException(
                "Usuario autenticado no encontrado en la base de datos: "
                + identificador
        ));
    }

    /**
     * Cambia la contraseña de un usuario local.
     */
    @Transactional
    public void cambiarPassword(
            String identificador,
            String actual,
            String nueva,
            String confirmacion) {

        Usuario usuario = buscarPorIdentificador(identificador)
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
        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }
}