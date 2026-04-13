package com.taller.inventario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class ProductoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @NotNull
    private Double precioCompra;

    @NotNull
    private Double porcentajeGanancia;

    @NotNull
    private Double precioVenta;

    @NotNull
    private Integer stock;

    public ProductoProveedor() {}

    // GETTERS Y SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }

    public Double getPorcentajeGanancia() { return porcentajeGanancia; }
    public void setPorcentajeGanancia(Double porcentajeGanancia) { this.porcentajeGanancia = porcentajeGanancia; }

    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
