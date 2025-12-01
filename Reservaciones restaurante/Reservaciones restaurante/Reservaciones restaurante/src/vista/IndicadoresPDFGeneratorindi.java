package vista;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.File;
import java.awt.Desktop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

// La clase debe coincidir con el nombre de tu archivo
public class IndicadoresPDFGeneratorindi {
    
    private static final String RUTA_ARCHIVO = "ReporteDeIndicadoresSemanal.pdf";

    public static void generarIndicadoresPDF() {
        
        // --- 1. CÁLCULO DE FECHAS ---
        java.time.LocalDate fechaFin = java.time.LocalDate.now();
        java.time.LocalDate fechaInicio = fechaFin.minusDays(6); 
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String strFechaInicio = fechaInicio.format(formatter);
        String strFechaFin = fechaFin.format(formatter);
        
        // Variables de conteo
        int reservasRealizadas = 0; // Se usa para Mesas ocupadas y Reservas totales
        int clientesAtendidos = 0; // SUM(capacidad)
        int satisfechos = 0;
        int insatisfechos = 0;
        // >> NUEVA VARIABLE PARA BILLETERAS DIGITALES
int billeterasDigitales = 0;
        // --- 2. CONEXIÓN Y EJECUCIÓN DE CONSULTAS ---
        Connection con = null;

        try { 
            con = conex.Conexion.getConexion();
            
            // --- CONSULTA 1: RESERVAS, MESAS Y CLIENTES (Tabla 'reservas') ---
            String sqlReservas = "SELECT COUNT(*) AS total_reservas, SUM(capacidad) AS total_clientes FROM reservas WHERE fecha BETWEEN ? AND ?"; 
            try (PreparedStatement ps = con.prepareStatement(sqlReservas)) {
                ps.setString(1, strFechaInicio);
                ps.setString(2, strFechaFin);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        reservasRealizadas = rs.getInt("total_reservas");
                        clientesAtendidos = rs.getInt("total_clientes"); 
                    }
                }
            }
            
            // --- CONSULTA 2: OPINIONES SATISFECHAS (estado = 0) ---
            String sqlSatisfechos = "SELECT COUNT(*) AS total FROM opiniones WHERE estado = 0 AND fechaopinion BETWEEN ? AND ?"; 
            try (PreparedStatement ps = con.prepareStatement(sqlSatisfechos)) {
                ps.setString(1, strFechaInicio);
                ps.setString(2, strFechaFin);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        satisfechos = rs.getInt("total");
                    }
                }
            }
            
            // --- CONSULTA 3: OPINIONES INSATISFECHAS (estado = 1) ---
            String sqlInsatisfechos = "SELECT COUNT(*) AS total FROM opiniones WHERE estado = 1 AND fechaopinion BETWEEN ? AND ?"; 
            try (PreparedStatement ps = con.prepareStatement(sqlInsatisfechos)) {
                ps.setString(1, strFechaInicio);
                ps.setString(2, strFechaFin);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        insatisfechos = rs.getInt("total");
                        
                    }
                    
                }
              
            }
            // --- CONSULTA 4: BILLETERAS DIGITALES (Yape y Plin) ---
String sqlBilleteras = "SELECT COUNT(*) AS total FROM reservas WHERE metodo_pago IN ('Yape', 'Plin') AND fecha BETWEEN ? AND ?"; 
try (PreparedStatement ps = con.prepareStatement(sqlBilleteras)) {
    ps.setString(1, strFechaInicio);
    ps.setString(2, strFechaFin);
    
    try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            billeterasDigitales = rs.getInt("total");
        }
    }
}
            
            // -------------------------------------------------------------------
            // --- 3. GENERACIÓN DEL PDF (REEMPLAZO DEL JOPTIONPANE) ---
            // -------------------------------------------------------------------
            
            Document documento = new Document();
            PdfWriter.getInstance(documento, new FileOutputStream(RUTA_ARCHIVO));
            documento.open();
            
            // Fuentes
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 10);
            
            // Encabezado y Período
            Paragraph titulo = new Paragraph("--- Reporte Semanal de indicadores---", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);
            documento.add(titulo);
            
            Paragraph periodo = new Paragraph(String.format("Período: %s al %s", strFechaInicio, strFechaFin), fontEncabezado);
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingAfter(15);
            documento.add(periodo);

            // Sección y Datos
          
            documento.add(new Paragraph("INDICADORES DE RESERVA Y CLIENTES", fontEncabezado));
           
            
            documento.add(new Paragraph("\n"));
            documento.add(new Paragraph(String.format(" Clientes Atendidos en la Semana: %d", clientesAtendidos), fontNormal));
            documento.add(new Paragraph(String.format("️ Mesas ocupadas en la semana: %d", reservasRealizadas), fontNormal)); 
            documento.add(new Paragraph(String.format(" Reservas totales en la semana: %d", reservasRealizadas), fontNormal));
            
            documento.add(new Paragraph("\n"));
            
            // Sección de Satisfacción
           documento.add(new Paragraph("\n"));
            documento.add(new Paragraph("INDICADORES DE OPINIÓN", fontEncabezado));
           documento.add(new Paragraph("\n"));
            
            documento.add(new Paragraph(String.format(" Clientes Satisfechos (estado 0): %d", satisfechos), fontNormal));
            documento.add(new Paragraph(String.format("? Clientes Insatisfechos (estado 1): %d", insatisfechos), fontNormal));
            // Sección de Pago Digital
documento.add(new Paragraph("\n"));
documento.add(new Paragraph("INDICADORES DE PAGO DIGITAL", fontEncabezado));
documento.add(new Paragraph("\n"));


documento.add(new Paragraph(String.format(" Pagos con billetera digital (Yape/Plin) (Reservas): %d", billeterasDigitales), fontNormal));

documento.add(new Paragraph("\n"));
// ******************************************************
           
            
            documento.add(new Paragraph("Reporte hecho el dia: " + LocalDateTime.now().toString(), fontNormal));

            // Cerrar documento
            documento.close();
            
            // Abrir el PDF automáticamente
            File archivoPdf = new File(RUTA_ARCHIVO);
            if (archivoPdf.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoPdf);
            } else {
                JOptionPane.showMessageDialog(null, "El archivo PDF se generó, pero no se pudo abrir automáticamente.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al generar el reporte semanal: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error en Reporte Semanal PDF: " + e.getMessage());
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ignored) { /* Ignorar error al cerrar */ }
        }
    }
}