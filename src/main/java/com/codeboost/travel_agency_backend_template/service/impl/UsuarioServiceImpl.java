package com.codeboost.travel_agency_backend_template.service.impl;

import com.codeboost.travel_agency_backend_template.domain.model.Usuario;
import com.codeboost.travel_agency_backend_template.repository.UsuarioRepository;
import com.codeboost.travel_agency_backend_template.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log =
            LoggerFactory.getLogger(UsuarioServiceImpl.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder   passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    @Override
    @Transactional
    public Usuario registrar(String nombre, String email, String password) {
        if (usuarioRepository.existsByEmail(email))
            throw new IllegalArgumentException("Correo ya registrado: " + email);
        Usuario u = Usuario.builder()
                .nombre(nombre).email(email)
                .password(passwordEncoder.encode(password))
                .build();
        log.info("Nuevo usuario: {}", email);
        return usuarioRepository.save(u);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    @Override @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    @Override @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    @Transactional
    public Usuario actualizar(Long id, String nombre, String email,
                              String rol, boolean activo) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        // Si cambia email, verificar que no exista
        if (!u.getEmail().equals(email) && usuarioRepository.existsByEmail(email))
            throw new IllegalArgumentException("El correo ya está en uso.");
        u.setNombre(nombre);
        u.setEmail(email);
        u.setRol(rol);
        u.setActivo(activo);
        return usuarioRepository.save(u);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        if (!passwordEncoder.matches(passwordActual, u.getPassword()))
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        if (passwordNueva.length() < 8)
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres.");
        u.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(u);
        log.info("Contraseña cambiada para usuario ID: {}", id);
    }

    @Override
    @Transactional
    public void cambiarPasswordAdmin(Long id, String passwordNueva) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        if (passwordNueva.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        u.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(u);
        log.info("Admin cambió contraseña del usuario ID: {}", id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado: " + id);
        usuarioRepository.deleteById(id);
        log.info("Usuario eliminado ID: {}", id);
    }
}