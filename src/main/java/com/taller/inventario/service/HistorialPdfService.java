package com.taller.inventario.service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.taller.inventario.model.Movimiento;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class HistorialPdfService {

    public void generarPdf(List<Movimiento> movimientos,
                           HttpServletResponse response) throws Exception {

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        document.add(new Paragraph("Historial de movimientos"));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);

        table.addCell("Fecha");
        table.addCell("Tipo");
        table.addCell("Producto");
        table.addCell("Cantidad");
        table.addCell("Cliente");

        for (Movimiento m : movimientos) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

            table.addCell(m.getFecha().format(formatter));            
            table.addCell(m.getTipo().toString());
            table.addCell(m.getProductoProveedor().getProducto().getNombre());
            table.addCell(String.valueOf(m.getCantidad()));
            table.addCell(m.getCliente() != null ? m.getCliente() : "-");
        }

        document.add(table);
        document.close();
    }
}