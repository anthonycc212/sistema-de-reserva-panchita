package conex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalasDao {
    Connection con;

    public SalasDao() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/baserestaurante", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Insertar una sala
    public boolean insertarSala(Salas sala) {
        String sql = "INSERT INTO salas (nombre, tipo, capacidad) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sala.getNombre());
            ps.setString(2, sala.getTipo());
            ps.setInt(3, sala.getCapacidad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean registrarSala(Salas sala) {
        return insertarSala(sala);
    }

    // Listar todas las salas
    public List<Salas> listarSalas() {
        List<Salas> lista = new ArrayList<>();
        String sql = "SELECT * FROM salas";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Salas s = new Salas();
                s.setId(rs.getInt("id"));
                s.setNombre(rs.getString("nombre"));
                s.setTipo(rs.getString("tipo"));
                s.setCapacidad(rs.getInt("capacidad"));
                lista.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Modificar una sala
    public boolean modificarSala(Salas sala) {
        String sql = "UPDATE salas SET nombre = ?, tipo = ?, capacidad = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sala.getNombre());
            ps.setString(2, sala.getTipo());
            ps.setInt(3, sala.getCapacidad());
            ps.setInt(4, sala.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una sala
    public boolean eliminarSala(int id) {
        String sql = "DELETE FROM salas WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
/**
 *
 * @author USER
 */