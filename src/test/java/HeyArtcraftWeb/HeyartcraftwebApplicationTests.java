package HeyArtcraftWeb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que la aplicación arranque. Los valores son de relleno: el pool de
 * conexiones solo abre la conexión a Oracle en la primera llamada a la BD.
 */
@SpringBootTest(properties = {"WALLET_PATH=target/sin-wallet", "DB_PASSWORD=sin-conexion"})
class HeyArtcraftWebApplicationTests {

	@Test
	void contextLoads() {
	}

}
