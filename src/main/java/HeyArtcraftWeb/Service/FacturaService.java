package HeyArtcraftWeb.Service;

import HeyArtcraftWeb.Domain.Factura;
import HeyArtcraftWeb.Repository.FacturaRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> listarDelUsuario(Integer idUsuario) {
        return facturaRepository.listarPorUsuario(idUsuario);
    }
}
