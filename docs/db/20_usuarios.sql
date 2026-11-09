-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 20_usuarios.sql
-- Modulo: USUARIOS Y ENVIO (Opcion 3)
-- Tablas: rol, usuario, usuario_rol, direccion_envio, factura,
--         mensaje_contacto
-- Autor: Armando Layana Camacho (cubre la Opcion 3)
--
-- REGLA DE ARQUITECTURA (indicada por el profesor):
--   La logica de negocio vive en la base de datos. La aplicacion web
--   SOLO llama a estos procedimientos y funciones; esta prohibido usar
--   SELECT, INSERT, UPDATE o DELETE desde la capa web.
--
-- CONTENIDO (en este orden, porque unos objetos usan a otros):
--   1. Funciones
--   2. CRUD por procedimientos almacenados (una seccion por tabla)
--   3. Procedimientos con cursores (reportes)
--   4. Vistas
--
-- COMO EJECUTARLO: conectado como PROYECTO_SC504, despues de
-- 01_ddl.sql y 02_datos_semilla.sql. Run Script (F5).
-- Cada bloque termina con "/" para que SQL Developer lo compile.
--
-- CONVENCIONES (mismo estilo de los ejemplos del profesor):
--   * Parametros de entrada: p_nombre. Tipos con %TYPE de la columna.
--   * Datos de salida de una sola fila: parametros OUT. Si no existe la
--     fila, las cadenas quedan en 'N/A' y los numeros/fechas en NULL.
--   * Las funciones que pueden no encontrar datos usan pout_error
--     (TRUE = no encontrado), igual que el ejemplo del profesor.
--   * Listados de varias filas: SYS_REFCURSOR en p_cursor.
--   * Insertar devuelve el ID nuevo en un parametro OUT.
--   * Variables locales con prefijo var_.


-- 1. FUNCIONES

-- fn_usuario_existe_correo: indica si un correo ya esta registrado.
-- Devuelve 1 si existe, 0 si no.
CREATE OR REPLACE FUNCTION fn_usuario_existe_correo (
    p_correo IN usuario.correo%TYPE
) RETURN NUMBER IS
    var_total NUMBER;              -- cantidad de usuarios con ese correo
BEGIN
    -- Cuenta los usuarios con ese correo (sin importar mayusculas)
    SELECT COUNT(*) INTO var_total
    FROM usuario
    WHERE LOWER(correo) = LOWER(p_correo);

    IF var_total > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
/

-- fn_usuario_nombre: devuelve el nombre de un usuario.
-- pout_error = TRUE si el usuario no existe (y devuelve 'N/A').
CREATE OR REPLACE FUNCTION fn_usuario_nombre (
    p_id_usuario IN  usuario.id_usuario%TYPE,
    pout_error   OUT BOOLEAN
) RETURN VARCHAR2 IS
    var_nombre usuario.nombre%TYPE;
BEGIN
    SELECT nombre INTO var_nombre
    FROM usuario
    WHERE id_usuario = p_id_usuario;

    pout_error := FALSE;
    RETURN var_nombre;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        pout_error := TRUE;
        var_nombre := 'N/A';
        RETURN var_nombre;
END;
/

-- fn_usuario_tiene_rol: indica si un usuario tiene un rol.
-- Devuelve 1 si lo tiene, 0 si no.
CREATE OR REPLACE FUNCTION fn_usuario_tiene_rol (
    p_id_usuario IN usuario.id_usuario%TYPE,
    p_nombre_rol IN rol.nombre_rol%TYPE
) RETURN NUMBER IS
    var_total NUMBER;
BEGIN
    -- Une usuario_rol con rol para buscar el nombre del rol
    SELECT COUNT(*) INTO var_total
    FROM usuario_rol ur
    INNER JOIN rol r ON r.id_rol = ur.id_rol
    WHERE ur.id_usuario = p_id_usuario
      AND r.nombre_rol  = p_nombre_rol;

    IF var_total > 0 THEN
        RETURN 1;
    ELSE
        RETURN 0;
    END IF;
END;
/

