package HeyArtcraftWeb;

import HeyArtcraftWeb.Service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Módulo 7 - Perfil del Cliente (prerrequisito): autenticación y control de
 * acceso. Ocultar un botón en la vista NO es seguridad: toda ruta protegida
 * en el HTML debe estar también declarada aquí.
 */
@Configuration
public class SecurityConfig {

    /** Páginas y recursos que cualquier visitante puede ver sin iniciar sesión. */
    private static final String[] PUBLIC_URLS = {
        "/", "/index", "/catalogo/**", "/css/**", "/js/**", "/img/**",
        "/webjars/**", "/login", "/oauth2/", "/registro", "/acceso_denegado", "/error", "/favicon.ico"
    };

    /**
     * El carrito se arma sin cuenta (HU-13 y HU-14 siguen siendo públicas);
     * solo confirmar la compra exige iniciar sesión, para que todo pedido
     * tenga dueño y pueda aparecer en el historial (HU-18).
     */
    private static final String[] CARRITO_PUBLIC_URLS = {
        "/carrito", "/carrito/agregar", "/carrito/eliminar/**"
    };

    /** Módulo 7: el perfil y la compra confirmada son de clientes autenticados. */
    private static final String[] CLIENTE_URLS = {
        "/perfil/**", "/carrito/confirmar"
    };

    /** Acciones que modifican el catálogo: solo administrador. */
    private static final String[] ADMIN_URLS = {
        "/producto/guardar", "/producto/eliminar", "/producto/modificar/**",
        "/categoria/guardar", "/categoria/eliminar", "/categoria/modificar/**",
        "/usuario/**",
        // Módulo 10 y 11: gestión e historial de pedidos (HU-26 a HU-30)
        "/pedidos/admin/**"
    };
    
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    /**
     * Bean para encriptar contraseñas de usuarios locales
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers(CARRITO_PUBLIC_URLS).permitAll()
                        .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                        .requestMatchers(CLIENTE_URLS).hasAnyRole("CLIENTE", "ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService))
                .defaultSuccessUrl("/", true))             
                // HU-20: cerrar sesión y volver a la pantalla de login
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso_denegado"));

        return http.build();
    }
}
