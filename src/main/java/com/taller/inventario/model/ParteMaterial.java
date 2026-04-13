package com.taller.inventario.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;



@Entity
public class ParteMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String operario;

    private String material;

    private Integer unidades;

    private String incidencias;

    private LocalDate fecha;

    @ManyToOne
    private OrdenTrabajo ordenTrabajo;

    // ===== CONSTRUCTORES =====

    public Long getId() {
        return id;
    }

    public String getOperario() {
        return operario;
    }

    public String getMaterial() {
        return material;
    }

    public Integer getUnidades() {
        return unidades;
    }

    public String getIncidencias() {
        return incidencias;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public OrdenTrabajo getOrdenTrabajo() {
        return ordenTrabajo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOperario(String operario) {
        this.operario = operario;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setUnidades(Integer unidades) {
        this.unidades = unidades;
    }

    public void setIncidencias(String incidencias) {
        this.incidencias = incidencias;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setOrdenTrabajo(OrdenTrabajo ordenTrabajo) {
        this.ordenTrabajo = ordenTrabajo;
    }



}