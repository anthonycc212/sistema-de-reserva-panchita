/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;
import javax.swing.JOptionPane;
/**
 *
 * @author antho
 */
public class SeedUsers {
    public static void main(String[] args) {
 
 UsuarioDAO dao = new UsuarioDAO();

 //--- Crear usuario Administrador--
Usuario admin = new Usuario();
 admin.setNombre_completo("Admin de Prueba");
 admin.setCorreo("admin.seed@restaurante.com");
 admin.setContrasena("Admin123*"); // Requiere hashing en producci n!
 admin.setRol("admin");
 admin.setTelefono("999888777");
 //--- Crear usuario Cliente--
Usuario cliente = new Usuario();
 cliente.setNombre_completo("Cliente de Prueba");
 cliente.setCorreo("cliente.seed@restaurante.com");
 cliente.setContrasena("Cliente456-"); // Requiere hashing en

 cliente.setRol("cliente");
 cliente.setTelefono("911222333");
 System.out.println("Intentando registrar usuarios de prueba...");
 // Evitar duplicados si el correo ya existe
 if (dao.buscarPorCorreo(admin.getCorreo()) == null) {
 dao.registrar(admin);
 System.out.println("-> Usuario ’admin’ registrado con xito .");
 } else {
 System.out.println("-> El usuario ’admin’ ya existe.");
 }
 if (dao.buscarPorCorreo(cliente.getCorreo()) == null) {
 dao.registrar(cliente);
 System.out.println("-> Usuario ’cliente’ registrado con xito .");
 } else {
 System.out.println("-> El usuario ’cliente’ ya existe.");
 }
 JOptionPane.showMessageDialog(null, "Proceso finalizado.", "SeedUsers",
 1);
 } 
}
