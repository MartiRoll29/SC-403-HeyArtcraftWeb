package HeyArtcraftWeb.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 100)
    private String nombre;

    @NotBlank
    @Email
    @Size(max = 150)
    private String correo;

    /**
     * En el registro llega en texto plano desde el formulario; lo que viene
     * de la BD es siempre el hash BCrypt (columna contrasena).
     */
    @ToString.Exclude
    @Size(min = 6)
    private String password;

    private LocalDateTime fechaRegistro;

    private List<Rol> roles = new ArrayList<>();
}
