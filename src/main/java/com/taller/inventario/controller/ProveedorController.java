package com.taller.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taller.inventario.model.Proveedor;
import com.taller.inventario.repository.ProveedorRepository;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorRepository proveedorRepo;

    public ProveedorController(ProveedorRepository proveedorRepo) {
        this.proveedorRepo = proveedorRepo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", proveedorRepo.findAll());
        return "proveedor_list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedor_form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Proveedor proveedor) {
        proveedorRepo.save(proveedor);
        return "redirect:/proveedor";
    }
}
