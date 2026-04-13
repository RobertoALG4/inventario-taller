package com.taller.inventario.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taller.inventario.model.Movimiento;
import com.taller.inventario.model.TipoMovimiento;
import com.taller.inventario.repository.MovimientoRepository;
import com.taller.inventario.service.HistorialPdfService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final MovimientoRepository movimientoRepository;
    private final HistorialPdfService historialPdfService;

    public HistorialController(MovimientoRepository movimientoRepository,
                               HistorialPdfService historialPdfService) {
        this.movimientoRepository = movimientoRepository;
        this.historialPdfService = historialPdfService;
    }

    @GetMapping
    public String verHistorial(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by("fecha").descending());

        LocalDateTime inicio = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaFin != null) ? fechaFin.atTime(23, 59, 59) : null;

        Page<Movimiento> movimientosPage =
                movimientoRepository.buscarConFechas(tipo, buscar, inicio, fin, pageable);

        model.addAttribute("movimientos", movimientosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", movimientosPage.getTotalPages());
        model.addAttribute("buscar", buscar);
        model.addAttribute("tipoSeleccionado", tipo);

        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        return "historial";
    }

    // 🔥 EXPORTAR PDF
    @GetMapping("/pdf")
    public void exportarPdf(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) TipoMovimiento tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletResponse response
    ) throws Exception {

        LocalDateTime inicio = null;
        LocalDateTime fin = null;

        // ✅ Si hay fecha inicio
        if (fechaInicio != null) {
            inicio = fechaInicio.atStartOfDay();
        }

        // ✅ Si hay fecha fin
        if (fechaFin != null) {
            fin = fechaFin.atTime(23, 59, 59);
        }

        List<Movimiento> movimientos = movimientoRepository
                .buscarConFechas(tipo, buscar, inicio, fin, Pageable.unpaged())
                .getContent();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=historial.pdf");

        historialPdfService.generarPdf(movimientos, response);
    }
}