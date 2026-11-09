package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Rol;
import HeyArtcraftWeb.Domain.Usuario;
import HeyArtcraftWeb.Domain.UsuarioRol;
import HeyArtcraftWeb.Repository.UsuarioRepository;
import HeyArtcraftWeb.Repository.UsuarioRolRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public AdminService(UsuarioRepository usuarioRepository,
            UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    /** Une, para mostrarlas, las listas de sp_usuario_listar y sp_usuario_rol_listar. */
    public List<Usuario> usuariosConRoles() {
        Map<Integer, List<Rol>> rolesPorUsuario = usuarioRolRepository.listar().stream()
                .collect(Collectors.groupingBy(
                        UsuarioRol::getIdUsuario,
                        Collectors.mapping(
                                asignacion -> new Rol(asignacion.getIdRol(), asignacion.getNombreRol()),
                                Collectors.toList())));

        List<Usuario> usuarios = usuarioRepository.listar();
        usuarios.forEach(usuario
                -> usuario.setRoles(rolesPorUsuario.getOrDefault(usuario.getIdUsuario(), List.of())));
        return usuarios;
    }
}
