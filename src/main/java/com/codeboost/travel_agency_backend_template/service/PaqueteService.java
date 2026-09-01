package com.codeboost.travel_agency_backend_template.service;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PaqueteService {
    List<Paquete> listarTodos();
    List<Paquete> listarActivos();
    Optional<Paquete> buscarPorId(Long id);
    Paquete guardar(Paquete paquete, MultipartFile imagen);
    Paquete actualizar(Long id, Paquete datos, MultipartFile imagen);
    boolean ordenEnUso(int orden, Long paqueteIdExcluido);
    void eliminar(Long id);
    void toggleActivo(Long id);
}
