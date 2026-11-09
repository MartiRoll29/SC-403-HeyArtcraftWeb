-- ============================================================
-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 02_datos_semilla.sql
-- Autor: Armando Layana Camacho (Opcion 1: Base de datos)
--
-- QUE HACE: carga los catalogos y un grupo pequeno de datos de
-- prueba para que el equipo pueda probar sus procedimientos.
-- (El poblado completo va en 03_poblado.sql)
-- COMO EJECUTARLO: despues de 01_ddl.sql, conectado como
--                  PROYECTO_SC504, Run Script (F5).
-- NOTA: todos los datos de personas son ficticios.
-- ============================================================

-- ------------------------------------------------------------
-- 1. CATALOGOS
-- ------------------------------------------------------------

-- Roles del sistema
INSERT INTO rol (id_rol, nombre_rol) VALUES (seq_rol.NEXTVAL, 'ADMIN');
INSERT INTO rol (id_rol, nombre_rol) VALUES (seq_rol.NEXTVAL, 'CLIENTE');

-- Estados del pedido (en el orden en que avanza una compra)
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'Pendiente');
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'Pagado');
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'En produccion');
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'Enviado');
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'Entregado');
INSERT INTO estado_pedido (id_estado, nombre_estado) VALUES (seq_estado_pedido.NEXTVAL, 'Cancelado');

-- Tamanos disponibles
INSERT INTO tamano (id_tamano, descripcion_tamano) VALUES (seq_tamano.NEXTVAL, 'Pequeno (15 cm)');
INSERT INTO tamano (id_tamano, descripcion_tamano) VALUES (seq_tamano.NEXTVAL, 'Mediano (25 cm)');
INSERT INTO tamano (id_tamano, descripcion_tamano) VALUES (seq_tamano.NEXTVAL, 'Grande (40 cm)');

-- Materiales
INSERT INTO material (id_material, nombre_material) VALUES (seq_material.NEXTVAL, 'Madera');
INSERT INTO material (id_material, nombre_material) VALUES (seq_material.NEXTVAL, 'Acrilico');
INSERT INTO material (id_material, nombre_material) VALUES (seq_material.NEXTVAL, 'Cristal');
INSERT INTO material (id_material, nombre_material) VALUES (seq_material.NEXTVAL, 'MDF');

-- Categorias de productos
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion)
VALUES (seq_categoria.NEXTVAL, 'Hogar', 'Decoracion personalizada para el hogar');
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion)
VALUES (seq_categoria.NEXTVAL, 'Bodas', 'Detalles y recuerdos para bodas');
INSERT INTO categoria (id_categoria, nombre_categoria, descripcion)
VALUES (seq_categoria.NEXTVAL, 'Regalos', 'Regalos personalizados para toda ocasion');

-- ------------------------------------------------------------
-- 2. PRODUCTOS DE PRUEBA (la categoria se busca por nombre)
-- ------------------------------------------------------------
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Hogar'),
        'Cuadro grabado', 'Cuadro decorativo con grabado personalizado', 12000, NULL);
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Regalos'),
        'Caja de recuerdos', 'Caja personalizada para guardar recuerdos', 9500, NULL);
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Bodas'),
        'Letrero de bienvenida', 'Letrero para recibir a los invitados', 18000, NULL);
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Regalos'),
        'Llavero personalizado', 'Llavero con nombre o frase', 3500, NULL);
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Bodas'),
        'Copa grabada', 'Copa de cristal con grabado', 7500, NULL);
INSERT INTO producto (id_producto, id_categoria, nombre_producto, descripcion, precio_base, imagen_url)
VALUES (seq_producto.NEXTVAL, (SELECT id_categoria FROM categoria WHERE nombre_categoria = 'Hogar'),
        'Portarretratos', 'Portarretratos con texto personalizado', 8000, NULL);

-- ------------------------------------------------------------
-- 3. TAMANOS PERMITIDOS POR PRODUCTO (con ajuste de precio)
--    Pequeno: +0 | Mediano: +2500 | Grande: +5000
-- ------------------------------------------------------------
-- Cuadro grabado: pequeno, mediano, grande
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Cuadro grabado'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Pequeno%'), 0);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Cuadro grabado'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Mediano%'), 2500);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Cuadro grabado'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Grande%'), 5000);
-- Caja de recuerdos: pequeno, mediano
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Caja de recuerdos'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Pequeno%'), 0);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Caja de recuerdos'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Mediano%'), 2500);
-- Letrero de bienvenida: mediano, grande
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Letrero de bienvenida'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Mediano%'), 0);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Letrero de bienvenida'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Grande%'), 5000);
-- Llavero personalizado: solo pequeno
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Llavero personalizado'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Pequeno%'), 0);
-- Copa grabada: pequeno, mediano
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Copa grabada'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Pequeno%'), 0);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Copa grabada'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Mediano%'), 2500);
-- Portarretratos: pequeno, mediano, grande
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Portarretratos'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Pequeno%'), 0);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Portarretratos'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Mediano%'), 2500);
INSERT INTO producto_tamano (id_producto_tamano, id_producto, id_tamano, ajuste_precio)
VALUES (seq_producto_tamano.NEXTVAL, (SELECT id_producto FROM producto WHERE nombre_producto = 'Portarretratos'),
        (SELECT id_tamano FROM tamano WHERE descripcion_tamano LIKE 'Grande%'), 5000);

