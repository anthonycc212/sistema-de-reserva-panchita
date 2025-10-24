package vista;

import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class HistorialReservasDialog extends javax.swing.JDialog {

    private DefaultTableModel modelo;
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

        btnFiltrar.setBackground(new java.awt.Color(255, 255, 255));
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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 849, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtBuscar)))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnexportar)
                    .addComponent(btneliminarreserva, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btncerrar))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32))
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblReservas;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
