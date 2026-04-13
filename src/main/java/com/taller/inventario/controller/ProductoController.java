package com.taller.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taller.inventario.model.Producto;
import com.taller.inventario.model.Proveedor;
import com.taller.inventario.repository.ProductoRepository;
import com.taller.inventario.service.ProductoService;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoRepository productoRepo;
    private final ProductoService productoService;

    public ProductoController(ProductoRepository productoRepo,
                              ProductoService productoService) {
        this.productoRepo = productoRepo;
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", productoRepo.findAll());
        return "producto_list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("producto", new Producto());
        return "producto_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto) {

        // 🔥 SOLO si es nuevo producto
        if (producto.getId() == null) {

            // ⚠️ Necesitamos al menos un proveedor asociado
            if (producto.getProductoProveedores() != null &&
                !producto.getProductoProveedores().isEmpty()) {

                Proveedor proveedor =
                        producto.getProductoProveedores()
                                .get(0)
                                .getProveedor();

                if (proveedor != null) {
                    String referencia =
                            productoService.generarReferencia(proveedor);
                    producto.setReferencia(referencia);
                }
            }
        }

        productoRepo.save(producto);
        return "redirect:/producto";
    }
}