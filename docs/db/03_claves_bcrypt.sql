-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 03_claves_bcrypt.sql
--
-- Reemplaza las contrasenas semilla (texto plano) de los 3 usuarios de
-- ejemplo por hashes BCrypt, para que puedan iniciar sesion desde la web.
-- Contrasena de demostracion de los tres usuarios: Demo2026!
--
-- COMO EJECUTARLO: conectado como PROYECTO_SC504 en SQL Developer,
-- despues de 20_usuarios.sql. Run Script (F5).
-- No crea objetos: solo llama procedimientos existentes.

SET DEFINE OFF
SET SERVEROUTPUT ON

DECLARE
    -- Busca al usuario por correo y le asigna el hash indicado
    PROCEDURE asignar_clave (
        p_correo IN usuario.correo%TYPE,
        p_hash   IN usuario.contrasena%TYPE
    ) IS
        var_id_usuario usuario.id_usuario%TYPE;
        var_nombre     usuario.nombre%TYPE;
        var_contrasena usuario.contrasena%TYPE;
    BEGIN
        sp_usuario_obtener_correo(p_correo, var_id_usuario, var_nombre, var_contrasena);

        IF var_id_usuario IS NULL THEN
            DBMS_OUTPUT.PUT_LINE('No existe: ' || p_correo);
        ELSE
            sp_usuario_cambiar_contrasena(var_id_usuario, p_hash);
            DBMS_OUTPUT.PUT_LINE('Clave actualizada: ' || p_correo);
        END IF;
    END;
BEGIN
    asignar_clave('admin@ejemplo.com',    '$2a$10$KSJzMKh9YeI3MasdBT0uFenu9Blnr4GjqcuQneNrc2j.p5.SUkn5e');
    asignar_clave('cliente1@ejemplo.com', '$2a$10$yUbAjaRyuWeaxHUNEKd4z.qtvnVMtpUcU/6P/TED8s692ZSKFoV1S');
    asignar_clave('cliente2@ejemplo.com', '$2a$10$pW5stNOygRRqVz1x1SVFRu0XJLKi9T5mRDwSu/qcMgPBzdt2I.bPC');
END;
/
