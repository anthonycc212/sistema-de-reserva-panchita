package vista;

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
import java.math.BigDecimal;
public class HistorialReservasDialog extends javax.swing.JDialog {

    private DefaultTableModel modelo;
    private void cargarReservas() {
    DefaultTableModel modelo = (DefaultTableModel) tblReservas.getModel();
    modelo.setRowCount(0); // Limpiar la tabla antes de recargar

    try (Connection con = Conexion.getConexion()) {
        PreparedStatement ps = con.prepareStatement("SELECT * FROM reservas");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            modelo.addRow(new Object[]{
                rs.getInt("id"),
                rs.getInt("mesa"),
                rs.getDate("fecha"),
                rs.getTime("hora"),
                rs.getString("cliente"),
                rs.getInt("capacidad"),
                rs.getString("sala"),
                rs.getString("codigo_reserva"),
                rs.getString("metodo_pago"),
                rs.getString("estado_pago"),
                rs.getInt("estacionamiento"),
                rs.getBigDecimal("precio"),
                rs.getTimestamp("fecha_eliminacion")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + e.getMessage());
    }
}
    private TableRowSorter<DefaultTableModel> sorter;

    public HistorialReservasDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocationRelativeTo(null);

        String[] columnas = {"ID", "Codigo Reserva", "Cliente", "Sala", "Mesa", "Fecha", "Hora", "Capacidad", "Método Pago", "Estado Pago"};
        modelo = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable
            }
        };
        

        tblReservas.setModel(modelo);
        sorter = new TableRowSorter<>(modelo);
        tblReservas.setRowSorter(sorter);

        cargarDatosReservas();

        btnFiltrar.addActionListener(e -> aplicarFiltro());
        btncerrar.addActionListener(e -> dispose());
        btnexportar.addActionListener(e -> exportarCSV());
        txtBuscar.addActionListener(e -> aplicarFiltro());
    }
