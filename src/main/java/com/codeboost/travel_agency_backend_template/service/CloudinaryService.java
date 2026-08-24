package com.codeboost.travel_agency_backend_template.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {

    /**
     * Sube un archivo a Cloudinary.
     * @param file   archivo a subir
     * @param folder carpeta destino en Cloudinary
     * @return Map con "url" y "public_id"
     */
    Map<String, String> upload(MultipartFile file, String folder);

    /**
     * Elimina un archivo de Cloudinary por su public_id.
     * @param publicId identificador público del recurso
     */
    void delete(String publicId);
}