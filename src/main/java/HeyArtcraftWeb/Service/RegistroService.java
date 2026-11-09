package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.UsuarioRolRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistroService {

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

    public Usuario registrarNuevoUsuario(Usuario usuario) {
        Rol rolCliente = rolRepository.buscarPorNombre("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no existe en BD"));

        Integer idUsuario = usuarioRepository.insertar(
                usuario.getNombre(),
                usuario.getCorreo(),
                passwordEncoder.encode(usuario.getPassword()));
        usuario.setIdUsuario(idUsuario);

        usuarioRolRepository.insertar(idUsuario, rolCliente.getIdRol());

        // El correo de bienvenida solo se envía si el servicio está activo.
        correoService.ifAvailable(correo
                -> correo.enviarBienvenida(usuario.getCorreo(), usuario.getNombre()));

        return usuario;
    }
}
