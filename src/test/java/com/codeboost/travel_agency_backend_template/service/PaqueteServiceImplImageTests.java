package com.codeboost.travel_agency_backend_template.service;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import com.codeboost.travel_agency_backend_template.repository.PaqueteRepository;
import com.codeboost.travel_agency_backend_template.service.impl.PaqueteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaqueteServiceImplImageTests {

    @Mock
    private PaqueteRepository paqueteRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    private PaqueteServiceImpl paqueteService;
    private Paquete existente;
    private Paquete datos;
    private MockMultipartFile imagen;

    @BeforeEach
    void preparar() {
        paqueteService = new PaqueteServiceImpl(paqueteRepository, cloudinaryService);

        existente = paqueteValido();
        existente.setId(10L);
        existente.setImagenUrl("https://cdn.example/old.jpg");
        existente.setImagenPublicId("old-id");
        existente.setCiudades("Ciudad anterior");

        datos = paqueteValido();
        datos.setCiudades("Cancún, Playa del Carmen, Tulum");
        imagen = new MockMultipartFile(
                "imagen", "nueva.jpg", "image/jpeg",
                new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF});

        when(paqueteRepository.findById(10L)).thenReturn(Optional.of(existente));
        when(paqueteRepository.existsByOrdenAndIdNot(1, 10L)).thenReturn(false);
        when(cloudinaryService.upload(any(), isNull())).thenReturn(Map.of(
                "url", "https://cdn.example/new.jpg",
                "public_id", "new-id"));
    }

    @Test
    void eliminaLaImagenAnteriorSoloDespuesDeGuardarLaNueva() {
        when(paqueteRepository.saveAndFlush(any(Paquete.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        paqueteService.actualizar(10L, datos, imagen);

        InOrder orden = inOrder(cloudinaryService, paqueteRepository);
        orden.verify(cloudinaryService).upload(any(), isNull());
        orden.verify(paqueteRepository).saveAndFlush(any(Paquete.class));
        orden.verify(cloudinaryService).delete("old-id");
        assertThat(existente.getCiudades()).isEqualTo("Cancún, Playa del Carmen, Tulum");
    }

    @Test
    void conservaLaImagenAnteriorSiFallaElGuardado() {
        when(paqueteRepository.saveAndFlush(any(Paquete.class)))
                .thenThrow(new IllegalStateException("database unavailable"));

        assertThatThrownBy(() -> paqueteService.actualizar(10L, datos, imagen))
                .isInstanceOf(IllegalStateException.class);

        verify(cloudinaryService).delete("new-id");
        verify(cloudinaryService, never()).delete("old-id");
    }

    private Paquete paqueteValido() {
        Paquete paquete = new Paquete();
        paquete.setNombre("Viaje internacional");
        paquete.setDescripcion("Descripción completa del paquete turístico.");
        paquete.setDestino("Destino");
        paquete.setCiudades("Ciudad");
        paquete.setCategoria("Internacional");
        paquete.setOrden(1);
        paquete.setActivo(true);
        return paquete;
    }
}
