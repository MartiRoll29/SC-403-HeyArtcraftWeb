-- =====================================================
-- Tablas de usuarios y envío (Oracle)
-- Basado en el diagrama relacional (Fig. 2) del proyecto
-- =====================================================

-- 1. ROL
CREATE TABLE rol (
    id_rol      NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre_rol  VARCHAR2(30) NOT NULL,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uk_rol_nombre UNIQUE (nombre_rol)
);

-- 2. USUARIO
CREATE TABLE usuario (
    id_usuario      NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre          VARCHAR2(100) NOT NULL,
    correo          VARCHAR2(120) NOT NULL,
    contrasena      VARCHAR2(255) NOT NULL,
    fecha_registro  DATE DEFAULT SYSDATE NOT NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uk_usuario_correo UNIQUE (correo)
);

-- 3. USUARIO_ROL (muchos a muchos)
CREATE TABLE usuario_rol (
    id_usuario  NUMBER NOT NULL,
    id_rol      NUMBER NOT NULL,
    CONSTRAINT pk_usuario_rol PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuariorol_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_usuariorol_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol)
);

-- 4. ESTADO_PEDIDO (catálogo de estados)
CREATE TABLE estado_pedido (
    id_estado     NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre_estado VARCHAR2(30) NOT NULL,
    CONSTRAINT pk_estado_pedido PRIMARY KEY (id_estado),
    CONSTRAINT uk_estado_pedido_nombre UNIQUE (nombre_estado)
);

-- 5. DIRECCION_ENVIO (varias por usuario)
CREATE TABLE direccion_envio (
    id_direccion   NUMBER GENERATED ALWAYS AS IDENTITY,
    id_usuario     NUMBER NOT NULL,
    provincia      VARCHAR2(40)  NOT NULL,
    canton         VARCHAR2(40)  NOT NULL,
    distrito       VARCHAR2(40)  NOT NULL,
    senas_exactas  VARCHAR2(250) NOT NULL,
    codigo_postal  VARCHAR2(10),
    CONSTRAINT pk_direccion_envio PRIMARY KEY (id_direccion),
    CONSTRAINT fk_direccion_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

-- 6. PEDIDO
CREATE TABLE pedido (
    id_pedido     NUMBER GENERATED ALWAYS AS IDENTITY,
    id_usuario    NUMBER NOT NULL,
    id_estado     NUMBER NOT NULL,
    id_direccion  NUMBER NOT NULL,
    fecha_pedido  DATE DEFAULT SYSDATE NOT NULL,
    monto_total   NUMBER(12,2) NOT NULL,
    CONSTRAINT pk_pedido PRIMARY KEY (id_pedido),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_pedido_estado FOREIGN KEY (id_estado) REFERENCES estado_pedido (id_estado),
    CONSTRAINT fk_pedido_direccion FOREIGN KEY (id_direccion) REFERENCES direccion_envio (id_direccion)
);

-- =====================================================
-- Datos iniciales de los catálogos
-- (los estados son un ejemplo, ajústalos a tu proyecto)
-- =====================================================
INSERT INTO rol (nombre_rol) VALUES ('ADMIN');
INSERT INTO rol (nombre_rol) VALUES ('CLIENTE');

INSERT INTO estado_pedido (nombre_estado) VALUES ('Pendiente');
INSERT INTO estado_pedido (nombre_estado) VALUES ('Pagado');
INSERT INTO estado_pedido (nombre_estado) VALUES ('En preparacion');
INSERT INTO estado_pedido (nombre_estado) VALUES ('Enviado');
INSERT INTO estado_pedido (nombre_estado) VALUES ('Entregado');
INSERT INTO estado_pedido (nombre_estado) VALUES ('Cancelado');

COMMIT;
