/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;
 import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class UsuarioDAO {
    
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean registrar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre_completo, correo, contrasena, rol, telefono) VALUES (?, ?, ?, ?, ?)";
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre_completo());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getContrasena());
            ps.setString(4, u.getRol());
            ps.setString(5, u.getTelefono());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";
        Usuario u = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre_completo(rs.getString("nombre_completo"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                u.setTelefono(rs.getString("telefono"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar usuario: " + e.getMessage());
        }
        return u;
    }

    public boolean modificar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre_completo=?, contrasena=?, rol=?, telefono=? WHERE correo=?";
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre_completo());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getCorreo());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al modificar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(String correo) {
        String sql = "DELETE FROM usuarios WHERE correo=?";
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }

    // ✅ Método que faltaba
    public List<Usuario> listar() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombre_completo(rs.getString("nombre_completo"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                u.setTelefono(rs.getString("telefono"));
                lista.add(u);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }
}