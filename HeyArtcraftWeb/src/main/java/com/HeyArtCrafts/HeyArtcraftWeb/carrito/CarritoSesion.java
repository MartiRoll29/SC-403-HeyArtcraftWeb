package com.HeyArtCrafts.HeyArtcraftWeb.carrito;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Carrito de compras propio de cada sesión HTTP (HU-13/HU-14).
 * No depende de un usuario autenticado: cuando el módulo de login exista,
 * el carrito puede seguir funcionando igual y, opcionalmente, asociarse al
 * usuario en el momento de confirmar la compra.
 */
@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarritoSesion implements Serializable {

    private final List<CarritoItem> items = new ArrayList<>();

    public List<CarritoItem> getItems() {
        return items;
    }

    public void agregar(CarritoItem item) {
        items.add(item);
    }

    public void eliminar(int indice) {
        if (indice >= 0 && indice < items.size()) {
            items.remove(indice);
        }
    }

    public BigDecimal calcularSubtotal() {
        return items.stream()
                .map(CarritoItem::getPrecioUnitario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void vaciar() {
        items.clear();
    }

    public boolean estaVacio() {
        return items.isEmpty();
    }
}
