-- ============================================================
-- SC-504 Lenguajes de Base de Datos - Proyecto integrador
-- Avance 2 | 01_ddl.sql  (15 tablas, secuencias, PK, FK, CHECK)
-- Autor: Armando Layana Camacho (Opcion 1: Base de datos)
--
-- COMO EJECUTARLO: conectado como PROYECTO_SC504,
--                  SQL Developer > Run Script (F5).
-- Si necesita repetirlo, primero ejecute 99_limpiar.sql.
-- ============================================================

-- ------------------------------------------------------------
-- 1. SECUENCIAS: generan los ids autoincrementales (una por tabla
--    con id propio). Uso: nombre_secuencia.NEXTVAL
-- ------------------------------------------------------------
CREATE SEQUENCE seq_rol              START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_usuario          START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_categoria        START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_producto         START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_estado_pedido    START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_tamano           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_material         START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_producto_tamano  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_direccion_envio  START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_pedido           START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_detalle_pedido   START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_factura          START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE seq_mensaje_contacto START WITH 1 INCREMENT BY 1;

-- ------------------------------------------------------------
-- 2. TABLAS (en orden: primero las que no dependen de otras)
-- ------------------------------------------------------------

-- 2.1 ROL: catalogo de roles del sistema (ADMIN, CLIENTE)
CREATE TABLE rol (
    id_rol      NUMBER(10)    NOT NULL,
    nombre_rol  VARCHAR2(50)  NOT NULL,
    CONSTRAINT pk_rol        PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre_rol)
);

-- 2.2 USUARIO: cuentas registradas
CREATE TABLE usuario (
    id_usuario      NUMBER(10)    NOT NULL,
    nombre          VARCHAR2(100) NOT NULL,
    correo          VARCHAR2(150) NOT NULL,
    contrasena      VARCHAR2(255) NOT NULL,
    fecha_registro  DATE          DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_usuario        PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_correo UNIQUE (correo)
);

-- 2.3 USUARIO_ROL: relacion muchos a muchos entre usuario y rol
CREATE TABLE usuario_rol (
    id_usuario  NUMBER(10) NOT NULL,
    id_rol      NUMBER(10) NOT NULL,
    CONSTRAINT pk_usuario_rol         PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_rol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_usuario_rol_rol     FOREIGN KEY (id_rol)     REFERENCES rol (id_rol)
);

-- 2.4 CATEGORIA: clasificacion de productos (Hogar, Bodas, Regalos...)
CREATE TABLE categoria (
    id_categoria      NUMBER(10)    NOT NULL,
    nombre_categoria  VARCHAR2(100) NOT NULL,
    descripcion       VARCHAR2(500),
    CONSTRAINT pk_categoria        PRIMARY KEY (id_categoria),
    CONSTRAINT uq_categoria_nombre UNIQUE (nombre_categoria)
);

-- 2.5 PRODUCTO: informacion basica del catalogo
CREATE TABLE producto (
    id_producto      NUMBER(10)    NOT NULL,
    id_categoria     NUMBER(10)    NOT NULL,
    nombre_producto  VARCHAR2(150) NOT NULL,
    descripcion      VARCHAR2(1000),
    precio_base      NUMBER(10,2)  NOT NULL,
    imagen_url       VARCHAR2(500),
    CONSTRAINT pk_producto           PRIMARY KEY (id_producto),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria (id_categoria),
    CONSTRAINT ck_producto_precio    CHECK (precio_base >= 0)
);

-- 2.6 ESTADO_PEDIDO: catalogo de estados (sustituye el texto libre)
CREATE TABLE estado_pedido (
    id_estado      NUMBER(10)   NOT NULL,
    nombre_estado  VARCHAR2(50) NOT NULL,
    CONSTRAINT pk_estado_pedido        PRIMARY KEY (id_estado),
    CONSTRAINT uq_estado_pedido_nombre UNIQUE (nombre_estado)
);

-- 2.7 TAMANO: catalogo de dimensiones disponibles
CREATE TABLE tamano (
    id_tamano           NUMBER(10)   NOT NULL,
    descripcion_tamano  VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_tamano        PRIMARY KEY (id_tamano),
    CONSTRAINT uq_tamano_descr  UNIQUE (descripcion_tamano)
);

-- 2.8 MATERIAL: catalogo de materiales (madera, acrilico, cristal...)
CREATE TABLE material (
    id_material      NUMBER(10)   NOT NULL,
    nombre_material  VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_material        PRIMARY KEY (id_material),
    CONSTRAINT uq_material_nombre UNIQUE (nombre_material)
);

-- 2.9 PRODUCTO_TAMANO: tamanos de cada producto con ajuste de precio
CREATE TABLE producto_tamano (
    id_producto_tamano  NUMBER(10)   NOT NULL,
    id_producto         NUMBER(10)   NOT NULL,
    id_tamano           NUMBER(10)   NOT NULL,
    ajuste_precio       NUMBER(10,2) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_producto_tamano          PRIMARY KEY (id_producto_tamano),
    CONSTRAINT fk_producto_tamano_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT fk_producto_tamano_tamano   FOREIGN KEY (id_tamano)   REFERENCES tamano (id_tamano),
    CONSTRAINT uq_producto_tamano          UNIQUE (id_producto, id_tamano)
);

