package com.codeboost.travel_agency_backend_template.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.codeboost.travel_agency_backend_template.service.CloudinaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private static final Logger log =
            LoggerFactory.getLogger(CloudinaryServiceImpl.class);

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public Map<String, String> upload(MultipartFile file, String folder) {
        // Validaciones básicas
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException(
                    "Solo se permiten imágenes (JPG, PNG, WebP, GIF).");
        }

        if (file.getSize() > 10 * 1024 * 1024) { // 10 MB
            throw new IllegalArgumentException(
                    "La imagen no debe superar 10 MB.");
        }

        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder",          folder != null ? folder : "niagara-viajes");
            options.put("resource_type",   "image");
            options.put("quality",         "auto");
            options.put("fetch_format",    "auto");
            options.put("width",           1200);
            options.put("crop",            "limit");

            Map<?, ?> result = cloudinary.uploader()
                    .upload(file.getBytes(), options);

            String url      = result.get("secure_url").toString();
            String publicId = result.get("public_id").toString();

            log.info("Imagen subida a Cloudinary — public_id: {}", publicId);

            Map<String, String> response = new HashMap<>();
            response.put("url",       url);
            response.put("public_id", publicId);
            return response;

        } catch (IOException e) {
            log.error("Error al subir imagen a Cloudinary: {}", e.getMessage());
            throw new RuntimeException(
                    "No se pudo subir la imagen. Intenta de nuevo.");
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            log.warn("Se intentó eliminar una imagen sin public_id.");
            return;
        }
        try {
            Map<?, ?> result = cloudinary.uploader()
                    .destroy(publicId, ObjectUtils.emptyMap());

            String resultStr = result.get("result") != null
                    ? result.get("result").toString() : "unknown";
            log.info("Imagen eliminada de Cloudinary — public_id: {} | resultado: {}",
                    publicId, resultStr);

        } catch (IOException e) {
            log.error("Error al eliminar imagen de Cloudinary (public_id: {}): {}",
                    publicId, e.getMessage());
            // No lanzamos excepción para no interrumpir el flujo principal
        }
    }
}