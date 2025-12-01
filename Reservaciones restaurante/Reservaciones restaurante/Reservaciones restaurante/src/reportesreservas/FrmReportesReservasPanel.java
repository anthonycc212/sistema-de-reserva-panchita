package reportesreservas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.Dimension;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import java.io.FileOutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;
public class FrmReportesReservasPanel extends JFrame {
    private JTextField txtId, txtCliente, txtFecha, txtHora, txtMesa, txtCapacidad, txtSala, txtCodigo, txtMetodoPago, txtEstadoPago, txtEstacionamiento;
    private JTextField txtFechaInicio, txtFechaFin;
    private JTable tablaReservas;
    private DefaultTableModel modeloTabla;
    private Connection conexion;
    private ArbolReservasPorFecha arbol;

    public FrmReportesReservasPanel() {
        setTitle("Reportes de Reservas por Fecha (BD + Árbol)");
        setSize(1000, 850);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        conectarBD();
        arbol = new ArbolReservasPorFecha();

        JPanel panelIzquierdo = new JPanel(null);
        panelIzquierdo.setPreferredSize(new Dimension(340, 1300));
        JScrollPane scrollPanelIzquierdo = new JScrollPane(panelIzquierdo);
        scrollPanelIzquierdo.setBounds(0, 0, 340, 800);
        add(scrollPanelIzquierdo);
        txtId = crearCampo(panelIzquierdo, "ID:", 20, 0, false);
        txtCliente = crearCampo(panelIzquierdo, "Cliente:", 20, 55, true);
        txtFecha = crearCampo(panelIzquierdo, "Fecha (YYYY-MM-DD):", 20, 110, true);
        txtHora = crearCampo(panelIzquierdo, "Hora:", 20, 165, true);
        txtMesa = crearCampo(panelIzquierdo, "Mesa:", 20, 220, true);
        txtCapacidad = crearCampo(panelIzquierdo, "Capacidad:", 20, 275, true);
        txtSala = crearCampo(panelIzquierdo, "Sala:", 20, 330, true);
        txtCodigo = crearCampo(panelIzquierdo, "Código Reserva:", 20, 385, true);
        txtMetodoPago = crearCampo(panelIzquierdo, "Método de Pago:", 20, 440, true);
        txtEstadoPago = crearCampo(panelIzquierdo, "Estado de Pago:", 20, 495, true);
        txtEstacionamiento = crearCampo(panelIzquierdo, "Estacionamiento:", 20, 550, true);
        txtFechaInicio = crearCampo(panelIzquierdo, "Desde (YYYY-MM-DD):", 20, 605, true);
        txtFechaFin = crearCampo(panelIzquierdo, "Hasta (YYYY-MM-DD):", 20, 660, true);

        int btnX = 20, btnY = 710, btnAltura = 28, btnAncho = 300, btnEspaciado = 35;

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(btnX, btnY, btnAncho, btnAltura);
        panelIzquierdo.add(btnAgregar);

        JButton btnConsultar = new JButton("Consultar por Fecha");
        btnConsultar.setBounds(btnX, btnY + btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnConsultar);

        JButton btnBuscarRango = new JButton("Buscar entre Fechas");
        btnBuscarRango.setBounds(btnX, btnY + 2 * btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnBuscarRango);
        JButton btnVerTodos = new JButton("Ver Todos");
        btnVerTodos.setBounds(btnX, btnY + 3 * btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnVerTodos);

        JButton btnOrdenadoFecha = new JButton("Mostrar ordenado por Fecha");
        btnOrdenadoFecha.setBounds(btnX, btnY + 4 * btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnOrdenadoFecha);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(btnX, btnY + 5 * btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnEliminar);

        JButton btnEditar = new JButton("Editar");
        btnEditar.setBounds(btnX, btnY + 6 * btnEspaciado, btnAncho, btnAltura);
        panelIzquierdo.add(btnEditar);

       JButton btnExportar = new JButton("Exportar a PDF");
btnExportar.setBounds(btnX, btnY + 7 * btnEspaciado, btnAncho, btnAltura);
panelIzquierdo.add(btnExportar);
JButton btnReporte = new JButton("Generar Reporte");
btnReporte.setBounds(btnX, btnY + 8 * btnEspaciado, btnAncho, btnAltura);
panelIzquierdo.add(btnReporte);
btnReporte.addActionListener(e -> generarReportePDF());

btnExportar.addActionListener(e -> exportarTablaAPDF());


        modeloTabla = new DefaultTableModel(
            new Object[]{"ID", "Cliente", "Fecha", "Hora", "Mesa", "Capacidad", "Sala", "Código", "Método Pago", "Estado Pago", "Estacionamiento"}, 0);
        tablaReservas = new JTable(modeloTabla);
        JScrollPane scrollPane = new JScrollPane(tablaReservas);
        scrollPane.setBounds(360, 20, 600, 770);
        add(scrollPane);

        btnAgregar.addActionListener(e -> {
            agregarReservaBD();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Reserva guardada correctamente.");
        });
        btnConsultar.addActionListener(e -> {
            consultarPorFechaDesdeArbol();
            JOptionPane.showMessageDialog(this, "Consulta realizada correctamente.");
        });

        btnBuscarRango.addActionListener(e -> {
            try {
                java.util.Date desde = java.sql.Date.valueOf(txtFechaInicio.getText());
                java.util.Date hasta = java.sql.Date.valueOf(txtFechaFin.getText());
                buscarReservasEntreFechasDesdeArbol(desde, hasta);
                JOptionPane.showMessageDialog(this, "Búsqueda por rango realizada correctamente.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fechas inválidas: " + ex.getMessage());
            }
        });

        btnVerTodos.addActionListener(e -> {
            mostrarTodasBD();
            JOptionPane.showMessageDialog(this, "Todas las reservas fueron cargadas correctamente.");
        });

        btnOrdenadoFecha.addActionListener(e -> {
            mostrarOrdenadoPorFechaDesdeArbol();
            JOptionPane.showMessageDialog(this, "Reservas mostradas ordenadas por fecha.");
        });

        btnEliminar.addActionListener(e -> {
            eliminarReservaBD();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Reserva eliminada correctamente.");
        });

        btnEditar.addActionListener(e -> {
            editarReservaBD();
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Reserva editada correctamente.");
        });

        btnExportar.addActionListener(e -> exportarTablaACSV());
        

        tablaReservas.getSelectionModel().addListSelectionListener(e -> cargarDatosSeleccionados());
setLocationRelativeTo(null);
        setVisible(true);
    }
   private void generarReportePDF() {
    Document documento = new Document();

    try {
        // Ruta del archivo (puede ser Descargas o el proyecto)
        String ruta = System.getProperty("user.home") + "/Desktop/reporte_reservas.pdf";
        PdfWriter.getInstance(documento, new FileOutputStream(ruta));

        documento.open();

        // ----- ENCABEZADO -----
        documento.add(new Paragraph("       RESTAURANTE LA PANCHITA"));
        documento.add(new Paragraph("     --- REPORTE DE RESERVAS ---"));
        documento.add(new Paragraph("Fecha: " + java.time.LocalDate.now()));
        documento.add(new Paragraph("Hora : " + java.time.LocalTime.now().withNano(0)));
        documento.add(new Paragraph("----------------------------------------\n"));

        int filas = tablaReservas.getRowCount();
        int columnas = tablaReservas.getColumnCount();

        // ----- RESERVAS -----
        for (int f = 0; f < filas; f++) {

            documento.add(new Paragraph("Reserva N° " + (f + 1)));
            documento.add(new Paragraph("----------------------------------------"));

            for (int c = 0; c < columnas; c++) {
                String campo = tablaReservas.getColumnName(c);
                Object valor = tablaReservas.getValueAt(f, c);

                documento.add(new Paragraph(
                        String.format("%-12s: %s", campo, (valor != null ? valor.toString() : ""))
                ));
            }

            documento.add(new Paragraph("----------------------------------------\n"));
        }

        // Pie del ticket
        documento.add(new Paragraph("Gracias por usar el sistema de reservas."));
        documento.add(new Paragraph("        *** FIN DEL REPORTE ***"));

        documento.close();

        // ----- ABRIR PDF -----
        File pdfFile = new File(ruta);
        if (pdfFile.exists()) {
            Desktop.getDesktop().open(pdfFile);
        }

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al generar el PDF: " + ex.getMessage());
    }
}
    private void exportarTablaAPDF() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Guardar PDF");

