package com.codeboost.travel_agency_backend_template.security;

import com.codeboost.travel_agency_backend_template.domain.model.Usuario;
import com.codeboost.travel_agency_backend_template.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log =
            LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        String normalizedEmail = email == null
                ? ""
                : email.strip().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    log.warn("Login fallido — email no registrado: {}", normalizedEmail);
                    return new UsernameNotFoundException(
                            "Usuario no encontrado: " + normalizedEmail);
                });

        if (!usuario.isActivo()) {
            log.warn("Login fallido — cuenta desactivada: {}", normalizedEmail);
            throw new UsernameNotFoundException("Cuenta desactivada: " + normalizedEmail);
        }

        log.info("Login exitoso para: {}", normalizedEmail);

        return new User(
                usuario.getEmail(),
                usuario.getPassword(),
                List.of(new SimpleGrantedAuthority(usuario.getRol()))
        );
    }
}
