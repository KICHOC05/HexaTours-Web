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
        return paqueteRepository.findAllByOrderByOrdenAscCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paquete> listarActivos() {
        return paqueteRepository.findByActivoTrueOrderByOrdenAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paquete> buscarPorId(Long id) {
        return paqueteRepository.findById(id);
    }

    @Override
    @Transactional
    public Paquete guardar(Paquete paquete, MultipartFile imagen) {
        subirImagenSiExiste(paquete, imagen, null);
        log.info("Guardando nuevo paquete: {}", paquete.getNombre());
        return paqueteRepository.save(paquete);
    }

    @Override
    @Transactional
    public Paquete actualizar(Long id, Paquete datos, MultipartFile imagen) {
        Paquete existing = paqueteRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Paquete no encontrado con ID: " + id));

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
        existing.setFeatured(datos.isFeatured());
        existing.setUpdatedAt(LocalDateTime.now());

        // Subir nueva imagen si se proporcionó
        subirImagenSiExiste(existing, imagen, existing.getImagenPublicId());

        log.info("Actualizando paquete ID {}: {}", id, existing.getNombre());
        return paqueteRepository.save(existing);
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
       Si hay publicId anterior lo elimina primero
    ───────────────────────────────────────── */
    private void subirImagenSiExiste(Paquete paquete,
                                     MultipartFile imagen,
                                     String publicIdAnterior) {
        if (imagen == null || imagen.isEmpty()) return;

        try {
            // Eliminar imagen anterior si existe
            if (publicIdAnterior != null && !publicIdAnterior.isBlank()) {
                cloudinaryService.delete(publicIdAnterior);
            }
            // Subir nueva imagen
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
}