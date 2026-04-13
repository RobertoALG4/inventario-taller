package com.taller.inventario.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taller.inventario.repository.OrdenTrabajoRepository;
import com.taller.inventario.repository.ProductoProveedorRepository;
import com.taller.inventario.service.MovimientoService;

@Controller
@RequestMapping("/movimiento")
public class MovimientoController {

    private final ProductoProveedorRepository ppRepo;
    private final MovimientoService movimientoService;
    private final OrdenTrabajoRepository ordenRepo;

    // Constructor actualizado
    public MovimientoController(ProductoProveedorRepository ppRepo,
                                MovimientoService movimientoService,
                                OrdenTrabajoRepository ordenRepo) {
        this.ppRepo = ppRepo;
        this.movimientoService = movimientoService;
        this.ordenRepo = ordenRepo;
    }

    // =========================
    // ======== ENTRADA ========
    // =========================
    @GetMapping("/entrada")
    public String entradaForm(Model model) {
        model.addAttribute("productos", ppRepo.findAll());
        return "entrada_form";
    }

    @PostMapping("/guardarEntrada")
    public String guardarEntrada(
            @RequestParam Long productoProveedorId,
            @RequestParam Integer cantidad,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String bastidor,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Boolean facturado,
            Model model) {

        try {
            movimientoService.registrarEntrada(
                    productoProveedorId,
                    cantidad,
                    cliente,
                    matricula,
                    bastidor,
                    descripcion,
                    facturado
            );
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productos", ppRepo.findAll());
            return "entrada_form";
        }

        return "redirect:/historial";
    }

    // =========================
    // ======== SALIDA =========
    // =========================
    @GetMapping("/salida")
    public String salidaForm(Model model) {
        model.addAttribute("productosProveedor", ppRepo.findAll());
        model.addAttribute("informes", ordenRepo.findAll());
        return "salida_form";
    }

    @PostMapping("/guardarSalida")
    public String guardarSalida(
            @RequestParam Long productoProveedorId,
            @RequestParam Integer cantidad,
            @RequestParam String cliente,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) String bastidor,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Boolean facturado,
            @RequestParam(required = false) Long informeId,
            Model model) {

        try {
            movimientoService.registrarSalida(
                    productoProveedorId,
                    cantidad,
                    cliente,
                    matricula,
                    bastidor,
                    descripcion,
                    facturado,
                    informeId
            );
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("productosProveedor", ppRepo.findAll());
            model.addAttribute("informes", ordenRepo.findAll());
            return "salida_form";
        }

        return "redirect:/historial";
    }
}