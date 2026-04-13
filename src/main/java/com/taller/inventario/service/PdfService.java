package com.taller.inventario.service;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.taller.inventario.model.MaterialInforme;
import com.taller.inventario.model.MaterialUtilizado;
import com.taller.inventario.model.OrdenTrabajo;
import com.taller.inventario.model.ProcesoInforme;

@Service
public class PdfService {

    public byte[] generarInformePDF(

            OrdenTrabajo orden,

            List<MaterialInforme> materialesInventario,

            List<MaterialUtilizado> materialesManual,

            List<ProcesoInforme> procesos
    ) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {

            PdfWriter writer = new PdfWriter(baos);

            PdfDocument pdf = new PdfDocument(writer);

            Document document = new Document(pdf);


            // ===== TITULO =====

            document.add(new Paragraph("INFORME DE TALLER Nº " + orden.getId())
                    .setBold()
                    .setFontSize(16));

            document.add(new Paragraph(" "));


            // ===== DATOS CLIENTE =====

            document.add(new Paragraph("DATOS DEL CLIENTE").setBold());

            document.add(new Paragraph("Cliente: " + valor(orden.getCliente())));

            document.add(new Paragraph("Vehículo: "
                    + valor(orden.getMarcaVehiculo())
                    + " "
                    + valor(orden.getModeloVehiculo())));

            document.add(new Paragraph("Matrícula: "
                    + valor(orden.getMatricula())));

            document.add(new Paragraph("Bastidor: "
                    + valor(orden.getBastidor())));

            document.add(new Paragraph("Fecha recepción: "
                    + valor(orden.getFechaRecepcion())));

            document.add(new Paragraph(" "));


            // ===== DESCRIPCION =====

            document.add(new Paragraph("DESCRIPCIÓN DEL TRABAJO")
                    .setBold());

            document.add(new Paragraph(valor(orden.getDescripcionTrabajo())));

            document.add(new Paragraph(" "));


            // ===== PROCESOS =====

            document.add(new Paragraph("PROCESOS REALIZADOS")
                    .setBold());

            Table tablaProcesos = new Table(4);

            tablaProcesos.addCell("Operario");
            tablaProcesos.addCell("Descripción");
            tablaProcesos.addCell("Horas");
            tablaProcesos.addCell("Fecha");

            if (procesos != null) {

                for (ProcesoInforme p : procesos) {

                    tablaProcesos.addCell(valor(p.getOperario()));

                    tablaProcesos.addCell(valor(p.getDescripcion()));

                    tablaProcesos.addCell(valor(p.getHoras()));

                    tablaProcesos.addCell(valor(p.getFecha()));
                }
            }

            document.add(tablaProcesos);

            document.add(new Paragraph(" "));


            // ===== MATERIALES MANUALES =====

            document.add(new Paragraph("MATERIALES UTILIZADOS")
                    .setBold());

            Table tablaMaterialManual = new Table(4);

            tablaMaterialManual.addCell("Material");

            tablaMaterialManual.addCell("Unidades");

            tablaMaterialManual.addCell("Operario");

            tablaMaterialManual.addCell("Fecha");

            if (materialesManual != null) {

                for (MaterialUtilizado m : materialesManual) {

                    tablaMaterialManual.addCell(valor(m.getNombre()));

                    tablaMaterialManual.addCell(valor(m.getUnidades()));

                    tablaMaterialManual.addCell(valor(m.getOperario()));

                    tablaMaterialManual.addCell(valor(m.getFecha()));
                }
            }

            document.add(tablaMaterialManual);

            document.add(new Paragraph(" "));


            // ===== INVENTARIO =====

            document.add(new Paragraph("MATERIAL DE INVENTARIO")
                    .setBold());

            Table tablaInventario = new Table(5);

            tablaInventario.addCell("Producto");

            tablaInventario.addCell("Proveedor");

            tablaInventario.addCell("Cantidad");

            tablaInventario.addCell("Precio");

            tablaInventario.addCell("Total");

            double total = 0;

            for (MaterialInforme m : materialesInventario) {

                double precio =
                        m.getProductoProveedor().getPrecioVenta();

                double subtotal =
                        precio * m.getCantidad();

                total += subtotal;

                tablaInventario.addCell(
                        m.getProductoProveedor()
                                .getProducto()
                                .getNombre());

                tablaInventario.addCell(
                        m.getProductoProveedor()
                                .getProveedor()
                                .getNombre());

                tablaInventario.addCell(
                        String.valueOf(m.getCantidad()));

                tablaInventario.addCell(
                        String.valueOf(precio));

                tablaInventario.addCell(
                        String.valueOf(subtotal));
            }

            document.add(tablaInventario);


            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "TOTAL INVENTARIO: "
                            + total
                            + " €"
            ).setBold());


            document.close();

        }

        catch (Exception e) {

            e.printStackTrace();

        }

        return baos.toByteArray();
    }



    private String valor(Object o) {

        return o != null
                ? o.toString()
                : "-";
    }

}