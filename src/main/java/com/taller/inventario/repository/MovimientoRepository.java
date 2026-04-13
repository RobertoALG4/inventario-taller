package com.taller.inventario.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller.inventario.model.Movimiento;
import com.taller.inventario.model.TipoMovimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("""
        SELECT m FROM Movimiento m
        LEFT JOIN m.productoProveedor pp
        LEFT JOIN pp.producto p
        WHERE 
        (:tipo IS NULL OR m.tipo = :tipo)
        AND (
            :buscar IS NULL 
            OR :buscar = ''
            OR LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.cliente) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.matricula) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.bastidor) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(p.referencia) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%'))
        )
        ORDER BY m.fecha DESC
    """)
    Page<Movimiento> buscar(@Param("tipo") TipoMovimiento tipo,
                            @Param("buscar") String buscar,
                            Pageable pageable);

    // 🔥 CON FECHAS (la que usa todo ahora)
    @Query("""
        SELECT m FROM Movimiento m
        LEFT JOIN m.productoProveedor pp
        LEFT JOIN pp.producto p
        WHERE 
        (:tipo IS NULL OR m.tipo = :tipo)
        AND (
            :buscar IS NULL 
            OR :buscar = ''
            OR LOWER(m.descripcion) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.cliente) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.matricula) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(m.bastidor) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(p.referencia) LIKE LOWER(CONCAT('%', :buscar, '%'))
            OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :buscar, '%'))
        )
        AND (:inicio IS NULL OR m.fecha >= :inicio)
        AND (:fin IS NULL OR m.fecha <= :fin)
        ORDER BY m.fecha DESC
    """)
    Page<Movimiento> buscarConFechas(@Param("tipo") TipoMovimiento tipo,
                                     @Param("buscar") String buscar,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fin") LocalDateTime fin,
                                     Pageable pageable);
}