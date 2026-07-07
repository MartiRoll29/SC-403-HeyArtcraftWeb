package com.HeyArtCrafts.HeyArtcraftWeb.repository;

import com.HeyArtCrafts.HeyArtcraftWeb.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();
}
