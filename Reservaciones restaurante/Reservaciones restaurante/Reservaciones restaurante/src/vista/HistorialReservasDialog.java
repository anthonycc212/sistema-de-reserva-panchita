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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
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

        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservas = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnexportar = new javax.swing.JButton();
        btncerrar = new javax.swing.JButton();
        btneliminarreserva = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnrepind = new javax.swing.JButton();
        impresiondereporte = new javax.swing.JButton();
        btnrepeli = new javax.swing.JButton();

        jButton1.setText("jButton1");

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

        jButton2.setText("jButton2");

        jButton3.setText("jButton3");

        btnrepind.setText("Reporte de indicadores");
        btnrepind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepindActionPerformed(evt);
            }
        });

        impresiondereporte.setText("imprimir reporte pdf");
        impresiondereporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                impresiondereporteActionPerformed(evt);
            }
        });

        btnrepeli.setText("Reporte de eliminacion de reserva");
        btnrepeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnrepeliActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(166, 166, 166)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnexportar, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(67, 67, 67)
                                .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(btncerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 849, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnrepind)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(impresiondereporte))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(btnrepeli, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(19, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 566, Short.MAX_VALUE)
                    .addComponent(jButton2)
                    .addGap(0, 567, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(567, Short.MAX_VALUE)
                    .addComponent(jButton3)
                    .addContainerGap(566, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtBuscar)))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnexportar)
                    .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncerrar))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnrepind)
                            .addComponent(impresiondereporte))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnrepeli, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jButton2)
                    .addGap(0, 0, Short.MAX_VALUE)))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addContainerGap(278, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(294, Short.MAX_VALUE)))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        String textoBusqueda = txtBuscar.getText().trim();
    cargarDatosReservasFiltradas(textoBusqueda);
    }//GEN-LAST:event_btnFiltrarActionPerformed

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

    private void btneliminarreservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btneliminarreservaActionPerformed
        int filaSeleccionada = tblReservas.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una reserva para eliminar");
        return;
    }

    int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar la reserva seleccionada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

    if (confirmacion == JOptionPane.YES_OPTION) {
        // Obtener el ID de la reserva (columna 0)
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);

        // Sentencia SQL para eliminar
        String sql = "DELETE FROM reservas WHERE id = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idReserva);

            int resultado = ps.executeUpdate();

            if (resultado > 0) {
                JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente");
                cargarDatosReservas(); // Método que recarga la tabla
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar la reserva");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar la reserva: " + e.getMessage());
        }
    }

    }//GEN-LAST:event_btneliminarreservaActionPerformed

    private void btnrepindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepindActionPerformed

  try (Connection con = conex.Conexion.getConexion()) {

        Statement st = con.createStatement();

        // --- 1. Nivel de mesas ocupadas ---
        String sqlMesas = "SELECT COUNT(*) AS ocupadas FROM reservas";
        ResultSet rs = st.executeQuery(sqlMesas);
        int mesasOcupadas = 0;
        if (rs.next()) {
            mesasOcupadas = rs.getInt("ocupadas");
        }

        String sqlTotalMesas = "SELECT COUNT(*) AS total FROM salas";
        rs = st.executeQuery(sqlTotalMesas);
        int totalMesas = 1; // evitar división por cero
        if (rs.next()) {
            totalMesas = rs.getInt("total");
        }
        double nivelMesas = ((double) mesasOcupadas / totalMesas) * 100;

        // --- 2. Índice de satisfacción ---
        String sqlSatis = "SELECT estado FROM opiniones";
        rs = st.executeQuery(sqlSatis);
        int totalOpiniones = 0;
        int satisfechos = 0;
        int minSatis = 1; 
        int maxSatis = 0;
        double sumaSatis = 0;

        while (rs.next()) {
            int estado = rs.getInt("estado"); // 1 = satisfecho, 0 = no satisfecho
            totalOpiniones++;
            sumaSatis += estado;
            if (estado < minSatis) minSatis = estado;
            if (estado > maxSatis) maxSatis = estado;
            if (estado == 1) satisfechos++;
        }

        double indiceSatisfaccion = (totalOpiniones > 0) ? ((double) satisfechos / totalOpiniones) * 100 : 0;
        double promSatis = (totalOpiniones > 0) ? (sumaSatis / totalOpiniones) * 100 : 0;

        // --- 3. Rotación de clientes ---
        String sqlRot = "SELECT COUNT(*) AS reservas FROM reservas";
        rs = st.executeQuery(sqlRot);
        int rotacion = 0;
        if (rs.next()) {
            rotacion = rs.getInt("reservas");
        }
        int minRot = rotacion; 
        int maxRot = rotacion;
        double promRot = rotacion;

        // --- 4. Productividad ---
        String sqlAdmins = "SELECT COUNT(*) AS admins FROM usuarios WHERE rol = 'admin'";
        rs = st.executeQuery(sqlAdmins);
        int numAdmins = 1; // evitar división por cero
        if (rs.next()) {
            numAdmins = rs.getInt("admins");
            if (numAdmins == 0) numAdmins = 1; 
        }
        double productividad = (double) rotacion / numAdmins;
        double minProd = productividad; 
        double maxProd = productividad;
        double promProd = productividad;

        // --- Construir mensaje separado ---
        String mensaje = String.format(
            "=== Indicadores del día ===\n\n" +
            "Nivel de mesas ocupadas: %.2f%%\n" +
            "Índice de satisfacción: %.2f%%\n" +
            "Rotación de clientes: %d\n" +
            "Productividad: %.2f reservas por admin\n\n" +
            "=== Estadísticas (Min, Max, Prom) ===\n\n" +
            "Nivel de mesas ocupadas: %.2f%%, %.2f%%, %.2f%%\n" +
            "Índice de satisfacción: %d%%, %d%%, %.2f%%\n" +
            "Rotación de clientes: %d, %d, %.2f\n" +
            "Productividad: %.2f, %.2f, %.2f",
            nivelMesas,
            indiceSatisfaccion,
            rotacion,
            productividad,
            // estadísticas
            nivelMesas, nivelMesas, nivelMesas,
            minSatis * 100, maxSatis * 100, promSatis,
            minRot, maxRot, promRot,
            minProd, maxProd, promProd
        );

        JOptionPane.showMessageDialog(this, mensaje);

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error al generar indicadores: " + e.getMessage());
    }
  
    }//GEN-LAST:event_btnrepindActionPerformed

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
 // TODO add your handling code here:
    }//GEN-LAST:event_impresiondereporteActionPerformed

    private void btnrepeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnrepeliActionPerformed
      int filaSeleccionada = tblReservas.getSelectedRow();
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(this, "Seleccione una reserva para eliminar");
        return;
    }

    int confirmar = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
    if (confirmar != JOptionPane.YES_OPTION) {
        return;
    }

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "")) {

        // --- 1. Leer datos de la fila ---
        int idReserva = (int) tblReservas.getValueAt(filaSeleccionada, 0);
        String codigoReserva = (String) tblReservas.getValueAt(filaSeleccionada, 1);
        String cliente = (String) tblReservas.getValueAt(filaSeleccionada, 2);
        String sala = (String) tblReservas.getValueAt(filaSeleccionada, 3);
        int mesa = (int) tblReservas.getValueAt(filaSeleccionada, 4);
        Date fecha = (Date) tblReservas.getValueAt(filaSeleccionada, 5);
        Time hora = (Time) tblReservas.getValueAt(filaSeleccionada, 6);
        int capacidad = (int) tblReservas.getValueAt(filaSeleccionada, 7);
        String metodoPago = (String) tblReservas.getValueAt(filaSeleccionada, 8);
        String estadoPago = (String) tblReservas.getValueAt(filaSeleccionada, 9);
        int estacionamiento = (int) tblReservas.getValueAt(filaSeleccionada, 10);
        BigDecimal precio = (BigDecimal) tblReservas.getValueAt(filaSeleccionada, 11);
        Timestamp fechaEliminacion = new Timestamp(System.currentTimeMillis());

        // --- 2. Insertar en reservas_eliminadas ---
        String sqlInsert = "INSERT INTO reservas_eliminadas "
                + "(id, codigo_reserva, cliente, sala, mesa, fecha, hora, capacidad, metodo_pago, estado_pago, estacionamiento, precio, fecha_eliminacion) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
            psInsert.setInt(1, idReserva);
            psInsert.setString(2, codigoReserva);
            psInsert.setString(3, cliente);
            psInsert.setString(4, sala);
            psInsert.setInt(5, mesa);
            psInsert.setDate(6, fecha);
            psInsert.setTime(7, hora);
            psInsert.setInt(8, capacidad);
            psInsert.setString(9, metodoPago);
            psInsert.setString(10, estadoPago);
            psInsert.setInt(11, estacionamiento);
            psInsert.setBigDecimal(12, precio);
            psInsert.setTimestamp(13, fechaEliminacion);

            psInsert.executeUpdate();
        }

        // --- 3. Borrar de la tabla principal ---
        String sqlDelete = "DELETE FROM reservas WHERE id = ?";
        try (PreparedStatement psDelete = conn.prepareStatement(sqlDelete)) {
            psDelete.setInt(1, idReserva);
            psDelete.executeUpdate();
        }

        JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente");

        // --- 4. Refrescar JTable ---
        cargarReservas(); // asegúrate de tener un método que recargue la tabla

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al eliminar la reserva: " + ex.getMessage());
    } // TODO add your handling code here:
    }//GEN-LAST:event_btnrepeliActionPerformed

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
    private javax.swing.JButton impresiondereporte;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReservas;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
