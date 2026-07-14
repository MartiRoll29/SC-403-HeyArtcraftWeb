-- CREAR USUARIO
DROP USER IF EXISTS 'usuario_heyartcraftweb'@'localhost';
CREATE USER 'usuario_heyartcraftweb'@'localhost'
IDENTIFIED BY '123456';

-- CREAR BASE DE DATOS
DROP DATABASE IF EXISTS heyartcraftweb;
CREATE DATABASE heyartcraftweb;
GRANT ALL PRIVILEGES ON heyartcraftweb.*
TO 'usuario_heyartcraftweb'@'localhost';
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
categoria_id INT NOT NULL,
FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

-- DATOS DE PRUEBA
-- Categorías
INSERT INTO categoria (nombre) VALUES
('Hogar'),
('Bodas'),
('Regalitos');

-- Producto
INSERT INTO producto (nombre, precio, ruta_imagen, categoria_id) VALUES
('Letrero 3 Hilos', 15000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 1),
('Espejo de pared', 25000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 1),
('Rótulo Pared', 12000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 1),
('Letreros de Pared', 10000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 1),
('Círculo de Promesas', 42000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 2),
('Portanillos', 12500.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 2),
('Nombres Personalizados', 250.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 2),
('Detalle', 5900.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 2),
('Joyeros', 15800.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 3),
('Estante Carritos', 12750.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 3),
('Álbum Estuche', 16200.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 3),
('Soporte telefónico', 11000.00, 'https://github.com/aryets/SC-403-HeyArtcraftWeb/blob/Pre-Production/src/main/resources/static/img/producto.jpeg', 3);

-- Fin del Script