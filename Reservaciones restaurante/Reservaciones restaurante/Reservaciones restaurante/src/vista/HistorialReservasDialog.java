package vista;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import conex.Conexion;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.sql.DriverManager;
import java.math.BigDecimal;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.time.LocalDate;

public class HistorialReservasDialog extends javax.swing.JDialog {

    private DefaultTableModel modelo;

    private TableRowSorter<DefaultTableModel> sorter;

    public HistorialReservasDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        // Centrar la ventana en la pantalla
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
            // Filtrar visualmente la JTable
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
// Consulta SQL filtrada
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
        // AJUSTE DE TAMAÑO INICIAL PARA MEJOR RESOLUCIÓN: 1100x700
        setPreferredSize(new java.awt.Dimension(1100, 700));

        jPanel2.setBackground(new java.awt.Color(102, 204, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Elephant", 3, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/lista-de-espera (1).png"))); // NOI18N
        // Se ajusta el texto para que se vea mejor centrado
        jLabel1.setText("             HISTORIAL DE RESERVAS");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 0, 800, 101));

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

        btnrepeli.setText("Reporte de Eliminadas");
        btnrepeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepeliActionPerformed(evt);
            }
        });

        btnexportar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/exportar.png"))); // NOI18N
        btnexportar.setText("Exportar CSV");
        btnexportar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnexportar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnexportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnexportarActionPerformed(evt);
            }
        });

        btncerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cerrar-sesion.png"))); // NOI18N
        btncerrar.setText("Cerrar");
        btncerrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btncerrar.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btneliminarreserva.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btneliminarreserva.setText("Eliminar");
        btneliminarreserva.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btneliminarreserva.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btneliminarreserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btneliminarreservaActionPerformed(evt);
            }
        });

        btnrepind.setText("Reporte de Indicadores");
        btnrepind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepindActionPerformed(evt);
            }
        });

<<<<<<< HEAD
        impresiondereporte.setText("Imprimir Reporte PDF");
        impresiondereporte.addActionListener(new java.awt.event.ActionListener() {
=======
        reporteestadistico.setText("reporte estadistico ");
        reporteestadistico.addActionListener(new java.awt.event.ActionListener() {
>>>>>>> ce1857457fdc9ce1d23fd7bbc1a55393328d1f10
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
<<<<<<< HEAD
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(btnrepind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(impresiondereporte, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(btnrepeli, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addContainerGap())
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnexportar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(30, 30, 30)
                                                .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(30, 30, 30)
                                                .addComponent(btncerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btncerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btneliminarreserva, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnexportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(btnFiltrar)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2))))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnrepind)
                                                .addGap(18, 18, 18)
                                                .addComponent(impresiondereporte)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnrepeli)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
=======
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
>>>>>>> ce1857457fdc9ce1d23fd7bbc1a55393328d1f10
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

<<<<<<< HEAD
    private void impresiondereporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_impresiondereporteActionPerformed
        try (Connection con = Conexion.getConexion()) {
            if (con == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
                return;
            }

            // Elegir tipo de reporte
            String[] opciones = {"Normal", "Estadístico"};
            int seleccionReporte = JOptionPane.showOptionDialog(this,
                    "Seleccione el tipo de reporte a generar:",
                    "Generar PDF",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccionReporte == JOptionPane.CLOSED_OPTION) return;

            // Elegir archivo
            JFileChooser fileChooser = new JFileChooser();
            int seleccionArchivo = fileChooser.showSaveDialog(this);
            if (seleccionArchivo != JFileChooser.APPROVE_OPTION) return;

            File archivo = fileChooser.getSelectedFile();
            if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
                archivo = new File(archivo.getAbsolutePath() + ".pdf");
            }
            archivo.getParentFile().mkdirs();

            // Crear PDF
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(archivo));
            document.open();
            document.add(new Paragraph("Reporte de Indicadores"));
            document.add(new Paragraph("\n"));

            Statement st = con.createStatement();

            if (seleccionReporte == 0) { // Normal
                ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total_reservas FROM reservas");
                int totalReservas = rs.next() ? rs.getInt("total_reservas") : 0;

                document.add(new Paragraph("Total de reservas: " + totalReservas));

            } else { // Estadístico
                ResultSet rs = st.executeQuery("SELECT COUNT(*) AS total_reservas, SUM(precio) AS total_precio FROM reservas");
                if (rs.next()) {
                    int total = rs.getInt("total_reservas");
                    double totalPrecio = rs.getDouble("total_precio");
                    document.add(new Paragraph("Total de reservas: " + total));
                    document.add(new Paragraph("Total recaudado: S/. " + totalPrecio));
                }
            }

            document.close();
            JOptionPane.showMessageDialog(this, "PDF generado correctamente en: " + archivo.getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }//GEN-LAST:event_impresiondereporteActionPerformed

    private void btnrepindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepindActionPerformed
