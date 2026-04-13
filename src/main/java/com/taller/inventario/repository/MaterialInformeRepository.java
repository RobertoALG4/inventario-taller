package com.taller.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.inventario.model.MaterialInforme;
import com.taller.inventario.model.OrdenTrabajo;

public interface MaterialInformeRepository extends JpaRepository<MaterialInforme, Long> {

    List<MaterialInforme> findByInforme(OrdenTrabajo informe);

}