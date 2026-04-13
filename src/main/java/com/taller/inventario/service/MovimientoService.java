package com.taller.inventario.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.inventario.model.MaterialInforme;
import com.taller.inventario.model.Movimiento;
import com.taller.inventario.model.OrdenTrabajo;
import com.taller.inventario.model.ProductoProveedor;
import com.taller.inventario.model.TipoMovimiento;
import com.taller.inventario.repository.MaterialInformeRepository;
import com.taller.inventario.repository.MovimientoRepository;
import com.taller.inventario.repository.OrdenTrabajoRepository;
import com.taller.inventario.repository.ProductoProveedorRepository;

@Service
public class MovimientoService {

    private final MovimientoRepository movRepo;
    private final ProductoProveedorRepository ppRepo;
    private final OrdenTrabajoRepository ordenRepo;
    private final MaterialInformeRepository materialRepo;

    public MovimientoService(
            MovimientoRepository movRepo,
            ProductoProveedorRepository ppRepo,
            OrdenTrabajoRepository ordenRepo,
            MaterialInformeRepository materialRepo
    ) {

        this.movRepo = movRepo;
        this.ppRepo = ppRepo;
        this.ordenRepo = ordenRepo;
        this.materialRepo = materialRepo;
    }


    // ================= ENTRADA =================

    @Transactional
    public void registrarEntrada(
            Long productoProveedorId,
            Integer cantidad,
            String cliente,
            String matricula,
            String bastidor,
            String descripcion,
            Boolean facturado
    ) {

        ProductoProveedor pp =
                ppRepo.findById(productoProveedorId)
                        .orElseThrow();

        pp.setStock(pp.getStock() + cantidad);

        ppRepo.save(pp);


        Movimiento mov = new Movimiento();

        mov.setTipo(TipoMovimiento.ENTRADA);

        mov.setFecha(LocalDateTime.now());

        mov.setProductoProveedor(pp);

        mov.setCantidad(cantidad);

        mov.setCliente(cliente);

        mov.setMatricula(matricula);

        mov.setBastidor(bastidor);


        mov.setDescripcion(

                descripcion != null && !descripcion.isEmpty()

                        ? descripcion

                        : "Entrada proveedor: "
                        + pp.getProveedor().getNombre()
        );

        mov.setFacturado(Boolean.TRUE.equals(facturado));

        movRepo.save(mov);
    }



    // ================= SALIDA =================

    @Transactional
    public void registrarSalida(

            Long productoProveedorId,

            Integer cantidad,

            String cliente,

            String matricula,

            String bastidor,

            String descripcion,

            Boolean facturado,

            Long informeId
    ) {

        ProductoProveedor pp =
                ppRepo.findById(productoProveedorId)

                        .orElseThrow(() ->
                                new RuntimeException("ProductoProveedor no encontrado")
                        );


        if (pp.getStock() < cantidad) {

            throw new RuntimeException(

                    "Stock insuficiente. Disponible: "
                            + pp.getStock()
            );
        }


        // restar stock

        pp.setStock(pp.getStock() - cantidad);


        // crear movimiento

        Movimiento mov = new Movimiento();

        mov.setFecha(LocalDateTime.now());

        mov.setTipo(TipoMovimiento.SALIDA);

        mov.setProductoProveedor(pp);

        mov.setCantidad(cantidad);

        mov.setCliente(cliente);

        mov.setMatricula(matricula);

        mov.setBastidor(bastidor);


        String descFinal;


        if (descripcion != null && !descripcion.isBlank()) {

            descFinal = descripcion;

        }

        else {

            descFinal = "Salida - "
                    + (matricula != null ? matricula : "")
                    + (bastidor != null ? " / " + bastidor : "");
        }


        mov.setDescripcion(descFinal);

        mov.setFacturado(facturado != null && facturado);


        // guardar cambios

        ppRepo.save(pp);

        movRepo.save(mov);



        // ================= VINCULAR AL INFORME =================

        if (informeId != null) {

            OrdenTrabajo orden =
                    ordenRepo.findById(informeId)

                            .orElseThrow(() ->
                                    new RuntimeException("Informe no encontrado")
                            );


            // comprobar si ya existe el mismo material en el informe

            MaterialInforme material =
                    new MaterialInforme();


            material.setInforme(orden);

            material.setProductoProveedor(pp);

            material.setCantidad(cantidad);

            material.setDescripcion(descFinal);


            materialRepo.save(material);
        }

    }



    // ================= FACTURADO =================

    @Transactional
    public void cambiarEstadoFacturado(Long movimientoId) {

        Movimiento mov =
                movRepo.findById(movimientoId)

                        .orElseThrow(() ->
                                new RuntimeException("Movimiento no encontrado")
                        );


        if (mov.getTipo() == TipoMovimiento.SALIDA) {

            mov.setFacturado(
                    !Boolean.TRUE.equals(mov.getFacturado())
            );

            movRepo.save(mov);
        }
    }

}