private void generarReportePDF() {
    Connection con = Conexion.getConexion();
    if (con == null) {
        JOptionPane.showMessageDialog(this, "No hay conexión a la base de datos");
        return;
    }

    // Rango: últimos 7 días (incluyendo hoy)
    LocalDate hoy = LocalDate.now();
    LocalDate hace7dias = hoy.minusDays(6);

    String query = """
            SELECT fecha, hora, precio
            FROM reservas
            WHERE fecha BETWEEN ? AND ?
            ORDER BY fecha ASC
            """;

    double totalGeneral = 0.0;

    try (PreparedStatement ps = con.prepareStatement(query)) {

        ps.setDate(1, java.sql.Date.valueOf(hace7dias));
        ps.setDate(2, java.sql.Date.valueOf(hoy));

        ResultSet rs = ps.executeQuery();

        // Ruta donde se guardará el PDF (escritorio)
        String home = System.getProperty("user.home");
        String rutaPDF = home + "/Desktop/Reporte_Semanal_Reservas.pdf";

        // Crear documento PDF
        com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
        com.itextpdf.text.pdf.PdfWriter.getInstance(doc, new java.io.FileOutputStream(rutaPDF));
        doc.open();

        // Título del PDF
        com.itextpdf.text.Font tituloFont = 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);

        com.itextpdf.text.Paragraph titulo = 
                new com.itextpdf.text.Paragraph("REPORTE DE INGRESOS (Últimos 7 días)\n\n", tituloFont);

        titulo.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        doc.add(titulo);

        com.itextpdf.text.Font texto = 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12);

        // Listar cada reserva
        while (rs.next()) {
            Date fecha = rs.getDate("fecha");
            String hora = rs.getString("hora");
            double precio = rs.getDouble("precio");

            totalGeneral += precio;

            String registro = String.format(
                "📅 Fecha: %s    🕒 Hora: %s    💵 Precio: S/ %.2f\n",
                fecha.toString(), hora, precio
            );

            doc.add(new com.itextpdf.text.Paragraph(registro, texto));
        }

        doc.add(new com.itextpdf.text.Paragraph("\n---------------------------------------------\n"));

        // Total general
        com.itextpdf.text.Font totalFont = 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);

        doc.add(new com.itextpdf.text.Paragraph(
                "TOTAL GENERAL: S/ " + String.format("%.2f", totalGeneral), totalFont));

        doc.close();

        // Abrir PDF automáticamente
        java.awt.Desktop.getDesktop().open(new java.io.File(rutaPDF));

        JOptionPane.showMessageDialog(this, "PDF generado correctamente en el Escritorio");

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
    }
}
    private void cargarDatosReservas() {
        modelo.setRowCount(0); // Limpiar tabla

        String url = "jdbc:mysql://localhost:3306/baserestaurante";
        String usuario = "root";
        String contraseña = "";

        String sql = "SELECT id, codigo_reserva, cliente, sala, mesa, fecha, hora, capacidad, metodo_pago, estado_pago "
                   + "FROM reservas ORDER BY fecha DESC, hora DESC";

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("codigo_reserva"),
                    rs.getString("cliente"),
                    rs.getString("sala"),
                    rs.getString("mesa"),
                    rs.getDate("fecha"),
                    rs.getTime("hora"),
                    rs.getInt("capacidad"),
                    rs.getString("metodo_pago"),
                    rs.getString("estado_pago")
                };
                modelo.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + e.getMessage());
        }
    }

    private void aplicarFiltro() {
        String texto = txtBuscar.getText();
        if (texto.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Buscar en todas las columnas (incluyendo método y estado de pago)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    private void exportarCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo CSV");
        int seleccion = fileChooser.showSaveDialog(this);

        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            exportarTablaA_CSV(tblReservas, archivo);
        }
    }

    private void cargarDatosReservasFiltradas(String textoBusqueda) {
        String[] columnas = {"ID", "Codigo Reserva", "Cliente", "Sala", "Mesa", "Fecha", "Hora", "Capacidad", "Método Pago", "Estado Pago"};
        DefaultTableModel modeloFiltrado = new DefaultTableModel(null, columnas);

        String sql = "SELECT id, codigo_reserva, cliente, sala, mesa, fecha, hora, capacidad, metodo_pago, estado_pago FROM reservas "
                   + "WHERE cliente LIKE ? OR sala LIKE ? OR mesa LIKE ? OR fecha LIKE ? OR metodo_pago LIKE ? OR estado_pago LIKE ? "
                   + "ORDER BY fecha DESC, hora DESC";
//llamada de reservas 
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String filtro = "%" + textoBusqueda + "%";
            for (int i = 1; i <= 6; i++) {
                ps.setString(i, filtro);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Object[] fila = {
                    rs.getInt("id"),
                    rs.getString("codigo_reserva"),
                    rs.getString("cliente"),
                    rs.getString("sala"),
                    rs.getString("mesa"),
                    rs.getDate("fecha"),
                    rs.getTime("hora"),
                    rs.getInt("capacidad"),
                    rs.getString("metodo_pago"),
                    rs.getString("estado_pago")
                };
                modeloFiltrado.addRow(fila);
            }

            tblReservas.setModel(modeloFiltrado);
            sorter.setModel(modeloFiltrado);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar reservas: " + e.getMessage());
        }
    }

    private void exportarTablaA_CSV(JTable tabla, File archivo) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivo))) {
            // Escribir cabeceras
            TableModel modeloExportar = tabla.getModel();
            for (int i = 0; i < modeloExportar.getColumnCount(); i++) {
                pw.print(modeloExportar.getColumnName(i));
                if (i < modeloExportar.getColumnCount() - 1) pw.print(";");
            }
            pw.println();

            // Escribir filas
            for (int row = 0; row < modeloExportar.getRowCount(); row++) {
                for (int col = 0; col < modeloExportar.getColumnCount(); col++) {
                    pw.print(modeloExportar.getValueAt(row, col));
                    if (col < modeloExportar.getColumnCount() - 1) pw.print(";");
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(this, "Reservas exportadas con éxito a:\n" + archivo.getAbsolutePath());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + e.getMessage());
        }
    }

    // No olvides incluir el método initComponents() y las variables btnFiltrar, btncerrar, btnexportar, txtBuscar, tblReservas según tu GUI



    
    
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservas = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnrepeli = new javax.swing.JButton();
        btnexportar = new javax.swing.JButton();
        btncerrar = new javax.swing.JButton();
        btneliminarreserva = new javax.swing.JButton();
        btnrepind = new javax.swing.JButton();
        reporteestadistico = new javax.swing.JButton();
        reportedeingresos = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Elephant", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lista-de-espera (1).png"))); // NOI18N
        jLabel1.setText("                                     HISTORIAL    RESERVAS");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 0, 849, 101));

        getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        tblReservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID ", "Cliente", "Sala", "Mesa", "Fecha", "Hora", "Capacidad", "Codigo Reserva", "Metodo pago", "Estado Pago"
            }
        ));
        jScrollPane1.setViewportView(tblReservas);

        jLabel2.setFont(new java.awt.Font("Imprint MT Shadow", 3, 24)); // NOI18N
        jLabel2.setText("Buscar  :");

        btnFiltrar.setForeground(new java.awt.Color(255, 255, 255));
        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/a-la-vista.png"))); // NOI18N
        btnFiltrar.setBorder(null);
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnrepeli.setText("Reporte de eliminacion de reserva");
        btnrepeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepeliActionPerformed(evt);
            }
        });

        btnexportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exportar.png"))); // NOI18N
        btnexportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnexportarActionPerformed(evt);
            }
        });

        btncerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cerrar-sesion.png"))); // NOI18N

        btneliminarreserva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btneliminarreserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarreservaActionPerformed(evt);
            }
        });

        btnrepind.setText("Reporte de indicadores");
        btnrepind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepindActionPerformed(evt);
            }
        });

        reporteestadistico.setText("reporte estadistico ");
        reporteestadistico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteestadisticoActionPerformed(evt);
            }
        });

        reportedeingresos.setText("reporte de ingresos ");
        reportedeingresos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportedeingresosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(233, 233, 233)
                                .addComponent(btnexportar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69)
                                .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(btncerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(reporteestadistico, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 880, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(54, 54, 54)
                                    .addComponent(btnrepind))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(37, 37, 37)
                                    .addComponent(btnrepeli, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addComponent(reportedeingresos, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(127, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(44, 44, 44))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(34, 34, 34)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(txtBuscar)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(31, 31, 31)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnexportar)))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(btncerrar)))
                                .addGap(18, 18, 18)))
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(159, 159, 159)
                        .addComponent(btnrepind)
                        .addGap(18, 18, 18)
                        .addComponent(btnrepeli)
                        .addGap(18, 18, 18)
                        .addComponent(reporteestadistico)
                        .addGap(18, 18, 18)
                        .addComponent(reportedeingresos)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(789, 789, 789))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnrepindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepindActionPerformed
