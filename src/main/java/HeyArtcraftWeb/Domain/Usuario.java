package HeyArtcraftWeb.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Módulo 7 - Perfil del Cliente: cuenta con la que un cliente o un
 * administrador inicia sesión. La contraseña se guarda siempre cifrada
 * con BCrypt, nunca en texto plano.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @NotBlank
    @Column(unique = true, nullable = false, length = 30)
    private String username;

    /** Hash BCrypt de la contraseña (nunca la contraseña en texto plano). */
    @Column(length = 512)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 60)
    private String apellidos;

    @Email
    @Column(unique = true, length = 120)
    private String correo;

    @Column(length = 25)
    private String telefono;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    @Column(nullable = false)
    private boolean activo = true;

    /**
     * Se excluye de toString/equals porque es LAZY: al imprimir el usuario
     * fuera de una transacción provocaría LazyInitializationException.
     */
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    private Set<Rol> roles = new HashSet<>();
}
