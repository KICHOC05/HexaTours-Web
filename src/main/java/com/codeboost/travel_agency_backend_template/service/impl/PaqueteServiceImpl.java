package com.codeboost.travel_agency_backend_template.service.impl;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import com.codeboost.travel_agency_backend_template.repository.PaqueteRepository;
import com.codeboost.travel_agency_backend_template.service.CloudinaryService;
import com.codeboost.travel_agency_backend_template.service.PaqueteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaqueteServiceImpl implements PaqueteService {

    private static final Logger log =
            LoggerFactory.getLogger(PaqueteServiceImpl.class);

    private final PaqueteRepository paqueteRepository;
    private final CloudinaryService cloudinaryService;

    // "niagara-viajes" es el valor por defecto si no está en properties
    @Value("${cloudinary.folder:niagara-viajes}")
    private String folder;

    public PaqueteServiceImpl(PaqueteRepository paqueteRepository,
                              CloudinaryService cloudinaryService) {
        this.paqueteRepository = paqueteRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> listarTodos() {
        return paqueteRepository.findTodosOrdenados();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> listarActivos() {
        return paqueteRepository.findActivosOrdenados();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paquete> buscarPorId(Long id) {
        return paqueteRepository.findById(id);
    }

    @Override
    @Transactional
    public Paquete guardar(Paquete paquete, MultipartFile imagen) {
        validarOrdenDisponible(paquete.getOrden(), null);
        normalizarCampos(paquete);
        boolean imagenSubida = tieneImagen(imagen);
        subirImagenSiExiste(paquete, imagen);
        log.info("Guardando nuevo paquete: {}", paquete.getNombre());
        try {
            return paqueteRepository.save(paquete);
        } catch (RuntimeException e) {
            if (imagenSubida) {
                cloudinaryService.delete(paquete.getImagenPublicId());
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public Paquete actualizar(Long id, Paquete datos, MultipartFile imagen) {
        Paquete existing = paqueteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Paquete no encontrado con ID: " + id));

        validarOrdenDisponible(datos.getOrden(), id);
        normalizarCampos(datos);

        // Actualizar campos
        existing.setNombre(datos.getNombre());
        existing.setDescripcion(datos.getDescripcion());
        existing.setIcono(datos.getIcono());
        existing.setBadges(datos.getBadges());
        existing.setOrden(datos.getOrden());
        existing.setPrecio(datos.getPrecio());
        existing.setActivo(datos.isActivo());
        existing.setDestino(datos.getDestino());
        existing.setCategoria(datos.getCategoria());
        existing.setWaMensaje(datos.getWaMensaje());
        existing.setFeatured(false);
        existing.setUpdatedAt(LocalDateTime.now());

        String publicIdAnterior = existing.getImagenPublicId();
        boolean reemplazaImagen = tieneImagen(imagen);
        subirImagenSiExiste(existing, imagen);

        log.info("Actualizando paquete ID {}: {}", id, existing.getNombre());
        try {
            Paquete actualizado = reemplazaImagen
                    ? paqueteRepository.saveAndFlush(existing)
                    : paqueteRepository.save(existing);

            if (reemplazaImagen
                    && publicIdAnterior != null
                    && !publicIdAnterior.isBlank()
                    && !publicIdAnterior.equals(actualizado.getImagenPublicId())) {
                cloudinaryService.delete(publicIdAnterior);
            }
            return actualizado;
        } catch (RuntimeException e) {
            if (reemplazaImagen) {
                cloudinaryService.delete(existing.getImagenPublicId());
            }
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean ordenEnUso(int orden, Long paqueteIdExcluido) {
        return paqueteIdExcluido == null
                ? paqueteRepository.existsByOrden(orden)
                : paqueteRepository.existsByOrdenAndIdNot(orden, paqueteIdExcluido);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Paquete p = paqueteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Paquete no encontrado con ID: " + id));

        // Eliminar imagen de Cloudinary primero
        cloudinaryService.delete(p.getImagenPublicId());

        paqueteRepository.deleteById(id);
        log.info("Paquete eliminado — ID: {} | nombre: {}", id, p.getNombre());
    }

    @Override
    @Transactional
    public void toggleActivo(Long id) {
        Paquete p = paqueteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Paquete no encontrado con ID: " + id));
        p.setActivo(!p.isActivo());
        p.setUpdatedAt(LocalDateTime.now());
        paqueteRepository.save(p);
        log.info("Toggle activo — ID: {} | activo: {}", id, p.isActivo());
    }

    /* ─────────────────────────────────────────
       Método privado: sube imagen si existe
       La eliminación de una imagen anterior se realiza después de persistir
    ───────────────────────────────────────── */
    private void subirImagenSiExiste(Paquete paquete,
                                     MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) return;

        try {
            // Primero subir la nueva imagen para conservar la anterior si falla la carga.
            Map<String, String> resultado =
                    cloudinaryService.upload(imagen, folder);

            paquete.setImagenUrl(resultado.get("url"));
            paquete.setImagenPublicId(resultado.get("public_id"));

        } catch (Exception e) {
            log.error("Error procesando imagen para paquete '{}': {}",
                    paquete.getNombre(), e.getMessage());
            throw new RuntimeException(
                    "Error al procesar la imagen: " + e.getMessage());
        }
    }

    private void validarOrdenDisponible(int orden, Long paqueteIdExcluido) {
        if (orden < 1 || orden > 9999) {
            throw new IllegalArgumentException("El orden debe estar entre 1 y 9999.");
        }
        if (ordenEnUso(orden, paqueteIdExcluido)) {
            throw new IllegalArgumentException("El número de orden ya está asignado a otro paquete.");
        }
    }

    private boolean tieneImagen(MultipartFile imagen) {
        return imagen != null && !imagen.isEmpty();
    }

    private void normalizarCampos(Paquete paquete) {
        paquete.setNombre(limpiar(paquete.getNombre()));
        paquete.setDescripcion(limpiar(paquete.getDescripcion()));
        paquete.setDestino(limpiar(paquete.getDestino()));
        paquete.setCategoria(limpiar(paquete.getCategoria()));
        paquete.setWaMensaje(limpiarOpcional(paquete.getWaMensaje()));
        paquete.setBadges(normalizarBadges(paquete.getBadges()));
    }

    private String limpiar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private String limpiarOpcional(String valor) {
        String limpio = limpiar(valor);
        return limpio == null || limpio.isBlank() ? null : limpio;
    }

    private String normalizarBadges(String badges) {
        if (badges == null || badges.isBlank()) {
            return null;
        }
        return java.util.Arrays.stream(badges.split(","))
                .map(String::trim)
                .filter(valor -> !valor.isBlank())
                .distinct()
                .collect(java.util.stream.Collectors.joining(","));
    }
}
