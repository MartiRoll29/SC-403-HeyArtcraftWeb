-- ================================================================
-- MODULO 7: PERFIL DEL CLIENTE  (HU-16, HU-17, HU-18, HU-19, HU-20)
-- Datos semilla de autenticacion.
--
-- IMPORTANTE: las tablas rol, usuario y usuario_rol NO se crean aqui.
-- Las crea Hibernate al arrancar la aplicacion, porque el proyecto usa
-- spring.jpa.hibernate.ddl-auto=update. Hibernate crea el esquema pero
-- nunca inserta datos: por eso este script es necesario.
--
-- COMO USARLO:
--   1. Arranque la aplicacion al menos una vez (crea las tablas).
--   2. Ejecute este script una sola vez.
--   3. Ingrese en /login con alguno de los usuarios de abajo.
--
-- Es idempotente: volver a ejecutarlo no duplica ni rompe nada.
-- ================================================================
USE heyartcraftweb;

-- ----------------------------------------------------------------
-- ROLES
-- El prefijo ROLE_ que exige Spring Security lo agrega la aplicacion
-- (UsuarioDetailsService), por eso aqui se guarda el nombre limpio.
-- ----------------------------------------------------------------
INSERT IGNORE INTO rol (rol) VALUES ('ADMIN'), ('CLIENTE');

-- ----------------------------------------------------------------
-- USUARIOS DE PRUEBA
-- La columna password guarda el hash BCrypt, nunca el texto plano.
-- Credenciales para probar en el ambiente local:
--     admin    /  admin123      (rol ADMIN)
--     cliente  /  cliente123    (rol CLIENTE)
--
-- Estas claves son solo para desarrollo. Cambielas desde
-- /perfil/password despues del primer ingreso.
--
-- Para regenerar un hash propio, imprima en Java el resultado de:
--     new BCryptPasswordEncoder().encode("suClaveNueva")
-- ----------------------------------------------------------------
INSERT IGNORE INTO usuario (username, password, nombre, apellidos, correo, telefono, activo)
VALUES
('admin',
 '$2a$10$ZO6tCEOo6wRg57GjZE8p0OEc49vtOuwixYTbrq5qLeY2DRs76/pY6',
 'Administrador', 'Hey Artcraft', 'admin@heyartcraft.com', '8698-6239', 1),
('cliente',
 '$2a$10$3vLuF7syldzoKneK1CqExuOV355ULB9.HV/yP3S/LdF8oR2m/A9Ia',
 'Cliente', 'De Prueba', 'cliente@heyartcraft.com', '8888-8888', 1);

-- ----------------------------------------------------------------
-- ASIGNACION DE ROLES
-- ----------------------------------------------------------------
INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u, rol r
WHERE u.username = 'admin' AND r.rol = 'ADMIN';

INSERT IGNORE INTO usuario_rol (id_usuario, id_rol)
SELECT u.id_usuario, r.id_rol
FROM usuario u, rol r
WHERE u.username = 'cliente' AND r.rol = 'CLIENTE';

-- ----------------------------------------------------------------
-- PEDIDOS ANTERIORES AL MODULO 7  (opcional)
--
-- Los pedidos creados antes de que existieran las cuentas quedaron con
-- id_usuario NULL, asi que no aparecen en el historial de nadie (HU-18).
-- Esta linea se los asigna al cliente de prueba para poder probar el
-- historial y la descarga de la factura.
--
-- Si prefiere descartarlos en vez de reasignarlos, comente el UPDATE y
-- use el DELETE de abajo.
-- ----------------------------------------------------------------
UPDATE pedido
SET id_usuario = (SELECT id_usuario FROM usuario WHERE username = 'cliente')
WHERE id_usuario IS NULL;

-- DELETE FROM detalle_pedido WHERE pedido_id IN (SELECT id FROM pedido WHERE id_usuario IS NULL);
-- DELETE FROM pedido WHERE id_usuario IS NULL;
