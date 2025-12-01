package vista;

import com.toedter.calendar.JDateChooser;
import conex.Conexion;
import java.sql.Connection;
import conex.Salas;
import conex.SalasDao;
import conex.login;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import vista.Estacionamiento;
// Importaciones de clases necesarias
import conex.UsuarioDAO;
import conex.Usuario;
import java.awt.Dimension;


public class frmsistema extends javax.swing.JFrame {
    private ListaReservas listaReservas = new ListaReservas();
    private boolean comboInicializado = false;
    // VARIABLES DE LA CLASE
    private boolean clienteSatisfecho;
    private login usuarioLogueado;

    public frmsistema() {
        initComponents();
        cargarTiposDeSala();
        this.setSize(1040, 730);
        this.setLocationRelativeTo(null);
        cargarTablaSalas();
        cargarTabla();
        cargarMesasEnPanel("principal");

        tablep.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                int fila = tablep.getSelectedRow();
                if (fila != -1) {
                    String nombre = (String) tablep.getValueAt(fila, 1);
                    String tipo = (String) tablep.getValueAt(fila, 2);
                    int capacidad = (int) tablep.getValueAt(fila, 3);

                    TxtNombreSala.setText(nombre);
                    cbxTipoSala.setSelectedItem(tipo);
                    spnCapacidad.setValue(capacidad);
                }
            }
        });

        // Acción para SALA PRINCIPAL
        btnsalaprincipal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(3); // Ir a pestaña "Mesas"
                cargarMesasEnPanel("principal");
            }
        });

        // Acción para SALA SECUNDARIA
        btnsalasecundaria.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jTabbedPane1.setSelectedIndex(3); // Ir a pestaña "Mesas"
                cargarMesasEnPanel("secundaria");
            }
        });

        // Por defecto mostramos la sala principal
        cargarMesasEnPanel("principal");

        // 💡 IMPLEMENTACIÓN DE ACTIVAR/DESACTIVAR USUARIOS
        // Se asume que los botones btnDesactivarUsuario y btnActivarUsuario
        // se inicializaron correctamente en initComponents()
        if (btnDesactivarUsuario != null) {
            btnDesactivarUsuario.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cambiarEstadoUsuario(0, "desactivar"); // 0 = Desactivar
                }
            });
        }
        if (btnActivarUsuario != null) {
            btnActivarUsuario.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cambiarEstadoUsuario(1, "activar"); // 1 = Activar
                }
            });
        }
        // 💡 FIN IMPLEMENTACIÓN DE ACTIVAR/DESACTIVAR
    }

    // Constructor con usuario logueado
    public frmsistema(login usuario) {
        this();
        this.usuarioLogueado = usuario;
        System.out.println("Usuario logueado: " + usuarioLogueado.getNombreCompleto() + " con rol: " + usuarioLogueado.getRol());
        //  Restricción por rol
        if (usuarioLogueado.getRol().equalsIgnoreCase("cliente")) {
            btnadministrador.setVisible(false);
        }

        if (usuarioLogueado.getRol().equalsIgnoreCase("cliente")) {
            btnadministrador.setVisible(false);  // Oculta el botón
            jPanel7.setVisible(false);           // Oculta el panel
        }
        if (usuarioLogueado.getRol().equalsIgnoreCase("cliente")) {
            btnadministrador.setVisible(false);  // Oculta el botón
            tablaUsuarios.setVisible(false);           // Oculta el panel
        }
        if (usuarioLogueado.getRol().equalsIgnoreCase("cliente")) {
            btnadministrador.setVisible(false);  // Oculta el botón
            btnhistorial.setVisible(false);           // Oculta el panel
        }
        if (usuarioLogueado.getRol().equalsIgnoreCase("cliente")) {
            btnadministrador.setVisible(false);  // Oculta el botón
            btnreportes.setVisible(false);           // Oculta el panel
        }

    }


    // Método para cargar salas en la tabla
    private void cargarTablaSalas() {
        SalasDao dao = new SalasDao();
        java.util.List<Salas> lista = dao.listarSalas(); // Debes implementar este método en SalasDao

        javax.swing.table.DefaultTableModel modelo = (javax.swing.table.DefaultTableModel) tablep.getModel();
        modelo.setRowCount(0); // Limpiar tabla

        for (Salas sala : lista) {
            modelo.addRow(new Object[]{
                    sala.getId(),
                    sala.getNombre(),
                    sala.getTipo(),
                    sala.getCapacidad()
            });
        }
    }



    // validacion de contraseña de administradores modificacion 20112025
    private boolean validarContrasena(String contrasena) {
        String patron = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,}$";
        return contrasena.matches(patron);
    }


    // Método para cargar tipos de sala en el ComboBox
    private void cargarTiposDeSala() {
        cbxTipoSala.removeAllItems();
        cbxTipoSala.addItem("Principal");
        cbxTipoSala.addItem("Secundaria");

    }

    private void agregarSala() {
        String nombre = TxtNombreSala.getText().trim();  // trim para eliminar espacios al inicio y fin
        System.out.println("Nombre leído: '" + nombre + "'");  // imprime en consola para verificar

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la sala es obligatorio");
            return;
        }

        String tipo = (String) cbxTipoSala.getSelectedItem();
        int capacidad = (Integer) spnCapacidad.getValue();

        Salas nuevaSala = new Salas();
        nuevaSala.setNombre(nombre);
        nuevaSala.setTipo(tipo);
        nuevaSala.setCapacidad(capacidad);

        SalasDao dao = new SalasDao();
        boolean guardado = dao.registrarSala(nuevaSala);

        if (guardado) {
            JOptionPane.showMessageDialog(this, "Sala registrada correctamente");
            cargarTablaSalas();
            TxtNombreSala.setText("");
            cbxTipoSala.setSelectedIndex(0);
            spnCapacidad.setValue(1);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la sala");
        }
    }


    // Método para limpiar los campos del formulario de sala
    private void limpiarCamposSala() {
        TxtNombreSala.setText("");
        cbxTipoSala.setSelectedIndex(0); // Primer ítem, como "principal"
        spnCapacidad.setValue(1); // Valor por defecto
        tablep.clearSelection(); // Deselecciona cualquier fila de la tabla
    }

    private JDateChooser dateChooserMesas = new JDateChooser();


    private void cargarMesasEnPanel(String sala) {
        PanelMesasContenedor.removeAll();
        PanelMesasContenedor.setLayout(new BorderLayout());

        // Configurar JDateChooser
        dateChooserMesas.setDateFormatString("yyyy-MM-dd");
        if (dateChooserMesas.getDate() == null) {
            dateChooserMesas.setDate(new java.util.Date()); // Establecer fecha actual por defecto
        }

        // Evento para recargar mesas al cambiar la fecha
        dateChooserMesas.addPropertyChangeListener("date", evt -> {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // Aquí haces el trabajo pesado fuera del hilo principal
                    return null;
                }

                @Override
                protected void done() {
                    // Actualizas la interfaz desde el hilo de la GUI
                    cargarMesasEnPanel(sala);
                }
            }.execute();
        });

        PanelMesasContenedor.add(dateChooserMesas, BorderLayout.NORTH);
        Date fechaSeleccionada = dateChooserMesas.getDate();
        PanelMesasContenedor.add(crearPanelMesas(sala, fechaSeleccionada), BorderLayout.CENTER);

        PanelMesasContenedor.revalidate();
        PanelMesasContenedor.repaint();
    }


    // Crear el panel con mesas personalizadas
    private JPanel crearPanelMesas(String sala, Date fechaSeleccionada) {
        int filas = 2;
        int columnas = 5;
        String prefijo = "P"; // Por defecto para principal
        Color fondo = new Color(0, 102, 102);
        Color texto = Color.WHITE;

        if ("secundaria".equalsIgnoreCase(sala)) {
            filas = 3;
            columnas = 4;
            prefijo = "S";
            fondo = new Color(102, 153, 255);
            texto = Color.BLACK;
        }

        JPanel panelMesas = new JPanel(new GridLayout(filas, columnas, 10, 10));
        Set<String> mesasReservadas = obtenerMesasReservadas(sala, fechaSeleccionada);

        int totalMesas = filas * columnas;
        for (int i = 1; i <= totalMesas; i++) {
            String nombreMesa = "Mesa " + prefijo + i;
            JButton btnMesa;

            if (mesasReservadas.contains(nombreMesa)) {
                btnMesa = new JButton("<html><center><img src='" + getClass().getResource("/imagenes/restaurante.png") +
                        "' width='50' height='50'><br><b style='color:red;'>" + nombreMesa + "<br>RESERVADA</b></center></html>");
                btnMesa.setBackground(Color.RED);
                btnMesa.setForeground(Color.WHITE);
                btnMesa.setEnabled(false);
            } else {
                btnMesa = new JButton("<html><center><img src='" + getClass().getResource("/imagenes/restaurante.png") +
                        "' width='50' height='50'><br>" + nombreMesa + "</center></html>");
                btnMesa.setBackground(fondo);
                btnMesa.setForeground(texto);
                btnMesa.setFocusPainted(false);
                btnMesa.setActionCommand(nombreMesa);

                btnMesa.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String mesaSeleccionada = e.getActionCommand();
                        Date fechaSeleccionada = dateChooserMesas.getDate();

                        // Cambiar a la pestaña de reservas (jPanel4)
                        jTabbedPane1.setSelectedIndex(4);

                        // Mostrar mensaje opcional
                        JOptionPane.showMessageDialog(null, "Has seleccionado: " + mesaSeleccionada);

                        // Asignar mesa seleccionada al campo de reservas
                        if (txtMesaSeleccionada != null) {
                            txtMesaSeleccionada.setText(mesaSeleccionada);
                        }

                        // Asignar fecha seleccionada al campo txtFecha
                        if (txtFecha != null && fechaSeleccionada != null) {
                            // Convertir Date a String en formato yyyy-MM-dd
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            txtFecha.setText(sdf.format(fechaSeleccionada));
                            // Asignar nombre del cliente si está logueado
                            if (usuarioLogueado != null && "cliente".equalsIgnoreCase(usuarioLogueado.getRol())) {
                                txtCliente.setText(usuarioLogueado.getNombreCompleto());
                            }
                        }
                    }
                });

            }

            panelMesas.add(btnMesa);
        }

        return panelMesas;
    }




    // Este método va dentro de la clase, fuera de cualquier otro método
    private Set<String> obtenerMesasReservadas(String sala, Date fecha) {
        Set<String> mesasReservadas = new HashSet<>();
        String sql = "SELECT mesa FROM reservas WHERE sala = ? AND fecha = ?";

        try (Connection conn = new Conexion().getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Ajustamos el parámetro para que coincida exactamente con lo que está en la base de datos
            String salaBD = "sala " + sala.toLowerCase();  // Ejemplo: "secundaria" -> "sala secundaria"
            ps.setString(1, salaBD);
            ps.setDate(2, new java.sql.Date(fecha.getTime()));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                mesasReservadas.add(rs.getString("mesa"));
            }

        } catch (SQLException e) {
            System.out.println("ⓘ Error al obtener mesas reservadas: " + e.getMessage());
        }

        return mesasReservadas;
    }









    private void modificarSala() {
        int fila = tablep.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una sala para modificar.");
            return;
        }

        // Obtener ID de la sala seleccionada
        int idSala = (int) tablep.getValueAt(fila, 0);

        // Obtener nuevos valores desde los campos
        String nuevoNombre = TxtNombreSala.getText();
        String nuevoTipo = (String) cbxTipoSala.getSelectedItem();
        int nuevaCapacidad = (Integer) spnCapacidad.getValue();

        // Validar campos
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la sala no puede estar vacío.");
            return;
        }

        // Crear objeto Sala con nuevos valores
        Salas salaModificada = new Salas();
        salaModificada.setId(idSala);
        salaModificada.setNombre(nuevoNombre);
        salaModificada.setTipo(nuevoTipo);
        salaModificada.setCapacidad(nuevaCapacidad);

        // Actualizar en base de datos
        SalasDao dao = new SalasDao();
        boolean actualizado = dao.modificarSala(salaModificada);

        if (actualizado) {
            JOptionPane.showMessageDialog(this, "Sala modificada correctamente.");
            cargarTablaSalas(); // refrescar tabla
        } else {
            JOptionPane.showMessageDialog(this, "Error al modificar la sala.");
        }
    }

    private void eliminarSalaSeleccionada() {
        int fila = tablep.getSelectedRow(); // Asegúrate que tu tabla se llame tablep
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una sala para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar la sala seleccionada?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) tablep.getValueAt(fila, 0); // Se asume que la columna 0 es el ID

            SalasDao dao = new SalasDao();
            if (dao.eliminarSala(id)) {
                JOptionPane.showMessageDialog(this, "Sala eliminada correctamente.");
                cargarTablaSalas(); // Refrescar tabla
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar la sala.");
            }
        }
    }

    private void limpiarCampos() {
        txtnombre.setText("");
        txtcorroelectronico.setText("");
        txtpassswords.setText("");
        txttelefono.setText("");
    }

    // 💡 MÉTODO MODIFICADO: Ahora carga la tabla de administradores
    private void cargarTabla() {
        String[] columnas = {"Nombre Completo", "Correo", "Rol", "Teléfono", "Estado"};
        DefaultTableModel modelo = new DefaultTableModel(null, columnas);

        String url = "jdbc:mysql://localhost:3306/baserestaurante";
        String usuario = "root";
        String contraseña = "";

        // 💡 NOTA: Solo se cargan administradores. Se podría mejorar incluyendo el estado.
        String sql = "SELECT nombre_completo, correo, rol, telefono, estado FROM usuarios WHERE rol = 'admin'";

        try (Connection conn = DriverManager.getConnection(url, usuario, contraseña);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String nombre = rs.getString("nombre_completo");
                String correo = rs.getString("correo");
                String rol = rs.getString("rol");
                String telefono = rs.getString("telefono");
                int estadoInt = rs.getInt("estado");

                String estadoTexto = (estadoInt == 1) ? "Activo" : "Desactivado";

                String[] fila = {nombre, correo, rol, telefono, estadoTexto};
                modelo.addRow(fila);
            }
            tablaUsuarios.setModel(modelo);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar tabla: " + e.getMessage());
        }
    }

    // 💡 MÉTODO NUEVO: Lógica central para cambiar el estado del usuario (Activación/Desactivación)
    private void cambiarEstadoUsuario(int nuevoEstado, String accion) {
        int fila = tablaUsuarios.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila de la tabla para " + accion + ".", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Obtener el correo del usuario (asumiendo que está en la columna 1, 'Correo')
        String correoUsuario = (String) tablaUsuarios.getValueAt(fila, 1);

        // 2. Obtener el objeto Usuario completo para obtener su ID
        conex.UsuarioDAO dao = new conex.UsuarioDAO();
        conex.Usuario usuario = dao.buscarPorCorreo(correoUsuario);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this, "Error: No se encontró el usuario en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Ejecutar la acción (Activar o Desactivar)
        if (nuevoEstado == 0) { // Desactivar
            if (dao.desactivarUsuario(usuario.getId())) {
                JOptionPane.showMessageDialog(this, "Cuenta " + accion + " correctamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Fallo al " + accion + " la cuenta.");
            }
        } else { // Activar (nuevoEstado == 1)
            if (dao.activarUsuario(usuario.getId())) {
                JOptionPane.showMessageDialog(this, "Cuenta " + accion + " correctamente.");
                cargarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Fallo al " + accion + " la cuenta.");
            }
        }
    }
