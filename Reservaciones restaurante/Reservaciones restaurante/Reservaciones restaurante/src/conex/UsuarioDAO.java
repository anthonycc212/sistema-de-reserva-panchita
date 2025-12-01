package conex;

import java.sql.*;
import java.util.*;
import javax.swing.JOptionPane;

public class UsuarioDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean registrar(Usuario u) {
        // Asegúrate de que aquí no estés intentando registrar la columna 'estado' si no la manejas en el formulario
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
        // Nota: Si usas 'id' como PK, busca el ID en lugar de 'id_usuario'
        String sql = "SELECT id, nombre_completo, correo, contrasena, rol, telefono FROM usuarios WHERE correo = ?";
        Usuario u = null;
        try {
            con = Conexion.getConexion();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) {
                u = new Usuario();
                u.setId(rs.getInt("id")); // Asegúrate de que esta columna es 'id'
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
        // Esto sigue siendo eliminación FÍSICA
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

    public List<Usuario> listar() {
        // Idealmente, aquí solo listarías usuarios ACTIVO (estado = 1) si fuera necesario.
        String sql = "SELECT * FROM usuarios";
        List<Usuario> lista = new ArrayList<>();
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

    // 💡 MÉTODO NUEVO: Desactivación (Eliminación Lógica, estado = 0)
    public boolean desactivarUsuario(int idUsuario) {
        // Usamos 'estado = 0' y 'id' como PK
        String sql = "UPDATE usuarios SET estado = 0 WHERE id = ?";

        try (Connection con = new conex.Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar el usuario: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // 💡 MÉTODO NUEVO: Activación (estado = 1)
    public boolean activarUsuario(int idUsuario) {
        // Usamos 'estado = 1' y 'id' como PK
        String sql = "UPDATE usuarios SET estado = 1 WHERE id = ?";

        try (Connection con = new conex.Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();

            return filasAfectadas > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al activar el usuario: " + e.getMessage(), "Error de BD", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}