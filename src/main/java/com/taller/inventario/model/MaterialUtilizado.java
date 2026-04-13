package com.taller.inventario.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class MaterialUtilizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private String operario;

    private String nombre;

    private String unidades;

    @ManyToOne
    private OrdenTrabajo informe;

    public Long getId() { return id; }

    public LocalDate getFecha() { return fecha; }

    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getOperario() { return operario; }

    public void setOperario(String operario) { this.operario = operario; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getUnidades() { return unidades; }

    public void setUnidades(String unidades) { this.unidades = unidades; }

    public OrdenTrabajo getInforme() { return informe; }

    public void setInforme(OrdenTrabajo informe) { this.informe = informe; }

}