// 💡 FIN DEL MÉTODO CAMBIARESTADOUSUARIO

    private void mostrarTicket(String mesa, String cliente, String fecha, int capacidad,
                               int hora, String sala, String codigoReserva,
                               String metodoPago, String estadoPago, double precio) {

        JDialog ticketDialog = new JDialog(this, "Ticket de Reserva", true);
        ticketDialog.setSize(500, 500);
        ticketDialog.setLayout(new BorderLayout());
        //ticket de reserva
        // Panel principal con borde
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Encabezado
        JLabel lblTitulo = new JLabel("RESTAURANTE A TU SABOR", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        mainPanel.add(lblTitulo);

        // Cuerpo del ticket
        JTextArea txtTicket = new JTextArea();
        txtTicket.setEditable(false);
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 14));
        //ticket de reserva
        StringBuilder contenido = new StringBuilder();
        contenido.append("\n          TICKET DE RESERVA          \n");
        contenido.append("==================================\n");
        contenido.append(String.format("%-15s: %s%n", "Código", codigoReserva));
        contenido.append(String.format("%-15s: %s%n", "Fecha", fecha));
        contenido.append(String.format("%-15s: %02d:00%n", "Hora", hora));
        contenido.append(String.format("%-15s: %s%n", "Sala", sala));
        contenido.append(String.format("%-15s: %s%n", "Mesa", mesa));
        contenido.append(String.format("%-15s: %d personas%n", "Capacidad", capacidad));
        contenido.append(String.format("%-15s: %s%n", "Cliente", cliente));
        contenido.append("----------------------------------\n");
        contenido.append(String.format("%-15s: %s%n", "Método Pago", metodoPago));
        contenido.append(String.format("%-15s: %s%n", "Estado", estadoPago));
        contenido.append(String.format("%-15s: S/ %.2f%n", "Total", precio));
        contenido.append("==================================\n");
        contenido.append("NOTA: Presentar este código al llegar\n");
        contenido.append("      Llegar 10 minutos antes\n");
        contenido.append("      Tel: 963 811 755\n\n");
        contenido.append("¡Gracias por su preferencia!");

        txtTicket.setText(contenido.toString());
        //importante de ejemplo para imprimir los reportes
        // Botones
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> ticketDialog.dispose());
        //boton imprimir
        JButton btnImprimir = new JButton("Imprimir");
        btnImprimir.addActionListener(e -> imprimirTicket(contenido.toString()));

        JPanel panelBotones = new JPanel();
        panelBotones.add(btnImprimir);
        panelBotones.add(btnCerrar);

        // Ensamblar componentes
        mainPanel.add(txtTicket);
        ticketDialog.add(mainPanel, BorderLayout.CENTER);
        ticketDialog.add(panelBotones, BorderLayout.SOUTH);

        ticketDialog.setLocationRelativeTo(this);
        ticketDialog.setVisible(true);
    }
    //acciones de imprimir
    private void imprimirTicket(String contenido) {
        try {
            JTextArea areaImpresion = new JTextArea(contenido);
            areaImpresion.setFont(new Font("Monospaced", Font.PLAIN, 12));
            if (areaImpresion.print()) {
                JOptionPane.showMessageDialog(this, "Ticket enviado a impresora");
            }
        } catch (PrinterException ex) {
            JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage());
        }
    }






















    private String salaSeleccionada = "";

    private String generarCodigoReserva() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numeros = "0123456789";
        StringBuilder codigo = new StringBuilder("RSV-");

        Random rand = new Random();
        for (int i = 0; i < 3; i++) {
            codigo.append(letras.charAt(rand.nextInt(letras.length())));
        }
        codigo.append("-");
        for (int i = 0; i < 4; i++) {
            codigo.append(numeros.charAt(rand.nextInt(numeros.length())));
        }

        return codigo.toString();  // Ejemplo: RSV-JKL-3921
    }


