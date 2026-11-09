package HeyArtcraftWeb.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Rol de un usuario (ADMIN o CLIENTE). El prefijo ROLE_ que exige Spring
 * Security se agrega en Java, por eso aquí va el nombre tal como está en la BD.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    private Integer idRol;
    private String nombreRol;
}