// --- 1. CALCULAR EL RANGO DE FECHAS ---

        LocalDate fechaFin = LocalDate.now();
        LocalDate fechaInicio = fechaFin.minusDays(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String strFechaInicio = fechaInicio.format(formatter);
        String strFechaFin = fechaFin.format(formatter);

        // Variables de conteo
        int reservasRealizadas = 0;
        int clientesAtendidos = 0; // SUM(capacidad)
        int satisfechos = 0;
        int insatisfechos = 0;

        // --- 2. CONEXIÓN Y EJECUCIÓN DE CONSULTAS ---

        try (Connection con = conex.Conexion.getConexion()) {

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

            // --- CONSULTA 2: OPINIONES SATISFECHAS (estado = 0, Satisfecho) ---
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

            // --- CONSULTA 3: OPINIONES INSATISFECHAS (estado = 1, Insatisfecho) ---
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

            // --- 3. MOSTRAR RESULTADO (FINAL) [Formato HTML] ---
            String mensajeHTML = String.format(
                    "<html>" +
                            "<head><style>body {font-family: Arial, sans-serif;}</style></head>" +
                            "<body>" +
                            "<h2>📈 Reporte Semanal Consolidado</h2>" +
                            "<p><b>Período:</b> %s al %s</p>" +
                            "<hr>" +
                            "<table border='0' cellpadding='5' cellspacing='0' width='100%%'>" +
                            "<tr><td colspan='2'><h3>📊 Datos de Actividad</h3></td></tr>" +
                            "<tr><td>👤 Clientes Atendidos:</td><td align='right'><b>%d</b></td></tr>" +
                            "<tr><td>🍽️ Mesas Ocupadas (Reservas):</td><td align='right'><b>%d</b></td></tr>" +
                            "<tr><td>📝 Reservas Totales:</td><td align='right'><b>%d</b></td></tr>" +
                            "<tr><td colspan='2'><h3>⭐ Feedback de Clientes</h3></td></tr>" +
                            "<tr><td>💚 Clientes Satisfechos (Estado 0):</td><td align='right'><b>%d</b></td></tr>" +
                            "<tr><td>❓ Clientes Insatisfechos (Estado 1):</td><td align='right'><b>%d</b></td></tr>" +
                            "</table>" +
                            "</body></html>",

                    strFechaInicio,
                    strFechaFin,
                    clientesAtendidos,
                    reservasRealizadas,
                    reservasRealizadas,
                    satisfechos,
                    insatisfechos
            );

            javax.swing.JOptionPane.showMessageDialog(null, mensajeHTML, "Reporte Semanal", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Error al generar el reporte semanal: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
=======
    private void btnrepindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepindActionPerformed
// Llama a la función estática que genera el PDF de Indicadores y lo abre
    // ¡Asegúrate de que este es el nombre exacto de la clase!
    IndicadoresPDFGeneratorindi.generarIndicadoresPDF();
// --- 1. CALCULAR EL RANGO DE FECHAS (Usando java.time.LocalDate) ---
    
    
>>>>>>> ce1857457fdc9ce1d23fd7bbc1a55393328d1f10
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

            // Variables para almacenar los datos (para evitar el error de ResultSet closed)
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

            // Dato de Auditoría (fecha de eliminación)
            String fechaEliminacion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Consultas SQL
            String sqlSelect = "SELECT * FROM reservas WHERE id = ?";
            String sqlInsertEliminadas = "INSERT INTO reservas_eliminadas (id, mesa, fecha, hora, cliente, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento, Precio, fecha_eliminacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            String sqlDelete = "DELETE FROM reservas WHERE id = ?";

            Connection con = null;

            try {
                con = conex.Conexion.getConexion();
                con.setAutoCommit(false); // Inicia Transacción

                // --- 3. PASO 1: SELECCIONAR Y ALMACENAR DATOS ---
                try (PreparedStatement psSelect = con.prepareStatement(sqlSelect)) {
                    psSelect.setInt(1, idReserva);
                    try (ResultSet rs = psSelect.executeQuery()) {

                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(null, "Error: Reserva no encontrada (ID: " + idReserva + ").");
                            con.setAutoCommit(true);
                            return;
                        }

                        // Lectura de los campos
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

                // --- 4. PASO 2: INSERTAR EN EL HISTORIAL ---
                try (PreparedStatement psInsert = con.prepareStatement(sqlInsertEliminadas)) {

                    // 12 parámetros de la reserva
                    psInsert.setInt(1, idReserva);
                    psInsert.setString(2, mesa);
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
                try (PreparedStatement psDelete = con.prepareStatement(sqlDelete)) {
                    psDelete.setInt(1, idReserva);
                    psDelete.executeUpdate();
                }

                // --- 6. PASO 4: CONFIRMAR TRANSACCIÓN Y ACTUALIZAR ---
                con.commit();
                JOptionPane.showMessageDialog(null, "Reserva eliminada y registrada en historial correctamente.", "Eliminación Exitosa", JOptionPane.INFORMATION_MESSAGE);

                // Recargar la tabla:
                cargarDatosReservas();

            } catch (SQLException e) {
                // --- 7. PASO 5: DESHACER TRANSACCIÓN en caso de error ---
                try {
                    if (con != null) {
                        con.rollback();
                    }
                } catch (SQLException ex) {
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
                } catch (SQLException ex) {
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
<<<<<<< HEAD
// 1. Elegir archivo de destino (PDF)
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte de Reservas Eliminadas (PDF)");
        int seleccionArchivo = fileChooser.showSaveDialog(this);
        if (seleccionArchivo != JFileChooser.APPROVE_OPTION) return;

        File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".pdf")) {
            archivo = new File(archivo.getAbsolutePath() + ".pdf");
        }

        // 2. Generar el PDF
        Document document = new Document();
        try (Connection con = conex.Conexion.getConexion();
             FileOutputStream fos = new FileOutputStream(archivo)) {

            PdfWriter.getInstance(document, fos);
            document.open();

            document.add(new Paragraph("REPORTE DE RESERVAS ELIMINADAS/CANCELADAS"));
            document.add(new Paragraph("Fecha de Generación: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            document.add(new Paragraph("\n"));

            // Consulta a la tabla de auditoría
            String sql = "SELECT id, cliente, codigo_reserva, fecha, fecha_eliminacion FROM reservas_eliminadas ORDER BY fecha_eliminacion DESC";
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                int contador = 0;
                while (rs.next()) {
                    document.add(new Paragraph("-------------------------------------------------------------------------------------------------------------------"));
                    document.add(new Paragraph("ID Reserva Original: " + rs.getInt("id")));
                    document.add(new Paragraph("Cliente: " + rs.getString("cliente")));
                    document.add(new Paragraph("Código: " + rs.getString("codigo_reserva")));
                    document.add(new Paragraph("Fecha Original: " + rs.getDate("fecha")));
                    document.add(new Paragraph("FECHA DE ELIMINACIÓN: " + rs.getString("fecha_eliminacion")));
                    contador++;
                }
                document.add(new Paragraph("\nTOTAL DE RESERVAS ELIMINADAS REGISTRADAS: " + contador));
            }

            document.close();
            JOptionPane.showMessageDialog(this, "Reporte de Reservas Eliminadas generado con éxito en:\n" + archivo.getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar el reporte de eliminadas: " + e.getMessage(), "Error PDF", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
=======
// Crear una instancia del JDialog del reporte
   // Crear una instancia de la ventana de reporte estilizado
     ReporteEliminadasDialog reporte = new  ReporteEliminadasDialog(null, true); 
    // Llama a la función estática que genera el PDF y lo abre
    ReportePDFGeneratoreli.generarReportePDFeli();
    // Muestra la ventana del reporte
    reporte.setVisible(true); // TODO add your handling code here:
>>>>>>> ce1857457fdc9ce1d23fd7bbc1a55393328d1f10
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