package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.MensajeContacto;
import HeyArtcraftWeb.Repository.MensajeContactoRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ContactoService {

    private static final Logger log = LoggerFactory.getLogger(ContactoService.class);

    private final MensajeContactoRepository mensajeRepository;

    public ContactoService(MensajeContactoRepository mensajeRepository) {
        this.mensajeRepository = mensajeRepository;
    }

    public void enviar(MensajeContacto mensaje) {
        try {
            mensajeRepository.insertar(mensaje);
        } catch (DataAccessException e) {
            log.error("No se pudo guardar el mensaje de contacto", e);
            throw new IllegalArgumentException("contacto.error");
        }
    }

    public List<MensajeContacto> listar() {
        return mensajeRepository.listar();
    }
}
