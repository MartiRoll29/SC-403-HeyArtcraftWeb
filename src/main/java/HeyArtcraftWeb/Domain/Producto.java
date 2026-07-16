package HeyArtcraftWeb.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private BigDecimal precio;
    @Column(name = "ruta_imagen", length = 1024)
    private String imagen;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "tamanos_disponibles", length = 120)
    private String tamanosDisponibles;

    @Column(nullable = false)
    private boolean personalizable = true;

    @Column(nullable = false)
    private boolean destacado = false;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
    public Categoria getCategoria() {
        return categoria;
    }
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTamanosDisponibles() {
        return tamanosDisponibles;
    }
    public void setTamanosDisponibles(String tamanosDisponibles) {
        this.tamanosDisponibles = tamanosDisponibles;
    }

    public boolean isPersonalizable() {
        return personalizable;
    }
    public void setPersonalizable(boolean personalizable) {
        this.personalizable = personalizable;
    }

    public boolean isDestacado() {
        return destacado;
    }
    public void setDestacado(boolean destacado) {
        this.destacado = destacado;
    }
}