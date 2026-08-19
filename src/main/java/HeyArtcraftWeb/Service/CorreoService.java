package HeyArtcraftWeb.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarBienvenida(String email, String nombre) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setSubject("¡Bienvenido/a a Hey Artcraft!");
        mensaje.setText("Hola " + nombre + ",\n\nTu cuenta en Hey Artcraft ha sido creada exitosamente, puedes realizar tus pedidos en la plataforma.\n\nSaludos,\nEquipo Hey Artcraft");
        mailSender.send(mensaje);
    }
}