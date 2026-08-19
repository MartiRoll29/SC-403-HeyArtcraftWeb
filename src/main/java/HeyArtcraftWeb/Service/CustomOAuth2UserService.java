package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private static final Logger log
            = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private static final PasswordEncoder PASSWORD_ENCODER
            = new BCryptPasswordEncoder();

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final CorreoService correoService;

    public CustomOAuth2UserService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            CorreoService correoService) {

        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.correoService = correoService;
    }

    @Override
    @Transactional
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_no_disponible"),
                    "Google no proporcionó el correo del usuario"
            );
        }

        log.info("Inicio de sesión con Google: {}", email);

        Usuario usuario = usuarioRepository.findByCorreo(email).orElse(null);

        Rol rolCliente = rolRepository.findByRol("CLIENTE")
                .orElseThrow(() ->
                new IllegalStateException("El rol CLIENTE no existe en la base de datos"));

        boolean usuarioNuevo = usuario == null;

        if (usuarioNuevo) {
            usuario = new Usuario();

            String username = "google_" + oidcUser.getSubject();

            if (username.length() > 30) {
                username = username.substring(0, 30);
            }

            usuario.setUsername(username);
            usuario.setCorreo(email);
            usuario.setNombre(oidcUser.getGivenName());
            usuario.setApellidos(oidcUser.getFamilyName());
            usuario.setRutaImagen(oidcUser.getPicture());
            usuario.setActivo(true);

            // Contraseña aleatoria cifrada: Google no utiliza contraseña local.
            usuario.setPassword(
                    PASSWORD_ENCODER.encode(UUID.randomUUID().toString())
            );
        }

        // También corrige usuarios existentes que todavía no tengan rol.
        boolean rolAgregado = usuario.getRoles().add(rolCliente);

        if (usuarioNuevo || rolAgregado) {
            usuario = usuarioRepository.saveAndFlush(usuario);
        }

        if (usuarioNuevo) {
            try {
                correoService.enviarBienvenida(
                        email,
                        usuario.getNombre() != null
                                ? usuario.getNombre()
                                : email
                );
            } catch (RuntimeException ex) {
                // Un fallo de correo no debe eliminar al usuario creado.
                log.warn(
                        "No se pudo enviar el correo de bienvenida a {}",
                        email
                );
            }
        }

        Set<GrantedAuthority> authorities
                = new HashSet<>(oidcUser.getAuthorities());

        usuario.getRoles().forEach(rol ->
            authorities.add(
                    new SimpleGrantedAuthority("ROLE_" + rol.getRol())
            )
        );

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        );
    }
}