// Métodos adicionales requeridos:

    public double calcularPrecio(int capacidad) {
        // Validación básica
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1 persona.");
        }

        double precioBase = 30.00;  // Precio fijo por mesa
        double porPersona = 10.00;  // Adicional por cada persona

        return precioBase + (capacidad * porPersona);
    }
    private String determinarEstadoPago(String metodoPago) {
        if (metodoPago == null) {
            return "Error: Método no seleccionado";
        }
        return metodoPago.equalsIgnoreCase("Efectivo") ? "Pendiente" : "Pagado";

    }













    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        btnabrirsalas = new javax.swing.JButton();
        btnadministrador = new javax.swing.JButton();
        btnsalas = new javax.swing.JButton();
        btnhistorial = new javax.swing.JButton();
        btnestacionamiento = new javax.swing.JButton();
        btndelivery = new javax.swing.JButton();
        btnplatos = new javax.swing.JButton();
        btncerrarsesion = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        PanelSalas = new javax.swing.JPanel();
        btnsalaprincipal = new javax.swing.JButton();
        btnsalasecundaria = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        btnregistrar = new javax.swing.JButton();
        txtnombre = new javax.swing.JTextField();
        txtcorroelectronico = new javax.swing.JTextField();
        txttelefono = new javax.swing.JTextField();
        txtpassswords = new javax.swing.JPasswordField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaUsuarios = new javax.swing.JTable();
        // 💡 BOTONES AÑADIDOS MANUALMENTE
        btnDesactivarUsuario = new javax.swing.JButton();
        btnActivarUsuario = new javax.swing.JButton();
        // FIN BOTONES
        jPanel3 = new javax.swing.JPanel();
        jpanel = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablep = new javax.swing.JTable();
        jPanel11 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        TxtNombreSala = new javax.swing.JTextField();
        cbxTipoSala = new javax.swing.JComboBox<>();
        spnCapacidad = new javax.swing.JSpinner();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        btnAgregarSala = new javax.swing.JButton();
        btnModificarSala = new javax.swing.JButton();
        btnEliminarSala = new javax.swing.JButton();
        btnLimpiarSala = new javax.swing.JButton();
        PanelMesasContenedor = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtMesaSeleccionada = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        spinnerHora = new javax.swing.JSpinner();
        btnreservaciones = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        spnCapaci = new javax.swing.JSpinner();
        txtFecha = new javax.swing.JTextField();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        botonopinion = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        lblLugar1 = new javax.swing.JLabel();
        lblLugar2 = new javax.swing.JLabel();
        lblLugar3 = new javax.swing.JLabel();
        lblLugar4 = new javax.swing.JLabel();
        lblLugar5 = new javax.swing.JLabel();
        lblLugar6 = new javax.swing.JLabel();
        lblLugar7 = new javax.swing.JLabel();
        lblLugar8 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        txtCodReserva = new javax.swing.JTextField();
        txtCliente_est = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        btnReservar = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        btnreportes = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Zilla Slab", 3, 48)); // NOI18N
        jLabel38.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/olla-caliente (1).png"))); // NOI18N
        jLabel38.setText("restaurante la lucha ");
        jLabel38.setFocusable(false);
        jLabel38.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        getContentPane().add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 20, 830, 90));

        btnabrirsalas.setBackground(new java.awt.Color(102, 102, 255));
        btnabrirsalas.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnabrirsalas.setForeground(new java.awt.Color(255, 255, 255));
        btnabrirsalas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/salida.png"))); // NOI18N
        btnabrirsalas.setText("Abrir Salas");
        btnabrirsalas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnabrirsalasActionPerformed(evt);
            }
        });
        getContentPane().add(btnabrirsalas, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 150, 100));

        btnadministrador.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnadministrador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/gerente.png"))); // NOI18N
        btnadministrador.setText("Administrador");
        btnadministrador.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        btnadministrador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnadministradorActionPerformed(evt);
            }
        });
        getContentPane().add(btnadministrador, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 180, 150, 40));

        btnsalas.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnsalas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/puerta-de-salida.png"))); // NOI18N
        btnsalas.setText("Salas");
        btnsalas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalasActionPerformed(evt);
            }
        });
        getContentPane().add(btnsalas, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 150, 40));

        btnhistorial.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnhistorial.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/reserva.png"))); // NOI18N
        btnhistorial.setText("Historial");
        btnhistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnhistorialActionPerformed(evt);
            }
        });
        getContentPane().add(btnhistorial, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 330, 150, -1));

        btnestacionamiento.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnestacionamiento.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/estacionamiento.png"))); // NOI18N
        btnestacionamiento.setText("Estacionamiento");
        btnestacionamiento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnestacionamientoActionPerformed(evt);
            }
        });
        getContentPane().add(btnestacionamiento, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 390, -1, 40));

        btndelivery.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btndelivery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/entrega-de-comida.png"))); // NOI18N
        btndelivery.setText("Delivery");
        btndelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btndeliveryActionPerformed(evt);
            }
        });
        getContentPane().add(btndelivery, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 450, 150, 40));

        btnplatos.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnplatos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/almuerzo (1).png"))); // NOI18N
        btnplatos.setText("Platos");
        btnplatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnplatosActionPerformed(evt);
            }
        });
        getContentPane().add(btnplatos, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 510, 150, 40));

        btncerrarsesion.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btncerrarsesion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/cerrar-sesion.png"))); // NOI18N
        btncerrarsesion.setText("Cerrar Sesion");
        btncerrarsesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btncerrarsesionActionPerformed(evt);
            }
        });
        getContentPane().add(btncerrarsesion, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 630, 150, 40));

        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PanelSalas.setBackground(new java.awt.Color(255, 255, 255));
        PanelSalas.setLayout(new java.awt.GridLayout(0, 5));

        btnsalaprincipal.setFont(new java.awt.Font("Lucida Sans Typewriter", 3, 10)); // NOI18N
        btnsalaprincipal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/puerta-principal (1).png"))); // NOI18N
        btnsalaprincipal.setText("Sala Principal");
        btnsalaprincipal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalaprincipalActionPerformed(evt);
            }
        });
        PanelSalas.add(btnsalaprincipal);

        btnsalasecundaria.setFont(new java.awt.Font("Lucida Sans Typewriter", 3, 10)); // NOI18N
        btnsalasecundaria.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/puerta-principal.png"))); // NOI18N
        btnsalasecundaria.setText("Sala Secundaria");
        btnsalasecundaria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnsalasecundariaActionPerformed(evt);
            }
        });
        PanelSalas.add(btnsalasecundaria);

        jPanel1.add(PanelSalas, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 0, 800, 500));

        jTabbedPane1.addTab("Panel", jPanel1);

        jPanel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jPanel8.setBackground(new java.awt.Color(153, 153, 153));

        jLabel8.setFont(new java.awt.Font("Poor Richard", 3, 24)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Administrador");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(44, 44, 44))
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Kristen ITC", 3, 12)); // NOI18N
        jLabel9.setText("Nombre Completo");

        jLabel10.setFont(new java.awt.Font("Kristen ITC", 3, 12)); // NOI18N
        jLabel10.setText("Correo Electronico");

        jLabel11.setFont(new java.awt.Font("Kristen ITC", 3, 12)); // NOI18N
        jLabel11.setText("Password");

        jLabel12.setFont(new java.awt.Font("Kristen ITC", 3, 12)); // NOI18N
        jLabel12.setText("Telefono");

        btnregistrar.setBackground(new java.awt.Color(102, 102, 102));
        btnregistrar.setFont(new java.awt.Font("Kristen ITC", 3, 12)); // NOI18N
        btnregistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnregistrar.setText("Registrar");
        btnregistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnregistrarActionPerformed(evt);
            }
        });

        txtnombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnombreActionPerformed(evt);
            }
        });

        txtpassswords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtpassswordsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(35, 35, 35)
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtpassswords)
                                                        .addComponent(txttelefono)))
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel10))
                                                .addGap(29, 29, 29)
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtnombre)
                                                        .addComponent(txtcorroelectronico)))))
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(53, 53, 53)
                                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel9)
                                        .addComponent(txtnombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel10)
                                        .addComponent(txtcorroelectronico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(25, 25, 25)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel11)
                                        .addComponent(txtpassswords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(txttelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                                .addComponent(btnregistrar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28))
        );

        tablaUsuarios.setBackground(new java.awt.Color(204, 204, 204));
        tablaUsuarios.setForeground(new java.awt.Color(51, 51, 255));
        tablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null}
                },
                new String [] {
                        "Id", "Nombre", "Correo Electronico", "Telefono"
                }
        ));
        jScrollPane2.setViewportView(tablaUsuarios);

        // 💡 POSICIONAMIENTO MANUAL DE BOTONES DE ACTIVACIÓN/DESACTIVACIÓN
        btnDesactivarUsuario.setText("Desactivar");
        btnActivarUsuario.setText("Activar");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(29, 29, 29)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 486, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnActivarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnDesactivarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnDesactivarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnActivarUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(50, Short.MAX_VALUE))
        );
        // FIN POSICIONAMIENTO MANUAL

        jTabbedPane1.addTab("Administrador", jPanel2);

        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jpanel.setBackground(new java.awt.Color(204, 204, 204));
        jpanel.setForeground(new java.awt.Color(204, 204, 204));

        jPanel10.setBackground(new java.awt.Color(102, 153, 255));

        jLabel13.setFont(new java.awt.Font("OCR A Extended", 3, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/nuevo.png"))); // NOI18N
        jLabel13.setText("Gestion de Salas");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel10Layout.createSequentialGroup()
                                .addGap(261, 261, 261)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
                jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                                .addGap(0, 10, Short.MAX_VALUE)
                                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        tablep.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null}
                },
                new String [] {
                        "ID", "Nombre Sala", "Tipo", "Capacidad"
                }
        ));
        jScrollPane3.setViewportView(tablep);

        jPanel11.setBackground(new java.awt.Color(255, 204, 204));

        jLabel14.setFont(new java.awt.Font("Microsoft Sans Serif", 3, 18)); // NOI18N
        jLabel14.setText("Formulario de Sala");

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addContainerGap(44, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(38, 38, 38))
        );
        jPanel12Layout.setVerticalGroup(
                jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                .addContainerGap(14, Short.MAX_VALUE)
                                .addComponent(jLabel14)
                                .addContainerGap())
        );

        jLabel15.setFont(new java.awt.Font("Yu Gothic Medium", 3, 14)); // NOI18N
        jLabel15.setText("Nombre");

        jLabel16.setFont(new java.awt.Font("Yu Gothic Medium", 3, 14)); // NOI18N
        jLabel16.setText("Tipo");

        jLabel17.setFont(new java.awt.Font("Yu Gothic Medium", 3, 14)); // NOI18N
        jLabel17.setText("Capacidad");

        cbxTipoSala.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTipoSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTipoSalaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel17, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(TxtNombreSala)
                                        .addComponent(cbxTipoSala, 0, 119, Short.MAX_VALUE)
                                        .addComponent(spnCapacidad))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
                jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel15)
                                        .addComponent(TxtNombreSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel16)
                                        .addComponent(cbxTipoSala, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel17)
                                        .addComponent(spnCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 56, Short.MAX_VALUE))
        );

        jPanel14.setBackground(new java.awt.Color(102, 102, 102));
        jPanel14.setForeground(new java.awt.Color(51, 102, 255));

        jLabel18.setFont(new java.awt.Font("Microsoft Sans Serif", 3, 18)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Botones");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
                jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(48, 48, 48))
        );
        jPanel14Layout.setVerticalGroup(
                jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
        );

        btnAgregarSala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/boton-guardar.png"))); // NOI18N
        btnAgregarSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarSalaActionPerformed(evt);
            }
        });

        btnModificarSala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/actualizar.png"))); // NOI18N
        btnModificarSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarSalaActionPerformed(evt);
            }
        });

        btnEliminarSala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/eliminar.png"))); // NOI18N
        btnEliminarSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarSalaActionPerformed(evt);
            }
        });

        btnLimpiarSala.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/limpiar.png"))); // NOI18N
        btnLimpiarSala.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLimpiarSalaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
                jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnAgregarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnEliminarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnModificarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLimpiarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20))
        );
        jPanel13Layout.setVerticalGroup(
                jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel13Layout.createSequentialGroup()
                                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAgregarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnModificarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btnLimpiarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 35, Short.MAX_VALUE)
                                        .addComponent(btnEliminarSala, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout jpanelLayout = new javax.swing.GroupLayout(jpanel);
        jpanel.setLayout(jpanelLayout);
        jpanelLayout.setHorizontalGroup(
                jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jpanelLayout.createSequentialGroup()
                                .addGroup(jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jpanelLayout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jpanelLayout.createSequentialGroup()
                                                .addGap(58, 58, 58)
                                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(102, 102, 102)
                                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(25, Short.MAX_VALUE))
        );
        jpanelLayout.setVerticalGroup(
                jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jpanelLayout.createSequentialGroup()
                                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jpanelLayout.createSequentialGroup()
                                                .addGap(23, 23, 23)
                                                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(28, Short.MAX_VALUE))
        );

        jPanel3.add(jpanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 770, 500));

        jTabbedPane1.addTab("Salas", jPanel3);

        javax.swing.GroupLayout PanelMesasContenedorLayout = new javax.swing.GroupLayout(PanelMesasContenedor);
        PanelMesasContenedor.setLayout(PanelMesasContenedorLayout);
        PanelMesasContenedorLayout.setHorizontalGroup(
                PanelMesasContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 830, Short.MAX_VALUE)
        );
        PanelMesasContenedorLayout.setVerticalGroup(
                PanelMesasContenedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 515, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Mesas", PanelMesasContenedor);

        jPanel5.setBackground(new java.awt.Color(153, 204, 255));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(204, 204, 204));

        jPanel6.setBackground(new java.awt.Color(102, 102, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Reservar Mesa");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(58, 58, 58)
                                .addComponent(jLabel3)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        txtMesaSeleccionada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMesaSeleccionadaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Yu Gothic UI Semilight", 3, 14)); // NOI18N
        jLabel2.setText("Mesa Seleccionada");

        jLabel4.setBackground(new java.awt.Color(255, 255, 255));
        jLabel4.setFont(new java.awt.Font("Yu Gothic UI Semilight", 3, 14)); // NOI18N
        jLabel4.setText("Cliente");

        jLabel5.setFont(new java.awt.Font("Yu Gothic UI Semilight", 3, 14)); // NOI18N
        jLabel5.setText("Fecha");

        jLabel6.setFont(new java.awt.Font("Yu Gothic UI Semilight", 3, 14)); // NOI18N
        jLabel6.setText("Hora");

        btnreservaciones.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/boton-guardar.png"))); // NOI18N
        btnreservaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnreservacionesActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Yu Gothic UI Semilight", 3, 14)); // NOI18N
        jLabel19.setText("Capacidad");

        btnModificar.setText("Modificar");
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setText("Eliminar");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(27, 27, 27)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(0, 0, Short.MAX_VALUE))))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(35, 35, 35)
                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(spnCapaci, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(txtMesaSeleccionada, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(spinnerHora)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addComponent(txtFecha))
                                .addGap(68, 68, 68))
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(60, 60, 60)
                                                .addComponent(btnreservaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addGap(45, 45, 45)
                                                .addComponent(btnModificar)
                                                .addGap(80, 80, 80)
                                                .addComponent(btnEliminar)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(32, 32, 32)
                                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtMesaSeleccionada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(26, 26, 26)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(22, 22, 22)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel19)
                                        .addComponent(spnCapaci, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(spinnerHora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnreservaciones)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnModificar)
                                        .addComponent(btnEliminar))
                                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 40, 340, 410));

        botonopinion.setText("Satisfaccion del cliente");
        botonopinion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonopinionActionPerformed(evt);
            }
        });
        jPanel5.add(botonopinion, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 280, -1, -1));

        jTabbedPane1.addTab("Reservas", jPanel5);

        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel9.add(lblLugar1, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 90, 60, 110));
        jPanel9.add(lblLugar2, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 60, 110));
        jPanel9.add(lblLugar3, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 80, 60, 110));
        jPanel9.add(lblLugar4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 90, 60, 110));
        jPanel9.add(lblLugar5, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 330, 60, 110));
        jPanel9.add(lblLugar6, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 320, 60, 110));
        jPanel9.add(lblLugar7, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 330, 60, 110));
        jPanel9.add(lblLugar8, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 330, 60, 110));

        jButton9.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("5");
        jButton9.setToolTipText("");
        jButton9.setBorderPainted(false);
        jButton9.setContentAreaFilled(false);
        jPanel9.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 360, 50, 70));

        jButton10.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("6");
        jButton10.setToolTipText("");
        jButton10.setBorderPainted(false);
        jButton10.setContentAreaFilled(false);
        jPanel9.add(jButton10, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 360, 50, 70));

        jButton11.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("7");
        jButton11.setToolTipText("");
        jButton11.setBorderPainted(false);
        jButton11.setContentAreaFilled(false);
        jPanel9.add(jButton11, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 360, 50, 70));

        jButton12.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("8");
        jButton12.setToolTipText("");
        jButton12.setBorderPainted(false);
        jButton12.setContentAreaFilled(false);
        jPanel9.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 360, 50, 70));

        jButton8.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("4");
        jButton8.setToolTipText("");
        jButton8.setBorderPainted(false);
        jButton8.setContentAreaFilled(false);
        jPanel9.add(jButton8, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, 50, 70));

        jButton7.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("2");
        jButton7.setToolTipText("");
        jButton7.setBorderPainted(false);
        jButton7.setContentAreaFilled(false);
        jPanel9.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 100, 50, 70));

        jButton6.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("3");
        jButton6.setToolTipText("");
        jButton6.setBorderPainted(false);
        jButton6.setContentAreaFilled(false);
        jPanel9.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 100, 50, 70));

        jButton5.setFont(new java.awt.Font("Arial", 1, 48)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("1");
        jButton5.setToolTipText("");
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jPanel9.add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 100, 50, 80));

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/estacionamiento.jpg"))); // NOI18N
        jLabel7.setToolTipText("");
        jPanel9.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(142, 34, -1, -1));

        txtCodReserva.setToolTipText("");
        txtCodReserva.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodReservaActionPerformed(evt);
            }
        });

        txtCliente_est.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCliente_estActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("COD. RESERVA");
        jLabel20.setToolTipText("");

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("CLIENTE");
        jLabel21.setToolTipText("");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("N° ESTACIONAMIENTO");
        jLabel22.setToolTipText("");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("REGISTRO");
        jLabel23.setToolTipText("");

        btnReservar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnReservar.setText("Reservar");
        btnReservar.setToolTipText("");

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("Cancelar");
        jButton2.setToolTipText("");

        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setText("Buscar");
        btnCancelar.setToolTipText("");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
                jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField3, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel15Layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtCodReserva, javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(txtCliente_est)
                                                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jTextField4)
                                                        .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                .addContainerGap())
                        .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnReservar, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
                jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel15Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel20)
                                .addGap(8, 8, 8)
                                .addComponent(txtCodReserva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCliente_est, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel23)
                                .addGap(4, 4, 4)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnReservar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnCancelar))
        );

        jPanel9.add(jPanel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 34, -1, 455));

        jTabbedPane1.addTab("Estacionamiento", jPanel9);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
                jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 830, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
                jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 515, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Platos", jPanel16);

        getContentPane().add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 830, 550));

        btnreportes.setFont(new java.awt.Font("Microsoft New Tai Lue", 3, 12)); // NOI18N
        btnreportes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/reportes.png"))); // NOI18N
        btnreportes.setText("Reportes");
        btnreportes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnreportesActionPerformed(evt);
            }
        });
        getContentPane().add(btnreportes, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 570, 150, 40));

        jLabel1.setBackground(new java.awt.Color(51, 51, 255));
        jLabel1.setForeground(new java.awt.Color(51, 51, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/imagen2 (1).png"))); // NOI18N
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, -10, 1280, 730));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btncerrarsesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btncerrarsesionActionPerformed
        this.dispose(); // Cierra frmsistema
        new frmlogin().setVisible(true); // Abre el login nuevamente
    }//GEN-LAST:event_btncerrarsesionActionPerformed

    private void btnabrirsalasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnabrirsalasActionPerformed
        jTabbedPane1.setSelectedIndex(0); // La pestaña "Panel" es el índice 0, donde está PanelSalas
    }//GEN-LAST:event_btnabrirsalasActionPerformed

    private void btnadministradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnadministradorActionPerformed
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_btnadministradorActionPerformed

    private void btnsalasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalasActionPerformed
        jTabbedPane1.setSelectedIndex(2);
    }//GEN-LAST:event_btnsalasActionPerformed

    private void btnhistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnhistorialActionPerformed
        HistorialReservasDialog dialog = new HistorialReservasDialog(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_btnhistorialActionPerformed

    private void btnestacionamientoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnestacionamientoActionPerformed
        // Crea una nueva instancia (objeto) de tu frame Estacionamiento.
        Estacionamiento ventana = new Estacionamiento();

        // Hace visible la ventana de Estacionamiento.
        ventana.setVisible(true);

    }//GEN-LAST:event_btnestacionamientoActionPerformed

    private void btnreservacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreservacionesActionPerformed
        // 1. Validar campos
        String mesa = txtMesaSeleccionada.getText().trim();
        String cliente = txtCliente.getText().trim();
        String fechaStr = txtFecha.getText().trim();


        if (mesa.isEmpty() || cliente.isEmpty() || fechaStr.isEmpty() || salaSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos correctamente.");
            return;

        }

        // 2. Obtener valores básicos
        int capacidad = (Integer) spnCapaci.getValue();
        int horaEntera = (Integer) spinnerHora.getValue();
        java.sql.Time horaSQL = java.sql.Time.valueOf(String.format("%02d:00:00", horaEntera));
        String codigoReserva = generarCodigoReserva();

        // 3. Calcular precio (usando estructura de datos)
        double precioBase = calcularPrecio(capacidad); // <- Solo capacidad
        // 4. Mostrar diálogo de pago (con precio)
        MetodoPagoDialog dialog = new MetodoPagoDialog(this, codigoReserva, precioBase);
        dialog.setVisible(true);

        // 5. Procesar resultado
        String metodoPago = dialog.getMetodoSeleccionado();
        double precioFinal = dialog.getPrecioFinal();
        String estadoPago = determinarEstadoPago(metodoPago);

        if (metodoPago == null || metodoPago.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reserva cancelada. No se eligió método de pago.");
            return;
        }

        // 6. Guardar en BD (con precio)
        Conexion conex = new Conexion();
        try (Connection cn = conex.getConexion();
             PreparedStatement ps = cn.prepareStatement(
                     "INSERT INTO reservas (mesa, cliente, fecha, capacidad, hora, sala, " +
                             "codigo_reserva, metodo_pago, estado_pago, precio) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, mesa);
            ps.setString(2, cliente);
            ps.setString(3, fechaStr);
            ps.setInt(4, capacidad);
            ps.setTime(5, horaSQL);
            ps.setString(6, salaSeleccionada.toLowerCase());
            ps.setString(7, codigoReserva);
            ps.setString(8, metodoPago);
            ps.setString(9, estadoPago);
            ps.setDouble(10, precioFinal);

            int filas = ps.executeUpdate();
            System.out.println("Mesa: " + mesa + " | Cliente: " + cliente + " | Fecha: " + fechaStr + " | Sala: " + salaSeleccionada);
            System.out.println("Sala seleccionada: " + salaSeleccionada);
            if (filas > 0) {
                // Mostrar ticket con precio
                mostrarTicket(mesa, cliente, fechaStr, capacidad, horaEntera,
                        salaSeleccionada, codigoReserva, metodoPago,
                        estadoPago, precioFinal);

                JOptionPane.showMessageDialog(this,
                        "Reserva registrada!\nMétodo: " + metodoPago +
                                "\nTotal: S/ " + String.format("%.2f", precioFinal));

                limpiarCampos();
                txtMesaSeleccionada.setText("");
                txtCliente.setText("");
                txtFecha.setText("");
                spnCapaci.setValue(1);
                spinnerHora.setValue(1);
                salaSeleccionada = "";


            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar la reserva.");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
        }

    }//GEN-LAST:event_btnreservacionesActionPerformed

    private void btnLimpiarSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarSalaActionPerformed
        limpiarCamposSala();
    }//GEN-LAST:event_btnLimpiarSalaActionPerformed

    private void btnEliminarSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarSalaActionPerformed

        eliminarSalaSeleccionada();
    }//GEN-LAST:event_btnEliminarSalaActionPerformed

    private void btnModificarSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarSalaActionPerformed
        modificarSala();
    }//GEN-LAST:event_btnModificarSalaActionPerformed

    private void btnAgregarSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarSalaActionPerformed

        // Obtener el texto y limpiar espacios en blanco
        String nombre = TxtNombreSala.getText().trim();
        System.out.println("Valor del campo nombre: '" + TxtNombreSala.getText() + "'");

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la sala es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tipo = (String) cbxTipoSala.getSelectedItem();
        int capacidad = (int) spnCapacidad.getValue();

        // Crear sala
        Salas nuevaSala = new Salas();
        nuevaSala.setNombre(nombre);
        nuevaSala.setTipo(tipo);
        nuevaSala.setCapacidad(capacidad);

        // Guardar en la base
        SalasDao dao = new SalasDao();
        boolean insertado = dao.insertarSala(nuevaSala);

        if (insertado) {
            JOptionPane.showMessageDialog(this, "Sala registrada exitosamente");
            cargarTablaSalas();
            // Limpiar campos
            TxtNombreSala.setText("");
            cbxTipoSala.setSelectedIndex(0);
            spnCapacidad.setValue(1);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar la sala", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAgregarSalaActionPerformed

    private void cbxTipoSalaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTipoSalaActionPerformed
        if (comboInicializado) {
            String tipoSeleccionado = (String) cbxTipoSala.getSelectedItem();
            if (tipoSeleccionado != null) {
                JOptionPane.showMessageDialog(this, "Tipo seleccionado: " + tipoSeleccionado);
            }
        }
    }//GEN-LAST:event_cbxTipoSalaActionPerformed

    private void btnregistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnregistrarActionPerformed

        String nombre = txtnombre.getText().trim();
        String correo = txtcorroelectronico.getText().trim();
        String password = txtpassswords.getText().trim();
        String telefono = txttelefono.getText().trim();
        String rol = "admin";

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return;
        }
