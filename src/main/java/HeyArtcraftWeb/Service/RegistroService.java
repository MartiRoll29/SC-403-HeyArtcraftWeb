package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.UsuarioRolRepository;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Registro de clientes. Los errores se lanzan como IllegalArgumentException
 * con la clave del mensaje que debe ver el usuario.
 */
@Service
public class RegistroService {

    private static final Logger log = LoggerFactory.getLogger(RegistroService.class);

    /** BCrypt solo admite contraseñas de hasta 72 bytes. */
    private static final int MAXIMO_BYTES_BCRYPT = 72;

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<CorreoService> correoService;

    public RegistroService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            UsuarioRolRepository usuarioRolRepository,
            PasswordEncoder passwordEncoder,
            ObjectProvider<CorreoService> correoService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    public void registrarNuevoUsuario(Usuario usuario) {
        String nombre = usuario.getNombre().trim();
        String correo = usuario.getCorreo().trim();

        if (usuario.getPassword().getBytes(StandardCharsets.UTF_8).length > MAXIMO_BYTES_BCRYPT) {
            throw new IllegalArgumentException("perfil.error.passwordLarga");
        }

        try {
            if (usuarioRepository.existeCorreo(correo)) {
                throw new IllegalArgumentException("usuario.error.correoDuplicado");
            }

            Rol rolCliente = rolRepository.buscarPorNombre("CLIENTE")
                    .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no existe en BD"));

            Integer idUsuario = usuarioRepository.insertar(
                    nombre, correo, passwordEncoder.encode(usuario.getPassword()));

            asignarRol(idUsuario, rolCliente.getIdRol());

        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("usuario.error.correoDuplicado");
        } catch (DataAccessException e) {
            log.error("No se pudo registrar al usuario", e);
            throw new IllegalArgumentException("registro.error");
        }

        enviarBienvenida(correo, nombre);
    }

    /**
     * Cada procedimiento hace su propio COMMIT: si falla la asignación del
     * rol, el usuario ya quedó guardado y hay que borrarlo para no dejar una
     * cuenta sin rol.
     */
    private void asignarRol(Integer idUsuario, Integer idRol) {
        try {
            usuarioRolRepository.insertar(idUsuario, idRol);
        } catch (DataAccessException e) {
            try {
                usuarioRepository.eliminar(idUsuario);
            } catch (DataAccessException errorAlBorrar) {
                log.error("Quedó el usuario {} sin rol y no se pudo borrar", idUsuario, errorAlBorrar);
            }
            throw e;
        }
    }

    private void enviarBienvenida(String correo, String nombre) {
        try {
            correoService.ifAvailable(servicio -> servicio.enviarBienvenida(correo, nombre));
        } catch (RuntimeException e) {
            // Un fallo de correo no debe anular una cuenta ya creada.
            log.warn("No se pudo enviar el correo de bienvenida", e);
        }
    }
}
