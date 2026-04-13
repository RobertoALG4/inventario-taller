package com.taller.inventario.controller;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taller.inventario.model.Producto;
import com.taller.inventario.model.ProductoProveedor;
import com.taller.inventario.model.Proveedor;
import com.taller.inventario.repository.ProductoProveedorRepository;
import com.taller.inventario.repository.ProductoRepository;
import com.taller.inventario.repository.ProveedorRepository;

@Controller
public class InventarioController {

    private final ProductoRepository productoRepo;
    private final ProveedorRepository proveedorRepo;
    private final ProductoProveedorRepository productoProveedorRepo;

    public InventarioController(ProductoRepository productoRepo,
                                ProveedorRepository proveedorRepo,
                                ProductoProveedorRepository productoProveedorRepo) {
        this.productoRepo = productoRepo;
        this.proveedorRepo = proveedorRepo;
        this.productoProveedorRepo = productoProveedorRepo;
    }

    // 🔎 LISTAR CON BUSCADOR PRO + PAGINACIÓN
    @GetMapping("/inventario")
    public String listar(@RequestParam(required = false) String buscar,
                         @RequestParam(defaultValue = "0") int page,
                         Model model) {

        int pageSize = 10; // 🔥 puedes cambiar a 20 si quieres
        Pageable pageable = PageRequest.of(page, pageSize);

        Page<Producto> paginaProductos;

        if (buscar != null && !buscar.trim().isEmpty()) {

            paginaProductos = productoRepo.buscarPorNombreOProveedor(buscar, pageable);

            String texto = buscar.toLowerCase();

            paginaProductos.getContent().forEach(producto -> {

                if (!producto.getNombre().toLowerCase().contains(texto)) {

                    producto.setProductoProveedores(
                        producto.getProductoProveedores()
                                .stream()
                                .filter(pp -> pp.getProveedor()
                                        .getNombre()
                                        .toLowerCase()
                                        .contains(texto))
                                .collect(Collectors.toList())
                    );
                }
            });

        } else {
            paginaProductos = productoRepo.findAll(pageable);
        }

        model.addAttribute("productos", paginaProductos.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaProductos.getTotalPages());
        model.addAttribute("buscar", buscar);

        return "inventario/lista";
    }

    @GetMapping("/inventario/nuevo")
    public String nuevo() {
        return "inventario/form";
    }

    @GetMapping("/inventario/editar/{productoId}/{proveedorId}")
    public String editar(@PathVariable Long productoId,
                         @PathVariable Long proveedorId,
                         Model model) {

        ProductoProveedor pp = productoProveedorRepo
                .findByProductoIdAndProveedorId(productoId, proveedorId)
                .orElseThrow();

        model.addAttribute("nombreProducto", pp.getProducto().getNombre());
        model.addAttribute("nombreProveedor", pp.getProveedor().getNombre());
        model.addAttribute("precioCompra", pp.getPrecioCompra());
        model.addAttribute("porcentajeGanancia", pp.getPorcentajeGanancia());
        model.addAttribute("stock", pp.getStock());

        return "inventario/form";
    }

    @GetMapping("/inventario/eliminar/{productoId}/{proveedorId}")
    public String eliminar(@PathVariable Long productoId,
                           @PathVariable Long proveedorId) {

        ProductoProveedor pp = productoProveedorRepo
                .findByProductoIdAndProveedorId(productoId, proveedorId)
                .orElseThrow();

        productoProveedorRepo.delete(pp);

        return "redirect:/inventario";
    }

    @PostMapping("/inventario/guardar")
    public String guardar(@RequestParam String nombreProducto,
                          @RequestParam String nombreProveedor,
                          @RequestParam Double precioCompra,
                          @RequestParam Double porcentajeGanancia,
                          @RequestParam Integer stock) {

        Producto producto = productoRepo.findByNombre(nombreProducto)
                .orElseGet(() -> {
                    Producto nuevo = new Producto();
                    nuevo.setNombre(nombreProducto);
                    return productoRepo.save(nuevo);
                });

        Proveedor proveedor = proveedorRepo.findByNombreIgnoreCase(nombreProveedor)
                .orElseGet(() -> {
                    Proveedor nuevo = new Proveedor();
                    nuevo.setNombre(nombreProveedor);
                    return proveedorRepo.save(nuevo);
                });

        Optional<ProductoProveedor> existente =
                productoProveedorRepo.findByProductoIdAndProveedorId(
                        producto.getId(),
                        proveedor.getId());

        ProductoProveedor pp = existente.orElseGet(() -> {
            ProductoProveedor nuevo = new ProductoProveedor();
            nuevo.setProducto(producto);
            nuevo.setProveedor(proveedor);
            return nuevo;
        });

        pp.setPrecioCompra(precioCompra);
        pp.setPorcentajeGanancia(porcentajeGanancia);
        pp.setPrecioVenta(precioCompra + (precioCompra * porcentajeGanancia / 100));
        pp.setStock(stock);

        productoProveedorRepo.save(pp);

        return "redirect:/inventario";
    }
}