# Procedimientos pendientes

Lo que la aplicación necesita y todavía no existe en la BD. Son propuestas: los nombres y parámetros finales los definen los módulos `30_catalogo_firmas.md` y `40_compra_firmas.md`.

## Módulo Catálogo

Interruptor: `app.modulos.catalogo.enabled=false`.

| Procedimiento propuesto | Parámetros | Pantalla |
|---|---|---|
| `sp_producto_listar` | OUT `p_cursor` | Catálogo, administración |
| `sp_producto_obtener` | `p_id_producto`, OUT datos del producto | Detalle, agregar al carrito |
| `sp_producto_insertar` | datos del producto, OUT `p_id_producto` | Administración |
| `sp_producto_actualizar` | `p_id_producto`, datos del producto | Administración |
| `sp_producto_eliminar` | `p_id_producto` | Administración |
| `sp_categoria_listar` | OUT `p_cursor` | Catálogo, administración |
| `sp_categoria_insertar` | `p_nombre_categoria`, `p_descripcion`, OUT `p_id_categoria` | Administración |
| `sp_categoria_actualizar` | `p_id_categoria`, `p_nombre_categoria`, `p_descripcion` | Administración |
| `sp_categoria_eliminar` | `p_id_categoria` | Administración |
| `sp_producto_tamano_listar` | `p_id_producto`, OUT `p_cursor` | Detalle: tamaños y `ajuste_precio` |
| `sp_producto_material_listar` | `p_id_producto`, OUT `p_cursor` | Detalle: materiales |

La tabla `producto` no tiene una columna de destacado. Hay que decidir qué mostrará la portada: por ejemplo, un `sp_producto_listar_recientes`.

## Módulo Compra

Interruptor: `app.modulos.compra.enabled=false`.

| Procedimiento propuesto | Parámetros | Pantalla |
|---|---|---|
| `sp_pedido_insertar` | `p_id_usuario`, `p_id_direccion`, OUT `p_id_pedido` | Confirmar compra |
| `sp_detalle_pedido_insertar` | `p_id_pedido`, `p_id_producto`, `p_id_tamano`, `p_id_material`, `p_cantidad`, `p_texto_personalizado`, OUT `p_id_detalle` | Confirmar compra |
| `sp_pedido_listar_usuario` | `p_id_usuario`, OUT `p_cursor` | Mis pedidos |
| `sp_pedido_obtener` | `p_id_pedido`, OUT datos del pedido | Factura PDF |
| `sp_detalle_pedido_listar` | `p_id_pedido`, OUT `p_cursor` | Factura PDF |
| `sp_pedido_listar_estado` | `p_nombre_estado`, OUT `p_cursor` | Administración de pedidos |
| `sp_pedido_cambiar_estado` | `p_id_pedido`, `p_nombre_estado` | Completar y cancelar pedido |
| `sp_pedido_buscar_historial` | `p_texto`, OUT `p_cursor` | Historial de administración |

Reglas que deben vivir en estos procedimientos: que el tamaño y el material sean válidos para el producto, que la dirección pertenezca al comprador, el cálculo de precios y los cambios de estado.

## Mejoras al módulo Usuarios y envío

Hoy se cubren desde Java porque la BD no se modifica.

| Procedimiento propuesto | Parámetros | Motivo |
|---|---|---|
| `sp_direccion_actualizar` con dueño | agregar `p_id_usuario` | Validar en la BD que la dirección sea del usuario. Hoy Java lo compara con `sp_direccion_obtener` |
| `sp_direccion_eliminar` con dueño | agregar `p_id_usuario` | Igual que el anterior |
| `sp_usuario_registrar` | `p_nombre`, `p_correo`, `p_contrasena`, `p_nombre_rol`, OUT `p_id_usuario` | Crear usuario y rol en una sola transacción. Hoy Java borra el usuario si falla la asignación del rol |
