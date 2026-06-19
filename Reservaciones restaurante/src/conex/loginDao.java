package conex;

import java.sql.*;

public class loginDao {

    // Método para validar login por nombre completo y contraseña (usado por loginclientes.java)
    public login log(String nombreCompleto, String contrasena) {
        login usuario = null;
        String sql = "SELECT * FROM usuarios WHERE nombre_completo = ? AND contrasena = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombreCompleto);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new login();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setTelefono(rs.getString("telefono")); // <-- LÍNEA AÑADIDA
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    // Método para registrar nuevos clientes
    public boolean Registrar(login lg) {
        String sql = "INSERT INTO usuarios (correo, telefono, contrasena, nombre_completo, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, lg.getCorreo());
            ps.setString(2, lg.getTelefono());
            ps.setString(3, lg.getContrasena());
            ps.setString(4, lg.getNombreCompleto());
            ps.setString(5, lg.getRol());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Nuevo método para validar login por correo y contraseña (usado por frmlogin.java)
    public login logPorCorreo(String correo, String contrasena) {
        login usuario = null;
        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    usuario = new login();
                    usuario.setId(rs.getInt("id"));
                    usuario.setCorreo(rs.getString("correo"));
                    usuario.setContrasena(rs.getString("contrasena"));
                    usuario.setNombreCompleto(rs.getString("nombre_completo"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setTelefono(rs.getString("telefono")); // <-- LÍNEA AÑADIDA
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuario;
    }
}