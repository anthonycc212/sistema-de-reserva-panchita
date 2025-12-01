/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author antho
 */
import java.sql.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;


public class reporteEstadistico extends JDialog {
   private JTextArea textAreaReporte;

    public reporteEstadistico(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Reporte de Historial de Eliminaciones");
        this.setSize(600, 800);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);

        // Inicializar el área de texto
        textAreaReporte = new JTextArea();
        textAreaReporte.setEditable(false);
        
        // Usar una fuente monoespaciada para que el formato de ticket se vea alineado
        textAreaReporte.setFont(new Font("Monospaced", Font.PLAIN, 13)); 

        // Agregar el área de texto a un scroll pane
        this.add(new JScrollPane(textAreaReporte), BorderLayout.CENTER);

        // Cargar los datos y generar el reporte
        generarReporte();
    }

    /**
     * Consulta los datos de reservas_eliminadas y genera el reporte formateado.
     */
    private void generarReporte() {
        
        // Consulta SQL ajustada a las 13 columnas de tu tabla
        String sql = "SELECT id, mesa, fecha, hora, cliente, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento, Precio, fecha FROM reservas ORDER BY fecha DESC";
        
        Connection con = null;
        StringBuilder reporte = new StringBuilder();
        
        // Formateadores
        DecimalFormat df = new DecimalFormat("S/ #,##0.00");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        // Encabezado del Reporte
        reporte.append("==================================================\n");
        reporte.append("         HISTORIAL DE Reporte estadistico\n");
        reporte.append("==================================================\n\n");
        
        int contador = 0;

        try {
            con = conex.Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                contador++;
                
                // --- INICIO DEL FORMATO TIPO TICKET PARA CADA RESERVA ---
                reporte.append("--------------------------------------------------\n");
                reporte.append(String.format("REPORTE #%-4d - ID RESERVA: %d\n", contador, rs.getInt("id")));
                reporte.append("--------------------------------------------------\n");
                
                reporte.append(String.format("Fecha : %s\n", rs.getString("fecha")));
                reporte.append(String.format("Código Reserva : %s\n", rs.getString("codigo_reserva")));
                reporte.append(String.format("Cliente        : %s\n", rs.getString("cliente")));
                reporte.append(String.format("Mesa / Sala    : %s / %s\n", rs.getString("mesa"), rs.getString("sala")));
                reporte.append(String.format("Fecha / Hora   : %s / %s\n", dateFormat.format(rs.getDate("fecha")), rs.getTime("hora").toString()));
                reporte.append(String.format("Capacidad      : %d personas\n", rs.getInt("capacidad")));
                reporte.append(String.format("Método Pago    : %s\n", rs.getString("metodo_pago")));
                reporte.append(String.format("Estado Pago    : %s\n", rs.getString("estado_pago")));
                
                // Manejar el campo Precio y Estacionamiento
                BigDecimal precio = rs.getBigDecimal("Precio");
                String precioFormateado = (precio != null) ? df.format(precio) : "S/ 0.00";
                
                reporte.append(String.format("Total Cobrado  : %s\n", precioFormateado));
                reporte.append(String.format("Estacionamiento: %d\n", rs.getInt("estacionamiento")));
                
                reporte.append("==================================================\n\n");
            }
            
            if (contador == 0) {
                 reporte.append("No se encontraron reservas eliminadas en el historial.");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            reporte.append("\n[ERROR DE BASE DE DATOS]");
            System.err.println("Error SQL Reporte: " + e.getMessage());
            
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
        
        // Mostrar el reporte en el JTextArea
        textAreaReporte.setText(reporte.toString());
    } 
   
}