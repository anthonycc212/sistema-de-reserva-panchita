package conex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * Gestiona la conexión a la base de datos MySQL.
 */
public class Conexion {

    /**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */
    public static Connection getConexion() {
        // Datos de la base de datos.
        String dbName = "baserestaurante"; 
        String dbUser = "root"; 
        String dbPassword = ""; 
        String url = "jdbc:mysql://localhost:3306/" + dbName;
        
        Connection con = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la Base de Datos: " + e.getMessage());
        }
       
        return con;
    }
}
/**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */
/**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */
/**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */
/**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */
/**
     * Método estático para obtener la conexión a la base de datos.
     * @return Objeto Connection o null si falla.
     */