-- ------------------------------------------------------------
-- 4. MATERIALES PERMITIDOS POR PRODUCTO
-- ------------------------------------------------------------
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Cuadro grabado'),
        (SELECT id_material FROM material WHERE nombre_material = 'Madera'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Cuadro grabado'),
        (SELECT id_material FROM material WHERE nombre_material = 'MDF'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Caja de recuerdos'),
        (SELECT id_material FROM material WHERE nombre_material = 'Madera'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Caja de recuerdos'),
        (SELECT id_material FROM material WHERE nombre_material = 'MDF'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Letrero de bienvenida'),
        (SELECT id_material FROM material WHERE nombre_material = 'Madera'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Letrero de bienvenida'),
        (SELECT id_material FROM material WHERE nombre_material = 'Acrilico'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Llavero personalizado'),
        (SELECT id_material FROM material WHERE nombre_material = 'Acrilico'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Copa grabada'),
        (SELECT id_material FROM material WHERE nombre_material = 'Cristal'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Portarretratos'),
        (SELECT id_material FROM material WHERE nombre_material = 'Madera'));
INSERT INTO producto_material (id_producto, id_material)
VALUES ((SELECT id_producto FROM producto WHERE nombre_producto = 'Portarretratos'),
        (SELECT id_material FROM material WHERE nombre_material = 'Acrilico'));

-- ------------------------------------------------------------
-- 5. USUARIOS DE PRUEBA (contrasena ficticia, no es un hash real)
-- ------------------------------------------------------------
INSERT INTO usuario (id_usuario, nombre, correo, contrasena)
VALUES (seq_usuario.NEXTVAL, 'Administrador del Sistema', 'admin@ejemplo.com', 'clave_demo_admin');
INSERT INTO usuario (id_usuario, nombre, correo, contrasena)
VALUES (seq_usuario.NEXTVAL, 'Maria Rodriguez', 'cliente1@ejemplo.com', 'clave_demo_1');
INSERT INTO usuario (id_usuario, nombre, correo, contrasena)
VALUES (seq_usuario.NEXTVAL, 'Carlos Jimenez', 'cliente2@ejemplo.com', 'clave_demo_2');

-- Asignacion de roles
INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES ((SELECT id_usuario FROM usuario WHERE correo = 'admin@ejemplo.com'),
        (SELECT id_rol FROM rol WHERE nombre_rol = 'ADMIN'));
INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES ((SELECT id_usuario FROM usuario WHERE correo = 'cliente1@ejemplo.com'),
        (SELECT id_rol FROM rol WHERE nombre_rol = 'CLIENTE'));
INSERT INTO usuario_rol (id_usuario, id_rol)
VALUES ((SELECT id_usuario FROM usuario WHERE correo = 'cliente2@ejemplo.com'),
        (SELECT id_rol FROM rol WHERE nombre_rol = 'CLIENTE'));

-- ------------------------------------------------------------
-- 6. DIRECCIONES DE ENVIO DE PRUEBA (cliente1 tiene dos)
-- ------------------------------------------------------------
INSERT INTO direccion_envio (id_direccion, id_usuario, provincia, canton, distrito, senas_exactas, codigo_postal)
VALUES (seq_direccion_envio.NEXTVAL, (SELECT id_usuario FROM usuario WHERE correo = 'cliente1@ejemplo.com'),
        'Heredia', 'Santo Domingo', 'Santo Tomas', 'Casa blanca, 200 m norte de la iglesia', '40302');
INSERT INTO direccion_envio (id_direccion, id_usuario, provincia, canton, distrito, senas_exactas, codigo_postal)
VALUES (seq_direccion_envio.NEXTVAL, (SELECT id_usuario FROM usuario WHERE correo = 'cliente1@ejemplo.com'),
        'San Jose', 'Escazu', 'San Rafael', 'Oficina 5, edificio azul', '10203');
INSERT INTO direccion_envio (id_direccion, id_usuario, provincia, canton, distrito, senas_exactas, codigo_postal)
VALUES (seq_direccion_envio.NEXTVAL, (SELECT id_usuario FROM usuario WHERE correo = 'cliente2@ejemplo.com'),
        'Alajuela', 'Alajuela', 'San Jose', 'Casa 12, residencial Los Pinos', '20101');

COMMIT;

-- ------------------------------------------------------------
-- 7. VERIFICACION: cantidad de filas por tabla (sirve de captura)
-- ------------------------------------------------------------
SELECT 'rol' AS tabla, COUNT(*) AS filas FROM rol
UNION ALL SELECT 'usuario',           COUNT(*) FROM usuario
UNION ALL SELECT 'usuario_rol',       COUNT(*) FROM usuario_rol
UNION ALL SELECT 'categoria',         COUNT(*) FROM categoria
UNION ALL SELECT 'producto',          COUNT(*) FROM producto
UNION ALL SELECT 'estado_pedido',     COUNT(*) FROM estado_pedido
UNION ALL SELECT 'tamano',            COUNT(*) FROM tamano
UNION ALL SELECT 'material',          COUNT(*) FROM material
UNION ALL SELECT 'producto_tamano',   COUNT(*) FROM producto_tamano
UNION ALL SELECT 'producto_material', COUNT(*) FROM producto_material
UNION ALL SELECT 'direccion_envio',   COUNT(*) FROM direccion_envio
UNION ALL SELECT 'pedido',            COUNT(*) FROM pedido
UNION ALL SELECT 'detalle_pedido',    COUNT(*) FROM detalle_pedido
UNION ALL SELECT 'factura',           COUNT(*) FROM factura
UNION ALL SELECT 'mensaje_contacto',  COUNT(*) FROM mensaje_contacto;
