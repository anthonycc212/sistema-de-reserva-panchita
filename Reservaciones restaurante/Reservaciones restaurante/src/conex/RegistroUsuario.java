/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;
 import javax.swing.*;
/**
 *
 * @author antho
 */

   

public class RegistroUsuario extends JFrame {

    private JTextField txtNombre, txtCorreo, txtRol, txtTelefono;
    private JPasswordField txtContrasena;
    private JButton btnRegistrar;

    public RegistroUsuario() {
        setTitle("Registro de Usuarios");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel lblNombre = new JLabel("Nombre Completo:");
        lblNombre.setBounds(30, 30, 120, 25);
        add(lblNombre);
        txtNombre = new JTextField();
        txtNombre.setBounds(160, 30, 180, 25);
        add(txtNombre);

        JLabel lblCorreo = new JLabel("Correo:");
        lblCorreo.setBounds(30, 70, 120, 25);
        add(lblCorreo);
        txtCorreo = new JTextField();
        txtCorreo.setBounds(160, 70, 180, 25);
        add(txtCorreo);

        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(30, 110, 120, 25);
        add(lblContrasena);
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(160, 110, 180, 25);
        add(txtContrasena);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(30, 150, 120, 25);
        add(lblRol);
        txtRol = new JTextField();
        txtRol.setBounds(160, 150, 180, 25);
        add(txtRol);

        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setBounds(30, 190, 120, 25);
        add(lblTelefono);
        txtTelefono = new JTextField();
        txtTelefono.setBounds(160, 190, 180, 25);
        add(txtTelefono);

        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBounds(130, 240, 120, 30);
        add(btnRegistrar);

        btnRegistrar.addActionListener(e -> registrarUsuario());
    }

    private void registrarUsuario() {
        String nombre = txtNombre.getText();
        String correo = txtCorreo.getText();
        String contrasena = new String(txtContrasena.getPassword());
        String rol = txtRol.getText();
        String telefono = txtTelefono.getText();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || rol.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios ⚠️");
            return;
        }

        Usuario u = new Usuario(nombre, correo, contrasena, rol, telefono);
        UsuarioDAO dao = new UsuarioDAO();
        dao.registrarUsuario(u);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistroUsuario().setVisible(true));
    }
}
