package com.taller.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.inventario.model.OrdenTrabajo;
import com.taller.inventario.model.ProcesoInforme;

public interface ProcesoInformeRepository
        extends JpaRepository<ProcesoInforme, Long> {

    List<ProcesoInforme> findByInforme(OrdenTrabajo informe);

}