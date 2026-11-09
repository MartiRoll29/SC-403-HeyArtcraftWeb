package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Direccion;
import HeyArtcraftWeb.Repository.DireccionRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Direcciones de envío del usuario conectado. Los errores se lanzan como
 * IllegalArgumentException con la clave del mensaje que debe ver el usuario.
 */
@Service
public class DireccionService {

    private static final Logger log = LoggerFactory.getLogger(DireccionService.class);

    private final DireccionRepository direccionRepository;

    public DireccionService(DireccionRepository direccionRepository) {
        this.direccionRepository = direccionRepository;
    }

    public List<Direccion> listarDelUsuario(Integer idUsuario) {
        return direccionRepository.listarPorUsuario(idUsuario);
    }

    /**
     * sp_direccion_actualizar y sp_direccion_eliminar no reciben al dueño, así
     * que aquí se comprueba con el id_usuario que devuelve sp_direccion_obtener.
     * Sin esto, cambiar el número en la URL daría acceso a direcciones ajenas.
     * La mejora propuesta para la BD está en docs/PROCEDIMIENTOS_PENDIENTES.md.
     */
    public Direccion obtenerPropia(Integer idDireccion, Integer idUsuario) {
        return direccionRepository.obtener(idDireccion)
                .filter(direccion -> direccion.getIdUsuario().equals(idUsuario))
                .orElseThrow(() -> new AccessDeniedException("La dirección no pertenece al usuario"));
    }

    public void crear(Direccion direccion, Integer idUsuario) {
        direccion.setIdUsuario(idUsuario);
        try {
            direccionRepository.insertar(direccion);
        } catch (DataAccessException e) {
            log.error("No se pudo crear la dirección", e);
            throw new IllegalArgumentException("perfil.error.bd");
        }
    }

    public void actualizar(Direccion direccion, Integer idUsuario) {
        obtenerPropia(direccion.getIdDireccion(), idUsuario);
        try {
            direccionRepository.actualizar(direccion);
        } catch (DataAccessException e) {
            log.error("No se pudo actualizar la dirección", e);
            throw new IllegalArgumentException("perfil.error.bd");
        }
    }

    public void eliminar(Integer idDireccion, Integer idUsuario) {
        obtenerPropia(idDireccion, idUsuario);
        try {
            direccionRepository.eliminar(idDireccion);
        } catch (DataIntegrityViolationException e) {
            // La llave foránea de pedido impide borrar una dirección ya usada.
            throw new IllegalArgumentException("direccion.error.enUso");
        } catch (DataAccessException e) {
            log.error("No se pudo eliminar la dirección", e);
            throw new IllegalArgumentException("perfil.error.bd");
        }
    }
}