-- fn_direccion_cantidad: cantidad de direcciones de envio de un usuario.
CREATE OR REPLACE FUNCTION fn_direccion_cantidad (
    p_id_usuario IN direccion_envio.id_usuario%TYPE
) RETURN NUMBER IS
    var_total NUMBER;
BEGIN
    SELECT COUNT(*) INTO var_total
    FROM direccion_envio
    WHERE id_usuario = p_id_usuario;

    RETURN var_total;
END;
/

-- fn_direccion_completa: arma la direccion en un solo texto:
-- "Provincia, Canton, Distrito. Senas exactas".
-- pout_error = TRUE si la direccion no existe (y devuelve 'N/A').
CREATE OR REPLACE FUNCTION fn_direccion_completa (
    p_id_direccion IN  direccion_envio.id_direccion%TYPE,
    pout_error     OUT BOOLEAN
) RETURN VARCHAR2 IS
    var_texto VARCHAR2(500);
BEGIN
    -- Concatena (||) las columnas de la direccion
    SELECT provincia || ', ' || canton || ', ' || distrito || '. ' || senas_exactas
    INTO var_texto
    FROM direccion_envio
    WHERE id_direccion = p_id_direccion;

    pout_error := FALSE;
    RETURN var_texto;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        pout_error := TRUE;
        var_texto := 'N/A';
        RETURN var_texto;
END;
/

-- fn_calcular_iva: calcula el impuesto de ventas (13%) sobre un
-- subtotal, redondeado a 2 decimales. Lo usa sp_factura_generar.
CREATE OR REPLACE FUNCTION fn_calcular_iva (
    p_subtotal IN NUMBER
) RETURN NUMBER IS
BEGIN
    RETURN ROUND(p_subtotal * 0.13, 2);
END;
/


-- 2. CRUD POR PROCEDIMIENTOS ALMACENADOS

-- 2.1 TABLA ROL

-- Crea un rol y devuelve su id
CREATE OR REPLACE PROCEDURE sp_rol_insertar (
    p_nombre_rol IN  rol.nombre_rol%TYPE,
    p_id_rol     OUT rol.id_rol%TYPE
) IS
BEGIN
    -- Pide el siguiente numero de la secuencia y lo deja en el parametro de salida
    SELECT seq_rol.NEXTVAL INTO p_id_rol FROM dual;

    INSERT INTO rol (id_rol, nombre_rol)
    VALUES (p_id_rol, p_nombre_rol);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;   -- deshace el insert si algo falla
        RAISE;      -- vuelve a lanzar el error para que lo vea quien llamo
END;
/

-- Cambia el nombre de un rol
CREATE OR REPLACE PROCEDURE sp_rol_actualizar (
    p_id_rol     IN rol.id_rol%TYPE,
    p_nombre_rol IN rol.nombre_rol%TYPE
) IS
BEGIN
    UPDATE rol
    SET nombre_rol = p_nombre_rol
    WHERE id_rol = p_id_rol;     -- nunca olvidar la condicion

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Elimina un rol (falla si algun usuario lo tiene asignado)
CREATE OR REPLACE PROCEDURE sp_rol_eliminar (
    p_id_rol IN rol.id_rol%TYPE
) IS
BEGIN
    DELETE FROM rol
    WHERE id_rol = p_id_rol;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista todos los roles (cursor de sistema)
CREATE OR REPLACE PROCEDURE sp_rol_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_rol, nombre_rol
        FROM rol
        ORDER BY id_rol;
END;
/

-- Devuelve el nombre de un rol por su id ('N/A' si no existe)
CREATE OR REPLACE PROCEDURE sp_rol_obtener (
    p_id_rol     IN  rol.id_rol%TYPE,
    p_nombre_rol OUT rol.nombre_rol%TYPE
) IS
BEGIN
    SELECT nombre_rol INTO p_nombre_rol
    FROM rol
    WHERE id_rol = p_id_rol;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_nombre_rol := 'N/A';
END;
/

-- 2.2 TABLA USUARIO
-- La contrasena llega YA cifrada desde Java; la base nunca
-- recibe ni guarda contrasenas en texto plano.

