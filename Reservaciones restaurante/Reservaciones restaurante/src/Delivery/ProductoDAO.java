package Delivery;

import conex.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ProductoDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    //// LISTAS DINAMICAS PARA CARGAR PRODUCTOS/////////
    /////////////////////////////
    public List<Producto> listarProductos() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT id, tipo, nombre, descripcion, precio FROM productos";
        try {
            con = Conexion.getConexion();
            if (con == null) {
                System.err.println("Error: No se pudo establecer la conexión a la base de datos en ProductoDAO.");
                return listaProductos; 
            }
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setId(rs.getInt("id"));
                prod.setTipo(rs.getString("tipo"));
                prod.setNombre(rs.getString("nombre"));
                prod.setDescripcion(rs.getString("descripcion"));
                prod.setPrecio(rs.getBigDecimal("precio"));
                listaProductos.add(prod);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null); 
             
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return listaProductos;
    }
}