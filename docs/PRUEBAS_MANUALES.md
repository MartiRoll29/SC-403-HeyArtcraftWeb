# Pruebas manuales y capturas (Avance 2)

Pasos para comprobar la aplicación contra Oracle y tomar las capturas del documento IEEE.

## Preparación

1. En SQL Developer, conectado como `PROYECTO_SC504`, ejecuta `docs/db/03_claves_bcrypt.sql` con F5. Solo hace falta una vez. Debe imprimir tres líneas `Clave actualizada`.
2. Detén cualquier instancia anterior de la aplicación y genera el jar:

```bash
mvn clean package
```

3. Arranca la aplicación desde la raíz del repositorio, en Git Bash:

```bash
WALLET_PATH=C:/GitHub/SC-403-HeyArtcraftWeb/Wallet_LenguajeBD DB_PASSWORD='TU_CLAVE' java -jar target/HeyArtcraftWeb-0.0.1-SNAPSHOT.jar
```

4. Abre `http://localhost:8081`.

Usuarios de demostración, todos con la clave `Demo2026!`:

| Correo | Rol |
|---|---|
| `admin@ejemplo.com` | ADMIN |
| `cliente1@ejemplo.com` | CLIENTE |
| `cliente2@ejemplo.com` | CLIENTE |

## Capturas

Sigue el orden: algunas pantallas muestran datos creados en los pasos anteriores.

| # | Usuario | Ruta | Acción | Resultado esperado | Procedimientos |
|---|---|---|---|---|---|
| 1 | Sin sesión | `/login` | Abrir | Formulario con correo y contraseña, sin botón de Google | - |
| 2 | Sin sesión | `/login` | Entrar con una clave incorrecta | "Correo o contraseña incorrectos." | `sp_usuario_obtener_correo` |
| 3 | Sin sesión | `/registro` | Crear una cuenta nueva | Vuelve al login con mensaje de cuenta creada | `fn_usuario_existe_correo`, `sp_rol_listar`, `sp_usuario_insertar`, `sp_usuario_rol_insertar` |
| 4 | Sin sesión | `/registro` | Repetir el mismo correo | "Ya existe una cuenta con ese correo." | `fn_usuario_existe_correo` |
| 5 | Cuenta nueva | `/` | Iniciar sesión | Correo en el menú, sin opciones de administrador | `sp_usuario_obtener_correo`, `sp_usuario_rol_listar_usuario` |
| 6 | `cliente1` | `/perfil` | Abrir | Nombre, correo y fecha de registro | `sp_usuario_obtener` |
| 7 | `cliente1` | `/perfil/editar` | Cambiar el nombre | Mensaje de confirmación y nombre nuevo | `sp_usuario_actualizar` |
| 8 | `cliente1` | `/perfil/password` | Formulario | Cambio de contraseña. Si la cambias, usa la nueva en adelante | `sp_usuario_cambiar_contrasena` |
| 9 | `cliente1` | `/perfil/direcciones` | Abrir | Las direcciones semilla de este cliente | `sp_direccion_listar_usuario` |
| 10 | `cliente1` | `/perfil/direcciones/nueva` | Agregar una dirección | Aparece en la lista | `sp_direccion_insertar` |
| 11 | `cliente1` | Editar en la lista | Cambiar las señas | Datos actualizados | `sp_direccion_obtener`, `sp_direccion_actualizar` |
| 12 | `cliente1` | Eliminar en la lista | Eliminar la dirección creada | Desaparece de la lista | `sp_direccion_eliminar` |
| 13 | `cliente1` | `/perfil/facturas` | Abrir | "Aún no tienes facturas." (ver nota) | `sp_factura_listar_usuario` |
| 14 | Sin sesión | `/contacto` | Enviar un mensaje | Mensaje de confirmación | `sp_mensaje_insertar` |
| 15 | `admin` | `/` | Iniciar sesión | Opciones de administrador en el menú | `sp_usuario_rol_listar_usuario` |
| 16 | `admin` | `/admin/usuarios` | Abrir | Usuarios con sus roles, incluida la cuenta nueva | `sp_usuario_listar`, `sp_usuario_rol_listar` |
| 17 | `admin` | `/admin/mensajes` | Abrir | El mensaje del paso 14 | `sp_mensaje_listar` |
| 18 | `cliente1` | `/admin/usuarios` | Abrir | Página de acceso denegado | - |
| 19 | Cualquiera | `/catalogo` | Abrir | Aviso de módulo en preparación | Pendiente de módulo |

**Nota sobre facturas:** los datos semilla no traen pedidos ni facturas, y esos datos dependen del módulo Compra. Hasta entonces, la pantalla solo puede mostrar su estado vacío.

## Comprobación en la BD

Para el documento, puedes confirmar en SQL Developer las filas creadas en los pasos 3, 10 y 14:

```sql
SELECT * FROM v_usuarios_roles;
SELECT * FROM v_direcciones_usuario;
SELECT * FROM v_mensajes_contacto;
```

## Si algo falla

- **`ORA-` en la consola:** anota el mensaje y el procedimiento.
- **El login rechaza las claves semilla:** falta ejecutar `03_claves_bcrypt.sql`.
- **`mvn clean` no puede borrar el jar:** la aplicación sigue corriendo; deténla primero.
