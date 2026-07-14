package com.HeyArtCrafts.HeyArtcraftWeb.controller;

import com.HeyArtCrafts.HeyArtcraftWeb.carrito.CarritoSesion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expone la cantidad de artículos en el carrito a todas las vistas
 * (usado en el navbar), sin que cada controlador tenga que repetirlo.
 */
@ControllerAdvice
public class CarritoModelAdvice {

    private final CarritoSesion carritoSesion;

    @Autowired
    public CarritoModelAdvice(CarritoSesion carritoSesion) {
        this.carritoSesion = carritoSesion;
    }

    @ModelAttribute("carritoCount")
    public int carritoCount() {
        return carritoSesion.getItems().size();
    }
}
