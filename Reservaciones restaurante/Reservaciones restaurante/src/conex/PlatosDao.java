/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;

/**
 *
 * @author antho
 */
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class PlatosDao {

    private List<Platos> lista = new LinkedList<>();

    // Método 1: Agregar a la lista en memoria
    public void agregarPlato(Platos p) {
        lista.add(p);
    }

    // Método 2: Obtener todos los platos en memoria
    public List<Platos> listarPlatos() {
        return lista;
    }

    // Método 3: Eliminar por índice de la lista
    public void eliminarPlato(int index) {
        if (index >= 0 && index < lista.size()) {
            lista.remove(index);
        }
    }

    // 🟢 MÉTODO 4: Guardar todos los platos de la lista en la base de datos
    public void guardarEnBD(List<Platos> lista) {
        String sql = "INSERT INTO platos (plato, descripcion, precio, tipo) VALUES (?, ?, ?, ?)";
        try (Connection con = new Conexion().getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (Platos p : lista) {
                ps.setString(1, p.getNombre());
                ps.setString(2, p.getDescripcion());
                ps.setDouble(3, p.getPrecio());
                ps.setString(4, "General");
                ps.addBatch();
            }
            ps.executeBatch();
            System.out.println("Platos guardados en la BD.");
        } catch (SQLException e) {
            System.out.println("Error al guardar en BD: " + e.getMessage());
        }
    }


    public List<Platos> cargarDesdeBD() {
        List<Platos> platos = new LinkedList<>();
        String sql = "SELECT * FROM platos";
        try (Connection con = new Conexion().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Platos p = new Platos();
                p.setNombre(rs.getString("plato"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getDouble("precio"));
                platos.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Error al cargar desde BD: " + e.getMessage());
        }
        return platos;
    }
}