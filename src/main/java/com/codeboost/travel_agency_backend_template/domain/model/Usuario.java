package com.codeboost.travel_agency_backend_template.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios",
       indexes = @Index(name = "idx_email", columnList = "email"))
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80)
    @Column(nullable = false, length = 80)
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Correo inválido")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    private String rol = "ROLE_USER";

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /* ── Constructores ── */
    public Usuario() {}

    public Usuario(Long id, String nombre, String email,
                   String password, String rol,
                   boolean activo, LocalDateTime createdAt) {
        this.id        = id;
        this.nombre    = nombre;
        this.email     = email;
        this.password  = password;
        this.rol       = rol;
        this.activo    = activo;
        this.createdAt = createdAt;
    }

    /* ── Builder estático ── */
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long          id;
        private String        nombre;
        private String        email;
        private String        password;
        private String        rol       = "ROLE_USER";
        private boolean       activo    = true;
        private LocalDateTime createdAt = LocalDateTime.now();

        public Builder id(Long id)                  { this.id = id; return this; }
        public Builder nombre(String nombre)        { this.nombre = nombre; return this; }
        public Builder email(String email)          { this.email = email; return this; }
        public Builder password(String password)    { this.password = password; return this; }
        public Builder rol(String rol)              { this.rol = rol; return this; }
        public Builder activo(boolean activo)       { this.activo = activo; return this; }
        public Builder createdAt(LocalDateTime c)   { this.createdAt = c; return this; }

        public Usuario build() {
            Usuario u = new Usuario();
            u.id        = this.id;
            u.nombre    = this.nombre;
            u.email     = this.email;
            u.password  = this.password;
            u.rol       = this.rol;
            u.activo    = this.activo;
            u.createdAt = this.createdAt;
            return u;
        }
    }

    /* ── Getters ── */
    public Long          getId()        { return id; }
    public String        getNombre()    { return nombre; }
    public String        getEmail()     { return email; }
    public String        getPassword()  { return password; }
    public String        getRol()       { return rol; }
    public boolean       isActivo()     { return activo; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    /* ── Setters ── */
    public void setId(Long id)                    { this.id = id; }
    public void setNombre(String nombre)          { this.nombre = nombre; }
    public void setEmail(String email)            { this.email = email; }
    public void setPassword(String password)      { this.password = password; }
    public void setRol(String rol)                { this.rol = rol; }
    public void setActivo(boolean activo)         { this.activo = activo; }
    public void setCreatedAt(LocalDateTime c)     { this.createdAt = c; }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", email='" + email + "', rol='" + rol + "'}";
    }
}