-- 2.10 PRODUCTO_MATERIAL: materiales permitidos por producto
CREATE TABLE producto_material (
    id_producto  NUMBER(10) NOT NULL,
    id_material  NUMBER(10) NOT NULL,
    CONSTRAINT pk_producto_material          PRIMARY KEY (id_producto, id_material),
    CONSTRAINT fk_producto_material_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT fk_producto_material_material FOREIGN KEY (id_material) REFERENCES material (id_material)
);

-- 2.11 DIRECCION_ENVIO: direcciones registradas por cada cliente
CREATE TABLE direccion_envio (
    id_direccion   NUMBER(10)    NOT NULL,
    id_usuario     NUMBER(10)    NOT NULL,
    provincia      VARCHAR2(50)  NOT NULL,
    canton         VARCHAR2(50)  NOT NULL,
    distrito       VARCHAR2(50)  NOT NULL,
    senas_exactas  VARCHAR2(300) NOT NULL,
    codigo_postal  VARCHAR2(10),
    CONSTRAINT pk_direccion_envio         PRIMARY KEY (id_direccion),
    CONSTRAINT fk_direccion_envio_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- 2.12 PEDIDO: orden de compra confirmada
CREATE TABLE pedido (
    id_pedido     NUMBER(10)   NOT NULL,
    id_usuario    NUMBER(10)   NOT NULL,
    id_estado     NUMBER(10)   NOT NULL,
    id_direccion  NUMBER(10)   NOT NULL,
    fecha_pedido  DATE         DEFAULT SYSDATE NOT NULL,
    monto_total   NUMBER(12,2) DEFAULT 0 NOT NULL,
    CONSTRAINT pk_pedido           PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_usuario   FOREIGN KEY (id_usuario)   REFERENCES usuario (id_usuario),
    CONSTRAINT fk_pedido_estado    FOREIGN KEY (id_estado)    REFERENCES estado_pedido (id_estado),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion) REFERENCES direccion_envio (id_direccion),
    CONSTRAINT ck_pedido_monto     CHECK (monto_total >= 0)
);

-- 2.13 DETALLE_PEDIDO: lineas de compra (producto, tamano, material, texto)
CREATE TABLE detalle_pedido (
    id_detalle           NUMBER(10)    NOT NULL,
    id_pedido            NUMBER(10)    NOT NULL,
    id_producto          NUMBER(10)    NOT NULL,
    id_tamano            NUMBER(10)    NOT NULL,
    id_material          NUMBER(10)    NOT NULL,
    cantidad             NUMBER(5)     NOT NULL,
    precio_unitario      NUMBER(10,2)  NOT NULL,
    texto_personalizado  VARCHAR2(500),
    CONSTRAINT pk_detalle_pedido          PRIMARY KEY (id_detalle),
    CONSTRAINT fk_detalle_pedido_pedido   FOREIGN KEY (id_pedido)   REFERENCES pedido (id_pedido),
    CONSTRAINT fk_detalle_pedido_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT fk_detalle_pedido_tamano   FOREIGN KEY (id_tamano)   REFERENCES tamano (id_tamano),
    CONSTRAINT fk_detalle_pedido_material FOREIGN KEY (id_material) REFERENCES material (id_material),
    CONSTRAINT ck_detalle_cantidad        CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio          CHECK (precio_unitario >= 0)
);

-- 2.14 FACTURA: comprobante emitido por pedido (una por pedido)
CREATE TABLE factura (
    id_factura     NUMBER(10)   NOT NULL,
    id_pedido      NUMBER(10)   NOT NULL,
    fecha_emision  DATE         DEFAULT SYSDATE NOT NULL,
    subtotal       NUMBER(12,2) NOT NULL,
    impuesto       NUMBER(12,2) NOT NULL,
    monto_total    NUMBER(12,2) NOT NULL,
    CONSTRAINT pk_factura        PRIMARY KEY (id_factura),
    CONSTRAINT fk_factura_pedido FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido),
    CONSTRAINT uq_factura_pedido UNIQUE (id_pedido),
    CONSTRAINT ck_factura_montos CHECK (subtotal >= 0 AND impuesto >= 0 AND monto_total >= 0)
);

-- 2.15 MENSAJE_CONTACTO: mensajes del formulario publico (tabla independiente,
--      no requiere cuenta de usuario)
CREATE TABLE mensaje_contacto (
    id_mensaje          NUMBER(10)    NOT NULL,
    nombre_remitente    VARCHAR2(100) NOT NULL,
    correo_remitente    VARCHAR2(150) NOT NULL,
    asunto              VARCHAR2(200) NOT NULL,
    mensaje             VARCHAR2(2000) NOT NULL,
    fecha_envio         DATE          DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_mensaje_contacto PRIMARY KEY (id_mensaje)
);
