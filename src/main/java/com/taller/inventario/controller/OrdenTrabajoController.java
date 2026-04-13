package com.taller.inventario.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taller.inventario.model.MaterialInforme;
import com.taller.inventario.model.MaterialUtilizado;
import com.taller.inventario.model.OrdenTrabajo;
import com.taller.inventario.model.ProcesoInforme;
import com.taller.inventario.repository.MaterialInformeRepository;
import com.taller.inventario.repository.MaterialUtilizadoRepository;
import com.taller.inventario.repository.OrdenTrabajoRepository;
import com.taller.inventario.repository.ProcesoInformeRepository;
import com.taller.inventario.repository.ProductoProveedorRepository;
import com.taller.inventario.service.MovimientoService;
import com.taller.inventario.service.PdfService;

@Controller
@RequestMapping("/informes")
public class OrdenTrabajoController {

    private final OrdenTrabajoRepository ordenRepo;
    private final MaterialInformeRepository materialRepo;
    private final ProductoProveedorRepository productoRepo;
    private final MovimientoService movimientoService;
    private final PdfService pdfService;

    // NUEVOS
    private final ProcesoInformeRepository procesoRepo;
    private final MaterialUtilizadoRepository materialUtilizadoRepo;

    public OrdenTrabajoController(
            OrdenTrabajoRepository ordenRepo,
            MaterialInformeRepository materialRepo,
            ProductoProveedorRepository productoRepo,
            MovimientoService movimientoService,
            PdfService pdfService,
            ProcesoInformeRepository procesoRepo,
            MaterialUtilizadoRepository materialUtilizadoRepo
    ) {

        this.ordenRepo = ordenRepo;
        this.materialRepo = materialRepo;
        this.productoRepo = productoRepo;
        this.movimientoService = movimientoService;
        this.pdfService = pdfService;

        this.procesoRepo = procesoRepo;
        this.materialUtilizadoRepo = materialUtilizadoRepo;
    }

    // ===== LISTA =====
    @GetMapping
    public String lista(Model model,
        org.springframework.security.core.Authentication auth) {

        boolean esMecanico = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_MECANICO"));

        if(esMecanico){

            model.addAttribute(
                "ordenes",
                ordenRepo.findAll()
                    .stream()
                    .filter(o -> !Boolean.TRUE.equals(o.getFinalizado()))
                    .toList()
            );

        }else{

            model.addAttribute("ordenes", ordenRepo.findAll());

        }

        return "informes/lista";
    }

    // ===== NUEVO =====
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("orden", new OrdenTrabajo());

