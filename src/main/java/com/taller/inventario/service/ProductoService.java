package com.taller.inventario.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.taller.inventario.model.Producto;
import com.taller.inventario.model.Proveedor;
import com.taller.inventario.repository.ProductoRepository;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public String generarReferencia(Proveedor proveedor) {

        if (proveedor == null || proveedor.getCodigo() == null) {
            return null;
        }

        String codigoProveedor = proveedor.getCodigo();

        List<Producto> productos = productoRepository
                .findByProductoProveedoresProveedorOrderByReferenciaDesc(proveedor)
                .stream()
                .filter(p -> p.getReferencia() != null)
                .toList();

        int siguienteNumero = 1;

        if (!productos.isEmpty()) {
            String ultimaRef = productos.get(0).getReferencia();
            if (ultimaRef.contains("-")) {
                String numeroStr =
                        ultimaRef.substring(ultimaRef.lastIndexOf("-") + 1);
                siguienteNumero = Integer.parseInt(numeroStr) + 1;
            }
        }

        return codigoProveedor + "-" + String.format("%03d", siguienteNumero);
    }

    public Page<Producto> buscar(String texto, Pageable pageable) {

        if (texto != null && texto.matches("[A-Z]+-\\d+")) {
            Optional<Producto> producto =
                    productoRepository.findByReferencia(texto);
            if (producto.isPresent()) {
                return new org.springframework.data.domain.PageImpl<>(
                        List.of(producto.get()), pageable, 1);
            }
        }

        return productoRepository.buscarPorNombreOProveedor(texto, pageable);
    }
}