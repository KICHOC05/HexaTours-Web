package com.codeboost.travel_agency_backend_template.web.validation;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public final class ImageFileValidator {

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");

    private ImageFileValidator() {
    }

    public static String validar(MultipartFile archivo, boolean obligatorio) {
        if (archivo == null || archivo.isEmpty()) {
            return obligatorio
                    ? "La imagen es obligatoria para publicar el paquete."
                    : null;
        }

        if (archivo.getSize() > MAX_IMAGE_SIZE) {
            return "La imagen no debe superar 10 MB.";
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            return "Solo se permiten imágenes JPG, PNG, WebP o GIF.";
        }

        try {
            byte[] cabecera = archivo.getInputStream().readNBytes(12);
            if (!firmaValida(contentType, cabecera)) {
                return "El contenido del archivo no corresponde a una imagen válida.";
            }
        } catch (IOException e) {
            return "No fue posible leer la imagen seleccionada.";
        }

        return null;
    }

    private static boolean firmaValida(String contentType, byte[] bytes) {
        return switch (contentType) {
            case "image/jpeg" -> bytes.length >= 3
                    && unsigned(bytes[0]) == 0xFF
                    && unsigned(bytes[1]) == 0xD8
                    && unsigned(bytes[2]) == 0xFF;
            case "image/png" -> bytes.length >= 8
                    && unsigned(bytes[0]) == 0x89
                    && unsigned(bytes[1]) == 0x50
                    && unsigned(bytes[2]) == 0x4E
                    && unsigned(bytes[3]) == 0x47
                    && unsigned(bytes[4]) == 0x0D
                    && unsigned(bytes[5]) == 0x0A
                    && unsigned(bytes[6]) == 0x1A
                    && unsigned(bytes[7]) == 0x0A;
            case "image/gif" -> bytes.length >= 6
                    && (ascii(bytes, 0, 6).equals("GIF87a")
                    || ascii(bytes, 0, 6).equals("GIF89a"));
            case "image/webp" -> bytes.length >= 12
                    && ascii(bytes, 0, 4).equals("RIFF")
                    && ascii(bytes, 8, 4).equals("WEBP");
            default -> false;
        };
    }

    private static int unsigned(byte value) {
        return value & 0xFF;
    }

    private static String ascii(byte[] bytes, int inicio, int longitud) {
        return new String(bytes, inicio, longitud, StandardCharsets.US_ASCII);
    }
}
