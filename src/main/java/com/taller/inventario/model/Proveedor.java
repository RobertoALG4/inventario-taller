package com.taller.inventario.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    private String nombre;

    private String telefono;

    @Column(unique = true)
    @NotBlank(message = "El código del proveedor es obligatorio")
    private String codigo;

    public Proveedor() {}

    public Proveedor(String nombre, String telefono, String codigo) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.codigo = codigo;
    }

    // 🔥 SE GENERA AUTOMÁTICAMENTE ANTES DE GUARDAR
    @PrePersist
    public void generarCodigoAutomatico() {
        if (this.codigo == null || this.codigo.isBlank()) {
            this.codigo = "PROV-" + UUID.randomUUID()
                                        .toString()
                                        .substring(0, 6)
                                        .toUpperCase();
        }
    }

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
}