package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Repository.RolRepository;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.UsuarioRolRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Inicio de sesión con Google. No forma parte del Avance 2: solo existe si
 * app.oauth.google.enabled=true (perfil "google").
 */
@Service
@ConditionalOnProperty(name = "app.oauth.google.enabled", havingValue = "true")
public class CustomOAuth2UserService extends OidcUserService {

    private static final Logger log
            = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<CorreoService> correoService;

    public CustomOAuth2UserService(
            UsuarioRepository usuarioRepository,
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

    @Override
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

        Rol rolCliente = rolRepository.buscarPorNombre("CLIENTE")
                .orElseThrow(() ->
                new IllegalStateException("El rol CLIENTE no existe en la base de datos"));

        Usuario existente = usuarioRepository.obtenerPorCorreo(email).orElse(null);
        boolean usuarioNuevo = existente == null;

        String nombre = oidcUser.getGivenName() != null ? oidcUser.getGivenName() : email;

        // Contraseña aleatoria cifrada: Google no utiliza contraseña local.
        Integer idUsuario = usuarioNuevo
                ? usuarioRepository.insertar(nombre, email,
                        passwordEncoder.encode(UUID.randomUUID().toString()))
                : existente.getIdUsuario();

        // También corrige usuarios existentes que todavía no tengan rol.
        List<Rol> roles = usuarioRolRepository.listarPorUsuario(idUsuario);
        if (roles.isEmpty()) {
            usuarioRolRepository.insertar(idUsuario, rolCliente.getIdRol());
            roles = List.of(rolCliente);
        }

        if (usuarioNuevo) {
            enviarBienvenida(email, nombre);
        }

        Set<GrantedAuthority> authorities
                = new HashSet<>(oidcUser.getAuthorities());

        roles.forEach(rol ->
            authorities.add(new SimpleGrantedAuthority("ROLE_" + rol.getNombreRol()))
        );

        return new DefaultOidcUser(
                authorities,
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        );
    }

    private void enviarBienvenida(String email, String nombre) {
        try {
            correoService.ifAvailable(correo -> correo.enviarBienvenida(email, nombre));
        } catch (RuntimeException ex) {
            // Un fallo de correo no debe impedir el inicio de sesión.
            log.warn("No se pudo enviar el correo de bienvenida a {}", email);
        }
    }
}