        return "informes/form";
    }

    // ===== EDITAR =====
    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model,
            org.springframework.security.core.Authentication auth
    ) {

        OrdenTrabajo orden = ordenRepo.findById(id).orElseThrow();

        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // bloquear edición si está finalizado y no es admin
        if(Boolean.TRUE.equals(orden.getFinalizado()) && !esAdmin){

            return "redirect:/informes";
        }

        // 🔥 cargar procesos existentes
        List<ProcesoInforme> procesos =
                procesoRepo.findByInforme(orden);

        // 🔥 cargar materiales manuales existentes
        List<MaterialUtilizado> materialesManual =
                materialUtilizadoRepo.findByInforme(orden);

        model.addAttribute("orden", orden);

        model.addAttribute("procesos", procesos);

        model.addAttribute("materialesManual", materialesManual);

        return "informes/form";
    }

    // ===== GUARDAR INFORME + PROCESOS + MATERIALES MANUALES =====
    @PostMapping("/guardar")
    public String guardar(

            @ModelAttribute OrdenTrabajo orden,

            // PROCESOS
            @RequestParam(required = false) List<String> procesosOperario,
            @RequestParam(required = false) List<String> procesosDescripcion,
            @RequestParam(required = false) List<Double> procesosHoras,
            @RequestParam(required = false) List<String> procesosFecha,

            // MATERIALES MANUALES
            @RequestParam(required = false) List<String> materialNombre,
            @RequestParam(required = false) List<String> materialCantidad,
            @RequestParam(required = false) List<String> materialOperario,
            @RequestParam(required = false) List<String> materialFecha,

            Model model
    ) {

        boolean matriculaVacia =
                orden.getMatricula() == null || orden.getMatricula().isBlank();

        boolean bastidorVacio =
                orden.getBastidor() == null || orden.getBastidor().isBlank();

        if (matriculaVacia && bastidorVacio) {

            model.addAttribute("error", "Debes introducir matrícula o bastidor");

            model.addAttribute("orden", orden);

            return "informes/form";
        }

        // guardar informe
        ordenRepo.save(orden);

        // borrar lineas antiguas al editar

        procesoRepo.deleteAll(
                procesoRepo.findByInforme(orden)
        );

        materialUtilizadoRepo.deleteAll(
                materialUtilizadoRepo.findByInforme(orden)
        );


        // ===== GUARDAR PROCESOS =====

        if (procesosOperario != null) {

            for (int i = 0; i < procesosOperario.size(); i++) {

                if (procesosDescripcion.get(i) == null ||
                        procesosDescripcion.get(i).isBlank())
                    continue;

                ProcesoInforme p = new ProcesoInforme();

                p.setInforme(orden);

                p.setOperario(procesosOperario.get(i));

                p.setDescripcion(procesosDescripcion.get(i));

                if (procesosHoras != null && procesosHoras.size() > i)
                    p.setHoras(procesosHoras.get(i));

                if (procesosFecha != null &&
                        procesosFecha.size() > i &&
                        !procesosFecha.get(i).isBlank())

                    p.setFecha(LocalDate.parse(procesosFecha.get(i)));

                procesoRepo.save(p);
            }
        }


        // ===== GUARDAR MATERIALES MANUALES =====

        if (materialNombre != null) {

            for (int i = 0; i < materialNombre.size(); i++) {

                if (materialNombre.get(i) == null ||
                        materialNombre.get(i).isBlank())
                    continue;

                MaterialUtilizado m = new MaterialUtilizado();

                m.setInforme(orden);

                m.setNombre(materialNombre.get(i));

                if (materialCantidad != null &&
                        materialCantidad.size() > i)

                    m.setUnidades(materialCantidad.get(i));

                if (materialOperario != null &&
                        materialOperario.size() > i)

                    m.setOperario(materialOperario.get(i));

                if (materialFecha != null &&
                        materialFecha.size() > i &&
                        !materialFecha.get(i).isBlank())

                    m.setFecha(LocalDate.parse(materialFecha.get(i)));

                materialUtilizadoRepo.save(m);
            }
        }

        return "redirect:/informes";
    }


    // ===== DETALLE =====
    @GetMapping("/{id}")
    public String verInforme(@PathVariable Long id, Model model) {

        OrdenTrabajo orden =
                ordenRepo.findById(id).orElseThrow();


        // inventario
        var materialesInventario =
                materialRepo.findByInforme(orden);


        // procesos
        var procesos =
                procesoRepo.findByInforme(orden);


        // materiales manuales
        var materialesManual =
                materialUtilizadoRepo.findByInforme(orden);


        model.addAttribute("orden", orden);

        model.addAttribute("material", new MaterialInforme());

        model.addAttribute("productos", productoRepo.findAll());

        model.addAttribute("materiales", materialesInventario);

        model.addAttribute("procesos", procesos);

        model.addAttribute("materialesManual", materialesManual);


        return "informes/detalle";
    }


    // ===== PDF =====
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPDF(@PathVariable Long id) {

        OrdenTrabajo orden =
                ordenRepo.findById(id).orElseThrow();

        List<MaterialInforme> materialesInventario =
                materialRepo.findByInforme(orden);

        List<ProcesoInforme> procesos =
                procesoRepo.findByInforme(orden);

        List<MaterialUtilizado> materialesManual =
                materialUtilizadoRepo.findByInforme(orden);

        byte[] pdf =
                pdfService.generarInformePDF(
                        orden,
                        materialesInventario,
                        materialesManual,
                        procesos
                );

        return ResponseEntity.ok()

                .header(
                        "Content-Disposition",
                        "attachment; filename=informe_" + id + ".pdf"
                )

                .contentType(MediaType.APPLICATION_PDF)

                .body(pdf);
    }


    // ===== MATERIAL DESDE INVENTARIO =====
    @PostMapping("/{id}/material")
    public String agregarMaterial(

            @PathVariable Long id,

            @ModelAttribute MaterialInforme material
    ) {

        OrdenTrabajo orden = ordenRepo.findById(id).orElseThrow();

        material.setInforme(orden);

        materialRepo.save(material);


        // salida automática inventario
        movimientoService.registrarSalida(

                material.getProductoProveedor().getId(),

                material.getCantidad(),

                orden.getCliente(),

                orden.getMatricula(),

                orden.getBastidor(),

                material.getDescripcion(),

                false,

                orden.getId()
        );

        return "redirect:/informes/" + id;
    }


    // ===== BUSCAR =====
    @GetMapping("/buscar")
    public String buscarInformes(

            @RequestParam String busqueda,

            Model model
    ) {

        List<OrdenTrabajo> resultados =
                ordenRepo.buscarPorVehiculo(busqueda);

        model.addAttribute("ordenes", resultados);

        return "informes/lista";
    }


    // ===== CAMBIAR ESTADO =====
    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Long id) {

        OrdenTrabajo orden = ordenRepo.findById(id).orElseThrow();

        orden.setFinalizado(
                !Boolean.TRUE.equals(orden.getFinalizado())
        );

        ordenRepo.save(orden);

        return "redirect:/informes/" + id;
    }

}