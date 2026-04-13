package com.taller.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller.inventario.model.OrdenTrabajo;

public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {

    @Query("""
        SELECT o FROM OrdenTrabajo o
        WHERE
        (:busqueda IS NULL OR :busqueda = '' OR
        LOWER(o.matricula) LIKE LOWER(CONCAT('%', :busqueda, '%'))
        OR LOWER(o.bastidor) LIKE LOWER(CONCAT('%', :busqueda, '%')))
    """)
    List<OrdenTrabajo> buscarPorVehiculo(@Param("busqueda") String busqueda);
}