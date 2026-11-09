package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Perfil del usuario. Los errores se lanzan como IllegalArgumentException con
 * la clave del mensaje que debe ver el usuario.
 */
@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    /** BCrypt solo admite contraseñas de hasta 72 bytes. */
    private static final int MAXIMO_BYTES_BCRYPT = 72;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtiene al usuario conectado con sp_usuario_obtener, usando el id que
     * se guardó en la sesión. No incluye la contraseña.
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

        // Con Google la sesión no trae el id: solo el correo.
        Optional<Usuario> usuario = autenticacion.getPrincipal() instanceof UsuarioDetails detalles
                ? usuarioRepository.obtener(detalles.getIdUsuario())
                : usuarioRepository.obtenerPorCorreo(autenticacion.getName());

        return usuario.orElseThrow(() -> new IllegalStateException(
                "Usuario autenticado no encontrado en la base de datos"));
    }

    /** Id del usuario conectado, sin consultar la BD cuando la sesión ya lo tiene. */
    public Integer getIdUsuarioAutenticado() {
        Authentication autenticacion
                = SecurityContextHolder.getContext().getAuthentication();

        return autenticacion != null && autenticacion.getPrincipal() instanceof UsuarioDetails detalles
                ? detalles.getIdUsuario()
                : getUsuarioAutenticado().getIdUsuario();
    }

    /**
     * Actualiza nombre y correo.
     *
     * @return true si cambió el correo, que es el dato con el que se inicia sesión
     */
    public boolean actualizarDatos(Usuario actual, String nombre, String correo) {
        String nombreNuevo = nombre.trim();
        String correoNuevo = correo.trim();

        try {
            // La BD compara el login sin distinguir mayúsculas, pero su llave
            // única sí las distingue: hay que descartar el duplicado antes.
            if (!correoNuevo.equalsIgnoreCase(actual.getCorreo())
                    && usuarioRepository.existeCorreo(correoNuevo)) {
                throw new IllegalArgumentException("usuario.error.correoDuplicado");
            }

            usuarioRepository.actualizar(actual.getIdUsuario(), nombreNuevo, correoNuevo);

        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("usuario.error.correoDuplicado");
        } catch (DataAccessException e) {
            log.error("No se pudieron actualizar los datos del usuario", e);
            throw new IllegalArgumentException("perfil.error.bd");
        }

        return !correoNuevo.equals(actual.getCorreo());
    }

    /**
     * Cambia la contraseña de un usuario local.
     */
    public void cambiarPassword(
            String correo,
            String actual,
            String nueva,
            String confirmacion) {

        try {
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

            if (nueva.getBytes(StandardCharsets.UTF_8).length > MAXIMO_BYTES_BCRYPT) {
                throw new IllegalArgumentException(
                        "perfil.error.passwordLarga"
                );
            }

            // La contraseña se guarda SIEMPRE cifrada.
            usuarioRepository.cambiarContrasena(
                    usuario.getIdUsuario(), passwordEncoder.encode(nueva));

        } catch (DataAccessException e) {
            log.error("No se pudo cambiar la contraseña", e);
            throw new IllegalArgumentException("perfil.error.bd");
        }
    }
}