// Validación de contraseña segura
        if (!validarContrasena(password)) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener:\n"
                            + "• Mínimo 8 caracteres\n"
                            + "• Una mayúscula\n"
                            + "• Una minúscula\n"
                            + "• Un número\n"
                            + "• Un carácter especial (@$!%*?&._-)",
                    "Contraseña no válida",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String url = "jdbc:mysql://localhost:3306/baserestaurante";
        String usuarioSQL = "root";      // pon tu usuario MySQL aquí
        String contraseñaSQL = "";       // pon tu contraseña aquí, o deja vacío si no hay

        try (Connection conn = DriverManager.getConnection(url, usuarioSQL, contraseñaSQL)) {
            String verificarCorreo = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
            try (PreparedStatement psVerificar = conn.prepareStatement(verificarCorreo)) {
                psVerificar.setString(1, correo);
                try (ResultSet rs = psVerificar.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        JOptionPane.showMessageDialog(this, "El correo ya está registrado.");
                        return;
                    }
                }
            }

            String sql = "INSERT INTO usuarios (nombre_completo, correo, contrasena, rol, telefono) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, correo);
                ps.setString(3, password); // en producción, hash!
                ps.setString(4, rol);
                ps.setString(5, telefono);

                int filas = ps.executeUpdate();
                if (filas > 0) {
                    JOptionPane.showMessageDialog(this, "Administrador registrado correctamente.");
                    limpiarCampos();
                    cargarTabla();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al registrar el administrador.");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage());
        }

    }//GEN-LAST:event_btnregistrarActionPerformed

    private void btnsalasecundariaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalasecundariaActionPerformed
        salaSeleccionada = "Sala Secundaria";
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_btnsalasecundariaActionPerformed

    private void btnsalaprincipalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnsalaprincipalActionPerformed
        salaSeleccionada = "Sala Principal";
        jTabbedPane1.setSelectedIndex(3);
    }//GEN-LAST:event_btnsalaprincipalActionPerformed

    private void btnplatosActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Definir el índice de la pestaña "Platos"
        final int INDICE_PLATOS = 6;

        // 2. Limpiar el contenido anterior de la pestaña (jPanel16)
        jPanel16.removeAll();

        // 3. Establecer el LayoutManager
        jPanel16.setLayout(new BorderLayout());

        // 4. Crear una instancia del panel de Platos
        conex.MainPlato platosPanel = new conex.MainPlato();

        // 5. Establecer tamaño preferido para forzar redibujo
        platosPanel.setPreferredSize(new Dimension(830, 550));

        // 6. Añadir el nuevo panel al contenedor de la pestaña
        jPanel16.add(platosPanel, BorderLayout.CENTER);

        // 7. Forzar la validación y el repintado de la interfaz.
        jPanel16.revalidate();
        jPanel16.repaint();

        // 8. Cambiar a la pestaña "Platos"
        jTabbedPane1.setSelectedIndex(INDICE_PLATOS);
    } // FIN del btnplatosActionPerformed

    private void txtCodReservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodReservaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodReservaActionPerformed

    private void txtCliente_estActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCliente_estActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCliente_estActionPerformed

    private void btndeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btndeliveryActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);

        // Crear una nueva instancia del formulario de Delivery, pasándole la referencia
        Delivery.frmDelivery formDelivery = new Delivery.frmDelivery(this, usuarioLogueado); // <-- Cambio clave aquí

        // Opcional: Centrar el formulario en la pantalla
        formDelivery.setLocationRelativeTo(null);

        // Hacer visible el formulario
        formDelivery.setVisible(true);
    }//GEN-LAST:event_btndeliveryActionPerformed

    private void btnreportesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnreportesActionPerformed
        new reportesreservas.FrmReportesReservasPanel().setVisible(true);
        // TODO add your handling code here:
    }//GEN-LAST:event_btnreportesActionPerformed

    private void txtMesaSeleccionadaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMesaSeleccionadaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMesaSeleccionadaActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        btnModificar.addActionListener(e -> {
            // Abre la ventana emergente de modificación
            new VentanaModificarReserva(this);
        });
