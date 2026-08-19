package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistroService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final CorreoService correoService;

    public RegistroService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            PasswordEncoder passwordEncoder,
            CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
        this.correoService = correoService;
    }

    @Transactional
    public Usuario registrarNuevoUsuario(Usuario usuario) {
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Rol por defecto: CLIENTE
        Rol rolCliente = rolRepository.findByRol("CLIENTE")
                .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no existe en BD"));
        usuario.getRoles().add(rolCliente);

        // Guardar usuario
        Usuario guardado = usuarioRepository.save(usuario);

        // Enviar correo de bienvenida
        correoService.enviarBienvenida(usuario.getCorreo(), usuario.getNombre());

        return guardado;
    }
}