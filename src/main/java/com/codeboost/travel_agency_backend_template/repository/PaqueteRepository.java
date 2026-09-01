package com.codeboost.travel_agency_backend_template.repository;

import com.codeboost.travel_agency_backend_template.domain.model.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PaqueteRepository extends JpaRepository<Paquete, Long> {

    @Query("""
            SELECT p
            FROM Paquete p
            WHERE p.activo = true
            ORDER BY
                CASE WHEN p.orden < 1 THEN 2147483647 ELSE p.orden END ASC,
                p.createdAt DESC,
                p.id ASC
            """)
    List<Paquete> findActivosOrdenados();

    @Query("""
            SELECT p
            FROM Paquete p
            ORDER BY
                CASE WHEN p.orden < 1 THEN 2147483647 ELSE p.orden END ASC,
                p.createdAt DESC,
                p.id ASC
            """)
    List<Paquete> findTodosOrdenados();

    boolean existsByOrden(int orden);

    boolean existsByOrdenAndIdNot(int orden, Long id);
}
