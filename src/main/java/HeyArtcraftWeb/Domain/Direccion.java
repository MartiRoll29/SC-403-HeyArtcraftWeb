package HeyArtcraftWeb.Domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dirección de envío de un usuario (tabla direccion_envio).
 */
@Data
@NoArgsConstructor
public class Direccion {

    private Integer idDireccion;
    private Integer idUsuario;

    @NotBlank
    @Size(max = 50)
    private String provincia;

    @NotBlank
    @Size(max = 50)
    private String canton;

    @NotBlank
    @Size(max = 50)
    private String distrito;

    @NotBlank
    @Size(max = 300)
    private String senasExactas;

    @Size(max = 10)
    private String codigoPostal;
}
