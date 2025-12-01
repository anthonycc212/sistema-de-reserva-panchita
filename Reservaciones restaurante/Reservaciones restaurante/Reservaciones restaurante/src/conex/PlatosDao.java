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

    // Constructor vacío
    public PlatosDao() {
    }

    // Método 1: Agregar a la lista en memoria
    public void agregarPlato(Platos p) {
        lista.add(p);
    }

    // Método 2: Obtener todos los platos en memoria
    public List<Platos> listarPlatos() {
        return lista;
    }

    // Método 3: Eliminar por índice de la lista en memoria
    public void eliminarPlato(int index) {
        if (index >= 0 && index < lista.size()) {
            lista.remove(index);
        }
    }

    // Método Adicional: Limpiar la lista en memoria
    public void clearPlatos() {
        lista.clear();
    }

    // 🟢 MÉTODO 4: Guardar todos los platos de la lista en la base de datos
    // NOTA: Este método está diseñado para inserción masiva. Si el plato ya existe en BD,
    // esto generará duplicados a menos que la tabla 'platos' tenga una restricción UNIQUE.
    public void guardarEnBD(List<Platos> lista) {
        // Asumiendo que 'tipo' siempre es "General" y que la tabla tiene 4 columnas: id, plato, descripcion, precio, tipo
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

    // 🟡 MÉTODO 5: Cargar platos desde la base de datos
    // 💡 IMPORTANTE: Ahora cargamos el ID para futuras operaciones de modificación/eliminación.
    public List<Platos> cargarDesdeBD() {
        List<Platos> platos = new LinkedList<>();
        // 💡 Seleccionamos el ID del plato
        String sql = "SELECT id, plato, descripcion, precio FROM platos";
        try (Connection con = new Conexion().getConexion();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Platos p = new Platos();
                // 💡 Asignamos el ID
                p.setId(rs.getInt("id"));
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