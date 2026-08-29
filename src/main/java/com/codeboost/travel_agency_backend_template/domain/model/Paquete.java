package com.codeboost.travel_agency_backend_template.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "paquetes")
public class Paquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500)
    @Column(length = 500)
    private String descripcion;

    // Icono FontAwesome: "fas fa-map-marked-alt"
    @Column(length = 60)
    private String icono = "fas fa-globe";

    // Badges separados por coma: "Leisure,Grupos"
    @Column(length = 200)
    private String badges;

    // URL imagen Cloudinary
    @Column(length = 500)
    private String imagenUrl;

    // Public ID de Cloudinary (para poder eliminarla)
    @Column(length = 200)
    private String imagenPublicId;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(nullable = false)
    private int orden = 1;

    // Precio "Desde" en MXN — null = "Consultar precio"
    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Campos a AGREGAR en Paquete.java:

// "Quintana Roo", "Suiza", "Italia", etc.
@Column(length = 100)
private String destino;

// "Leisure", "Grupos de Lujo", "Corporativo · Team Building"
@Column(length = 100)
private String categoria;

// Mensaje para el botón de WhatsApp
@Column(length = 300)
private String waMensaje;

// Si es la card grande (featured)
@Column(nullable = false)
private boolean featured = false;

    /* ── Constructores ── */
    public Paquete() {}

    /* ── Getters y Setters ── */
    public Long getId()                      { return id; }
    public void setId(Long id)               { this.id = id; }

    public String getNombre()                { return nombre; }
    public void setNombre(String n)          { this.nombre = n; }

    public String getDescripcion()           { return descripcion; }
    public void setDescripcion(String d)     { this.descripcion = d; }

    public String getIcono()                 { return icono; }
    public void setIcono(String i)           { this.icono = i; }

    public String getBadges()                { return badges; }
    public void setBadges(String b)          { this.badges = b; }

    public String getImagenUrl()             { return imagenUrl; }
    public void setImagenUrl(String u)       { this.imagenUrl = u; }

    public String getImagenPublicId()        { return imagenPublicId; }
    public void setImagenPublicId(String p)  { this.imagenPublicId = p; }

    public boolean isActivo()                { return activo; }
    public void setActivo(boolean a)         { this.activo = a; }

    public int getOrden()                    { return orden; }
    public void setOrden(int o)              { this.orden = o; }

    public BigDecimal getPrecio()            { return precio; }
    public void setPrecio(BigDecimal p)      { this.precio = p; }

    public LocalDateTime getCreatedAt()      { return createdAt; }
    public void setCreatedAt(LocalDateTime c){ this.createdAt = c; }

    public LocalDateTime getUpdatedAt()      { return updatedAt; }
    public void setUpdatedAt(LocalDateTime u){ this.updatedAt = u; }

    public String getDestino()               { return destino; }
    public void setDestino(String d)         { this.destino = d; }

    public String getCategoria()             { return categoria; }
    public void setCategoria(String c)       { this.categoria = c; }

    public String getWaMensaje()             { return waMensaje; }
    public void setWaMensaje(String w)       { this.waMensaje = w; }

    public boolean isFeatured()              { return featured; }
    public void setFeatured(boolean f)       { this.featured = f; }

    /* Helper: lista de badges */
    public String[] getBadgesArray() {
        if (badges == null || badges.isBlank()) return new String[0];
        return badges.split(",");
    }
}
