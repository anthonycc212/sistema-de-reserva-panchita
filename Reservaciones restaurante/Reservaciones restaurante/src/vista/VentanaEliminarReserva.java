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

public class VentanaEliminarReserva extends JDialog {
    private JTextField txtId;
    private JButton btnEliminar, btnCancelar;
    private JTable tablaReservas;
    private DefaultTableModel modelo;

    public VentanaEliminarReserva(JFrame parent) {
        super(parent, "Eliminar Reserva", true);
        setSize(600, 400);
        setLayout(null);
        setLocationRelativeTo(parent);

        JLabel lblId = new JLabel("ID a eliminar:");
        lblId.setBounds(30, 30, 100, 25);
        add(lblId);

        txtId = new JTextField();
        txtId.setBounds(130, 30, 100, 25);
        add(txtId);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(250, 30, 100, 30);
        add(btnEliminar);

        btnCancelar = new JButton("Cerrar");
        btnCancelar.setBounds(370, 30, 100, 30);
        add(btnCancelar);

        // ===== Tabla de reservas =====
        modelo = new DefaultTableModel();
        modelo.setColumnIdentifiers(new String[]{"ID", "Mesa", "Cliente", "Fecha", "Hora", "Precio"});

        tablaReservas = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaReservas);
        scroll.setBounds(30, 80, 520, 250);
        add(scroll);

        // Eventos
        btnEliminar.addActionListener(e -> eliminarReserva());
        btnCancelar.addActionListener(e -> dispose());

        tablaReservas.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fila = tablaReservas.getSelectedRow();
                if (fila >= 0) {
                    txtId.setText(tablaReservas.getValueAt(fila, 0).toString());
                }
            }
        });

        cargarReservas();
        setVisible(true);
    }

    private void cargarReservas() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "")) {
            modelo.setRowCount(0);
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

    private void eliminarReserva() {
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas eliminar esta reserva?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "")) {
            String sql = "DELETE FROM reservas WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, Integer.parseInt(txtId.getText()));
            int filas = ps.executeUpdate();

            if (filas > 0) {
                JOptionPane.showMessageDialog(this, "🗑️ Reserva eliminada correctamente.");
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(this, "⚠️ No se encontró la reserva.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, " Error: " + e.getMessage());
        }
    }
}