package HeyArtcraftWeb.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Módulo 7 - Perfil del Cliente: rol asignado a un usuario (ADMIN o CLIENTE).
 * El prefijo ROLE_ que exige Spring Security se agrega en UsuarioDetailsService,
 * por eso aquí se guarda el nombre limpio.
 */
@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(unique = true, nullable = false, length = 25)
    private String rol;
}
