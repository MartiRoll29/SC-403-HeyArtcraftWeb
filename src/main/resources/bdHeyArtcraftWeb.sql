-- CREAR USUARIO
-- (DROP USER IF EXISTS requiere MySQL 5.7+; con GRANT en la versión
-- instalada aquí basta con CREATE USER en una base nueva)
CREATE USER 'heyartcraftweb'@'localhost'
IDENTIFIED BY '123456';

-- CREAR BASE DE DATOS
DROP DATABASE IF EXISTS heyartcraftweb;
CREATE DATABASE heyartcraftweb CHARACTER SET utf8 COLLATE utf8_unicode_ci;
GRANT ALL PRIVILEGES ON heyartcraftweb.*
TO 'heyartcraftweb'@'localhost';
FLUSH PRIVILEGES;
USE heyartcraftweb;

-- TABLA CATEGORIA
CREATE TABLE categoria (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(100) NOT NULL
);

-- TABLA PRODUCTO
CREATE TABLE producto (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(100) NOT NULL,
precio DECIMAL(8,2) NOT NULL,
ruta_imagen varchar(1024),
descripcion VARCHAR(1000),
tamanos_disponibles VARCHAR(120),
personalizable BOOLEAN NOT NULL DEFAULT TRUE,
categoria_id INT NOT NULL,
FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- TABLA PEDIDO (Módulo 6 - Carrito de Compras, tabla transaccional)
CREATE TABLE pedido (
id INT AUTO_INCREMENT PRIMARY KEY,
fecha_creacion DATETIME NOT NULL,
subtotal DECIMAL(10,2) NOT NULL,
incluye_envio BOOLEAN NOT NULL DEFAULT FALSE,
costo_envio DECIMAL(10,2) NOT NULL DEFAULT 0,
total DECIMAL(10,2) NOT NULL,
estado VARCHAR(30) NOT NULL DEFAULT 'CONFIRMADO'
);

-- TABLA DETALLE_PEDIDO (líneas del pedido, cada una con su personalización)
CREATE TABLE detalle_pedido (
id INT AUTO_INCREMENT PRIMARY KEY,
pedido_id INT NOT NULL,
producto_id INT,
nombre_producto VARCHAR(120) NOT NULL,
texto_personalizado VARCHAR(255),
tamano_seleccionado VARCHAR(60),
especificaciones VARCHAR(500),
precio_unitario DECIMAL(10,2) NOT NULL,
FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
FOREIGN KEY (producto_id) REFERENCES producto(id) ON DELETE SET NULL
);

-- DATOS DE PRUEBA
-- Categorías
INSERT INTO categoria (nombre) VALUES
('Hogar'),
('Bodas'),
('Regalitos');

-- Producto
-- (rutas de imagen corregidas para apuntar a los archivos reales en static/img;
--  antes las 12 filas apuntaban a la misma URL rota de GitHub)
INSERT INTO producto (nombre, precio, ruta_imagen, descripcion, tamanos_disponibles, personalizable, categoria_id) VALUES
('Letrero 3 Hilos', 15000.00, '/img/3hilos.png', 'Letrero de madera grabado con la frase "La cuerda de tres hilos no se rompe fácilmente", personalizado con el apellido de la familia.', 'Pequeño,Mediano,Grande', TRUE, 1),
('Espejo de pared', 25000.00, '/img/espejo.png', 'Espejo decorativo con marco de madera personalizado, ideal para la sala o el cuarto.', 'Mediano,Grande', TRUE, 1),
('Rótulo Pared', 12000.00, '/img/rotulo.png', 'Rótulo de madera para pared con el nombre o frase que usted elija.', 'Pequeño,Mediano,Grande', TRUE, 1),
('Letreros de Pared', 10000.00, '/img/letrero.png', 'Letrero decorativo de madera personalizado para decorar cualquier espacio del hogar.', 'Pequeño,Mediano', TRUE, 1),
('Círculo de Promesas', 42000.00, '/img/promesa.png', 'Base circular en madera y acrílico grabada con los nombres y la fecha de la boda, con espacio para que los invitados dejen su firma.', 'Mediano,Grande', TRUE, 2),
('Portanillos', 12500.00, '/img/portanillos.png', 'Cajita de madera tallada para guardar los anillos de compromiso o boda, personalizable con iniciales y fecha.', 'Pequeño,Mediano', TRUE, 2),
('Nombres Personalizados', 250.00, '/img/nombre.png', 'Nombre individual cortado en madera o acrílico, ideal para decoración de mesas o regalos.', 'Pequeño,Mediano,Grande', TRUE, 2),
('Detalle', 5900.00, '/img/detalle.png', 'Detalle decorativo personalizado, perfecto como recuerdo de boda o evento especial.', 'Único', TRUE, 2),
('Joyeros', 15800.00, '/img/joyero.png', 'Joyero de madera con tapa personalizada, ideal para guardar accesorios.', 'Pequeño,Mediano,Grande', TRUE, 3),
('Estante Carritos', 12750.00, '/img/estante.png', 'Estante decorativo de madera personalizado con el nombre que usted elija.', 'Mediano,Grande', TRUE, 3),
('Álbum Estuche', 16200.00, '/img/estuche.PNG', 'Estuche de madera para álbum de fotos, personalizado con nombres y fecha.', 'Mediano,Grande', TRUE, 3),
('Soporte telefónico', 11000.00, '/img/soporte.png', 'Soporte de madera para teléfono, personalizable con iniciales o frase corta.', 'Único', TRUE, 3);

-- Fin del Script