// Llama a la función estática que genera el PDF de Indicadores y lo abre
    // ¡Asegúrate de que este es el nombre exacto de la clase!
    IndicadoresPDFGeneratorindi.generarIndicadoresPDF();
// --- 1. CALCULAR EL RANGO DE FECHAS (Usando java.time.LocalDate) ---
    
    
    }//GEN-LAST:event_btnrepindActionPerformed

    private void btneliminarreservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarreservaActionPerformed
      // --- 1. OBTENER FILA Y VALIDAR ---
    int filaSeleccionada = tblReservas.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una reserva para eliminar");
        return;
    }

    int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de eliminar la reserva seleccionada? Se registrará en el historial.", 
            "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

    if (confirmacion == JOptionPane.YES_OPTION) {
        
        // --- 2. PREPARACIÓN DE DATOS Y SQL ---
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
        
        // Variables para almacenar los datos (SOLUCIÓN al error de ResultSet closed)
        String mesa = null;
        java.sql.Date fecha = null;
        java.sql.Time hora = null;
        String cliente = null;
        int capacidad = 0;
        String sala = null;
        String codigoReserva = null;
        String metodoPago = null;
        String estadoPago = null;
        int estacionamiento = 0;
        java.math.BigDecimal precio = null;
        
        // Dato de Auditoría (solo fecha)
        String fechaEliminacion = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Consultas SQL
        String sqlSelect = "SELECT * FROM reservas WHERE id = ?"; 
        // 13 columnas en total para el INSERT (sin 'usuario_eliminacion')
        String sqlInsertEliminadas = "INSERT INTO reservas_eliminadas (id, mesa, fecha, hora, cliente, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento, Precio, fecha_eliminacion) "
                                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        String sqlDelete = "DELETE FROM reservas WHERE id = ?"; 
        
        Connection con = null;

        try {
            con = conex.Conexion.getConexion();
            con.setAutoCommit(false); // Inicia Transacción
            
            // --- 3. PASO 1: SELECCIONAR Y ALMACENAR DATOS ---
            try (java.sql.PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                psSelect.setInt(1, idReserva);
                try (java.sql.ResultSet rs = psSelect.executeQuery()) {
                    
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "Error: Reserva no encontrada (ID: " + idReserva + ").");
                        con.setAutoCommit(true); 
                        return;
                    }

                    // Lectura de los 12 campos de la reserva
                    mesa = rs.getString("mesa");
                    fecha = rs.getDate("fecha");
                    hora = rs.getTime("hora");
                    cliente = rs.getString("cliente");
                    capacidad = rs.getInt("capacidad");
                    sala = rs.getString("sala");
                    codigoReserva = rs.getString("codigo_reserva");
                    metodoPago = rs.getString("metodo_pago");
                    estadoPago = rs.getString("estado_pago");
                    estacionamiento = rs.getInt("estacionamiento");
                    precio = rs.getBigDecimal("Precio"); 
                }
            }
            
            // --- 4. PASO 2: INSERTAR EN EL HISTORIAL (13 Parámetros) ---
            try (java.sql.PreparedStatement psInsert = con.prepareStatement(sqlInsertEliminadas)) {
                
                // 12 parámetros de la reserva
                psInsert.setInt(1, idReserva); 
                psInsert.setString(2, mesa); // Ahora inserta la cadena de texto
                psInsert.setDate(3, fecha);
                psInsert.setTime(4, hora);
                psInsert.setString(5, cliente);
                psInsert.setInt(6, capacidad);
                psInsert.setString(7, sala);
                psInsert.setString(8, codigoReserva);
                psInsert.setString(9, metodoPago);
                psInsert.setString(10, estadoPago);
                psInsert.setInt(11, estacionamiento);
                psInsert.setBigDecimal(12, precio); 
                
                // 1 parámetro de auditoría
                psInsert.setString(13, fechaEliminacion); 
                
                psInsert.executeUpdate();
            }
            
            // --- 5. PASO 3: ELIMINAR DE LA TABLA ORIGINAL ---
            try (java.sql.PreparedStatement psDelete = con.prepareStatement(sqlDelete)) {
                psDelete.setInt(1, idReserva); 
                psDelete.executeUpdate();
            }
            
            // --- 6. PASO 4: CONFIRMAR TRANSACCIÓN Y ACTUALIZAR ---
            con.commit(); 
            JOptionPane.showMessageDialog(null, "Reserva eliminada y registrada en historial correctamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            
            // Asegúrate de tener este método en tu clase para recargar la tabla:
            cargarDatosReservas(); 
            
        } catch (java.sql.SQLException e) {
            // --- 7. PASO 5: DESHACER TRANSACCIÓN en caso de error ---
            try {
                if (con != null) {
                    con.rollback(); 
                }
            } catch (java.sql.SQLException ex) {
                System.err.println("Error al realizar rollback: " + ex.getMessage());
            }
            JOptionPane.showMessageDialog(null, "Error SQL: La reserva no pudo ser eliminada o registrada.\n" + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            System.err.println("Error SQL: " + e.getMessage());
            
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true); 
                    con.close();
                }
            } catch (java.sql.SQLException ex) {
                System.err.println("Error al cerrar conexión: " + ex.getMessage());
            }
        }
    }
    }//GEN-LAST:event_btneliminarreservaActionPerformed

    private void btnexportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnexportarActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo CSV");
        fileChooser.setSelectedFile(new File("reservas.csv"));

        int seleccion = fileChooser.showSaveDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".csv")) {
                archivo = new File(archivo.getAbsolutePath() + ".csv");
            }
            exportarTablaA_CSV(tblReservas, archivo);
        }
    }//GEN-LAST:event_btnexportarActionPerformed

    private void btnrepeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepeliActionPerformed
