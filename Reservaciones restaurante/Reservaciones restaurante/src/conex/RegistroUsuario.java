/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

public class RegistroUsuario extends JFrame {

    private JTextField txtNombre, txtCorreo, txtContrasena, txtTelefono;
    private JTable tablaUsuarios;
    private DefaultTableModel modelo;
    private UsuarioDAO dao = new UsuarioDAO();
    

    public RegistroUsuario() {
        setTitle("Registro de Usuarios");
        setSize(750, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblTitulo = new JLabel("Registro de Usuarios");
        lblTitulo.setBounds(280, 10, 200, 30);
        add(lblTitulo);

        JLabel lblNombre = new JLabel("Nombre completo:");
        lblNombre.setBounds(50, 60, 120, 25);
        add(lblNombre);

        txtNombre = new JTextField();
        txtNombre.setBounds(180, 60, 200, 25);
        add(txtNombre);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(50, 100, 120, 25);
        add(lblCorreo);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(180, 100, 200, 25);
        add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 140, 120, 25);
        add(lblContrasena);

        txtContrasena = new JTextField();
        txtContrasena.setBounds(180, 140, 200, 25);
        add(txtContrasena);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(50, 180, 120, 25);
        add(lblTelefono);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(180, 180, 200, 25);
        add(txtTelefono);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(50, 220, 120, 25);
        add(lblRol);

        JTextField txtRol = new JTextField("cliente");
        txtRol.setEditable(false);
        txtRol.setBounds(180, 220, 200, 25);
        add(txtRol);

        // ----- Botones -----
        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(420, 60, 120, 30);
        add(btnRegistrar);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBounds(420, 100, 120, 30);
        add(btnBuscar);

        JButton btnModificar = new JButton("Modificar");
        btnModificar.setBounds(420, 140, 120, 30);
        add(btnModificar);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(420, 180, 120, 30);
        add(btnEliminar);

        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(420, 220, 120, 30);
        add(btnLimpiar);

        // Tabla 
        String[] columnas = {"ID", "Nombre", "Correo", "Rol", "Teléfono"};
        modelo = new DefaultTableModel(columnas, 0);
        tablaUsuarios = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tablaUsuarios);
        scroll.setBounds(50, 280, 640, 200);
        add(scroll);

        listarUsuarios(); // Muestra todos los usuarios al iniciar

        //  Acciones 
        btnRegistrar.addActionListener(e -> {
            Usuario u = new Usuario();
            u.setNombre_completo(txtNombre.getText());
            u.setCorreo(txtCorreo.getText());
            u.setContrasena(txtContrasena.getText());
            u.setTelefono(txtTelefono.getText());
            u.setRol(txtRol.getText());
String contrasena = txtContrasena.getText();

if (!validarContrasena(contrasena)) {
    JOptionPane.showMessageDialog(this,
        "La contraseña debe tener al menos 8 caracteres, incluir una mayúscula, " +
        "una minúscula, un número y un carácter especial (@$!%*?&._-).",
        "Contraseña inválida",
        JOptionPane.WARNING_MESSAGE);
    return; // Detiene el registro
}
            if (dao.registrar(u)) {
                JOptionPane.showMessageDialog(this, "Usuario registrado correctamente");
                listarUsuarios();
                limpiarCampos();
            }
        });

        btnBuscar.addActionListener(e -> {
            String correo = txtCorreo.getText();
            if (correo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese un correo para buscar");
                return;
            }

            Usuario u = dao.buscarPorCorreo(correo);
            if (u != null) {
                txtNombre.setText(u.getNombre_completo());
                txtContrasena.setText(u.getContrasena());
                txtTelefono.setText(u.getTelefono());
                txtRol.setText(u.getRol());
            } else {
                JOptionPane.showMessageDialog(this, "Usuario no encontrado");
            }
        });

        btnModificar.addActionListener(e -> {
            String correo = txtCorreo.getText();
            Usuario u = dao.buscarPorCorreo(correo);
            if (u == null) {
                JOptionPane.showMessageDialog(this, "No se encontró el usuario con ese correo");
                return;
            }

            u.setNombre_completo(txtNombre.getText());
            u.setContrasena(txtContrasena.getText());
            u.setTelefono(txtTelefono.getText());
            u.setRol(txtRol.getText());

            if (dao.modificar(u)) {
                JOptionPane.showMessageDialog(this, "Usuario modificado correctamente");
                listarUsuarios();
                limpiarCampos();
            }
        });

        btnEliminar.addActionListener(e -> {
            String correo = txtCorreo.getText();
            if (correo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el correo del usuario a eliminar");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea eliminar este usuario?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.eliminar(correo)) {
                    JOptionPane.showMessageDialog(this, "Usuario eliminado correctamente");
                    listarUsuarios();
                    limpiarCampos();
                }
            }
        });

        btnLimpiar.addActionListener(e -> limpiarCampos());
    }

    //  Métodos auxiliares lista
    private void listarUsuarios() {
        modelo.setRowCount(0);
        List<Usuario> lista = dao.listar();
        for (Usuario u : lista) {
            modelo.addRow(new Object[]{
                u.getId(),
                u.getNombre_completo(),
                u.getCorreo(),
                u.getRol(),
                u.getTelefono()
            });
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCorreo.setText("");
        txtContrasena.setText("");
        txtTelefono.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroUsuario().setVisible(true));
    }
    private boolean validarContrasena(String contrasena) {
    String patron = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,}$";
    return contrasena.matches(patron);
}
}