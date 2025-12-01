/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;
import conex.Conexion; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author antho
 */
public class ReporteIngresosDetalle {

    // Clase interna que guarda cada registro (fecha + precio)
    public static class Ingreso {
        public LocalDate fecha;
        public double precio;

        public Ingreso(LocalDate fecha, double precio) {
            this.fecha = fecha;
            this.precio = precio;
        }
    }

    // Resultado final (lista + suma total)
    public static class ResultadoIngresos {
        public final List<Ingreso> ingresos;
        public final double sumaTotal;

        public ResultadoIngresos(List<Ingreso> ingresos, double sumaTotal) {
            this.ingresos = ingresos;
            this.sumaTotal = sumaTotal;
        }
    }

    public static ResultadoIngresos obtenerIngresosSemanales() {

        List<Ingreso> listaIngresos = new ArrayList<>();
        double suma = 0.0;

        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(6);

        String sql = "SELECT fecha, Precio FROM reservas WHERE fecha BETWEEN ? AND ? ORDER BY fecha ASC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, inicio.toString());
            ps.setString(2, hoy.toString());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate fecha = rs.getDate("fecha").toLocalDate();
                    double precio = rs.getDouble("Precio");

                    listaIngresos.add(new Ingreso(fecha, precio));
                    suma += precio;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error obtenerIngresosSemanales: " + e.getMessage());
        }

        return new ResultadoIngresos(listaIngresos, suma);
    }
    
}
