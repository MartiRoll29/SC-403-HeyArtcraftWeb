# Mapeo UI <-> BD (SC-504, Avance 2)

Resultado de la Fase 0. Relaciona cada pantalla con los procedimientos que usará.
Las firmas salen de `docs/db/20_usuarios_firmas.md` y se cotejaron con `docs/db/20_usuarios.sql`.

Estados:
- **Listo para migrar**: todos los procedimientos necesarios existen hoy.
- **Nueva**: la pantalla no existe en SC-403 y se crea en la Fase 3.
- **Pendiente de módulo**: depende de Catálogo o Compra, que aún no existen en la BD.

## 1. Inventario de lo que hay que eliminar

| Elemento | Cant. | Dónde |
|---|---|---|
| `@Entity` | 6 | `Domain/`: Categoria, DetallePedido, Pedido, Producto, Rol, Usuario |
| `JpaRepository` | 5 | `Repository/`: Categoria, Pedido, Producto, Rol, Usuario |
| `@Query` (JPQL) | 3 | `CategoriaRepository:11`, `PedidoRepository:24`, `PedidoRepository:44` |
| `@Transactional` | 24 | CategoriaService 5, PedidoService 8, ProductoService 5, UsuarioService 3, RegistroService 1, UsuarioDetailsService 1, CustomOAuth2UserService 1 |
| `EntityManager`, `JdbcTemplate` | 0 | No se usan |

`@Transactional` no es JPA, pero deja de tener efecto: cada procedimiento hace su propio `COMMIT`. Se quita para no sugerir una atomicidad que no existe.

### Repositorios actuales y su reemplazo

| Método actual | Reemplazo |
|---|---|
| `UsuarioRepository.findByCorreo` | `sp_usuario_obtener_correo` |
| `UsuarioRepository.findByUsername` | Se elimina: el login es por correo |
| `UsuarioRepository.findByUsernameAndActivoTrue` | Se elimina: `activo` no existe |
| `UsuarioRepository.existsByUsernameOrCorreo` | `fn_usuario_existe_correo` |
| `UsuarioRepository.save` (alta) | `sp_usuario_insertar` + `sp_usuario_rol_insertar` |
| `UsuarioRepository.save` (contraseña) | `sp_usuario_cambiar_contrasena` |
| `RolRepository.findByRol` | `sp_rol_listar` y filtrar la lista en Java |
| `ProductoRepository.*`, `CategoriaRepository.*` | Pendiente de módulo Catálogo |
| `PedidoRepository.*` | Pendiente de módulo Compra |

## 2. Pantallas existentes

| Ruta | Controlador | Plantilla | Procedimientos | Estado |
|---|---|---|---|---|
| `GET /login`, `POST /login` | `ProjectConfig`, Spring Security | `login` | `sp_usuario_obtener_correo`, `sp_usuario_rol_listar_usuario` | Listo para migrar |
| `GET/POST /registro` | `RegistroController` | `registro` | `fn_usuario_existe_correo`, `sp_usuario_insertar`, `sp_rol_listar`, `sp_usuario_rol_insertar` | Listo para migrar |
| `GET /perfil` | `PerfilController` | `perfil/perfil` | `sp_usuario_obtener` | Listo para migrar |
| `GET/POST /perfil/editar` (nueva) | `PerfilController` | `perfil/editar` | `sp_usuario_obtener`, `fn_usuario_existe_correo`, `sp_usuario_actualizar` | Migrada en la Fase 2 |
| `GET/POST /perfil/password` | `PerfilController` | `perfil/password` | `sp_usuario_obtener_correo` (hash actual), `sp_usuario_cambiar_contrasena` | Listo para migrar |
| `GET /acceso_denegado` | `ProjectConfig` | `acceso_denegado` | Ninguno | Sin cambios |
| `GET /` | `HomeController` | `index` | Productos: módulo Catálogo | Hero visible, destacados pendientes de módulo |
| `GET /catalogo`, `/catalogo/{id}` | `CatalogoController` | `catalogo/lista`, `catalogo/detalle` | Módulo Catálogo | Pendiente de módulo |
| `GET /catalogo/listado` | `CatalogoController` | `catalogo/listado` | Módulo Catálogo | Pendiente de módulo |
| `POST /producto/guardar`, `/producto/eliminar`, `GET /producto/modificar/{id}` | `ProductoController` | `catalogo/listado` | Módulo Catálogo | Pendiente de módulo |
| `POST /categoria/guardar`, `/categoria/eliminar`, `GET /categoria/categoria/modificar/{id}` | `CategoriaController` | `catalogo/listado` | Módulo Catálogo | Pendiente de módulo |
| `GET /carrito`, `POST /carrito/eliminar/{i}` | `CarritoController` | `carrito/ver` | Ninguno (sesión) | Funciona, sin cambios |
| `POST /carrito/agregar` | `CarritoController` | `carrito/ver` | Módulo Catálogo (obtener producto) | Pendiente de módulo |
| `POST /carrito/confirmar` | `CarritoController` | `carrito/ver` | Módulo Compra | Pendiente de módulo |
| `GET /perfil/pedidos` | `PerfilController` | `perfil/pedidos` | Módulo Compra | Pendiente de módulo |
| `GET /perfil/pedidos/{id}/factura` | `PerfilController` | PDF (`FacturaPdfService`) | Módulo Compra (detalle de pedido) | Pendiente de módulo |
| `GET /pedidos/admin`, `/historial`, `POST /{id}/completar`, `/{id}/cancelar` | `PedidoAdminController` | `pedidosAdmin/*` | Módulo Compra | Pendiente de módulo |