// TODO add your handling code here:
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        btnEliminar.addActionListener(e -> new VentanaEliminarReserva(this));        // TODO add your handling code here:
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void txtpassswordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtpassswordsActionPerformed
        String contrasena = new String(txtpassswords.getPassword());

        if (!validarContrasena(contrasena)) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener mínimo:\n" +
                            "- 8 caracteres\n" +
                            "- Una letra mayúscula\n" +
                            "- Una letra minúscula\n" +
                            "- Un número\n" +
                            "- Un carácter especial (@$!%*?&._-)",
                    "Contraseña inválida",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        JOptionPane.showMessageDialog(this, "Contraseña válida");  // TODO add your handling code here:
    }//GEN-LAST:event_txtpassswordsActionPerformed

    private void botonopinionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonopinionActionPerformed
        // Panel principal para el JOptionPane
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1, 10, 10));

        // Título
        JLabel titulo = new JLabel("Satisfacción del cliente", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titulo);

        // Botones de opción
        JRadioButton rbSatisfecho = new JRadioButton("Satisfecho");
        JRadioButton rbNoSatisfecho = new JRadioButton("No satisfecho");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbSatisfecho);
        grupo.add(rbNoSatisfecho);

        panel.add(rbSatisfecho);
        panel.add(rbNoSatisfecho);

        // Mostrar ventana de diálogo
        int opcion = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Opinión del cliente",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        boolean clienteSatisfecho = false;

        // --- Lógica de Guardado ---
        if (opcion == JOptionPane.OK_OPTION) {

            // 1. Determinar el estado seleccionado
            if (rbSatisfecho.isSelected()) {
                clienteSatisfecho = true;
                JOptionPane.showMessageDialog(this, "Registrado: Cliente satisfecho");
            } else if (rbNoSatisfecho.isSelected()) {
                clienteSatisfecho = false;
                JOptionPane.showMessageDialog(this, "Registrado: Cliente NO está satisfecho");
            } else {
                // El usuario presionó OK sin seleccionar nada.
                JOptionPane.showMessageDialog(this, "Debes seleccionar una opción.");
                return; // Salir del método
            }

            // 2. Definir los valores para la base de datos

            // *** LÍNEA CLAVE MODIFICADA ***
            // Nueva lógica: 1 = No Satisfecho, 0 = Satisfecho
            int estado = clienteSatisfecho ? 0 : 1;

            // 3. Obtener la fecha actual y convertirla a java.sql.Date
            java.sql.Date fechaActualSQL = new java.sql.Date(new Date().getTime());

            // 4. Consulta SQL: Incluye 'estado' y 'fechaopinion'
            String sql = "INSERT INTO opiniones (estado, fechaopinion) VALUES (?, ?)";

            try (Connection con = conex.Conexion.getConexion();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                // Parámetro 1: Estado (0 o 1)
                ps.setInt(1, estado);

                // Parámetro 2: Fecha
                ps.setDate(2, fechaActualSQL);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Opinión guardada correctamente.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar opinión: " + ex.getMessage());
            }
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_botonopinionActionPerformed

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
            java.util.logging.Logger.getLogger(frmsistema.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmsistema.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmsistema.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmsistema.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new frmsistema().setVisible(true);
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelMesasContenedor;
    private javax.swing.JPanel PanelSalas;
    private javax.swing.JTextField TxtNombreSala;
    private javax.swing.JButton botonopinion;
    private javax.swing.JButton btnAgregarSala;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarSala;
    private javax.swing.JButton btnLimpiarSala;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnModificarSala;
    // 💡 NUEVOS BOTONES DE ESTADO (Añadidos aquí para que NetBeans los use)
    private javax.swing.JButton btnActivarUsuario;
    private javax.swing.JButton btnDesactivarUsuario;
    // FIN BOTONES DE ESTADO
    private javax.swing.JButton btnReservar;
    private javax.swing.JButton btnabrirsalas;
    private javax.swing.JButton btnadministrador;
    private javax.swing.JButton btncerrarsesion;
    private javax.swing.JButton btndelivery;
    private javax.swing.JButton btnestacionamiento;
    private javax.swing.JButton btnhistorial;
    private javax.swing.JButton btnplatos;
    private javax.swing.JButton btnregistrar;
    private javax.swing.JButton btnreportes;
    private javax.swing.JButton btnreservaciones;
    private javax.swing.JButton btnsalaprincipal;
    private javax.swing.JButton btnsalas;
    private javax.swing.JButton btnsalasecundaria;
    private javax.swing.JComboBox<String> cbxTipoSala;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JPanel jpanel;
    private javax.swing.JLabel lblLugar1;
    private javax.swing.JLabel lblLugar2;
    private javax.swing.JLabel lblLugar3;
    private javax.swing.JLabel lblLugar4;
    private javax.swing.JLabel lblLugar5;
    private javax.swing.JLabel lblLugar6;
    private javax.swing.JLabel lblLugar7;
    private javax.swing.JLabel lblLugar8;
    private javax.swing.JSpinner spinnerHora;
    private javax.swing.JSpinner spnCapaci;
    private javax.swing.JSpinner spnCapacidad;
    private javax.swing.JTable tablaUsuarios;
    private javax.swing.JTable tablep;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtCliente_est;
    private javax.swing.JTextField txtCodReserva;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtMesaSeleccionada;
    private javax.swing.JTextField txtcorroelectronico;
    private javax.swing.JTextField txtnombre;
    private javax.swing.JPasswordField txtpassswords;
    private javax.swing.JTextField txttelefono;
    // End of variables declaration//GEN-END:variables
}