    if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
        try {
            String ruta = fileChooser.getSelectedFile().getAbsolutePath();
            if (!ruta.toLowerCase().endsWith(".pdf")) {
                ruta += ".pdf";
            }

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(ruta));
            document.open();

            PdfPTable pdfTable = new PdfPTable(tablaReservas.getColumnCount());
            pdfTable.setWidthPercentage(100);

            // Encabezados
            for (int i = 0; i < tablaReservas.getColumnCount(); i++) {
                PdfPCell celda = new PdfPCell(new Phrase(tablaReservas.getColumnName(i)));
                celda.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(celda);
            }

            // Filas
            for (int i = 0; i < tablaReservas.getRowCount(); i++) {
                for (int j = 0; j < tablaReservas.getColumnCount(); j++) {
                    Object valor = tablaReservas.getValueAt(i, j);
                    pdfTable.addCell(valor != null ? valor.toString() : "");
                }
            }

            document.add(pdfTable);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF generado correctamente.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + ex.getMessage());
        }
    }
}
    private JTextField crearCampo(JPanel panel, String label, int x, int y, boolean editable) {
        JLabel lbl = new JLabel(label);
        lbl.setBounds(x, y, 150, 25);
        panel.add(lbl);

        JTextField txt = new JTextField();
        txt.setBounds(x, y + 25, 300, 25);
        txt.setEditable(editable);
        panel.add(txt);

        return txt;
    }

    private void limpiarCampos() {
        txtId.setText(""); txtCliente.setText(""); txtFecha.setText(""); txtHora.setText("");
        txtMesa.setText(""); txtCapacidad.setText(""); txtSala.setText(""); txtCodigo.setText("");
        txtMetodoPago.setText(""); txtEstadoPago.setText(""); txtEstacionamiento.setText("");
        txtFechaInicio.setText(""); txtFechaFin.setText("");
    }

    private void conectarBD() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost/baserestaurante", "root", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión: " + e.getMessage());
        }
    }

    private void agregarReservaBD() {
        String sql = "INSERT INTO reservas (cliente, fecha, hora, mesa, capacidad, sala, codigo_reserva, metodo_pago, estado_pago, estacionamiento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, txtCliente.getText());
            ps.setDate(2, java.sql.Date.valueOf(txtFecha.getText()));
            ps.setString(3, txtHora.getText());
            ps.setString(4, txtMesa.getText());
            ps.setString(5, txtCapacidad.getText());
            ps.setString(6, txtSala.getText());
            ps.setString(7, txtCodigo.getText());
            ps.setString(8, txtMetodoPago.getText());
            ps.setString(9, txtEstadoPago.getText());
            ps.setString(10, txtEstacionamiento.getText());
            ps.executeUpdate();
            mostrarTodasBD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al agregar: " + e.getMessage());
        }
    }

    private void editarReservaBD() {
        int id = Integer.parseInt(txtId.getText());
        String sql = "UPDATE reservas SET cliente=?, fecha=?, hora=?, mesa=?, capacidad=?, sala=?, codigo_reserva=?, metodo_pago=?, estado_pago=?, estacionamiento=? WHERE id=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, txtCliente.getText());
            ps.setDate(2, java.sql.Date.valueOf(txtFecha.getText()));
            ps.setString(3, txtHora.getText());
            ps.setString(4, txtMesa.getText());
            ps.setString(5, txtCapacidad.getText());
            ps.setString(6, txtSala.getText());
            ps.setString(7, txtCodigo.getText());
            ps.setString(8, txtMetodoPago.getText());
            ps.setString(9, txtEstadoPago.getText());
            ps.setString(10, txtEstacionamiento.getText());
            ps.setInt(11, id);
            ps.executeUpdate();
            mostrarTodasBD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al editar: " + e.getMessage());
        }
    }

    private void eliminarReservaBD() {
        int id = Integer.parseInt(txtId.getText());
        String sql = "DELETE FROM reservas WHERE id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            mostrarTodasBD();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar: " + e.getMessage());
        }
    }

    private void mostrarTodasBD() {
        modeloTabla.setRowCount(0);
        arbol = new ArbolReservasPorFecha();
        try (Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM reservas")) {
            while (rs.next()) {
                Reserva r = new Reserva(
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getDate("fecha"),
                    rs.getString("hora"),
                    rs.getString("mesa"),
                    rs.getString("capacidad"),
                    rs.getString("sala"),
                    rs.getString("codigo_reserva"),
                    rs.getString("metodo_pago"),
                    rs.getString("estado_pago"),
                    rs.getString("estacionamiento")
                );
                arbol.insertar(r);
                modeloTabla.addRow(new Object[]{
                    r.getId(), r.getCliente(), r.getFecha(), r.getHora(), r.getMesa(), r.getCapacidad(),
                    r.getSala(), r.getCodigoReserva(), r.getMetodoPago(), r.getEstadoPago(), r.getEstacionamiento()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar: " + e.getMessage());
        }
    }

    private void consultarPorFechaDesdeArbol() {
        modeloTabla.setRowCount(0);
        try {
            java.util.Date fecha = java.sql.Date.valueOf(txtFecha.getText());
            List<Reserva> lista = arbol.buscarPorFecha(fecha);
            for (Reserva r : lista) {
                modeloTabla.addRow(new Object[]{
                    r.getId(), r.getCliente(), r.getFecha(), r.getHora(), r.getMesa(),
                    r.getCapacidad(), r.getSala(), r.getCodigoReserva(),
                    r.getMetodoPago(), r.getEstadoPago(), r.getEstacionamiento()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar: " + e.getMessage());
        }
    }

    private void buscarReservasEntreFechasDesdeArbol(java.util.Date inicio, java.util.Date fin) {
        modeloTabla.setRowCount(0);
        List<Reserva> lista = new ArrayList<>();
        buscarEntreFechasRec(arbol.getRaiz(), inicio, fin, lista);
        for (Reserva r : lista) {
            modeloTabla.addRow(new Object[]{
                r.getId(), r.getCliente(), r.getFecha(), r.getHora(), r.getMesa(),
                r.getCapacidad(), r.getSala(), r.getCodigoReserva(),
                r.getMetodoPago(), r.getEstadoPago(), r.getEstacionamiento()
            });
        }
    }

    private void buscarEntreFechasRec(NodoArbolReserva nodo, java.util.Date inicio, java.util.Date fin, List<Reserva> lista) {
        if (nodo == null) return;
        java.util.Date actual = nodo.reserva.getFecha();
        if (actual.after(inicio)) buscarEntreFechasRec(nodo.izquierda, inicio, fin, lista);
        if (!actual.before(inicio) && !actual.after(fin)) lista.add(nodo.reserva);
        if (actual.before(fin)) buscarEntreFechasRec(nodo.derecha, inicio, fin, lista);
    }

    private void mostrarOrdenadoPorFechaDesdeArbol() {
        modeloTabla.setRowCount(0);
        List<Reserva> ordenadas = new ArrayList<>();
        arbol.inOrden(ordenadas);
        for (Reserva r : ordenadas) {
            modeloTabla.addRow(new Object[]{
                r.getId(), r.getCliente(), r.getFecha(), r.getHora(), r.getMesa(),
                r.getCapacidad(), r.getSala(), r.getCodigoReserva(),
                r.getMetodoPago(), r.getEstadoPago(), r.getEstacionamiento()
            });
        }
    }

    private void exportarTablaACSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                for (int i = 0; i < tablaReservas.getColumnCount(); i++) {
                    writer.write(tablaReservas.getColumnName(i));
                    if (i < tablaReservas.getColumnCount() - 1) writer.write(",");
                }
                writer.write("\n");
                for (int i = 0; i < tablaReservas.getRowCount(); i++) {
                    for (int j = 0; j < tablaReservas.getColumnCount(); j++) {
                        writer.write(tablaReservas.getValueAt(i, j).toString());
                        if (j < tablaReservas.getColumnCount() - 1) writer.write(",");
                    }
                    writer.write("\n");
                }
                JOptionPane.showMessageDialog(this, "Exportación completada.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
            }
        }
    }

    private void cargarDatosSeleccionados() {
        int fila = tablaReservas.getSelectedRow();
        if (fila != -1) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtCliente.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtFecha.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtHora.setText(modeloTabla.getValueAt(fila, 3).toString());
            txtMesa.setText(modeloTabla.getValueAt(fila, 4).toString());
            txtCapacidad.setText(modeloTabla.getValueAt(fila, 5).toString());
            txtSala.setText(modeloTabla.getValueAt(fila, 6).toString());
            txtCodigo.setText(modeloTabla.getValueAt(fila, 7).toString());
            txtMetodoPago.setText(modeloTabla.getValueAt(fila, 8).toString());
            txtEstadoPago.setText(modeloTabla.getValueAt(fila, 9).toString());
            txtEstacionamiento.setText(modeloTabla.getValueAt(fila, 10).toString());
        }
    }
}