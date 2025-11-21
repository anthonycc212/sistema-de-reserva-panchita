package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MetodoPagoDialog extends JDialog {
    private final ListaMetodosPago metodosPago;
    private String metodoSeleccionado;
    private double precioFinal;
    private final double precioBase;
    
    // Componentes para mostrar detalles
    private JLabel lblPrecioTotal;
    private JLabel lblDetalleDescuento;

    public MetodoPagoDialog(JFrame parent, String codigoReserva, double precioBase) {
        super(parent, "Selección de Método de Pago", true);
        this.metodosPago = new ListaMetodosPago();
        this.precioBase = precioBase;
        this.precioFinal = precioBase;
        
        configurarInterfaz();
        cargarMetodosPago();
    }
    
    private void configurarInterfaz() {
        setLayout(new BorderLayout(10, 10));
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        
        // Panel superior con información de precio
        JPanel panelPrecio = new JPanel(new GridLayout(3, 1, 5, 5));
        panelPrecio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel lblPrecioBase = new JLabel("Precio base: S/ " + String.format("%.2f", precioBase));
        lblPrecioTotal = new JLabel("Total a pagar: S/ " + String.format("%.2f", precioFinal));
        lblDetalleDescuento = new JLabel(" ");
        
        lblPrecioTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblDetalleDescuento.setForeground(new Color(0, 100, 0));
        
        panelPrecio.add(lblPrecioBase);
        panelPrecio.add(lblPrecioTotal);
        panelPrecio.add(lblDetalleDescuento);
        
        // Panel central con métodos de pago
        JPanel panelMetodos = new JPanel();
        panelMetodos.setLayout(new BoxLayout(panelMetodos, BoxLayout.Y_AXIS));
        panelMetodos.setBorder(BorderFactory.createTitledBorder("Seleccione método de pago"));
        
        JScrollPane scrollMetodos = new JScrollPane(panelMetodos);
        scrollMetodos.setPreferredSize(new Dimension(400, 200));
        
        // Panel inferior con botones
        JButton btnConfirmar = new JButton("Confirmar Pago");
        btnConfirmar.addActionListener(this::confirmarPago);
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cancelarPago());
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnConfirmar);
        
        // Ensamblar componentes
        add(panelPrecio, BorderLayout.NORTH);
        add(scrollMetodos, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarMetodosPago() {
        ButtonGroup grupoMetodos = new ButtonGroup();
        JPanel panelMetodos = (JPanel) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView();
        
        for (String metodo : metodosPago.obtenerNombresMetodos()) {
            JRadioButton rb = new JRadioButton(obtenerTextoBoton(metodo));
            rb.setActionCommand(metodo);
            
            rb.addActionListener(e -> {
                metodoSeleccionado = metodo; // Aquí asignamos el método seleccionado
                actualizarPrecio(metodo);
            });
            
            grupoMetodos.add(rb);
            panelMetodos.add(rb);
            panelMetodos.add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }
    
    private String obtenerTextoBoton(String metodo) {
        ListaMetodosPago.NodoMetodoPago nodo = metodosPago.buscarMetodo(metodo);
        if (nodo != null) {
            if (nodo.factorDescuento < 1.0) {
                return String.format("%s (%.0f%% descuento)", metodo, (1 - nodo.factorDescuento) * 100);
            } else if (nodo.factorDescuento > 1.0) {
                return String.format("%s (%.0f%% recargo)", metodo, (nodo.factorDescuento - 1) * 100);
            }
        }
        return metodo;
    }
    
    private void actualizarPrecio(String metodo) {
        ListaMetodosPago.NodoMetodoPago nodo = metodosPago.buscarMetodo(metodo);
        if (nodo != null) {
            precioFinal = precioBase * nodo.factorDescuento;
            lblPrecioTotal.setText("Total a pagar: S/ " + String.format("%.2f", precioFinal));
            
            if (nodo.factorDescuento < 1.0) {
                double descuento = precioBase - precioFinal;
                lblDetalleDescuento.setText(String.format("Descuento aplicado: -S/ %.2f (%.0f%%)", 
                    descuento, (1 - nodo.factorDescuento) * 100));
            } else if (nodo.factorDescuento > 1.0) {
                double recargo = precioFinal - precioBase;
                lblDetalleDescuento.setText(String.format("Recargo aplicado: +S/ %.2f (%.0f%%)", 
                    recargo, (nodo.factorDescuento - 1) * 100));
            } else {
                lblDetalleDescuento.setText(" ");
            }
        }
    }
    
    private void confirmarPago(ActionEvent e) {
        if (metodoSeleccionado == null) {
            JOptionPane.showMessageDialog(this, 
                "Por favor seleccione un método de pago", 
                "Método no seleccionado", 
                JOptionPane.WARNING_MESSAGE);
        } else {
            dispose();
        }
    }
    
    private void cancelarPago() {
        metodoSeleccionado = null;
        dispose();
    }
    
    public String getMetodoSeleccionado() {
        return metodoSeleccionado;
    }
    
    public double getPrecioFinal() {
        return precioFinal;
    }
    
    public double getDescuentoAplicado() {
        return precioBase - precioFinal;
    }
}