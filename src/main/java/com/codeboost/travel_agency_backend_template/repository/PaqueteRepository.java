package com.codeboost.travel_agency_backend_template.repository;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    List<Paquete> findByActivoTrueOrderByOrdenAsc();

    List<Paquete> findAllByOrderByOrdenAscCreatedAtDesc();
}