/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Delivery;

import conex.login;
import java.awt.CardLayout;
import java.awt.Image;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.ArrayList;
import Delivery.Producto;
import Delivery.Pedido;
import Delivery.DetallePedido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

/**
 *
 * @author otito
 */
public class frmDelivery extends javax.swing.JFrame {

   
    private ProductoDAO productoDAO;
    private PedidoDAO pedidoDAO;
    
    private DefaultTableModel modeloProductosDisponibles;
    private DefaultTableModel modeloProductosSeleccionados;
    private DefaultTableModel modeloResumenPedido;
    private DefaultTableModel modeloConfirmacionPedido;
    
    private List<SeleccionItems> itemsSeleccionados;

    private String direccionEnvio;
    private String referenciaEnvio;
    private String distritoEnvio;
    private String ciudadEnvio;
    private String telefonoEntrega;
    private BigDecimal costoDeEnvio = BigDecimal.ZERO;
    private String numeroPedidoActual;
    private javax.swing.JFrame parentFrame;

   
    public frmDelivery(javax.swing.JFrame parent, login usuario) {
        initComponents(); 
        this.parentFrame = parent;

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {

                if (parentFrame != null) {
                    parentFrame.setVisible(true);
                }
            }
        });

        productoDAO = new ProductoDAO();
        pedidoDAO = new PedidoDAO();
        
        //////////LISTA DINAMICA PARA ALMACENAR PRODUCTOS /////////
        ///////////////////////////////////////////////////////////
        itemsSeleccionados = new ArrayList<>();

        modeloProductosDisponibles = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Descripción", "Precio"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductosDisponibles.setModel(modeloProductosDisponibles);

        modeloProductosSeleccionados = new DefaultTableModel(
            new Object[]{"Nombre", "Cantidad", "Precio Unit.", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductosSeleccionados.setModel(modeloProductosSeleccionados);

        modeloResumenPedido = new DefaultTableModel(
            new Object[]{"Producto", "Cant.", "P.Unit.", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblResumenPedido.setModel(modeloResumenPedido);

        modeloConfirmacionPedido = new DefaultTableModel(
            new Object[]{"Producto", "Cant.", "P.Unit.", "Subtotal"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tblConfirmacionPedido.setModel(modeloConfirmacionPedido);

        cargarProductosDisponibles();
        actualizarTablaSeleccionados();
        generarYMostrarNumeroPedidoPreview();
        configurarAccionesBotonesConfirmacion();
        cargarYRedimensionarImagenMetodoPago();
        
        precargarDatosCliente(usuario); 
    }

    private void precargarDatosCliente(login usuario) {
        if (usuario != null) {
            jtxtNombreCliente.setText(usuario.getNombreCompleto());
            jtxtCorreo.setText(usuario.getCorreo());
            jtxtTelefono.setText(usuario.getTelefono());
        }
    }

    private void generarYMostrarNumeroPedidoPreview() {
        if (pedidoDAO != null) {
          
            this.numeroPedidoActual = pedidoDAO.previsualizarSiguienteNumeroPedido(); 
            if (this.numeroPedidoActual == null) {
                this.numeroPedidoActual = "(Error N° Prev.)";
                System.err.println("Error: No se pudo previsualizar el número de pedido desde la BD.");
                 JOptionPane.showMessageDialog(this, 
                    "Error al intentar previsualizar un número de pedido.", 
                    "Error Numeración", 
                    JOptionPane.WARNING_MESSAGE);
            }
        } else {
             this.numeroPedidoActual = "(DAO no disp.)"; 
             System.err.println("Error crítico: pedidoDAO no está inicializado al generar número de pedido para previsualizar.");
        }
        System.out.println("Número de Pedido Previsualizado: " + this.numeroPedidoActual); 
        ActualizarCampos_NumeroPedido(this.numeroPedidoActual);
    }

    private void ActualizarCampos_NumeroPedido(String textoNumeroPedido) {
        String textoCompleto = "Pedido N° " + textoNumeroPedido;
        if (txtPedidoNroDP != null) { 
            txtPedidoNroDP.setText(textoCompleto);
        }
        if (jTextField10 != null) { 
            jTextField10.setText(textoCompleto);
        }
    }

    private void cargarYRedimensionarImagenMetodoPago() {
        try {
            String imagePath = "/imagenes/yapeplin.png"; 
            java.net.URL imgUrl = getClass().getResource(imagePath);

            if (imgUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imgUrl);
                Image originalImage = originalIcon.getImage();
                
                int labelWidth = lblLogoPago.getWidth(); 
                int labelHeight = lblLogoPago.getHeight();

                if (labelWidth <= 0) labelWidth = 200; 
                if (labelHeight <= 0) labelHeight = 90; 

                Image scaledImage = originalImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                lblLogoPago.setIcon(scaledIcon); 
                lblLogoPago.setText(""); 
            } else {
                System.err.println("No se pudo encontrar la imagen en la ruta: " + imagePath);
                if (lblLogoPago != null) { 
                    lblLogoPago.setText("Img no encontrada");
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar o redimensionar la imagen del método de pago: " + e.getMessage());
            e.printStackTrace();
            if (lblLogoPago != null) { 
                lblLogoPago.setText("Error img");
            }
        }
    }    
    private void configurarAccionesBotonesConfirmacion() {
        btnConfirmar_Pagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmar_PagarActionPerformed(evt);
            }
        });
        btnModificar_Pedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificar_PedidoActionPerformed(evt);
            }
        });
        btnCancelar_Pedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelar_PedidoActionPerformed(evt);
            }
        });
    }

    private void cargarProductosDisponibles() {
        modeloProductosDisponibles.setRowCount(0);
        List<Producto> productos = productoDAO.listarProductos(); 
        if (productos != null) {
            for (Producto prod : productos) {
                modeloProductosDisponibles.addRow(new Object[]{
                    prod.getId(),
                    prod.getNombre(),
                    prod.getDescripcion(),
                    prod.getPrecio()
                });
            }
        } else {
            System.err.println("No se pudieron cargar los productos desde la base de datos.");
        }
    }

    private void actualizarTablaSeleccionados() {
        modeloProductosSeleccionados.setRowCount(0);
        BigDecimal totalGeneral = BigDecimal.ZERO;

        if (itemsSeleccionados != null) {
            for (SeleccionItems item : itemsSeleccionados) {
                modeloProductosSeleccionados.addRow(new Object[]{
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getProducto().getPrecio(),
                    item.getSubtotal()
                });
                totalGeneral = totalGeneral.add(item.getSubtotal());
            }
        }
        if (lblTotalPedido != null) {
            lblTotalPedido.setText("Total: S/ " + totalGeneral.setScale(2, RoundingMode.HALF_UP).toString());
        } else {
            System.err.println("lblTotalPedido no está inicializado.");
        }
    }

    private void agregarProductoSeleccionado() {
        int filaSeleccionada = tblProductosDisponibles.getSelectedRow();
        if (filaSeleccionada != -1) {
            int idProducto = (Integer) modeloProductosDisponibles.getValueAt(filaSeleccionada, 0);
            String nombre = (String) modeloProductosDisponibles.getValueAt(filaSeleccionada, 1);
            BigDecimal precio = (BigDecimal) modeloProductosDisponibles.getValueAt(filaSeleccionada, 3);

            Producto productoTemp = new Producto();
            productoTemp.setId(idProducto);
            productoTemp.setNombre(nombre);
            productoTemp.setPrecio(precio);

            boolean encontrado = false;
            for (SeleccionItems item : itemsSeleccionados) {
                if (item.getProducto().getId() == idProducto) {
                    item.incrementarCantidad(1); 
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                itemsSeleccionados.add(new SeleccionItems(productoTemp, 1));
            }
            actualizarTablaSeleccionados();
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista para agregar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void actualizarCostoEnvio() {
        if (cmbDistritoDE == null || txtCostoEnvioDE == null) {
            return;
        }
        String distritoSeleccionado = "";
        if (cmbDistritoDE.getSelectedItem() != null) {
            distritoSeleccionado = (String) cmbDistritoDE.getSelectedItem();
        }

        if (distritoSeleccionado.equals("Miraflores")) {
            this.costoDeEnvio = new BigDecimal("5.00");
        } else if (distritoSeleccionado.equals("San Isidro")) {
            this.costoDeEnvio = new BigDecimal("6.00");
        } else if (distritoSeleccionado.equals("Surco")) {
            this.costoDeEnvio = new BigDecimal("7.00");
        } else if (distritoSeleccionado.equals("La Victoria")) {
            this.costoDeEnvio = new BigDecimal("3.00");
        } else if (distritoSeleccionado.equals("Lince")) {
            this.costoDeEnvio = new BigDecimal("4.00");
        } else if (distritoSeleccionado.equals("Seleccione distrito...") || distritoSeleccionado.isEmpty()) {
            this.costoDeEnvio = BigDecimal.ZERO;
        } else {
            this.costoDeEnvio = new BigDecimal("10.00"); 
        }
        txtCostoEnvioDE.setText(this.costoDeEnvio.setScale(2, RoundingMode.HALF_UP).toString());
    }

    private void quitarProductoSeleccionado() {
        int filaSeleccionada = tblProductosSeleccionados.getSelectedRow();
        if (filaSeleccionada != -1) {
            if (filaSeleccionada < itemsSeleccionados.size()) {
                SeleccionItems itemARemover = itemsSeleccionados.get(filaSeleccionada);
                boolean fueRemovidoCompletamente = false;

                if (itemARemover.getCantidad() > 1) {
                    itemARemover.incrementarCantidad(-1); 
                } else {
                    itemsSeleccionados.remove(filaSeleccionada); 
                    fueRemovidoCompletamente = true;
                }
                actualizarTablaSeleccionados();
                if (!fueRemovidoCompletamente && filaSeleccionada < tblProductosSeleccionados.getRowCount()) {
                    tblProductosSeleccionados.setRowSelectionInterval(filaSeleccionada, filaSeleccionada);
                }
            } else {
                System.err.println("Error: Fila seleccionada (" + filaSeleccionada + ") fuera de rango en itemsSeleccionados.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la lista de pedido para quitar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cargarVistaDetallePedido() {
        ActualizarCampos_NumeroPedido(this.numeroPedidoActual != null ? this.numeroPedidoActual : "(Error al generar N°)");
        txtNombreClienteDP.setText(jtxtNombreCliente.getText());
        txtCorreoDP.setText(jtxtCorreo.getText());
        txtTelefonoDP.setText(jtxtTelefono.getText()); 

        modeloResumenPedido.setRowCount(0);
        BigDecimal subTotalPedido = BigDecimal.ZERO;

        if (itemsSeleccionados != null) {
            for (SeleccionItems item : itemsSeleccionados) {
                modeloResumenPedido.addRow(new Object[]{
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getProducto().getPrecio(),
                    item.getSubtotal()
                });
                subTotalPedido = subTotalPedido.add(item.getSubtotal());
            }
        }
        jtxtSubTotal.setText(subTotalPedido.setScale(2, RoundingMode.HALF_UP).toString());
    }

    private boolean actualizarYValidarDatosDeEnvio() {
        System.out.println("DEBUG: Entrando a actualizarYValidarDatosDeEnvio()");
        if (txtDireccionDE == null || cmbDistritoDE == null || txtTelefonoEntregaDE == null) {
            System.err.println("Error Interno: Componentes del panel de envío no están listos (son null).");
            JOptionPane.showMessageDialog(this, "Error interno al procesar datos de envío. Contacte al administrador.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtDireccionDE.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese la dirección de envío.", "Dato Faltante", JOptionPane.WARNING_MESSAGE);
            txtDireccionDE.requestFocusInWindow(); 
            return false;
        }
        this.direccionEnvio = txtDireccionDE.getText();

        if (cmbDistritoDE.getSelectedItem() == null || 
            cmbDistritoDE.getSelectedItem().toString().equals("Seleccione distrito...") || 
            cmbDistritoDE.getSelectedItem().toString().isEmpty() ) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un distrito.", "Dato Faltante", JOptionPane.WARNING_MESSAGE);
            cmbDistritoDE.requestFocusInWindow(); 
            return false;
        }
        this.distritoEnvio = (String) cmbDistritoDE.getSelectedItem();

        if (txtCiudadDE != null) { 
           this.ciudadEnvio = txtCiudadDE.getText();
        } else {
            this.ciudadEnvio = ""; 
        }

        if (txtTelefonoEntregaDE.getText().trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "Por favor, ingrese el teléfono de entrega.", "Dato Faltante", JOptionPane.WARNING_MESSAGE);
            txtTelefonoEntregaDE.requestFocusInWindow(); 
            return false;
        }
        this.telefonoEntrega = txtTelefonoEntregaDE.getText();
        
        if (txtReferenciaDE != null) { 
           this.referenciaEnvio = txtReferenciaDE.getText();
        } else {
           this.referenciaEnvio = "";
        }

        System.out.println("DEBUG: Saliendo de actualizarYValidarDatosDeEnvio() - Datos válidos.");
        return true; 
    }


    private void cargarVistaConfirmarPedidoFinal() {
        System.out.println("DEBUG: Entrando a cargarVistaConfirmarPedidoFinal()"); 
        ActualizarCampos_NumeroPedido(this.numeroPedidoActual != null ? this.numeroPedidoActual : "(Error al generar N°)");

        txtNombreCliente_Confirmar.setText(jtxtNombreCliente.getText());
        txtDNI_Confirmar.setText(jtxtDNI.getText());
        txtCorreo_Confirmar.setText(jtxtTelefono.getText() != null && !jtxtTelefono.getText().isEmpty() ? jtxtTelefono.getText() : "N/A"); 
        txtCorreoCliente_Confirmar.setText(jtxtCorreo.getText());
        
        txtDireccionEntrega_Confirmar.setText(this.direccionEnvio != null && !this.direccionEnvio.trim().isEmpty() ? this.direccionEnvio : "N/A");
        txtDistrito_Confirmar.setText(this.distritoEnvio != null && !this.distritoEnvio.trim().isEmpty() && !this.distritoEnvio.equals("Seleccione distrito...") ? this.distritoEnvio : "N/A");

        modeloConfirmacionPedido.setRowCount(0);
        BigDecimal subTotalConfirmacion = BigDecimal.ZERO;

        if (itemsSeleccionados != null) {
            for (SeleccionItems item : itemsSeleccionados) {
                modeloConfirmacionPedido.addRow(new Object[]{
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getProducto().getPrecio(),
                    item.getSubtotal()
                });
                subTotalConfirmacion = subTotalConfirmacion.add(item.getSubtotal());
            }
        }

        txtSubTotal_Confirmar.setText(subTotalConfirmacion.setScale(2, RoundingMode.HALF_UP).toString());
        txtCostoEnvio_Confirmar.setText(this.costoDeEnvio.setScale(2, RoundingMode.HALF_UP).toString());
        BigDecimal totalAPagar = subTotalConfirmacion.add(this.costoDeEnvio);
        txtTotalPagar_Confirmar.setText(totalAPagar.setScale(2, RoundingMode.HALF_UP).toString());
        
        if (cmbMetodoPago_Confirmar != null && cmbMetodoPago_Confirmar.getItemCount() > 0) {
            cmbMetodoPago_Confirmar.setSelectedIndex(0); 
        }
        jCheckBox1.setSelected(false);
        System.out.println("DEBUG: Saliendo de cargarVistaConfirmarPedidoFinal()"); 
    }
    
    private void reiniciarProcesoPedido() {
        if (itemsSeleccionados != null) {
            itemsSeleccionados.clear();
        }
        actualizarTablaSeleccionados(); 
        if (modeloResumenPedido != null) {
            modeloResumenPedido.setRowCount(0); 
        }
        if (modeloConfirmacionPedido != null) {
            modeloConfirmacionPedido.setRowCount(0); 
        }

        jtxtNombreCliente.setText("");
        jtxtDNI.setText("");
        jtxtCorreo.setText("");
        jtxtTelefono.setText(""); 

        txtNombreClienteDP.setText("");
        txtTelefonoDP.setText("");
        txtCorreoDP.setText("");
        jtxtSubTotal.setText("0.00");

        txtDireccionDE.setText("");
        txtCiudadDE.setText("");
        if (cmbDistritoDE != null && cmbDistritoDE.getItemCount() > 0) {
            cmbDistritoDE.setSelectedIndex(0); 
        }
        txtCostoEnvioDE.setText("0.00");
        txtTelefonoEntregaDE.setText("");
        this.costoDeEnvio = BigDecimal.ZERO;
        this.direccionEnvio = null; 
        this.distritoEnvio = null;  
        this.ciudadEnvio = null;
        this.telefonoEntrega = null;
        this.referenciaEnvio = null; 

        txtNombreCliente_Confirmar.setText("");
        txtDNI_Confirmar.setText("");
        txtCorreo_Confirmar.setText(""); 
        txtCorreoCliente_Confirmar.setText("");
        txtDireccionEntrega_Confirmar.setText("");
        txtDistrito_Confirmar.setText("");
        txtSubTotal_Confirmar.setText("0.00");
        txtCostoEnvio_Confirmar.setText("0.00");
        txtTotalPagar_Confirmar.setText("0.00");
        
        if (cmbMetodoPago_Confirmar != null && cmbMetodoPago_Confirmar.getItemCount() > 0) {
            cmbMetodoPago_Confirmar.setSelectedIndex(0); 
        }
        jTextAreaPago.setText(""); 
        jCheckBox1.setSelected(false);

        generarYMostrarNumeroPedidoPreview();

        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Datos_Cliente");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_Carta = new javax.swing.JButton();
        btnConfirmar_Pedido = new javax.swing.JButton();
        btnDetalle_Envio = new javax.swing.JButton();
        btnDatos_Cliente = new javax.swing.JButton();
        btnDetalle_Pedido = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanelCliente = new javax.swing.JPanel();
        jlabelCliente = new javax.swing.JLabel();
        jtxtNombreCliente = new javax.swing.JTextField();
        jlabelCorreo = new javax.swing.JLabel();
        jtxtCorreo = new javax.swing.JTextField();
        jlabelDNI = new javax.swing.JLabel();
        jtxtDNI = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jtxtTelefono = new javax.swing.JTextField();
        lblTitulo_DatosDelCliente = new javax.swing.JLabel();
        jPanelCarta = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPaneSeleccionados = new javax.swing.JScrollPane();
        tblProductosSeleccionados = new javax.swing.JTable();
        jScrollPaneDisponibles = new javax.swing.JScrollPane();
        tblProductosDisponibles = new javax.swing.JTable();
        panelAccionesDisponibles = new javax.swing.JPanel();
        lblTitulo_Carta = new javax.swing.JLabel();
        panelAccionesSeleccionados = new javax.swing.JPanel();
        panelControlGeneral = new javax.swing.JPanel();
        btnAgregarProducto = new javax.swing.JButton();
        btnQuitarProducto = new javax.swing.JButton();
        lblTotalPedido = new javax.swing.JLabel();
        jPanelDetalle_Pedido = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblResumenPedido = new javax.swing.JTable();
        jtxtSubTotal = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPedidoNroDP = new javax.swing.JTextField();
        txtNombreClienteDP = new javax.swing.JTextField();
        txtTelefonoDP = new javax.swing.JTextField();
        txtCorreoDP = new javax.swing.JTextField();
        lblTitulo_DetalleDelPedido = new javax.swing.JLabel();
        jPanelDetalle_envio = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtDireccionDE = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtCiudadDE = new javax.swing.JTextField();
        txtCostoEnvioDE = new javax.swing.JTextField();
        cmbDistritoDE = new javax.swing.JComboBox<>();
        btnContinuarAConfirmacion = new javax.swing.JButton();
        btnAtrasADetallePedido = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        txtTelefonoEntregaDE = new javax.swing.JTextField();
        txtReferenciaDE = new javax.swing.JTextField(); 
        jLabelReferencia = new javax.swing.JLabel(); 
        lblTitulo_DetalleDelEnvio = new javax.swing.JLabel();
        jPanelConfirmarPedidoFinal = new javax.swing.JPanel();
        lblTitulo_ConfirmaTuPedido = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblConfirmacionPedido = new javax.swing.JTable();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        btnConfirmar_Pagar = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaPago = new javax.swing.JTextArea();
        jCheckBox1 = new javax.swing.JCheckBox();
        btnModificar_Pedido = new javax.swing.JButton();
        btnCancelar_Pedido = new javax.swing.JButton();
        txtNombreCliente_Confirmar = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        txtDNI_Confirmar = new javax.swing.JTextField();
        txtCorreo_Confirmar = new javax.swing.JTextField();
        txtCorreoCliente_Confirmar = new javax.swing.JTextField();
        txtDireccionEntrega_Confirmar = new javax.swing.JTextField();
        txtDistrito_Confirmar = new javax.swing.JTextField();
        txtSubTotal_Confirmar = new javax.swing.JTextField();
        txtCostoEnvio_Confirmar = new javax.swing.JTextField();
        txtTotalPagar_Confirmar = new javax.swing.JTextField();
        cmbMetodoPago_Confirmar = new javax.swing.JComboBox<>();
        lblLogoPago = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_Carta.setText("Carta");
        btn_Carta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_CartaActionPerformed(evt);
            }
        });
        getContentPane().add(btn_Carta, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, -1, -1));

        btnConfirmar_Pedido.setText("Confirmar Pedido");
        btnConfirmar_Pedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmar_PedidoActionPerformed(evt);
            }
        });
        getContentPane().add(btnConfirmar_Pedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 480, -1, -1));

        btnDetalle_Envio.setText("Detalle del Envio");
        btnDetalle_Envio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalle_EnvioActionPerformed(evt);
            }
        });
        getContentPane().add(btnDetalle_Envio, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 360, -1, -1));

        btnDatos_Cliente.setText("Datos del Cliente");
        btnDatos_Cliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDatos_ClienteActionPerformed(evt);
            }
        });
        getContentPane().add(btnDatos_Cliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, -1, -1));

        btnDetalle_Pedido.setText("Detalles del Pedido");
        btnDetalle_Pedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDetalle_PedidoActionPerformed(evt);
            }
        });
        getContentPane().add(btnDetalle_Pedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 300, -1, -1));

        jPanel1.setLayout(new java.awt.CardLayout());

        jPanelCliente.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jlabelCliente.setText("Cliente");
        jPanelCliente.add(jlabelCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, 45, 25));
        jPanelCliente.add(jtxtNombreCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 120, 220, 30));

        jlabelCorreo.setText("Correo");
        jPanelCliente.add(jlabelCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 260, -1, -1));
        jPanelCliente.add(jtxtCorreo, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 250, 220, 30));

        jlabelDNI.setText("DNI");
        jPanelCliente.add(jlabelDNI, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 190, -1, -1));
        jPanelCliente.add(jtxtDNI, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 190, 220, 30));

        jLabel24.setText("Telefono");
        jPanelCliente.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 320, -1, -1));
        jPanelCliente.add(jtxtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 320, 220, 30));

        lblTitulo_DatosDelCliente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo_DatosDelCliente.setText("DATOS DEL CLIENTE");
        jPanelCliente.add(lblTitulo_DatosDelCliente, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 30, 170, 40));

        jPanel1.add(jPanelCliente, "Datos_Cliente");

        jPanelCarta.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanelCarta.setLayout(new java.awt.BorderLayout());

        jSplitPane1.setResizeWeight(0.5);

        tblProductosSeleccionados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneSeleccionados.setViewportView(tblProductosSeleccionados);

        jSplitPane1.setRightComponent(jScrollPaneSeleccionados);

        tblProductosDisponibles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPaneDisponibles.setViewportView(tblProductosDisponibles);

        jSplitPane1.setLeftComponent(jScrollPaneDisponibles);

        jPanelCarta.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        panelAccionesDisponibles.setLayout(new java.awt.BorderLayout());

        lblTitulo_Carta.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo_Carta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTitulo_Carta.setText("NUESTRA CARTA");
        lblTitulo_Carta.setBorder(javax.swing.BorderFactory.createEmptyBorder(45, 25, 15, 0));
        panelAccionesDisponibles.add(lblTitulo_Carta, java.awt.BorderLayout.CENTER);

        jPanelCarta.add(panelAccionesDisponibles, java.awt.BorderLayout.PAGE_START);

        panelControlGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnAgregarProducto.setText("Agregar");
        btnAgregarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductoActionPerformed(evt);
            }
        });
        panelControlGeneral.add(btnAgregarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 200, 40));

        btnQuitarProducto.setText("Quitar");
        btnQuitarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarProductoActionPerformed(evt);
            }
        });
        panelControlGeneral.add(btnQuitarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 190, 40));

        lblTotalPedido.setText("Costo Total");
        panelControlGeneral.add(lblTotalPedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 460, 40));

        panelAccionesSeleccionados.add(panelControlGeneral);

        jPanelCarta.add(panelAccionesSeleccionados, java.awt.BorderLayout.PAGE_END);

        jPanel1.add(jPanelCarta, "Carta");

        jPanelDetalle_Pedido.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Pedido Nro:");
        jPanelDetalle_Pedido.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 130, -1, -1));

        tblResumenPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblResumenPedido);

        jPanelDetalle_Pedido.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 300, -1, 180));

        jtxtSubTotal.setEditable(false);
        jPanelDetalle_Pedido.add(jtxtSubTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 530, 90, 30));

        jLabel2.setText("Sub Total");
        jPanelDetalle_Pedido.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 530, 60, 30));

        jLabel3.setText("Nombre del Cliente:");
        jPanelDetalle_Pedido.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 170, -1, -1));

        jLabel4.setText("Telefono de contacto:");
        jPanelDetalle_Pedido.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 210, -1, -1));

        jLabel5.setText("Correo electronico:");
        jPanelDetalle_Pedido.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 250, -1, -1));

        txtPedidoNroDP.setEditable(false);
        jPanelDetalle_Pedido.add(txtPedidoNroDP, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 130, 260, -1));

        txtNombreClienteDP.setEditable(false);
        jPanelDetalle_Pedido.add(txtNombreClienteDP, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, 260, -1));

        txtTelefonoDP.setEditable(false);
        txtTelefonoDP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTelefonoDPActionPerformed(evt);
            }
        });
        jPanelDetalle_Pedido.add(txtTelefonoDP, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 210, 260, -1));

        txtCorreoDP.setEditable(false);
        jPanelDetalle_Pedido.add(txtCorreoDP, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 250, 260, -1));

        lblTitulo_DetalleDelPedido.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo_DetalleDelPedido.setText("DETALLE DEL PEDIDO");
        jPanelDetalle_Pedido.add(lblTitulo_DetalleDelPedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 60, 170, 40));

        jPanel1.add(jPanelDetalle_Pedido, "Detalle_Pedido");

        jPanelDetalle_envio.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel6.setText("Direccion de envio:");
        jPanelDetalle_envio.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 190, 120, 20));

        jLabel7.setText("DIRECCION DE ENTREGA");
        jPanelDetalle_envio.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, -1, -1));
        jPanelDetalle_envio.add(txtDireccionDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 290, -1));

        jLabel8.setText("Ciudad");
        jPanelDetalle_envio.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, 50, -1));

        jLabel9.setText("Distrito");
        jPanelDetalle_envio.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 310, -1, -1));
        
        jLabelReferencia.setText("Referencia:"); 
        jPanelDetalle_envio.add(jLabelReferencia, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, 120, 20)); 
        jPanelDetalle_envio.add(txtReferenciaDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 220, 290, -1)); 


        jLabel10.setText("Costo de envio");
        jPanelDetalle_envio.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 370, -1, -1));

        txtCiudadDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCiudadDEActionPerformed(evt);
            }
        });
        jPanelDetalle_envio.add(txtCiudadDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 250, 290, -1));

        txtCostoEnvioDE.setEditable(false);
        jPanelDetalle_envio.add(txtCostoEnvioDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 370, 290, -1));

        cmbDistritoDE.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione distrito...", "San Isidro", "Miraflores", "Jesus Maria", "Lince", "Pueblo Libre", "La Victoria", "Surco", "Otro" }));
        cmbDistritoDE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDistritoDEActionPerformed(evt);
            }
        });
        jPanelDetalle_envio.add(cmbDistritoDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 310, 280, -1));

        btnContinuarAConfirmacion.setText("Continuar");
        btnContinuarAConfirmacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnContinuarAConfirmacionActionPerformed(evt);
            }
        });
        jPanelDetalle_envio.add(btnContinuarAConfirmacion, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 480, -1, -1));

        btnAtrasADetallePedido.setText("Atras");
        btnAtrasADetallePedido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtrasADetallePedidoActionPerformed(evt);
            }
        });
        jPanelDetalle_envio.add(btnAtrasADetallePedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 480, -1, -1));

        jLabel23.setText("Telefono");
        jPanelDetalle_envio.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 420, -1, -1));
        jPanelDetalle_envio.add(txtTelefonoEntregaDE, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 420, 290, -1));

        lblTitulo_DetalleDelEnvio.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo_DetalleDelEnvio.setText("DETALLE DEL ENVIO");
        jPanelDetalle_envio.add(lblTitulo_DetalleDelEnvio, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, 170, 40));

        jPanel1.add(jPanelDetalle_envio, "Detalle_Envio");

        jPanelConfirmarPedidoFinal.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitulo_ConfirmaTuPedido.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTitulo_ConfirmaTuPedido.setText("CONFIRMA TU PEDIDO");
        jPanelConfirmarPedidoFinal.add(lblTitulo_ConfirmaTuPedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 170, 40));

        jLabel12.setText("Pedido Nro:");
        jPanelConfirmarPedidoFinal.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 80, -1, -1));

        jLabel13.setText("Nombre de Cliente:");
        jPanelConfirmarPedidoFinal.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 110, -1, -1));

        jLabel14.setText("DNI:");
        jPanelConfirmarPedidoFinal.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, -1, -1));

        jLabel15.setText("Telefono del Cliente");
        jPanelConfirmarPedidoFinal.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 170, -1, -1));

        jLabel16.setText("Correo del Cliente");
        jPanelConfirmarPedidoFinal.add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 210, 100, -1));

        jLabel17.setText("Direccion de Entrega");
        jPanelConfirmarPedidoFinal.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 240, -1, -1));

        jLabel18.setText("Distrito");
        jPanelConfirmarPedidoFinal.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 270, -1, -1));

        tblConfirmacionPedido.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tblConfirmacionPedido);

        jPanelConfirmarPedidoFinal.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 310, 730, 160));

        jLabel19.setText("SubTotal:");
        jPanelConfirmarPedidoFinal.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 510, -1, -1));

        jLabel20.setText("Costo de Envio");
        jPanelConfirmarPedidoFinal.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 550, -1, -1));

        jLabel21.setText("Total a pagar");
        jPanelConfirmarPedidoFinal.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 590, -1, -1));

        btnConfirmar_Pagar.setText("Confirmar y Pagar");
        jPanelConfirmarPedidoFinal.add(btnConfirmar_Pagar, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 770, 130, -1));

        jLabel22.setText("Metodo de Pago");
        jPanelConfirmarPedidoFinal.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 630, -1, -1));

        jCheckBox1.setText("\"Acepto los términos y condiciones\"");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        jPanelConfirmarPedidoFinal.add(jCheckBox1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 690, -1, -1));

        btnModificar_Pedido.setText("Modificar Pedido");
        jPanelConfirmarPedidoFinal.add(btnModificar_Pedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 730, -1, -1));

        btnCancelar_Pedido.setText("Cancelar Pedido");
        jPanelConfirmarPedidoFinal.add(btnCancelar_Pedido, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 770, -1, -1));

        txtNombreCliente_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtNombreCliente_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 110, 250, -1));

        jTextField10.setEditable(false);
        jPanelConfirmarPedidoFinal.add(jTextField10, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 250, -1));

        txtDNI_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtDNI_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 140, 250, -1));

        txtCorreo_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtCorreo_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 250, -1));

        txtCorreoCliente_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtCorreoCliente_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 200, 250, -1));

        txtDireccionEntrega_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtDireccionEntrega_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 250, -1));

        txtDistrito_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtDistrito_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 260, 250, -1));

        txtSubTotal_Confirmar.setEditable(false);
        txtSubTotal_Confirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSubTotal_ConfirmarActionPerformed(evt);
            }
        });
        jPanelConfirmarPedidoFinal.add(txtSubTotal_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 510, 150, -1));

        txtCostoEnvio_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtCostoEnvio_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 550, 150, -1));

        txtTotalPagar_Confirmar.setEditable(false);
        jPanelConfirmarPedidoFinal.add(txtTotalPagar_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 590, 150, -1));

        cmbMetodoPago_Confirmar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccione...", "Efectivo", "Plin", "Yape", "POS" }));
        jPanelConfirmarPedidoFinal.add(cmbMetodoPago_Confirmar, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 630, 110, -1));

        lblLogoPago.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lblLogoPago.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblLogoPago.setMaximumSize(new java.awt.Dimension(100, 100));
        lblLogoPago.setMinimumSize(new java.awt.Dimension(100, 100));
        lblLogoPago.setPreferredSize(new java.awt.Dimension(111, 111));
        jPanelConfirmarPedidoFinal.add(lblLogoPago, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 520, 200, 90));

        jTextAreaPago.setColumns(20);
        jTextAreaPago.setRows(5);
        jScrollPane2.setViewportView(jTextAreaPago);

        jPanelConfirmarPedidoFinal.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 60, -1, -1));

        jPanel1.add(jPanelConfirmarPedidoFinal, "ConfirmarPedidoFinal");

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 120, 920, 810));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDatos_ClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDatos_ClienteActionPerformed
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Datos_Cliente");
    }//GEN-LAST:event_btnDatos_ClienteActionPerformed

    private void btnDetalle_PedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalle_PedidoActionPerformed
        cargarVistaDetallePedido(); 
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Detalle_Pedido");
    }//GEN-LAST:event_btnDetalle_PedidoActionPerformed

    private void btn_CartaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_CartaActionPerformed
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Carta");
    }//GEN-LAST:event_btn_CartaActionPerformed

    private void btnAgregarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductoActionPerformed
        agregarProductoSeleccionado();
    }//GEN-LAST:event_btnAgregarProductoActionPerformed

    private void btnQuitarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarProductoActionPerformed
        quitarProductoSeleccionado();
    }//GEN-LAST:event_btnQuitarProductoActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void txtTelefonoDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTelefonoDPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTelefonoDPActionPerformed

    private void txtCiudadDEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCiudadDEActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCiudadDEActionPerformed

    private void cmbDistritoDEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDistritoDEActionPerformed
        actualizarCostoEnvio();
    }//GEN-LAST:event_cmbDistritoDEActionPerformed

    private void btnDetalle_EnvioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDetalle_EnvioActionPerformed
        if (txtTelefonoEntregaDE != null) {
            if (jtxtTelefono != null && !jtxtTelefono.getText().isEmpty()) {
                txtTelefonoEntregaDE.setText(jtxtTelefono.getText());
            } else if (txtTelefonoDP != null && !txtTelefonoDP.getText().isEmpty()) { 
                txtTelefonoEntregaDE.setText(txtTelefonoDP.getText());
            } else {
                txtTelefonoEntregaDE.setText(""); 
            }
        }

        if (cmbDistritoDE != null) {
            actualizarCostoEnvio();
        }

        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Detalle_Envio");
    }//GEN-LAST:event_btnDetalle_EnvioActionPerformed

    private void btnContinuarAConfirmacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnContinuarAConfirmacionActionPerformed
        System.out.println("DEBUG: btnContinuarAConfirmacion - INICIO"); 

        if (!actualizarYValidarDatosDeEnvio()) { 
            return; 
        }
        
        System.out.println("DEBUG: Llamando a cargarVistaConfirmarPedidoFinal() desde btnContinuarAConfirmacion..."); 
        cargarVistaConfirmarPedidoFinal(); 
        
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "ConfirmarPedidoFinal");
        System.out.println("DEBUG: btnContinuarAConfirmacion - FIN"); 
    }//GEN-LAST:event_btnContinuarAConfirmacionActionPerformed

    private void btnAtrasADetallePedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtrasADetallePedidoActionPerformed
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Detalle_Pedido");
    }//GEN-LAST:event_btnAtrasADetallePedidoActionPerformed

    private void btnConfirmar_PedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmar_PedidoActionPerformed
        System.out.println("DEBUG: btnConfirmar_PedidoActionPerformed (navegación lateral) - INICIO"); 
        
        if (!actualizarYValidarDatosDeEnvio()) {
            System.out.println("DEBUG: btnConfirmar_PedidoActionPerformed - Datos de envío no válidos o incompletos. No se navega a confirmación.");
            return; 
        }
        
        System.out.println("DEBUG: btnConfirmar_PedidoActionPerformed - Llamando a cargarVistaConfirmarPedidoFinal...");
        cargarVistaConfirmarPedidoFinal(); 
        System.out.println("DEBUG: btnConfirmar_PedidoActionPerformed - Después de cargarVistaConfirmarPedidoFinal"); 
        CardLayout cl = (CardLayout) jPanel1.getLayout(); 
        cl.show(jPanel1, "ConfirmarPedidoFinal"); 
        System.out.println("DEBUG: btnConfirmar_PedidoActionPerformed - FIN - Panel ConfirmarPedidoFinal mostrado"); 
    }//GEN-LAST:event_btnConfirmar_PedidoActionPerformed

    private void txtSubTotal_ConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSubTotal_ConfirmarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubTotal_ConfirmarActionPerformed

    private void btnConfirmar_PagarActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        if (!jCheckBox1.isSelected()) {
            JOptionPane.showMessageDialog(this, "Debe aceptar los términos y condiciones.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String metodoPagoSeleccionado = "";
        if (cmbMetodoPago_Confirmar.getSelectedIndex() <= 0 ) { 
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un método de pago válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        metodoPagoSeleccionado = (String) cmbMetodoPago_Confirmar.getSelectedItem();

        if (!actualizarYValidarDatosDeEnvio()) {
             JOptionPane.showMessageDialog(this, "Faltan datos de envío o son incorrectos. Por favor, revise el panel 'Detalle del Envío'.", "Datos de Envío Incompletos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (itemsSeleccionados.isEmpty()) {
             JOptionPane.showMessageDialog(this, "No hay productos en el pedido.", "Pedido Vacío", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String numeroPedidoAGuardar = null;
        if (pedidoDAO != null) {
            numeroPedidoAGuardar = pedidoDAO.obtenerYConsumirSiguienteNumeroPedido(); 
        }

        if (numeroPedidoAGuardar == null) {
            JOptionPane.showMessageDialog(this, 
                "Error crítico al generar el número de pedido final.\nNo se puede continuar con el pedido.", 
                "Error Numeración de Pedido", 
                JOptionPane.ERROR_MESSAGE);
            System.err.println("Error crítico: No se pudo generar y consumir el número de pedido desde la BD al confirmar.");
            ActualizarCampos_NumeroPedido("(Error al generar N°)"); 
            return; 
        }
        this.numeroPedidoActual = numeroPedidoAGuardar; 
        ActualizarCampos_NumeroPedido(this.numeroPedidoActual); 


        Pedido pedidoAGuardar = new Pedido();
        pedidoAGuardar.setNumeroPedido(this.numeroPedidoActual); 
        pedidoAGuardar.setNombreCliente(jtxtNombreCliente.getText());
        pedidoAGuardar.setDniCliente(jtxtDNI.getText());
        pedidoAGuardar.setTelefonoCliente(jtxtTelefono.getText());
        pedidoAGuardar.setCorreoCliente(jtxtCorreo.getText());
        pedidoAGuardar.setDireccionEnvio(this.direccionEnvio); 
        pedidoAGuardar.setDistritoEnvio(this.distritoEnvio);   
        pedidoAGuardar.setCiudadEnvio(this.ciudadEnvio != null ? this.ciudadEnvio : ""); 
        pedidoAGuardar.setReferenciaEnvio(this.referenciaEnvio != null ? this.referenciaEnvio : ""); 
        pedidoAGuardar.setTelefonoEntrega(this.telefonoEntrega); 
        pedidoAGuardar.setCostoEnvio(this.costoDeEnvio);
        
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        for(SeleccionItems item : itemsSeleccionados) {
            subtotalCalculado = subtotalCalculado.add(item.getSubtotal());
        }
        pedidoAGuardar.setSubtotalPedido(subtotalCalculado);
        pedidoAGuardar.setTotalPedido(subtotalCalculado.add(this.costoDeEnvio));
        pedidoAGuardar.setMetodoPago(metodoPagoSeleccionado); 
        pedidoAGuardar.setEstadoPedido("Pendiente"); 

        
        /////////////LISTA DINAMICA PARA GUARDAR DETALLES DEL PEDIDO///////////////
        ///////////////////////////////////////////////////////////////////////
        List<DetallePedido> detallesAGuardar = new ArrayList<>();
        for (SeleccionItems item : itemsSeleccionados) {
            DetallePedido detalle = new DetallePedido();
            detalle.setIdProducto(item.getProducto().getId());
            detalle.setNombreProducto(item.getProducto().getNombre());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getProducto().getPrecio());
            detalle.setSubtotalItem(item.getSubtotal());
            detallesAGuardar.add(detalle);
        }

        boolean guardadoExitoso = pedidoDAO.guardarPedidoCompleto(pedidoAGuardar, detallesAGuardar);

        if (guardadoExitoso) {
            JOptionPane.showMessageDialog(this, "Pedido Nro: " + this.numeroPedidoActual + " confirmado y guardado exitosamente.", "Pedido Confirmado", JOptionPane.INFORMATION_MESSAGE);
            reiniciarProcesoPedido(); 
        } else {
            JOptionPane.showMessageDialog(this, "Error al guardar el pedido. Verifique la consola para más detalles.", "Error de Pedido", JOptionPane.ERROR_MESSAGE);
           
            generarYMostrarNumeroPedidoPreview(); 
        }
    }                                                  

    private void btnModificar_PedidoActionPerformed(java.awt.event.ActionEvent evt) {                                                    
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        cl.show(jPanel1, "Carta"); 
    }                                                   

    private void btnCancelar_PedidoActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        int confirmacion = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de que desea cancelar el pedido actual?\nEl número de pedido previsualizado " + (this.numeroPedidoActual != null && !this.numeroPedidoActual.startsWith("(") ? this.numeroPedidoActual : "") + " podría ser utilizado por otro pedido si no se confirma este.", 
            "Cancelar Pedido", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            reiniciarProcesoPedido(); 
        }
    }                                                  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmDelivery(null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarProducto;
    private javax.swing.JButton btnAtrasADetallePedido;
    private javax.swing.JButton btnCancelar_Pedido;
    private javax.swing.JButton btnConfirmar_Pagar;
    private javax.swing.JButton btnConfirmar_Pedido;
    private javax.swing.JButton btnContinuarAConfirmacion;
    private javax.swing.JButton btnDatos_Cliente;
    private javax.swing.JButton btnDetalle_Envio;
    private javax.swing.JButton btnDetalle_Pedido;
    private javax.swing.JButton btnModificar_Pedido;
    private javax.swing.JButton btnQuitarProducto;
    private javax.swing.JButton btn_Carta;
    private javax.swing.JComboBox<String> cmbDistritoDE;
    private javax.swing.JComboBox<String> cmbMetodoPago_Confirmar;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabelReferencia;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelCarta;
    private javax.swing.JPanel jPanelCliente;
    private javax.swing.JPanel jPanelConfirmarPedidoFinal;
    private javax.swing.JPanel jPanelDetalle_Pedido;
    private javax.swing.JPanel jPanelDetalle_envio;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPaneDisponibles;
    private javax.swing.JScrollPane jScrollPaneSeleccionados;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea jTextAreaPago;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JLabel jlabelCliente;
    private javax.swing.JLabel jlabelCorreo;
    private javax.swing.JLabel jlabelDNI;
    private javax.swing.JTextField jtxtCorreo;
    private javax.swing.JTextField jtxtDNI;
    private javax.swing.JTextField jtxtNombreCliente;
    private javax.swing.JTextField jtxtSubTotal;
    private javax.swing.JTextField jtxtTelefono;
    private javax.swing.JLabel lblLogoPago;
    private javax.swing.JLabel lblTitulo_Carta;
    private javax.swing.JLabel lblTitulo_ConfirmaTuPedido;
    private javax.swing.JLabel lblTitulo_DatosDelCliente;
    private javax.swing.JLabel lblTitulo_DetalleDelEnvio;
    private javax.swing.JLabel lblTitulo_DetalleDelPedido;
    private javax.swing.JLabel lblTotalPedido;
    private javax.swing.JPanel panelAccionesDisponibles;
    private javax.swing.JPanel panelAccionesSeleccionados;
    private javax.swing.JPanel panelControlGeneral;
    private javax.swing.JTable tblConfirmacionPedido;
    private javax.swing.JTable tblProductosDisponibles;
    private javax.swing.JTable tblProductosSeleccionados;
    private javax.swing.JTable tblResumenPedido;
    private javax.swing.JTextField txtCiudadDE;
    private javax.swing.JTextField txtCorreoCliente_Confirmar;
    private javax.swing.JTextField txtCorreoDP;
    private javax.swing.JTextField txtCorreo_Confirmar;
    private javax.swing.JTextField txtCostoEnvioDE;
    private javax.swing.JTextField txtCostoEnvio_Confirmar;
    private javax.swing.JTextField txtDNI_Confirmar;
    private javax.swing.JTextField txtDireccionDE;
    private javax.swing.JTextField txtDireccionEntrega_Confirmar;
    private javax.swing.JTextField txtDistrito_Confirmar;
    private javax.swing.JTextField txtNombreClienteDP;
    private javax.swing.JTextField txtNombreCliente_Confirmar;
    private javax.swing.JTextField txtPedidoNroDP;
    private javax.swing.JTextField txtReferenciaDE;
    private javax.swing.JTextField txtSubTotal_Confirmar;
    private javax.swing.JTextField txtTelefonoDP;
    private javax.swing.JTextField txtTelefonoEntregaDE;
    private javax.swing.JTextField txtTotalPagar_Confirmar;
    // End of variables declaration//GEN-END:variables

    class SeleccionItems {
        Producto producto;
        int cantidad;
        BigDecimal subtotal;

        public SeleccionItems(Producto producto, int cantidad) {
            this.producto = producto;
            this.cantidad = cantidad;
            if (producto != null && producto.getPrecio() != null) {
                this.subtotal = producto.getPrecio().multiply(new BigDecimal(cantidad));
            } else {
                this.subtotal = BigDecimal.ZERO;
                System.err.println("Error: Producto o precio es null al crear SeleccionItems.");
            }
        }

        public Producto getProducto() { return producto; }
        public int getCantidad() { return cantidad; }
        public BigDecimal getSubtotal() { return subtotal; }

        public void incrementarCantidad(int cantidadAdicional) {
            this.cantidad += cantidadAdicional;
            if (this.cantidad < 0) this.cantidad = 0;
            if (this.producto != null && this.producto.getPrecio() != null) {
                this.subtotal = this.producto.getPrecio().multiply(new BigDecimal(this.cantidad));
            } else {
                this.subtotal = BigDecimal.ZERO;
            }
        }

        public void setCantidad(int cantidad) {
            this.cantidad = cantidad;
            if (this.cantidad < 0) this.cantidad = 0;
            if (this.producto != null && this.producto.getPrecio() != null) {
                this.subtotal = this.producto.getPrecio().multiply(new BigDecimal(this.cantidad));
            } else {
                this.subtotal = BigDecimal.ZERO;
            }
        }
    }
}