-- Registra un usuario y devuelve su id
CREATE OR REPLACE PROCEDURE sp_usuario_insertar (
    p_nombre     IN  usuario.nombre%TYPE,
    p_correo     IN  usuario.correo%TYPE,
    p_contrasena IN  usuario.contrasena%TYPE,
    p_id_usuario OUT usuario.id_usuario%TYPE
) IS
BEGIN
    SELECT seq_usuario.NEXTVAL INTO p_id_usuario FROM dual;

    -- fecha_registro no se envia: la tabla pone SYSDATE por defecto
    INSERT INTO usuario (id_usuario, nombre, correo, contrasena)
    VALUES (p_id_usuario, p_nombre, p_correo, p_contrasena);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Actualiza nombre y correo (la contrasena tiene su propio procedimiento)
CREATE OR REPLACE PROCEDURE sp_usuario_actualizar (
    p_id_usuario IN usuario.id_usuario%TYPE,
    p_nombre     IN usuario.nombre%TYPE,
    p_correo     IN usuario.correo%TYPE
) IS
BEGIN
    UPDATE usuario
    SET nombre = p_nombre,
        correo = p_correo
    WHERE id_usuario = p_id_usuario;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Cambia la contrasena (ya cifrada) de un usuario
CREATE OR REPLACE PROCEDURE sp_usuario_cambiar_contrasena (
    p_id_usuario IN usuario.id_usuario%TYPE,
    p_contrasena IN usuario.contrasena%TYPE
) IS
BEGIN
    UPDATE usuario
    SET contrasena = p_contrasena
    WHERE id_usuario = p_id_usuario;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Elimina un usuario (falla si tiene roles, direcciones o pedidos)
CREATE OR REPLACE PROCEDURE sp_usuario_eliminar (
    p_id_usuario IN usuario.id_usuario%TYPE
) IS
BEGIN
    DELETE FROM usuario
    WHERE id_usuario = p_id_usuario;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista los usuarios (NO incluye la contrasena)
CREATE OR REPLACE PROCEDURE sp_usuario_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_usuario, nombre, correo, fecha_registro
        FROM usuario
        ORDER BY id_usuario;
END;
/

-- Devuelve los datos de un usuario por id (sin contrasena).
-- Si no existe: nombre y correo en 'N/A', fecha en NULL.
CREATE OR REPLACE PROCEDURE sp_usuario_obtener (
    p_id_usuario     IN  usuario.id_usuario%TYPE,
    p_nombre         OUT usuario.nombre%TYPE,
    p_correo         OUT usuario.correo%TYPE,
    p_fecha_registro OUT usuario.fecha_registro%TYPE
) IS
BEGIN
    SELECT nombre, correo, fecha_registro
    INTO p_nombre, p_correo, p_fecha_registro
    FROM usuario
    WHERE id_usuario = p_id_usuario;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_nombre         := 'N/A';
        p_correo         := 'N/A';
        p_fecha_registro := NULL;
END;
/

-- Busca un usuario por correo y devuelve su id, nombre y contrasena cifrada.
-- Lo usa el inicio de sesion para comparar la clave.
-- Si no existe: id en NULL, nombre y contrasena en 'N/A'.
CREATE OR REPLACE PROCEDURE sp_usuario_obtener_correo (
    p_correo     IN  usuario.correo%TYPE,
    p_id_usuario OUT usuario.id_usuario%TYPE,
    p_nombre     OUT usuario.nombre%TYPE,
    p_contrasena OUT usuario.contrasena%TYPE
) IS
BEGIN
    SELECT id_usuario, nombre, contrasena
    INTO p_id_usuario, p_nombre, p_contrasena
    FROM usuario
    WHERE LOWER(correo) = LOWER(p_correo);
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_id_usuario := NULL;
        p_nombre     := 'N/A';
        p_contrasena := 'N/A';
END;
/

-- 2.3 TABLA USUARIO_ROL (llave compuesta: id_usuario + id_rol)

