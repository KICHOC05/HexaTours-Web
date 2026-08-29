package com.codeboost.travel_agency_backend_template.web.validation;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;

class ImageFileValidatorTests {

    @Test
    void aceptaUnaImagenPngConFirmaValida() {
        byte[] png = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A, 0x00
        };
        MockMultipartFile archivo = new MockMultipartFile(
                "imagen", "viaje.png", "image/png", png);

        assertThat(ImageFileValidator.validar(archivo, true)).isNull();
    }

    @Test
    void rechazaUnArchivoQueFingeSerImagen() {
        MockMultipartFile archivo = new MockMultipartFile(
                "imagen", "viaje.png", "image/png", "contenido no permitido".getBytes());

        assertThat(ImageFileValidator.validar(archivo, true))
                .contains("no corresponde a una imagen válida");
    }

    @Test
    void exigeImagenSoloCuandoEsObligatoria() {
        MockMultipartFile vacio = new MockMultipartFile(
                "imagen", "", "application/octet-stream", new byte[0]);

        assertThat(ImageFileValidator.validar(vacio, false)).isNull();
        assertThat(ImageFileValidator.validar(vacio, true)).contains("obligatoria");
    }
}
