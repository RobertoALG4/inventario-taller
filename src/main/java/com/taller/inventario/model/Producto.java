package com.taller.inventario.model;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;

@Entity
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @Column(unique = true, nullable = false)
    private String referencia;

    @OneToMany(mappedBy = "producto", fetch = FetchType.EAGER)
    private List<ProductoProveedor> productoProveedores;

    public Producto() {}

    public Producto(String nombre) {
        this.nombre = nombre;
    }

    // 🔥 GENERACIÓN AUTOMÁTICA ANTES DE GUARDAR
    @PrePersist
    public void generarReferencia() {
        if (this.referencia == null || this.referencia.isBlank()) {
            this.referencia = "PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    // 🔥 NUEVO: STOCK TOTAL
    public int getStockTotal() {
        if (productoProveedores == null) return 0;
        return productoProveedores.stream()
                .mapToInt(pp -> pp.getStock())
                .sum();
    }

    // 🔥 NUEVO: VALOR TOTAL STOCK
    public double getValorTotal() {
        if (productoProveedores == null) return 0;
        return productoProveedores.stream()
                .mapToDouble(pp -> pp.getStock() * pp.getPrecioVenta())
                .sum();
    }

    // GETTERS Y SETTERS

    public Long getId() {
        return id;
    }

    public String getNombre() { 
        return nombre; 
    }

    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }

    public String getReferencia() { 
        return referencia; 
    }

    public void setReferencia(String referencia) { 
        this.referencia = referencia; 
    }

    public List<ProductoProveedor> getProductoProveedores() {
        return productoProveedores;
    }

    public void setProductoProveedores(List<ProductoProveedor> productoProveedores) {
        this.productoProveedores = productoProveedores;
    }
}