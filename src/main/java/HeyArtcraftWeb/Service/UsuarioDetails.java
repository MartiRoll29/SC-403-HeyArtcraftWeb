package HeyArtcraftWeb.Service;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 * Usuario de la sesión. El nombre de usuario de Spring Security es el correo;
 * se guarda también el id para no tener que buscarlo otra vez en la BD.
 */
public class UsuarioDetails extends User {

    private final Integer idUsuario;

    public UsuarioDetails(Integer idUsuario, String correo, String contrasena,
            Collection<? extends GrantedAuthority> roles) {
        super(correo, contrasena, roles);
        this.idUsuario = idUsuario;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
