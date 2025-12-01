/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import conex.Conexion;


public class reportogeneradorpdfestadistico {
      // Ruta donde se guardará el PDF
    // Ruta donde se guardará el PDF (ajustado para reflejar el contenido)
    private static final String RUTA_ARCHIVO = "ReporteReservasUltimos7Dias.pdf";

    public static void generarReportePDFestadistico() {
        
        // Configuración de fechas para el filtro de 7 días
        java.time.LocalDate fechaFin = java.time.LocalDate.now();
        java.time.LocalDate fechaInicio = fechaFin.minusDays(6);    
        
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String strFechaInicio = fechaInicio.format(formatter);
        String strFechaFin = fechaFin.format(formatter);
        
        // Formateador para el precio dentro del PDF
        DecimalFormat df = new DecimalFormat("S/ #,##0.00");

        // Estructura para almacenar la suma total de cobros por día (Fecha -> Monto Total)
        // Estas variables se declaran FUERA del bucle, por lo que su alcance es correcto.
        Map<String, BigDecimal> cobrosPorDia = new HashMap<>();
        // Contador global de cobro total
        BigDecimal totalGeneral = BigDecimal.ZERO;

        // Consulta SQL con filtro de rango de fechas
        String sql = "SELECT id, mesa, fecha, hora, cliente, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento, Precio " +
                     "FROM reservas " +
                     "WHERE fecha BETWEEN ? AND ? " + 
                     "ORDER BY fecha DESC";
        
        Connection con = null; // Declarada fuera del try para que sea accesible en el finally
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
            Paragraph titulo = new Paragraph("REPORTE ESTADÍSTICO DE RESERVAS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Paragraph rango = new Paragraph("Periodo: " + strFechaInicio + " a " + strFechaFin, fontEncabezado);
            rango.setAlignment(Element.ALIGN_CENTER);
            rango.setSpacingAfter(20);
            documento.add(rango);

            // 4. CONEXIÓN Y DATOS
            con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            
            // Asignar valores a los placeholders
            ps.setString(1, strFechaInicio);
            ps.setString(2, strFechaFin);
            
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
                String fechaStr = rs.getString("fecha");
                // La variable 'precio' está declarada AQUI y SOLO es accesible dentro del bucle.
                BigDecimal precio = rs.getBigDecimal("Precio"); 
                String precioFormateado = df.format(precio);

                documento.add(new Paragraph("Fecha : " + fechaStr, fontNormal));
                documento.add(new Paragraph("Código Reserva  : " + rs.getString("codigo_reserva"), fontNormal));
                documento.add(new Paragraph("Cliente         : " + rs.getString("cliente"), fontNormal));
                documento.add(new Paragraph("Mesa / Sala     : " + rs.getString("mesa") + " / " + rs.getString("sala"), fontNormal));
                documento.add(new Paragraph("Fecha / Hora    : " + rs.getDate("fecha").toString() + " / " + rs.getTime("hora").toString(), fontNormal));
                documento.add(new Paragraph("Total Cobrado   : " + precioFormateado, fontNormal));
                documento.add(new Paragraph("--------------------------------------", fontNormal));
                
                // Agregar un salto de línea después de cada "ticket"
                documento.add(new Paragraph("\n"));
                
                // 4.1. AGREGACIÓN DE DATOS PARA ESTADÍSTICAS
                if (precio != null) {
                    totalGeneral = totalGeneral.add(precio);
                    // Sumar el precio al total del día. BigDecimal::add es un BiFunction
                    cobrosPorDia.merge(fechaStr, precio, BigDecimal::add);
                }
                
            } // CIERRE DEL BUCLE WHILE. 'precio' deja de existir aquí.
            
            if (contador == 0) {
                // Mensaje ajustado para el filtro de 7 días
                documento.add(new Paragraph("No se encontraron reservas registradas en el periodo del " + strFechaInicio + " al " + strFechaFin + ".", fontNormal));
            }

            // 5. CÁLCULO Y AGREGACIÓN DE ESTADÍSTICAS
            
            String diaMenosCobro = "N/A";
            // Inicializar con un valor muy alto
            BigDecimal montoMenosCobro = new BigDecimal(Double.MAX_VALUE); 
            
            String diaMasCobro = "N/A";
            // Inicializar con cero para que el primer valor sea mayor
            BigDecimal montoMasCobro = BigDecimal.ZERO; 
            
            int diasConCobro = 0;

            for (Map.Entry<String, BigDecimal> entry : cobrosPorDia.entrySet()) {
                diasConCobro++;
                String dia = entry.getKey();
                BigDecimal monto = entry.getValue();

                // DÍA CON MAYOR COBRO: Comparar si el monto actual es mayor
                if (monto.compareTo(montoMasCobro) > 0) {
                    montoMasCobro = monto;
                    diaMasCobro = dia;
                }
                
                // DÍA CON MENOR COBRO: Comparar si el monto actual es menor
                if (monto.compareTo(montoMenosCobro) < 0) {
                    montoMenosCobro = monto;
                    diaMenosCobro = dia;
                }
            }

            // Calcular Promedio
            BigDecimal promedioCobro = BigDecimal.ZERO;
            if (diasConCobro > 0) {
                // Dividir el total general por el número de días que tuvieron cobro (diasConCobro)
                promedioCobro = totalGeneral.divide(new BigDecimal(diasConCobro), 2, java.math.RoundingMode.HALF_UP);
            }
            
            // 6. IMPRIMIR ESTADÍSTICAS EN EL PDF
            documento.add(new Paragraph("\n\n")); // Espacio de separación
            documento.add(new Paragraph("======================================", fontEncabezado));
            documento.add(new Paragraph("      RESUMEN ESTADÍSTICO DE COBROS      ", fontEncabezado));
            documento.add(new Paragraph("======================================", fontEncabezado));

            if (diasConCobro > 0) {
                documento.add(new Paragraph("Cobro Total del Período: " + df.format(totalGeneral), fontEncabezado));
                documento.add(new Paragraph("--------------------------------------", fontNormal));
                documento.add(new Paragraph("Día con Menor Cobro: " + diaMenosCobro + " (" + df.format(montoMenosCobro) + ")", fontNormal));
                documento.add(new Paragraph("Día con Mayor Cobro: " + diaMasCobro + " (" + df.format(montoMasCobro) + ")", fontNormal));
                documento.add(new Paragraph("Promedio de Cobro Diario: " + df.format(promedioCobro), fontEncabezado));
            } else {
                documento.add(new Paragraph("No hay datos de cobro para generar estadísticas.", fontNormal));
            }
            documento.add(new Paragraph("======================================", fontEncabezado));


            // 7. CERRAR DOCUMENTO
            documento.close();
            
            // 8. ABRIR EL PDF AUTOMÁTICAMENTE
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
    } // Cierre del método generarReportePDFestadistico
    }
