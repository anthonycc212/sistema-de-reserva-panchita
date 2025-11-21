package conex;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ReservaDAO {

    public boolean actualizarMetodoPago(String codigoReserva, String metodoPago, String estadoPago) {
        String sql = "UPDATE reservas SET metodo_pago = ?, estado_pago = ? WHERE codigo_reserva = ?";
        try (Connection cn = Conexion.getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, metodoPago);
            ps.setString(2, estadoPago);
            ps.setString(3, codigoReserva);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar método de pago: " + e.getMessage());
            return false;
        }
    }
}
/**
 *
 * @author USER
 *//**
 *
 * @author USER
 */