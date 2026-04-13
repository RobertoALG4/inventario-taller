package com.taller.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller.inventario.model.Producto;
import com.taller.inventario.model.Proveedor;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar por nombre exacto
    Optional<Producto> findByNombre(String nombre);

    // Buscar por referencia exacta (🔥 necesario)
    Optional<Producto> findByReferencia(String referencia);

    // 🔥 NECESARIO para generar referencia automática
    List<Producto> findByProductoProveedoresProveedorOrderByReferenciaDesc(Proveedor proveedor);

    // Búsqueda general con paginación (nombre o proveedor)
    @Query("""
        SELECT DISTINCT p FROM Producto p
        LEFT JOIN p.productoProveedores pp
        LEFT JOIN pp.proveedor pr
        WHERE 
            (:texto IS NULL OR :texto = '' 
            OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(pr.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
            OR LOWER(p.referencia) LIKE LOWER(CONCAT('%', :texto, '%'))
            )
    """)
    Page<Producto> buscarPorNombreOProveedor(@Param("texto") String texto, Pageable pageable);

}