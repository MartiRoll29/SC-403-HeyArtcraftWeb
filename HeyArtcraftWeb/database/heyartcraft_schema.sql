-- =====================================================================
-- Hey Artcrafts - Script de base de datos
-- Módulo 4 (Catálogo) y Módulo 6 (Carrito de Compras)
-- Ejecutar en MySQL Workbench conectado a su servidor local.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS heyartcraft_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE heyartcraft_db;

-- ---------------------------------------------------------------------
-- Tabla: producto (Módulo 4 - Catálogo)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre              VARCHAR(120)   NOT NULL,
    descripcion         VARCHAR(1000),
    precio              DECIMAL(10,2)  NOT NULL,
    imagen_url          VARCHAR(255),
    tamanos_disponibles VARCHAR(120),
    personalizable      BOOLEAN        NOT NULL DEFAULT TRUE,
    activo              BOOLEAN        NOT NULL DEFAULT TRUE
);

-- ---------------------------------------------------------------------
-- Tabla: pedido (Módulo 6 - Carrito de Compras / tabla transaccional)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pedido (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_creacion  DATETIME       NOT NULL,
    subtotal        DECIMAL(10,2)  NOT NULL,
    incluye_envio   BOOLEAN        NOT NULL DEFAULT FALSE,
    costo_envio     DECIMAL(10,2)  NOT NULL DEFAULT 0,
    total           DECIMAL(10,2)  NOT NULL,
    estado          VARCHAR(30)    NOT NULL DEFAULT 'CONFIRMADO'
);

-- ---------------------------------------------------------------------
-- Tabla: detalle_pedido (líneas del pedido, cada una con su personalización)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id            BIGINT NOT NULL,
    producto_id          BIGINT,
    nombre_producto      VARCHAR(120)  NOT NULL,
    texto_personalizado  VARCHAR(255),
    tamano_seleccionado  VARCHAR(60),
    especificaciones     VARCHAR(500),
    precio_unitario      DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_pedido_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_pedido_producto
        FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE SET NULL
);

-- ---------------------------------------------------------------------
-- Datos de ejemplo: productos del catálogo
-- (imágenes ya incluidas en src/main/resources/static/img)
-- ---------------------------------------------------------------------
INSERT INTO producto (nombre, descripcion, precio, imagen_url, tamanos_disponibles, personalizable, activo) VALUES
('Rótulo Familiar de Madera',
 'Placa circular de madera con el nombre de su familia y una ilustración personalizada de su mascota. Ideal para decorar la entrada del hogar.',
 15000.00, '/img/producto1.jpeg', 'Pequeño,Mediano,Grande', TRUE, TRUE),

('Urna de Recuerdos para Boda',
 'Base decorativa en madera y acrílico grabado con los nombres y la fecha de la boda. Incluye corazones de madera para que los invitados firmen.',
 28000.00, '/img/producto2.jpeg', 'Mediano,Grande', TRUE, TRUE),

('Cajita de Madera para Anillos',
 'Caja circular de madera tallada a mano, ideal para guardar anillos de compromiso o boda. Personalizable con iniciales y fecha especial.',
 9500.00, '/img/producto3.jpeg', 'Pequeño,Mediano', TRUE, TRUE),

('Cuadro de Madera con Versículo',
 'Placa cuadrada de madera grabada con un versículo o frase especial, personalizada con el apellido de la familia.',
 12500.00, '/img/producto4.jpeg', 'Pequeño,Mediano,Grande', TRUE, TRUE);
