package com.taller.inventario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.inventario.model.ProductoProveedor;

public interface ProductoProveedorRepository extends JpaRepository<ProductoProveedor, Long> {

    Optional<ProductoProveedor> findByProductoIdAndProveedorId(Long productoId, Long proveedorId);

    List<ProductoProveedor> findByProductoId(Long productoId); // ESTE
}