package vista;

import com.itextpdf.text.Document; // Requerido por iText
import com.itextpdf.text.Paragraph; // Requerido por iText
import com.itextpdf.text.Font; // Requerido por iText
import com.itextpdf.text.FontFactory; // Requerido por iText
import com.itextpdf.text.Element; // Requerido por iText
import com.itextpdf.text.pdf.PdfWriter; // Requerido por iText

import java.io.FileOutputStream;
import java.io.File;
import java.awt.Desktop; // Necesario para abrir el PDF
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import conex.Conexion;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.LocalDateTime; // Necesaria para el registro de auditoría con hora
import java.time.format.DateTimeFormatter; // Necesaria para formatear la fecha/hora
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author antho
 */
public class ReportePDFGeneratoreli {
    
    // Ruta donde se guardará el PDF
    private static final String RUTA_ARCHIVO = "ReporteReservasEliminadas.pdf";

    public static void generarReportePDFeli() {
        
        // Consulta SQL ajustada a las 13 columnas de tu tabla
        String sql = "SELECT id, mesa, fecha, hora, cliente, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento, Precio, fecha_eliminacion FROM reservas_eliminadas ORDER BY fecha_eliminacion DESC";
        
        Connection con = null;
        Document documento = new Document();
        
        try {
            // 1. CREAR EL ARCHIVO PDF
            PdfWriter.getInstance(documento, new FileOutputStream(RUTA_ARCHIVO));
            documento.open();
            
            // 2. FUENTES
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.UNDERLINE);
            Font fontEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            // 3. ENCABEZADO PRINCIPAL
            Paragraph titulo = new Paragraph("HISTORIAL DE RESERVAS ELIMINADAS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20);
            documento.add(titulo);

            // 4. CONEXIÓN Y DATOS
            con = conex.Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            int contador = 0;
            
            while (rs.next()) {
                contador++;
                
                // --- INICIO DEL FORMATO TIPO TICKET ---
                documento.add(new Paragraph("======================================", fontNormal));
                Paragraph subtitulo = new Paragraph(String.format("REPORTE #%d - ID RESERVA: %d", contador, rs.getInt("id")), fontEncabezado);
                documento.add(subtitulo);
                documento.add(new Paragraph("======================================", fontNormal));
                
                // Detalles
                documento.add(new Paragraph("Fecha Eliminación: " + rs.getString("fecha_eliminacion"), fontNormal));
                documento.add(new Paragraph("Código Reserva   : " + rs.getString("codigo_reserva"), fontNormal));
                documento.add(new Paragraph("Cliente          : " + rs.getString("cliente"), fontNormal));
                documento.add(new Paragraph("Mesa / Sala      : " + rs.getString("mesa") + " / " + rs.getString("sala"), fontNormal));
                documento.add(new Paragraph("Fecha / Hora     : " + rs.getDate("fecha").toString() + " / " + rs.getTime("hora").toString(), fontNormal));
                documento.add(new Paragraph("Total Cobrado    : S/ " + rs.getBigDecimal("Precio"), fontNormal));
                documento.add(new Paragraph("--------------------------------------", fontNormal));
                
                // Agregar un salto de línea después de cada "ticket"
                documento.add(new Paragraph("\n"));
            }
            
            if (contador == 0) {
                 documento.add(new Paragraph("No se encontraron reservas eliminadas en el historial.", fontNormal));
            }

            // 5. CERRAR DOCUMENTO Y CONEXIÓN
            documento.close();
            con.close();
            
            // 6. ABRIR EL PDF AUTOMÁTICAMENTE
            File archivoPdf = new File(RUTA_ARCHIVO);
            if (archivoPdf.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(archivoPdf);
                } else {
                    JOptionPane.showMessageDialog(null, "El sistema no soporta la apertura automática de archivos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                 JOptionPane.showMessageDialog(null, "Error: El archivo PDF no se generó correctamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar o abrir el PDF: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error en PDF: " + e.getMessage());
            if (documento.isOpen()) {
                documento.close();
            }
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ignored) {}
        }
    }
    
}