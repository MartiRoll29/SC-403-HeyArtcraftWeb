-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 20_usuarios_pruebas.sql
-- Modulo: USUARIOS Y ENVIO (Opcion 3)
-- Autor: Armando Layana Camacho
--
-- QUE HACE: prueba cada funcion y procedimiento de 20_usuarios.sql
-- con bloques anonimos (DECLARE ... BEGIN ... END) y muestra el
-- resultado con DBMS_OUTPUT. Deja la base como estaba: cada prueba
-- borra lo que crea.
-- COMO EJECUTARLO: conectado como PROYECTO_SC504, Run Script (F5).
-- Cada prueba sirve de captura para el documento.
-- NOTA: en este archivo (solo pruebas) se usa SQL directo para preparar
-- y limpiar datos; la aplicacion web NO puede hacerlo.

SET SERVEROUTPUT ON;

-- LIMPIEZA PREVIA: borra restos de una prueba anterior que se haya interrumpido
DELETE FROM factura WHERE id_pedido IN (SELECT id_pedido FROM pedido WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com'));
DELETE FROM pedido WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com');
DELETE FROM direccion_envio WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com');
DELETE FROM usuario_rol WHERE id_usuario IN (SELECT id_usuario FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com');
DELETE FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com';
DELETE FROM rol WHERE nombre_rol LIKE 'ROL_PRUEBA%';
DELETE FROM mensaje_contacto WHERE correo_remitente = 'visitante@ejemplo.com';
COMMIT;

-- PRUEBA 1: ROL (insertar, actualizar, obtener, listar, eliminar)
DECLARE
    VARIDROL     rol.id_rol%TYPE;
    VARNOMBRE    rol.nombre_rol%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 1. ROL ===');
    sp_rol_insertar('ROL_PRUEBA', VARIDROL);
    DBMS_OUTPUT.PUT_LINE('Rol creado con id: ' || VARIDROL);

    sp_rol_actualizar(VARIDROL, 'ROL_PRUEBA_2');
    sp_rol_obtener(VARIDROL, VARNOMBRE);
    DBMS_OUTPUT.PUT_LINE('Rol obtenido (OUT): ' || VARNOMBRE);

    -- Id que no existe: debe devolver N/A
    sp_rol_obtener(-1, VARNOMBRE);
    DBMS_OUTPUT.PUT_LINE('Rol inexistente (OUT): ' || VARNOMBRE);

    DBMS_OUTPUT.PUT_LINE('Listado de roles:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID rol.id_rol%TYPE;
        VARNOMBRE rol.nombre_rol%TYPE;
    BEGIN
        sp_rol_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARNOMBRE;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARNOMBRE);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    sp_rol_eliminar(VARIDROL);
    DBMS_OUTPUT.PUT_LINE('Rol eliminado');
END;
/

-- PRUEBA 2: USUARIO (insertar, actualizar, contrasena, obtener, listar, funciones, eliminar)
DECLARE
    VARIDUSUARIO usuario.id_usuario%TYPE;
    VARIDOUT     usuario.id_usuario%TYPE;
    VARNOMBRE    usuario.nombre%TYPE;
    VARCORREO    usuario.correo%TYPE;
    VARCLAVE     usuario.contrasena%TYPE;
    VARFECHA     usuario.fecha_registro%TYPE;
    VARTEXTO     VARCHAR2(200);
    VARERROR     BOOLEAN;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 2. USUARIO ===');
    sp_usuario_insertar('Usuario Prueba', 'prueba@ejemplo.com', 'clave_cifrada_1', VARIDUSUARIO);
    DBMS_OUTPUT.PUT_LINE('Usuario creado con id: ' || VARIDUSUARIO);

    sp_usuario_actualizar(VARIDUSUARIO, 'Usuario Prueba Editado', 'prueba@ejemplo.com');
    sp_usuario_cambiar_contrasena(VARIDUSUARIO, 'clave_cifrada_2');

    sp_usuario_obtener(VARIDUSUARIO, VARNOMBRE, VARCORREO, VARFECHA);
    DBMS_OUTPUT.PUT_LINE('Obtener por id (OUT): ' || VARNOMBRE || ' | ' || VARCORREO || ' | ' || VARFECHA);

    sp_usuario_obtener_correo('PRUEBA@ejemplo.com', VARIDOUT, VARNOMBRE, VARCLAVE);
    DBMS_OUTPUT.PUT_LINE('Obtener por correo (OUT): ' || VARIDOUT || ' | ' || VARNOMBRE || ' | ' || VARCLAVE);

    -- Usuario que no existe: debe devolver N/A
    sp_usuario_obtener(-1, VARNOMBRE, VARCORREO, VARFECHA);
    DBMS_OUTPUT.PUT_LINE('Usuario inexistente (OUT): ' || VARNOMBRE || ' | ' || VARCORREO);

    DBMS_OUTPUT.PUT_LINE('Existe el correo (1 = si): ' || fn_usuario_existe_correo('prueba@ejemplo.com'));
    DBMS_OUTPUT.PUT_LINE('Existe otro correo (0 = no): ' || fn_usuario_existe_correo('no_existe@ejemplo.com'));

    -- Funcion con pout_error, igual que el ejemplo del profesor
    VARTEXTO := fn_usuario_nombre(VARIDUSUARIO, VARERROR);
    IF VARERROR THEN
        DBMS_OUTPUT.PUT_LINE('Nombre NO ENCONTRADO ' || VARTEXTO);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Nombre completo (funcion): ' || VARTEXTO);
    END IF;

    VARTEXTO := fn_usuario_nombre(-1, VARERROR);
    IF VARERROR THEN
        DBMS_OUTPUT.PUT_LINE('Nombre NO ENCONTRADO ' || VARTEXTO);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Nombre completo (funcion): ' || VARTEXTO);
    END IF;

    DBMS_OUTPUT.PUT_LINE('Listado de usuarios:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID usuario.id_usuario%TYPE;
        VARNOMBRE usuario.nombre%TYPE;
        VARCORREO usuario.correo%TYPE;
        VARFECHA usuario.fecha_registro%TYPE;
    BEGIN
        sp_usuario_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARNOMBRE, VARCORREO, VARFECHA;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARNOMBRE || ' | ' || VARCORREO || ' | ' || VARFECHA);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    sp_usuario_eliminar(VARIDUSUARIO);
    DBMS_OUTPUT.PUT_LINE('Usuario eliminado');
END;
/

-- PRUEBA 3: USUARIO_ROL (asignar, listar, cambiar, quitar, funcion)
DECLARE
    VARIDUSUARIO usuario.id_usuario%TYPE;
    VARIDROL     rol.id_rol%TYPE;
    VARIDCLIENTE rol.id_rol%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 3. USUARIO_ROL ===');
    -- Datos de apoyo: un usuario y un rol de prueba
    sp_usuario_insertar('Usuario Prueba Rol', 'prueba_rol@ejemplo.com', 'clave_cifrada', VARIDUSUARIO);
    sp_rol_insertar('ROL_PRUEBA', VARIDROL);
    SELECT id_rol INTO VARIDCLIENTE FROM rol WHERE nombre_rol = 'CLIENTE';

    sp_usuario_rol_insertar(VARIDUSUARIO, VARIDCLIENTE);
    DBMS_OUTPUT.PUT_LINE('Roles del usuario de prueba:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARIDROL usuario_rol.id_rol%TYPE;
        VARNOMROL rol.nombre_rol%TYPE;
    BEGIN
        sp_usuario_rol_listar_usuario(VARIDUSUARIO, VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARIDROL, VARNOMROL;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARIDROL || ' | ' || VARNOMROL);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    DBMS_OUTPUT.PUT_LINE('Es CLIENTE (1 = si): ' || fn_usuario_tiene_rol(VARIDUSUARIO, 'CLIENTE'));
    DBMS_OUTPUT.PUT_LINE('Es ADMIN (0 = no): '   || fn_usuario_tiene_rol(VARIDUSUARIO, 'ADMIN'));

    -- Cambia CLIENTE por el rol de prueba
    sp_usuario_rol_actualizar(VARIDUSUARIO, VARIDCLIENTE, VARIDROL);
    DBMS_OUTPUT.PUT_LINE('Todas las asignaciones:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARIDUSU usuario_rol.id_usuario%TYPE;
        VARNOMBRE usuario.nombre%TYPE;
        VARIDROL usuario_rol.id_rol%TYPE;
        VARNOMROL rol.nombre_rol%TYPE;
    BEGIN
        sp_usuario_rol_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARIDUSU, VARNOMBRE, VARIDROL, VARNOMROL;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARIDUSU || ' | ' || VARNOMBRE || ' | ' || VARIDROL || ' | ' || VARNOMROL);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    -- Limpieza
    sp_usuario_rol_eliminar(VARIDUSUARIO, VARIDROL);
    sp_usuario_eliminar(VARIDUSUARIO);
    sp_rol_eliminar(VARIDROL);
    DBMS_OUTPUT.PUT_LINE('Asignacion eliminada');
END;
/

-- PRUEBA 4: DIRECCION_ENVIO (insertar, actualizar, obtener, listar, funciones, eliminar)
DECLARE
    VARIDUSUARIO usuario.id_usuario%TYPE;
    VARIDDIR     direccion_envio.id_direccion%TYPE;
    VARIDDUENO   direccion_envio.id_usuario%TYPE;
    VARPROV      direccion_envio.provincia%TYPE;
    VARCANT      direccion_envio.canton%TYPE;
    VARDIST      direccion_envio.distrito%TYPE;
    VARSENAS     direccion_envio.senas_exactas%TYPE;
    VARCP        direccion_envio.codigo_postal%TYPE;
    VARTEXTO     VARCHAR2(500);
    VARERROR     BOOLEAN;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 4. DIRECCION_ENVIO ===');
    sp_usuario_insertar('Usuario Prueba Dir', 'prueba_dir@ejemplo.com', 'clave_cifrada', VARIDUSUARIO);

    sp_direccion_insertar(VARIDUSUARIO, 'Heredia', 'Heredia', 'Mercedes', 'Casa de prueba', '40101', VARIDDIR);
    DBMS_OUTPUT.PUT_LINE('Direccion creada con id: ' || VARIDDIR);

    sp_direccion_actualizar(VARIDDIR, 'Heredia', 'Barva', 'San Pedro', 'Casa de prueba editada', '40201');

    sp_direccion_obtener(VARIDDIR, VARIDDUENO, VARPROV, VARCANT, VARDIST, VARSENAS, VARCP);
    DBMS_OUTPUT.PUT_LINE('Direccion obtenida (OUT): ' || VARPROV || ' | ' || VARCANT || ' | ' || VARDIST || ' | ' || VARSENAS || ' | ' || VARCP);

    -- Id que no existe: debe devolver N/A
    sp_direccion_obtener(-1, VARIDDUENO, VARPROV, VARCANT, VARDIST, VARSENAS, VARCP);
    DBMS_OUTPUT.PUT_LINE('Direccion inexistente (OUT): ' || VARPROV);

    DBMS_OUTPUT.PUT_LINE('Cantidad de direcciones del usuario: ' || fn_direccion_cantidad(VARIDUSUARIO));

    VARTEXTO := fn_direccion_completa(VARIDDIR, VARERROR);
    IF VARERROR THEN
        DBMS_OUTPUT.PUT_LINE('Direccion NO ENCONTRADA ' || VARTEXTO);
    ELSE
        DBMS_OUTPUT.PUT_LINE('Direccion completa (funcion): ' || VARTEXTO);
    END IF;

    DBMS_OUTPUT.PUT_LINE('Direcciones del usuario:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID direccion_envio.id_direccion%TYPE;
        VARPROV direccion_envio.provincia%TYPE;
        VARCANT direccion_envio.canton%TYPE;
        VARDIST direccion_envio.distrito%TYPE;
        VARSENAS direccion_envio.senas_exactas%TYPE;
        VARCP direccion_envio.codigo_postal%TYPE;
    BEGIN
        sp_direccion_listar_usuario(VARIDUSUARIO, VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARPROV, VARCANT, VARDIST, VARSENAS, VARCP;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARPROV || ' | ' || VARCANT || ' | ' || VARDIST || ' | ' || VARSENAS || ' | ' || VARCP);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    DBMS_OUTPUT.PUT_LINE('Todas las direcciones:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID direccion_envio.id_direccion%TYPE;
        VARIDUSU direccion_envio.id_usuario%TYPE;
        VARPROV direccion_envio.provincia%TYPE;
        VARCANT direccion_envio.canton%TYPE;
        VARDIST direccion_envio.distrito%TYPE;
        VARSENAS direccion_envio.senas_exactas%TYPE;
        VARCP direccion_envio.codigo_postal%TYPE;
    BEGIN
        sp_direccion_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARIDUSU, VARPROV, VARCANT, VARDIST, VARSENAS, VARCP;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARIDUSU || ' | ' || VARPROV || ' | ' || VARCANT || ' | ' || VARDIST || ' | ' || VARSENAS || ' | ' || VARCP);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    -- Limpieza
    sp_direccion_eliminar(VARIDDIR);
    sp_usuario_eliminar(VARIDUSUARIO);
    DBMS_OUTPUT.PUT_LINE('Direccion eliminada');
END;
/

-- PRUEBA 5: FACTURA (generar, insertar, actualizar, obtener, listar, funcion IVA)
-- Necesita un pedido. El modulo de compra todavia no esta listo, asi que
-- SOLO EN ESTA PRUEBA se crea y se borra un pedido con SQL directo.
DECLARE
    VARIDUSUARIO usuario.id_usuario%TYPE;
    VARIDDIR     direccion_envio.id_direccion%TYPE;
    VARIDESTADO  estado_pedido.id_estado%TYPE;
    VARIDPEDIDO  pedido.id_pedido%TYPE;
    VARIDFACTURA factura.id_factura%TYPE;
    VARPEDIDO    factura.id_pedido%TYPE;
    VARFECHA     factura.fecha_emision%TYPE;
    VARSUB       factura.subtotal%TYPE;
    VARIMP       factura.impuesto%TYPE;
    VARTOTAL     factura.monto_total%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 5. FACTURA ===');
    -- Datos de apoyo: usuario, direccion y pedido de prueba
    sp_usuario_insertar('Usuario Prueba Factura', 'prueba_fac@ejemplo.com', 'clave_cifrada', VARIDUSUARIO);
    sp_direccion_insertar(VARIDUSUARIO, 'San Jose', 'Escazu', 'San Rafael', 'Oficina de prueba', '10203', VARIDDIR);
    SELECT id_estado INTO VARIDESTADO FROM estado_pedido WHERE nombre_estado = 'Pendiente';
    INSERT INTO pedido (id_pedido, id_usuario, id_estado, id_direccion, monto_total)
    VALUES (seq_pedido.NEXTVAL, VARIDUSUARIO, VARIDESTADO, VARIDDIR, 20000);
    SELECT seq_pedido.CURRVAL INTO VARIDPEDIDO FROM dual;
    DBMS_OUTPUT.PUT_LINE('Pedido de prueba con id: ' || VARIDPEDIDO || ' (monto 20000)');

    DBMS_OUTPUT.PUT_LINE('IVA de 20000 (debe ser 2600): ' || fn_calcular_iva(20000));

    sp_factura_generar(VARIDPEDIDO, VARIDFACTURA);
    DBMS_OUTPUT.PUT_LINE('Factura generada con id: ' || VARIDFACTURA);

    sp_factura_obtener(VARIDFACTURA, VARPEDIDO, VARFECHA, VARSUB, VARIMP, VARTOTAL);
    DBMS_OUTPUT.PUT_LINE('Factura obtenida (OUT): pedido ' || VARPEDIDO || ' | subtotal ' || VARSUB || ' | impuesto ' || VARIMP || ' | total ' || VARTOTAL);

    -- Pedido que no existe: la factura queda en NULL
    sp_factura_generar(-1, VARIDFACTURA);
    IF VARIDFACTURA IS NULL THEN
        DBMS_OUTPUT.PUT_LINE('Pedido inexistente: no se genero factura');
    END IF;

    -- Factura que no existe: los datos de salida quedan en NULL
    sp_factura_obtener(-1, VARPEDIDO, VARFECHA, VARSUB, VARIMP, VARTOTAL);
    IF VARPEDIDO IS NULL THEN
        DBMS_OUTPUT.PUT_LINE('Factura inexistente: datos de salida en NULL');
    END IF;

    -- Hay una factura por pedido: se elimina la generada y se inserta una manual
    SELECT id_factura INTO VARIDFACTURA FROM factura WHERE id_pedido = VARIDPEDIDO;
    sp_factura_eliminar(VARIDFACTURA);
    sp_factura_insertar(VARIDPEDIDO, 20000, 2600, 22600, VARIDFACTURA);
    DBMS_OUTPUT.PUT_LINE('Factura insertada con id: ' || VARIDFACTURA);

    sp_factura_actualizar(VARIDFACTURA, 20000, 2600, 22600);

    DBMS_OUTPUT.PUT_LINE('Facturas del usuario:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID factura.id_factura%TYPE;
        VARPEDIDO factura.id_pedido%TYPE;
        VARFECHA factura.fecha_emision%TYPE;
        VARSUB factura.subtotal%TYPE;
        VARIMP factura.impuesto%TYPE;
        VARTOTAL factura.monto_total%TYPE;
    BEGIN
        sp_factura_listar_usuario(VARIDUSUARIO, VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARPEDIDO, VARFECHA, VARSUB, VARIMP, VARTOTAL;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARPEDIDO || ' | ' || VARFECHA || ' | ' || VARSUB || ' | ' || VARIMP || ' | ' || VARTOTAL);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    DBMS_OUTPUT.PUT_LINE('Todas las facturas:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID factura.id_factura%TYPE;
        VARPEDIDO factura.id_pedido%TYPE;
        VARFECHA factura.fecha_emision%TYPE;
        VARSUB factura.subtotal%TYPE;
        VARIMP factura.impuesto%TYPE;
        VARTOTAL factura.monto_total%TYPE;
    BEGIN
        sp_factura_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARPEDIDO, VARFECHA, VARSUB, VARIMP, VARTOTAL;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARPEDIDO || ' | ' || VARFECHA || ' | ' || VARSUB || ' | ' || VARIMP || ' | ' || VARTOTAL);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    -- Limpieza
    sp_factura_eliminar(VARIDFACTURA);
    DELETE FROM pedido WHERE id_pedido = VARIDPEDIDO;
    COMMIT;
    sp_direccion_eliminar(VARIDDIR);
    sp_usuario_eliminar(VARIDUSUARIO);
    DBMS_OUTPUT.PUT_LINE('Factura y datos de apoyo eliminados');
END;
/

-- PRUEBA 6: MENSAJE_CONTACTO (insertar, actualizar, obtener, listar, eliminar)
DECLARE
    VARIDMSJ  mensaje_contacto.id_mensaje%TYPE;
    VARNOMBRE mensaje_contacto.nombre_remitente%TYPE;
    VARCORREO mensaje_contacto.correo_remitente%TYPE;
    VARASUNTO mensaje_contacto.asunto%TYPE;
    VARMSJ    mensaje_contacto.mensaje%TYPE;
    VARFECHA  mensaje_contacto.fecha_envio%TYPE;
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 6. MENSAJE_CONTACTO ===');
    sp_mensaje_insertar('Visitante Prueba', 'visitante@ejemplo.com', 'Consulta', 'Hola, quiero cotizar un cuadro.', VARIDMSJ);
    DBMS_OUTPUT.PUT_LINE('Mensaje creado con id: ' || VARIDMSJ);

    sp_mensaje_actualizar(VARIDMSJ, 'Visitante Prueba', 'visitante@ejemplo.com', 'Consulta editada', 'Hola, quiero cotizar dos cuadros.');

    sp_mensaje_obtener(VARIDMSJ, VARNOMBRE, VARCORREO, VARASUNTO, VARMSJ, VARFECHA);
    DBMS_OUTPUT.PUT_LINE('Mensaje obtenido (OUT): ' || VARNOMBRE || ' | ' || VARCORREO || ' | ' || VARASUNTO || ' | ' || VARMSJ);

    -- Id que no existe: debe devolver N/A
    sp_mensaje_obtener(-1, VARNOMBRE, VARCORREO, VARASUNTO, VARMSJ, VARFECHA);
    DBMS_OUTPUT.PUT_LINE('Mensaje inexistente (OUT): ' || VARASUNTO);

    DBMS_OUTPUT.PUT_LINE('Listado de mensajes:');
    DECLARE
        VARCURSOR SYS_REFCURSOR;
        VARID mensaje_contacto.id_mensaje%TYPE;
        VARNOMBRE mensaje_contacto.nombre_remitente%TYPE;
        VARCORREO mensaje_contacto.correo_remitente%TYPE;
        VARASUNTO mensaje_contacto.asunto%TYPE;
        VARMSJ mensaje_contacto.mensaje%TYPE;
        VARFECHA mensaje_contacto.fecha_envio%TYPE;
    BEGIN
        sp_mensaje_listar(VARCURSOR);
        LOOP
            FETCH VARCURSOR INTO VARID, VARNOMBRE, VARCORREO, VARASUNTO, VARMSJ, VARFECHA;
            EXIT WHEN VARCURSOR%NOTFOUND;
            DBMS_OUTPUT.PUT_LINE(VARID || ' | ' || VARNOMBRE || ' | ' || VARCORREO || ' | ' || VARASUNTO || ' | ' || VARMSJ || ' | ' || VARFECHA);
        END LOOP;
        CLOSE VARCURSOR;
    END;

    sp_mensaje_eliminar(VARIDMSJ);
    DBMS_OUTPUT.PUT_LINE('Mensaje eliminado');
END;
/

-- PRUEBA 7: REPORTES con cursores (usan los datos semilla)
BEGIN
    DBMS_OUTPUT.PUT_LINE('=== 7. REPORTE: usuarios con su rol ===');
    sp_reporte_usuarios_roles;
    DBMS_OUTPUT.PUT_LINE('=== 7. REPORTE: usuarios sin direccion ===');
    sp_reporte_usuarios_sin_dir;
    DBMS_OUTPUT.PUT_LINE('=== 7. REPORTE: total facturado por cliente ===');
    sp_reporte_total_facturado;
END;
/

-- PRUEBA 8: VISTAS
SELECT * FROM v_usuarios_roles;
SELECT * FROM v_direcciones_usuario;
SELECT * FROM v_facturas_detalle;
SELECT * FROM v_mensajes_contacto;

-- FIN: estas consultas deben devolver 0 (las pruebas se limpiaron solas)
SELECT COUNT(*) AS usuarios_de_prueba FROM usuario WHERE correo LIKE 'prueba%@ejemplo.com';
SELECT COUNT(*) AS roles_de_prueba    FROM rol WHERE nombre_rol LIKE 'ROL_PRUEBA%';
SELECT COUNT(*) AS mensajes_de_prueba FROM mensaje_contacto WHERE correo_remitente = 'visitante@ejemplo.com';
