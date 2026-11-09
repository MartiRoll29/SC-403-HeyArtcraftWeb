package HeyArtcraftWeb.Domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mensaje del formulario de contacto (tabla mensaje_contacto).
 */
@Data
@NoArgsConstructor
public class MensajeContacto {

    private Integer idMensaje;

    @NotBlank
    @Size(max = 100)
    private String nombreRemitente;

    @NotBlank
    @Email
    @Size(max = 150)
    private String correoRemitente;

    @NotBlank
    @Size(max = 200)
    private String asunto;

    @NotBlank
    @Size(max = 2000)
    private String mensaje;

    private LocalDateTime fechaEnvio;
}
