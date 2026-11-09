package HeyArtcraftWeb.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Fila de sp_usuario_rol_listar: una asignación de rol con sus nombres.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {

    private Integer idUsuario;
    private String nombre;
    private Integer idRol;
    private String nombreRol;
}