-- Asigna un rol a un usuario
CREATE OR REPLACE PROCEDURE sp_usuario_rol_insertar (
    p_id_usuario IN usuario_rol.id_usuario%TYPE,
    p_id_rol     IN usuario_rol.id_rol%TYPE
) IS
BEGIN
    INSERT INTO usuario_rol (id_usuario, id_rol)
    VALUES (p_id_usuario, p_id_rol);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Cambia un rol del usuario por otro
CREATE OR REPLACE PROCEDURE sp_usuario_rol_actualizar (
    p_id_usuario    IN usuario_rol.id_usuario%TYPE,
    p_id_rol_actual IN usuario_rol.id_rol%TYPE,
    p_id_rol_nuevo  IN usuario_rol.id_rol%TYPE
) IS
BEGIN
    UPDATE usuario_rol
    SET id_rol = p_id_rol_nuevo
    WHERE id_usuario = p_id_usuario
      AND id_rol     = p_id_rol_actual;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Quita un rol a un usuario
CREATE OR REPLACE PROCEDURE sp_usuario_rol_eliminar (
    p_id_usuario IN usuario_rol.id_usuario%TYPE,
    p_id_rol     IN usuario_rol.id_rol%TYPE
) IS
BEGIN
    DELETE FROM usuario_rol
    WHERE id_usuario = p_id_usuario
      AND id_rol     = p_id_rol;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista todas las asignaciones con los nombres (usa JOIN)
CREATE OR REPLACE PROCEDURE sp_usuario_rol_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT ur.id_usuario, u.nombre, ur.id_rol, r.nombre_rol
        FROM usuario_rol ur
        INNER JOIN usuario u ON u.id_usuario = ur.id_usuario
        INNER JOIN rol r     ON r.id_rol     = ur.id_rol
        ORDER BY ur.id_usuario, ur.id_rol;
END;
/

