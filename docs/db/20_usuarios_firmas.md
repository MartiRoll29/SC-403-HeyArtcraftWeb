# Firmas de los procedimientos y funciones - Modulo Usuarios y envio

Regla del profesor: la aplicacion web SOLO llama a estos procedimientos y funciones.
Esta prohibido usar SELECT, INSERT, UPDATE o DELETE desde Java.

Convenciones:
- Los listados de varias filas devuelven un `SYS_REFCURSOR` en el ultimo parametro (`p_cursor`).
- Los `*_obtener` (una sola fila) devuelven los datos en parametros `OUT`.
  Si no existe la fila: los textos quedan en `'N/A'` y los numeros/fechas en `NULL`.
- Los `*_insertar` devuelven el id nuevo en el ultimo parametro (`OUT`).
- Las funciones con `pout_error` (OUT BOOLEAN) lo ponen en `TRUE` cuando no encuentran el dato.
  Por tener un parametro OUT, solo se pueden llamar desde PL/SQL, no desde un SELECT.

## Funciones
| Funcion | Parametros | Devuelve |
|---|---|---|
| `fn_usuario_existe_correo` | `p_correo` | 1 si existe, 0 si no |
| `fn_usuario_nombre` | `p_id_usuario`, OUT `pout_error` | nombre, o `'N/A'` si no existe |
| `fn_usuario_tiene_rol` | `p_id_usuario`, `p_nombre_rol` | 1 / 0 |
| `fn_direccion_cantidad` | `p_id_usuario` | cantidad de direcciones |
| `fn_direccion_completa` | `p_id_direccion`, OUT `pout_error` | texto de la direccion, o `'N/A'` |
| `fn_calcular_iva` | `p_subtotal` | 13% del subtotal |

## Procedimientos
| Tabla | Procedimiento | Parametros (en orden) |
|---|---|---|
| rol | `sp_rol_insertar` | `p_nombre_rol`, OUT `p_id_rol` |
| rol | `sp_rol_actualizar` | `p_id_rol`, `p_nombre_rol` |
| rol | `sp_rol_eliminar` | `p_id_rol` |
| rol | `sp_rol_listar` | OUT `p_cursor` |
| rol | `sp_rol_obtener` | `p_id_rol`, OUT `p_nombre_rol` |
| usuario | `sp_usuario_insertar` | `p_nombre`, `p_correo`, `p_contrasena`, OUT `p_id_usuario` |
| usuario | `sp_usuario_actualizar` | `p_id_usuario`, `p_nombre`, `p_correo` |
| usuario | `sp_usuario_cambiar_contrasena` | `p_id_usuario`, `p_contrasena` |
| usuario | `sp_usuario_eliminar` | `p_id_usuario` |
| usuario | `sp_usuario_listar` | OUT `p_cursor` |
| usuario | `sp_usuario_obtener` | `p_id_usuario`, OUT `p_nombre`, OUT `p_correo`, OUT `p_fecha_registro` |
| usuario | `sp_usuario_obtener_correo` | `p_correo`, OUT `p_id_usuario`, OUT `p_nombre`, OUT `p_contrasena` (para el login) |
| usuario_rol | `sp_usuario_rol_insertar` | `p_id_usuario`, `p_id_rol` |
| usuario_rol | `sp_usuario_rol_actualizar` | `p_id_usuario`, `p_id_rol_actual`, `p_id_rol_nuevo` |
| usuario_rol | `sp_usuario_rol_eliminar` | `p_id_usuario`, `p_id_rol` |
| usuario_rol | `sp_usuario_rol_listar` | OUT `p_cursor` |
| usuario_rol | `sp_usuario_rol_listar_usuario` | `p_id_usuario`, OUT `p_cursor` |
| direccion_envio | `sp_direccion_insertar` | `p_id_usuario`, `p_provincia`, `p_canton`, `p_distrito`, `p_senas_exactas`, `p_codigo_postal`, OUT `p_id_direccion` |
| direccion_envio | `sp_direccion_actualizar` | `p_id_direccion`, `p_provincia`, `p_canton`, `p_distrito`, `p_senas_exactas`, `p_codigo_postal` |
| direccion_envio | `sp_direccion_eliminar` | `p_id_direccion` |
| direccion_envio | `sp_direccion_listar` | OUT `p_cursor` |
| direccion_envio | `sp_direccion_obtener` | `p_id_direccion`, OUT `p_id_usuario`, OUT `p_provincia`, OUT `p_canton`, OUT `p_distrito`, OUT `p_senas_exactas`, OUT `p_codigo_postal` |
| direccion_envio | `sp_direccion_listar_usuario` | `p_id_usuario`, OUT `p_cursor` |
| factura | `sp_factura_insertar` | `p_id_pedido`, `p_subtotal`, `p_impuesto`, `p_monto_total`, OUT `p_id_factura` |
| factura | `sp_factura_generar` | `p_id_pedido`, OUT `p_id_factura` (calcula IVA 13%; NULL si el pedido no existe) |
| factura | `sp_factura_actualizar` | `p_id_factura`, `p_subtotal`, `p_impuesto`, `p_monto_total` |
| factura | `sp_factura_eliminar` | `p_id_factura` |
| factura | `sp_factura_listar` | OUT `p_cursor` |
| factura | `sp_factura_obtener` | `p_id_factura`, OUT `p_id_pedido`, OUT `p_fecha_emision`, OUT `p_subtotal`, OUT `p_impuesto`, OUT `p_monto_total` |
| factura | `sp_factura_listar_usuario` | `p_id_usuario`, OUT `p_cursor` |
| mensaje_contacto | `sp_mensaje_insertar` | `p_nombre_remitente`, `p_correo_remitente`, `p_asunto`, `p_mensaje`, OUT `p_id_mensaje` |
| mensaje_contacto | `sp_mensaje_actualizar` | `p_id_mensaje`, `p_nombre_remitente`, `p_correo_remitente`, `p_asunto`, `p_mensaje` |
| mensaje_contacto | `sp_mensaje_eliminar` | `p_id_mensaje` |
| mensaje_contacto | `sp_mensaje_listar` | OUT `p_cursor` |
| mensaje_contacto | `sp_mensaje_obtener` | `p_id_mensaje`, OUT `p_nombre_remitente`, OUT `p_correo_remitente`, OUT `p_asunto`, OUT `p_mensaje`, OUT `p_fecha_envio` |

## Reportes (sin parametros, salida por DBMS_OUTPUT)
`sp_reporte_usuarios_roles`, `sp_reporte_usuarios_sin_dir`, `sp_reporte_total_facturado`

## Vistas
`v_usuarios_roles`, `v_direcciones_usuario`, `v_facturas_detalle`, `v_mensajes_contacto`
