-- ============================================================
-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 00_crear_usuario.sql
-- Autor: Armando Layana Camacho (Opcion 1: Base de datos)
--
-- QUE HACE: crea el usuario (esquema) del proyecto en Oracle Cloud.
-- COMO EJECUTARLO: conectado como ADMIN (conexion LenguajeBD_ADMIN),
--                  SQL Developer > Run Script (F5).
-- IMPORTANTE: cambie la clave antes de ejecutar y NO suba este
--             archivo a GitHub con la clave real.
-- ============================================================

-- Crea el usuario del proyecto (la clave en Autonomous Database debe
-- tener 12-30 caracteres, con mayuscula, minuscula y numero)
CREATE USER proyecto_sc504 IDENTIFIED BY "CambiaEstaClave2026";

-- Permisos necesarios para crear los objetos del proyecto
GRANT CREATE SESSION   TO proyecto_sc504;
GRANT CREATE TABLE     TO proyecto_sc504;
GRANT CREATE SEQUENCE  TO proyecto_sc504;
GRANT CREATE VIEW      TO proyecto_sc504;
GRANT CREATE PROCEDURE TO proyecto_sc504;
GRANT CREATE TRIGGER   TO proyecto_sc504;

-- Espacio para guardar datos (tablespace DATA en Autonomous Database)
ALTER USER proyecto_sc504 QUOTA UNLIMITED ON DATA;
