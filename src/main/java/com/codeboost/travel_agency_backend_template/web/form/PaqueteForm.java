package com.codeboost.travel_agency_backend_template.web.form;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class PaqueteForm {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres.")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres.")
    private String descripcion;

    @NotBlank(message = "El destino es obligatorio.")
    @Size(max = 100, message = "El destino no debe superar 100 caracteres.")
    private String destino;

    @NotBlank(message = "La categoría es obligatoria.")
    @Size(max = 100, message = "La categoría no debe superar 100 caracteres.")
    private String categoria;

    @Size(max = 200, message = "Los badges no deben superar 200 caracteres.")
    private String badges;

    @DecimalMin(value = "0.00", message = "El precio no puede ser negativo.")
    @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 8 enteros y 2 decimales.")
    private BigDecimal precio;

    @NotNull(message = "El orden es obligatorio.")
    @Min(value = 1, message = "El orden mínimo es 1.")
    @Max(value = 9999, message = "El orden máximo es 9999.")
    private Integer orden = 1;

    @Size(max = 300, message = "El mensaje de WhatsApp no debe superar 300 caracteres.")
    private String waMensaje;

    private boolean activo = true;
    private String imagenUrl;
    private MultipartFile imagen;

    public static PaqueteForm desde(Paquete paquete) {
        PaqueteForm form = new PaqueteForm();
        form.setId(paquete.getId());
        form.setNombre(paquete.getNombre());
        form.setDescripcion(paquete.getDescripcion());
        form.setDestino(paquete.getDestino());
        form.setCategoria(paquete.getCategoria());
        form.setBadges(paquete.getBadges());
        form.setPrecio(paquete.getPrecio());
        form.setOrden(paquete.getOrden() > 0 ? paquete.getOrden() : 1);
        form.setWaMensaje(paquete.getWaMensaje());
        form.setActivo(paquete.isActivo());
        form.setImagenUrl(paquete.getImagenUrl());
        return form;
    }

    public Paquete aPaquete() {
        Paquete paquete = new Paquete();
        paquete.setNombre(nombre);
        paquete.setDescripcion(descripcion);
        paquete.setDestino(destino);
        paquete.setCategoria(categoria);
        paquete.setBadges(badges);
        paquete.setPrecio(precio);
        paquete.setOrden(orden == null ? 0 : orden);
        paquete.setWaMensaje(waMensaje);
        paquete.setActivo(activo);
        return paquete;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getBadges() { return badges; }
    public void setBadges(String badges) { this.badges = badges; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public String getWaMensaje() { return waMensaje; }
    public void setWaMensaje(String waMensaje) { this.waMensaje = waMensaje; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
    public MultipartFile getImagen() { return imagen; }
    public void setImagen(MultipartFile imagen) { this.imagen = imagen; }
}