`UsuarioController` (`/usuarios`) existe pero no tiene ninguna ruta.

## 3. Pantallas nuevas (Fase 3)

Rutas propuestas. Hay que agregarlas a `SecurityConfig` y al menú de `general/fragmentos`.

| Ruta | Acceso | Controlador | Procedimientos |
|---|---|---|---|
| `GET /perfil/direcciones` | Autenticado | `DireccionController` (nuevo) | `sp_direccion_listar_usuario` |
| `GET/POST /perfil/direcciones/nueva` | Autenticado | `DireccionController` | `sp_direccion_insertar` |
| `GET/POST /perfil/direcciones/{id}/editar` | Autenticado, dueño | `DireccionController` | `sp_direccion_obtener`, `sp_direccion_actualizar` |
| `POST /perfil/direcciones/{id}/eliminar` | Autenticado, dueño | `DireccionController` | `sp_direccion_obtener`, `sp_direccion_eliminar` |
| `GET /perfil/facturas` | Autenticado | `PerfilController` | `sp_factura_listar_usuario` |
| `GET/POST /contacto` | Público | `ContactoController` (nuevo) | `sp_mensaje_insertar` |
| `GET /admin/mensajes` | ADMIN | `AdminController` (nuevo) | `sp_mensaje_listar` |
| `GET /admin/usuarios` | ADMIN | `AdminController` | `sp_usuario_listar`, `sp_usuario_rol_listar` |

`/admin/**` sustituye a la regla `/usuario/**` de `SecurityConfig`, que hoy no protege ninguna ruta real.

## 4. Procedimientos del módulo que no usará la web

| Objeto | Motivo |
|---|---|
| `fn_usuario_nombre`, `fn_direccion_completa` | Tienen `OUT BOOLEAN`, que JDBC no maneja. Se usan `sp_usuario_obtener` y `sp_direccion_obtener` |
| `sp_reporte_*` | Salida por `DBMS_OUTPUT`: se demuestran en SQL Developer |
| `v_*` | Consultarlas exigiría `SELECT` desde Java |
| `sp_rol_insertar/actualizar/eliminar`, `sp_rol_obtener` | Ninguna pantalla administra roles |
| `sp_factura_insertar/generar/actualizar/eliminar/obtener`, `sp_factura_listar` | Se usarán con el módulo Compra |
| `sp_direccion_listar`, `sp_mensaje_actualizar/eliminar/obtener` | Ninguna pantalla los necesita en este avance |

## 5. Hallazgos que afectan la migración

### BD (se reportan, no se modifica nada)

1. **`sp_direccion_actualizar` y `sp_direccion_eliminar` no validan al dueño.** Reciben solo `p_id_direccion`. Sin control, un cliente puede editar o borrar direcciones ajenas cambiando el número en la URL.
2. **El registro no es atómico.** `sp_usuario_insertar` hace `COMMIT` antes de `sp_usuario_rol_insertar`. Si el segundo falla, queda un usuario sin rol.
3. **Correos que difieren solo en mayúsculas.** `uq_usuario_correo` distingue mayúsculas, pero `sp_usuario_obtener_correo` compara con `LOWER` y no captura `TOO_MANY_ROWS`. Dos correos así romperían el login de ambos. `sp_usuario_actualizar` no lo impide, así que Java debe consultar antes `fn_usuario_existe_correo`.
4. **No hay pedidos ni facturas en los datos semilla.** `sp_factura_listar_usuario` devolverá vacío hasta que exista el módulo Compra. "Mis facturas" solo podrá capturarse con su estado vacío.

### Aplicación

5. `Usuario` pierde `username`, `apellidos`, `telefono`, `rutaImagen` y `activo`. Lo usan `perfil/fragmentos`, `PerfilController`, `UsuarioDetailsService` y `CustomOAuth2UserService`.
6. `RegistroService` y `CustomOAuth2UserService` dependen de `CorreoService`, y `ProductoService` de `FirebaseStorageService`. Al quedar desactivados con banderas, esas dependencias pasan a ser opcionales.
7. `ProductoService.delete` nunca borra el producto: termina sin llamar al repositorio. Revisar en la Fase 4.
8. `CategoriaController:37` repite el prefijo (`/categoria/categoria/modificar/{id}`). Revisar en la Fase 4.
9. El login de Google está en `login.html:47`; hay que ocultarlo cuando la bandera esté apagada.

### Seguridad

10. `Wallet_LenguajeBD/` y `Wallet_LenguajeBD.zip` no están ignorados por git hoy.
11. `docs/db/00_crear_usuario.sql` coincide con un patrón de clave (no se abrió). `docs/` aún no está versionado y el repositorio final será público.
12. `FirebaseConfig` usa `GoogleCredentials.getApplicationDefault()`. No hay archivos de credenciales de Firebase versionados, según los nombres de archivo de todo el historial.
13. `application.properties` aparece en 8 commits del historial, con sus claves.