// Crear una instancia del JDialog del reporte
   // Crear una instancia de la ventana de reporte estilizado
     ReporteEliminadasDialog reporte = new  ReporteEliminadasDialog(null, true); 
    // Llama a la función estática que genera el PDF y lo abre
    ReportePDFGeneratoreli.generarReportePDFeli();
    // Muestra la ventana del reporte
    reporte.setVisible(true); // TODO add your handling code here:
    }//GEN-LAST:event_btnrepeliActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        String textoBusqueda = txtBuscar.getText().trim();
        cargarDatosReservasFiltradas(textoBusqueda);
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void reporteestadisticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteestadisticoActionPerformed
       // Crear una instancia de la ventana de reporte estilizado
     reporteEstadistico reporte = new  reporteEstadistico(null, true); 
    // Llama a la función estática que genera el PDF y lo abre
     reportogeneradorpdfestadistico.generarReportePDFestadistico();
    // Muestra la ventana del reporte
    
    // Muestra la ventana del reporte
    reporte.setVisible(true); // TODO add your handling code here:
    }//GEN-LAST:event_reporteestadisticoActionPerformed

    private void reportedeingresosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportedeingresosActionPerformed
 generarReportePDF();
    }//GEN-LAST:event_reportedeingresosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(HistorialReservasDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(HistorialReservasDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(HistorialReservasDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(HistorialReservasDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                HistorialReservasDialog dialog = new HistorialReservasDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btncerrar;
    private javax.swing.JButton btneliminarreserva;
    private javax.swing.JButton btnexportar;
    private javax.swing.JButton btnrepeli;
    private javax.swing.JButton btnrepind;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton reportedeingresos;
    private javax.swing.JButton reporteestadistico;
    private javax.swing.JTable tblReservas;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
