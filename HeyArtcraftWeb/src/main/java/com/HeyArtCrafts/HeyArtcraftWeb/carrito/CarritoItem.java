package com.HeyArtCrafts.HeyArtcraftWeb.carrito;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Representa un producto personalizado agregado al carrito.
 * Vive únicamente en la sesión HTTP hasta que se confirma la compra (HU-15),
 * momento en el que se persiste como DetallePedido.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem implements Serializable {

    private Long productoId;
    private String nombreProducto;
    private String imagenUrl;
    private String textoPersonalizado;
    private String tamanoSeleccionado;
    private String especificaciones;
    private BigDecimal precioUnitario;
}
