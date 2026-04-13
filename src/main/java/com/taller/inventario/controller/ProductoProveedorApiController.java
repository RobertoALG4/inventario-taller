package com.taller.inventario.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taller.inventario.model.ProductoProveedor;
import com.taller.inventario.repository.ProductoProveedorRepository;

@RestController
@RequestMapping("/api")
public class ProductoProveedorApiController {

    private final ProductoProveedorRepository ppRepo;

    public ProductoProveedorApiController(ProductoProveedorRepository ppRepo) {
        this.ppRepo = ppRepo;
    }

    @GetMapping("/productoProveedor")
    public Map<String, Object> obtenerDatos(@RequestParam Long productoId,
                                        @RequestParam Long proveedorId) {

    Optional<ProductoProveedor> ppOpt =
            ppRepo.findByProductoIdAndProveedorId(productoId, proveedorId);

    Map<String, Object> map = new HashMap<>();

    if (!ppOpt.isPresent()) {
        map.put("stock", 0);
        map.put("precioCompra", 0);
        map.put("precioVenta", 0);
        map.put("porcentajeGanancia", 0);
        return map;
    }

    ProductoProveedor pp = ppOpt.get();

    map.put("stock", pp.getStock());
    map.put("precioCompra", pp.getPrecioCompra());
    map.put("precioVenta", pp.getPrecioVenta());
    map.put("porcentajeGanancia", pp.getPorcentajeGanancia());

    return map;
}

    @GetMapping("/productoProveedor/total")
    public Map<String, Object> obtenerStockTotal(@RequestParam Long productoId) {

        List<ProductoProveedor> lista =
                ppRepo.findByProductoId(productoId);

        int totalStock = lista.stream()
                .mapToInt(ProductoProveedor::getStock)
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("stock", totalStock);

        return response;
    }
}