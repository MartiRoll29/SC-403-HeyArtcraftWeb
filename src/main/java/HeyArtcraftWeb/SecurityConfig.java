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

    /**
     * Páginas y recursos públicos.
     */
    private static final String[] PUBLIC_URLS = {
        "/",
        "/index",
        "/catalogo/**",
        "/css/**",
        "/js/**",
        "/img/**",
        "/webjars/**",
        "/login",
        "/oauth2/**",
        "/login/oauth2/**",
        "/registro",
        "/acceso_denegado",
        "/error",
        "/favicon.ico"
    };

    /**
     * El carrito puede consultarse sin iniciar sesión.
     */
    private static final String[] CARRITO_PUBLIC_URLS = {
        "/carrito",
        "/carrito/agregar",
        "/carrito/eliminar/**"
    };

    /**
     * Rutas para clientes y administradores autenticados.
     */
    private static final String[] CLIENTE_URLS = {
        "/perfil/**",
        "/carrito/confirmar"
    };

    /**
     * Rutas exclusivas del administrador.
     */
    private static final String[] ADMIN_URLS = {
        "/producto/guardar",
        "/producto/eliminar",
        "/producto/modificar/**",
        "/categoria/guardar",
        "/categoria/eliminar",
        "/categoria/modificar/**",
        "/usuario/**",
        // Módulo 10 y 11: gestión e historial de pedidos (HU-26 a HU-30)
        "/pedidos/admin/**"
    };

    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(
            CustomOAuth2UserService customOAuth2UserService) {

        this.customOAuth2UserService = customOAuth2UserService;
    }

    /**
     * Cifrado de contraseñas para usuarios locales.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(CARRITO_PUBLIC_URLS).permitAll()
                .requestMatchers(ADMIN_URLS).hasRole("ADMIN")
                .requestMatchers(CLIENTE_URLS)
                .hasAnyRole("CLIENTE", "ADMIN")
                .anyRequest().authenticated()
                )
                .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                .oidcUserService(customOAuth2UserService)
                )
                .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
                )
                .exceptionHandling(exception -> exception
                .accessDeniedPage("/acceso_denegado")
                );

        return http.build();
    }
}