package HeyArtcraftWeb.Repository;

/**
 * Se lanza cuando una pantalla necesita un módulo cuyos procedimientos aún no
 * existen en la BD. La vista general/modulo_pendiente muestra el aviso.
 */
public class ModuloPendienteException extends RuntimeException {

    private final String modulo;

    public ModuloPendienteException(String modulo) {
        super("Módulo pendiente de entrega en la BD: " + modulo);
        this.modulo = modulo;
    }

    public String getModulo() {
        return modulo;
    }
}
