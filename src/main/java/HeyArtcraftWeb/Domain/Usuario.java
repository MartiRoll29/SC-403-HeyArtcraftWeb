package HeyArtcraftWeb.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Cuenta de un cliente o administrador (tabla usuario).
 * El inicio de sesión se hace con el correo.
 */
@Data
@NoArgsConstructor
public class Usuario {

    private Integer idUsuario;

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String correo;

    /** Hash BCrypt (columna contrasena). Nunca la contraseña en texto plano. */
    @ToString.Exclude
    private String password;

    private LocalDateTime fechaRegistro;

    private List<Rol> roles = new ArrayList<>();
}
