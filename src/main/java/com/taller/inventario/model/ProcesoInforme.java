package com.taller.inventario.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProcesoInforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private String operario;

    private String descripcion;

    private Double horas;

    @ManyToOne
    private OrdenTrabajo informe;

    public Long getId() { return id; }

    public LocalDate getFecha() { return fecha; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getOperario() { return operario; }

    public void setOperario(String operario) { this.operario = operario; }

    public String getDescripcion() { return descripcion; }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getHoras() { return horas; }

    public void setHoras(Double horas) { this.horas = horas; }

    public OrdenTrabajo getInforme() { return informe; }

    public void setInforme(OrdenTrabajo informe) { this.informe = informe; }

}