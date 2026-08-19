package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Service.CorreoService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CorreoService correoService;

    public CustomOAuth2UserService(UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            CorreoService correoService) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.correoService = correoService;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("Google attributes: {}", oAuth2User.getAttributes());

        String email = oAuth2User.getAttribute("email");
        String nombre = oAuth2User.getAttribute("given_name");
        String apellidos = oAuth2User.getAttribute("family_name");
        String foto = oAuth2User.getAttribute("picture");

        // Buscar usuario en BD
        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        if (usuario == null) {
            usuario = new Usuario();
            usuario.setCorreo(email);
            usuario.setUsername(email);       // siempre válido y único
            usuario.setNombre(nombre);        // puede ser null si quitaste @NotBlank
            usuario.setApellidos(apellidos);  // puede ser null si quitaste @NotBlank
            usuario.setPassword("oauth");     // dummy
            usuario.setRutaImagen(foto);

            Rol rolCliente = rolRepository.findByRol("CLIENTE")
                    .orElseThrow(() -> new IllegalStateException("Rol CLIENTE no existe en BD"));
            usuario.getRoles().add(rolCliente);

            usuario = usuarioRepository.save(usuario);
            log.info("Usuario Google guardado en BD con id {}", usuario.getIdUsuario());

            correoService.enviarBienvenida(email, nombre != null ? nombre : email);
        }

        // Construir authorities desde los roles en BD
        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getRol()))
                .collect(Collectors.toList());

        // Retornar usuario con roles correctos
        return new DefaultOAuth2User(
                authorities,
                oAuth2User.getAttributes(),
                "email"
        );
    }
}