-- Lista los roles de un usuario
CREATE OR REPLACE PROCEDURE sp_usuario_rol_listar_usuario (
    p_id_usuario IN  usuario_rol.id_usuario%TYPE,
    p_cursor     OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT ur.id_rol, r.nombre_rol
        FROM usuario_rol ur
        INNER JOIN rol r ON r.id_rol = ur.id_rol
        WHERE ur.id_usuario = p_id_usuario
        ORDER BY ur.id_rol;
END;
/

-- 2.4 TABLA DIRECCION_ENVIO

-- Registra una direccion de envio para un usuario y devuelve su id
CREATE OR REPLACE PROCEDURE sp_direccion_insertar (
    p_id_usuario    IN  direccion_envio.id_usuario%TYPE,
    p_provincia     IN  direccion_envio.provincia%TYPE,
    p_canton        IN  direccion_envio.canton%TYPE,
    p_distrito      IN  direccion_envio.distrito%TYPE,
    p_senas_exactas IN  direccion_envio.senas_exactas%TYPE,
    p_codigo_postal IN  direccion_envio.codigo_postal%TYPE,
    p_id_direccion  OUT direccion_envio.id_direccion%TYPE
) IS
BEGIN
    SELECT seq_direccion_envio.NEXTVAL INTO p_id_direccion FROM dual;

    INSERT INTO direccion_envio
        (id_direccion, id_usuario, provincia, canton, distrito, senas_exactas, codigo_postal)
    VALUES
        (p_id_direccion, p_id_usuario, p_provincia, p_canton, p_distrito, p_senas_exactas, p_codigo_postal);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Actualiza los datos de una direccion (el dueno no cambia)
CREATE OR REPLACE PROCEDURE sp_direccion_actualizar (
    p_id_direccion  IN direccion_envio.id_direccion%TYPE,
    p_provincia     IN direccion_envio.provincia%TYPE,
    p_canton        IN direccion_envio.canton%TYPE,
    p_distrito      IN direccion_envio.distrito%TYPE,
    p_senas_exactas IN direccion_envio.senas_exactas%TYPE,
    p_codigo_postal IN direccion_envio.codigo_postal%TYPE
) IS
BEGIN
    UPDATE direccion_envio
    SET provincia     = p_provincia,
        canton        = p_canton,
        distrito      = p_distrito,
        senas_exactas = p_senas_exactas,
        codigo_postal = p_codigo_postal
    WHERE id_direccion = p_id_direccion;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Elimina una direccion (falla si algun pedido la usa)
CREATE OR REPLACE PROCEDURE sp_direccion_eliminar (
    p_id_direccion IN direccion_envio.id_direccion%TYPE
) IS
BEGIN
    DELETE FROM direccion_envio
    WHERE id_direccion = p_id_direccion;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista todas las direcciones
CREATE OR REPLACE PROCEDURE sp_direccion_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_direccion, id_usuario, provincia, canton, distrito,
               senas_exactas, codigo_postal
        FROM direccion_envio
        ORDER BY id_direccion;
END;
/

-- Devuelve los datos de una direccion por id.
-- Si no existe: textos en 'N/A' y id_usuario en NULL.
CREATE OR REPLACE PROCEDURE sp_direccion_obtener (
    p_id_direccion  IN  direccion_envio.id_direccion%TYPE,
    p_id_usuario    OUT direccion_envio.id_usuario%TYPE,
    p_provincia     OUT direccion_envio.provincia%TYPE,
    p_canton        OUT direccion_envio.canton%TYPE,
    p_distrito      OUT direccion_envio.distrito%TYPE,
    p_senas_exactas OUT direccion_envio.senas_exactas%TYPE,
    p_codigo_postal OUT direccion_envio.codigo_postal%TYPE
) IS
BEGIN
    SELECT id_usuario, provincia, canton, distrito, senas_exactas, codigo_postal
    INTO p_id_usuario, p_provincia, p_canton, p_distrito, p_senas_exactas, p_codigo_postal
    FROM direccion_envio
    WHERE id_direccion = p_id_direccion;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_id_usuario    := NULL;
        p_provincia     := 'N/A';
        p_canton        := 'N/A';
        p_distrito      := 'N/A';
        p_senas_exactas := 'N/A';
        p_codigo_postal := 'N/A';
END;
/

-- Lista las direcciones de un usuario (para elegir donde enviar)
CREATE OR REPLACE PROCEDURE sp_direccion_listar_usuario (
    p_id_usuario IN  direccion_envio.id_usuario%TYPE,
    p_cursor     OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_direccion, provincia, canton, distrito,
               senas_exactas, codigo_postal
        FROM direccion_envio
        WHERE id_usuario = p_id_usuario
        ORDER BY id_direccion;
END;
/

-- 2.5 TABLA FACTURA

-- Inserta una factura con los montos indicados y devuelve su id
CREATE OR REPLACE PROCEDURE sp_factura_insertar (
    p_id_pedido   IN  factura.id_pedido%TYPE,
    p_subtotal    IN  factura.subtotal%TYPE,
    p_impuesto    IN  factura.impuesto%TYPE,
    p_monto_total IN  factura.monto_total%TYPE,
    p_id_factura  OUT factura.id_factura%TYPE
) IS
BEGIN
    SELECT seq_factura.NEXTVAL INTO p_id_factura FROM dual;

    INSERT INTO factura (id_factura, id_pedido, subtotal, impuesto, monto_total)
    VALUES (p_id_factura, p_id_pedido, p_subtotal, p_impuesto, p_monto_total);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Genera la factura de un pedido calculando los montos:
--   subtotal = monto_total del pedido
--   impuesto = 13% (fn_calcular_iva)
--   total    = subtotal + impuesto
-- (Supuesto: monto_total del pedido NO incluye impuesto.)
-- Si el pedido no existe, p_id_factura queda en NULL.
CREATE OR REPLACE PROCEDURE sp_factura_generar (
    p_id_pedido  IN  pedido.id_pedido%TYPE,
    p_id_factura OUT factura.id_factura%TYPE
) IS
    var_subtotal pedido.monto_total%TYPE;
    var_impuesto factura.impuesto%TYPE;
BEGIN
    -- Lee el monto del pedido
    SELECT monto_total INTO var_subtotal
    FROM pedido
    WHERE id_pedido = p_id_pedido;

    var_impuesto := fn_calcular_iva(var_subtotal);

    SELECT seq_factura.NEXTVAL INTO p_id_factura FROM dual;

    INSERT INTO factura (id_factura, id_pedido, subtotal, impuesto, monto_total)
    VALUES (p_id_factura, p_id_pedido, var_subtotal, var_impuesto, var_subtotal + var_impuesto);

    COMMIT;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_id_factura := NULL;      -- el pedido no existe
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Actualiza los montos de una factura
CREATE OR REPLACE PROCEDURE sp_factura_actualizar (
    p_id_factura  IN factura.id_factura%TYPE,
    p_subtotal    IN factura.subtotal%TYPE,
    p_impuesto    IN factura.impuesto%TYPE,
    p_monto_total IN factura.monto_total%TYPE
) IS
BEGIN
    UPDATE factura
    SET subtotal    = p_subtotal,
        impuesto    = p_impuesto,
        monto_total = p_monto_total
    WHERE id_factura = p_id_factura;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Elimina una factura
CREATE OR REPLACE PROCEDURE sp_factura_eliminar (
    p_id_factura IN factura.id_factura%TYPE
) IS
BEGIN
    DELETE FROM factura
    WHERE id_factura = p_id_factura;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista todas las facturas
CREATE OR REPLACE PROCEDURE sp_factura_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_factura, id_pedido, fecha_emision, subtotal, impuesto, monto_total
        FROM factura
        ORDER BY id_factura;
END;
/

-- Devuelve los datos de una factura por id.
-- Si no existe, todos los datos de salida quedan en NULL.
CREATE OR REPLACE PROCEDURE sp_factura_obtener (
    p_id_factura    IN  factura.id_factura%TYPE,
    p_id_pedido     OUT factura.id_pedido%TYPE,
    p_fecha_emision OUT factura.fecha_emision%TYPE,
    p_subtotal      OUT factura.subtotal%TYPE,
    p_impuesto      OUT factura.impuesto%TYPE,
    p_monto_total   OUT factura.monto_total%TYPE
) IS
BEGIN
    SELECT id_pedido, fecha_emision, subtotal, impuesto, monto_total
    INTO p_id_pedido, p_fecha_emision, p_subtotal, p_impuesto, p_monto_total
    FROM factura
    WHERE id_factura = p_id_factura;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_id_pedido     := NULL;
        p_fecha_emision := NULL;
        p_subtotal      := NULL;
        p_impuesto      := NULL;
        p_monto_total   := NULL;
END;
/

-- Lista las facturas de un cliente (une factura con pedido)
CREATE OR REPLACE PROCEDURE sp_factura_listar_usuario (
    p_id_usuario IN  pedido.id_usuario%TYPE,
    p_cursor     OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT f.id_factura, f.id_pedido, f.fecha_emision,
               f.subtotal, f.impuesto, f.monto_total
        FROM factura f
        INNER JOIN pedido p ON p.id_pedido = f.id_pedido
        WHERE p.id_usuario = p_id_usuario
        ORDER BY f.fecha_emision DESC;
END;
/

-- 2.6 TABLA MENSAJE_CONTACTO (formulario publico, sin cuenta)

-- Guarda un mensaje del formulario de contacto y devuelve su id
CREATE OR REPLACE PROCEDURE sp_mensaje_insertar (
    p_nombre_remitente IN  mensaje_contacto.nombre_remitente%TYPE,
    p_correo_remitente IN  mensaje_contacto.correo_remitente%TYPE,
    p_asunto           IN  mensaje_contacto.asunto%TYPE,
    p_mensaje          IN  mensaje_contacto.mensaje%TYPE,
    p_id_mensaje       OUT mensaje_contacto.id_mensaje%TYPE
) IS
BEGIN
    SELECT seq_mensaje_contacto.NEXTVAL INTO p_id_mensaje FROM dual;

    INSERT INTO mensaje_contacto
        (id_mensaje, nombre_remitente, correo_remitente, asunto, mensaje)
    VALUES
        (p_id_mensaje, p_nombre_remitente, p_correo_remitente, p_asunto, p_mensaje);

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Corrige los datos de un mensaje
CREATE OR REPLACE PROCEDURE sp_mensaje_actualizar (
    p_id_mensaje       IN mensaje_contacto.id_mensaje%TYPE,
    p_nombre_remitente IN mensaje_contacto.nombre_remitente%TYPE,
    p_correo_remitente IN mensaje_contacto.correo_remitente%TYPE,
    p_asunto           IN mensaje_contacto.asunto%TYPE,
    p_mensaje          IN mensaje_contacto.mensaje%TYPE
) IS
BEGIN
    UPDATE mensaje_contacto
    SET nombre_remitente = p_nombre_remitente,
        correo_remitente = p_correo_remitente,
        asunto           = p_asunto,
        mensaje          = p_mensaje
    WHERE id_mensaje = p_id_mensaje;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Elimina un mensaje
CREATE OR REPLACE PROCEDURE sp_mensaje_eliminar (
    p_id_mensaje IN mensaje_contacto.id_mensaje%TYPE
) IS
BEGIN
    DELETE FROM mensaje_contacto
    WHERE id_mensaje = p_id_mensaje;

    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
/

-- Lista los mensajes, del mas reciente al mas antiguo
CREATE OR REPLACE PROCEDURE sp_mensaje_listar (
    p_cursor OUT SYS_REFCURSOR
) IS
BEGIN
    OPEN p_cursor FOR
        SELECT id_mensaje, nombre_remitente, correo_remitente,
               asunto, mensaje, fecha_envio
        FROM mensaje_contacto
        ORDER BY fecha_envio DESC;
END;
/

-- Devuelve los datos de un mensaje por id.
-- Si no existe: textos en 'N/A' y fecha en NULL.
CREATE OR REPLACE PROCEDURE sp_mensaje_obtener (
    p_id_mensaje       IN  mensaje_contacto.id_mensaje%TYPE,
    p_nombre_remitente OUT mensaje_contacto.nombre_remitente%TYPE,
    p_correo_remitente OUT mensaje_contacto.correo_remitente%TYPE,
    p_asunto           OUT mensaje_contacto.asunto%TYPE,
    p_mensaje          OUT mensaje_contacto.mensaje%TYPE,
    p_fecha_envio      OUT mensaje_contacto.fecha_envio%TYPE
) IS
BEGIN
    SELECT nombre_remitente, correo_remitente, asunto, mensaje, fecha_envio
    INTO p_nombre_remitente, p_correo_remitente, p_asunto, p_mensaje, p_fecha_envio
    FROM mensaje_contacto
    WHERE id_mensaje = p_id_mensaje;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_nombre_remitente := 'N/A';
        p_correo_remitente := 'N/A';
        p_asunto           := 'N/A';
        p_mensaje          := 'N/A';
        p_fecha_envio      := NULL;
END;
/


-- 3. PROCEDIMIENTOS CON CURSORES (reportes)
-- Muestran el resultado con DBMS_OUTPUT (activar SET SERVEROUTPUT ON).

-- Reporte: cada usuario con el nombre de su rol.
-- Usa un ciclo FOR sobre la consulta (cursor implicito).
CREATE OR REPLACE PROCEDURE sp_reporte_usuarios_roles IS
BEGIN
    FOR X IN (SELECT u.nombre, u.correo, r.nombre_rol
              FROM usuario u
              INNER JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
              INNER JOIN rol r          ON r.id_rol      = ur.id_rol
              ORDER BY u.nombre) LOOP
        DBMS_OUTPUT.PUT_LINE(X.nombre || ' (' || X.correo || ') - Rol: ' || X.nombre_rol);
    END LOOP;
END;
/

-- Reporte: usuarios que todavia no tienen direccion de envio.
-- Usa un cursor explicito: DECLARE, OPEN, FETCH, CLOSE.
CREATE OR REPLACE PROCEDURE sp_reporte_usuarios_sin_dir IS
    -- Cursor explicito: define la consulta que se va a recorrer
    CURSOR c_sin_direccion IS
        SELECT u.id_usuario, u.nombre, u.correo
        FROM usuario u
        WHERE NOT EXISTS (SELECT 1
                          FROM direccion_envio d
                          WHERE d.id_usuario = u.id_usuario);

    -- Variables con el mismo tipo de las columnas (%TYPE)
    var_id     usuario.id_usuario%TYPE;
    var_nombre usuario.nombre%TYPE;
    var_correo usuario.correo%TYPE;
BEGIN
    OPEN c_sin_direccion;                                   -- abre el cursor
    LOOP
        FETCH c_sin_direccion INTO var_id, var_nombre, var_correo;   -- lee una fila
        EXIT WHEN c_sin_direccion%NOTFOUND;                 -- sale cuando ya no hay mas filas
        DBMS_OUTPUT.PUT_LINE('Sin direccion: ' || var_id || ' - ' || var_nombre || ' (' || var_correo || ')');
    END LOOP;
    CLOSE c_sin_direccion;                                  -- cierra el cursor
END;
/

-- Reporte: total facturado por cliente (usa GROUP BY y SUM).
-- Usa un cursor explicito: DECLARE, OPEN, FETCH, CLOSE.
CREATE OR REPLACE PROCEDURE sp_reporte_total_facturado IS
    CURSOR c_facturado IS
        SELECT u.nombre,
               COUNT(f.id_factura) AS cantidad_facturas,
               SUM(f.monto_total)  AS total_facturado
        FROM usuario u
        INNER JOIN pedido p  ON p.id_usuario = u.id_usuario
        INNER JOIN factura f ON f.id_pedido  = p.id_pedido
        GROUP BY u.nombre
        ORDER BY total_facturado DESC;

    var_nombre   usuario.nombre%TYPE;
    var_cantidad NUMBER;
    var_total    NUMBER;
BEGIN
    OPEN c_facturado;
    LOOP
        FETCH c_facturado INTO var_nombre, var_cantidad, var_total;
        EXIT WHEN c_facturado%NOTFOUND;
        DBMS_OUTPUT.PUT_LINE(var_nombre || ': ' || var_cantidad || ' factura(s), total ' || var_total);
    END LOOP;
    CLOSE c_facturado;
END;
/


-- 4. VISTAS

-- Usuarios con su rol (sin contrasena)
CREATE OR REPLACE VIEW v_usuarios_roles AS
SELECT u.id_usuario, u.nombre, u.correo, u.fecha_registro, r.nombre_rol
FROM usuario u
INNER JOIN usuario_rol ur ON ur.id_usuario = u.id_usuario
INNER JOIN rol r          ON r.id_rol      = ur.id_rol;

-- Direcciones de envio con el nombre de su dueno
CREATE OR REPLACE VIEW v_direcciones_usuario AS
SELECT d.id_direccion, d.id_usuario, u.nombre AS nombre_usuario,
       d.provincia, d.canton, d.distrito, d.senas_exactas, d.codigo_postal
FROM direccion_envio d
INNER JOIN usuario u ON u.id_usuario = d.id_usuario;

-- Facturas con el cliente y el estado del pedido
CREATE OR REPLACE VIEW v_facturas_detalle AS
SELECT f.id_factura, f.fecha_emision, f.subtotal, f.impuesto, f.monto_total,
       p.id_pedido, u.nombre AS cliente, e.nombre_estado AS estado_pedido
FROM factura f
INNER JOIN pedido p        ON p.id_pedido  = f.id_pedido
INNER JOIN usuario u       ON u.id_usuario = p.id_usuario
INNER JOIN estado_pedido e ON e.id_estado  = p.id_estado;

-- Mensajes de contacto, los mas recientes primero
CREATE OR REPLACE VIEW v_mensajes_contacto AS
SELECT id_mensaje, nombre_remitente, correo_remitente, asunto, mensaje, fecha_envio
FROM mensaje_contacto
ORDER BY fecha_envio DESC;


-- 5. VERIFICACION: cantidad de objetos creados y su estado
-- (si algo sale INVALID, ejecutar SHOW ERRORS o revisar USER_ERRORS)
SELECT object_type, COUNT(*) AS cantidad
FROM user_objects
WHERE object_type IN ('PROCEDURE', 'FUNCTION', 'VIEW')
GROUP BY object_type;

SELECT object_name, object_type, status
FROM user_objects
WHERE status = 'INVALID';
