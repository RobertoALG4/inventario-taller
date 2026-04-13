package com.taller.inventario.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.inventario.model.MaterialUtilizado;
import com.taller.inventario.model.OrdenTrabajo;

public interface MaterialUtilizadoRepository
        extends JpaRepository<MaterialUtilizado, Long> {

    List<MaterialUtilizado> findByInforme(OrdenTrabajo informe);

}