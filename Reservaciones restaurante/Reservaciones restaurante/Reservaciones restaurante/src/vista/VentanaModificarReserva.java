/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

/**
 *
 * @author antho
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.event.*;

public class VentanaModificarReserva extends JDialog {
    private JTextField txtId, txtMesa, txtCliente, txtFecha, txtHora, txtPrecio;
    private JButton btnGuardar, btnCancelar;
    private JTable tablaReservas;
    private DefaultTableModel modelo;

    public VentanaModificarReserva(JFrame parent) {
        super(parent, "Modificar Reserva", true);
        setSize(700, 500);
        setLayout(null);
        setLocationRelativeTo(parent);

        // Campos 
        JLabel lblId = new JLabel("ID:");
        lblId.setBounds(30, 30, 80, 25);
        add(lblId);
        txtId = new JTextField();
        txtId.setBounds(100, 30, 100, 25);
        add(txtId);

        JLabel lblMesa = new JLabel("Mesa:");
        lblMesa.setBounds(30, 70, 80, 25);
        add(lblMesa);
        txtMesa = new JTextField();
        txtMesa.setBounds(100, 70, 150, 25);
        add(txtMesa);

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setBounds(30, 110, 80, 25);
        add(lblCliente);
        txtCliente = new JTextField();
        txtCliente.setBounds(100, 110, 150, 25);
        add(txtCliente);

        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setBounds(30, 150, 80, 25);
        add(lblFecha);
        txtFecha = new JTextField();
        txtFecha.setBounds(100, 150, 150, 25);
        add(txtFecha);

        JLabel lblHora = new JLabel("Hora:");
        lblHora.setBounds(30, 190, 80, 25);
        add(lblHora);
        txtHora = new JTextField();
        txtHora.setBounds(100, 190, 150, 25);
        add(txtHora);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(30, 230, 80, 25);
        add(lblPrecio);
        txtPrecio = new JTextField();
        txtPrecio.setBounds(100, 230, 150, 25);
        add(txtPrecio);

        btnGuardar = new JButton("Guardar cambios");
        btnGuardar.setBounds(40, 280, 150, 35);
        add(btnGuardar);

        btnCancelar = new JButton("Cerrar");
        btnCancelar.setBounds(200, 280, 100, 35);
        add(btnCancelar);

        //Tabla 
        modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{"ID", "Mesa", "Cliente", "Fecha", "Hora", "Precio"});

        tablaReservas = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaReservas);
        scroll.setBounds(320, 30, 340, 350);
        add(scroll);

        // botones
        btnGuardar.addActionListener(e -> modificarReserva());
        btnCancelar.addActionListener(e -> dispose());

        // Al hacer clic en una fila de la tabla, llenar los campos
        tablaReservas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaReservas.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(tablaReservas.getValueAt(fila, 0).toString());
                    txtMesa.setText(tablaReservas.getValueAt(fila, 1).toString());
                    txtCliente.setText(tablaReservas.getValueAt(fila, 2).toString());
                    txtFecha.setText(tablaReservas.getValueAt(fila, 3).toString());
                    txtHora.setText(tablaReservas.getValueAt(fila, 4).toString());
                    txtPrecio.setText(tablaReservas.getValueAt(fila, 5).toString());
                }
            }
        });

        cargarReservas();
        setVisible(true);
    }

    // ? Cargar reservas en la tabla
    private void cargarReservas() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "")) {
            modelo.setRowCount(0); // limpiar tabla
            String sql = "SELECT id, mesa, cliente, fecha, hora, Precio FROM reservas";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                modelo.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("mesa"),
                        rs.getString("cliente"),
                        rs.getString("fecha"),
                        rs.getString("hora"),
                        rs.getBigDecimal("Precio")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar reservas: " + e.getMessage());
        }
    }

    // 🔹 Modificar reserva seleccionada
    private void modificarReserva() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "")) {
            String sql = "UPDATE reservas SET mesa=?, cliente=?, fecha=?, hora=?, Precio=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, txtMesa.getText());
            ps.setString(2, txtCliente.getText());
            ps.setString(3, txtFecha.getText());
            ps.setString(4, txtHora.getText());
            ps.setBigDecimal(5, new java.math.BigDecimal(txtPrecio.getText()));
            ps.setInt(6, Integer.parseInt(txtId.getText()));

            int filas = ps.executeUpdate();
            if (filas > 0) {
                JOptionPane.showMessageDialog(this, " Reserva modificada correctamente.");
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(this, "️ No se encontró la reserva.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Error: " + e.getMessage());
        }
    }
}