/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Delivery; 

import conex.Conexion; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.List;


public class PedidoDAO {

    Conexion cn = new Conexion(); 

 
    public String previsualizarSiguienteNumeroPedido() {
        String idContador = "PEDIDO_DELIVERY"; 
        String seriePreview = "A";
        int numeroPreview = 0; 
        String numeroPedidoFormateado = null;
        Connection connLocal = null; 
        PreparedStatement psLocal = null;
        ResultSet rsLocal = null;

        try {
            connLocal = Conexion.getConexion(); 
            if (connLocal == null) {
                System.err.println("PedidoDAO (Preview): No se pudo obtener conexión para previsualizar número de pedido.");
                return null;
            }
           
            String sqlSelect = "SELECT serie_actual, numero_actual FROM contadores_pedidos WHERE id_contador = ?";
            psLocal = connLocal.prepareStatement(sqlSelect);
            psLocal.setString(1, idContador);
            rsLocal = psLocal.executeQuery();

            if (rsLocal.next()) {
                seriePreview = rsLocal.getString("serie_actual");
                numeroPreview = rsLocal.getInt("numero_actual");
            } else {
                
                System.out.println("PedidoDAO (Preview): Contador no encontrado para " + idContador + ". Previsualizando inicio de secuencia.");
                seriePreview = "A";
                numeroPreview = 0;
            }
        
            int siguienteNumero = numeroPreview + 1;
            String siguienteSerie = seriePreview;

            if (siguienteNumero > 999) { 
                siguienteNumero = 1; 
                char letra = siguienteSerie.charAt(0);
                if (letra < 'Z') {
                    letra++;
                    siguienteSerie = String.valueOf(letra);
                } else {
                   
                    siguienteSerie = "A"; 
                }
            }
            numeroPedidoFormateado = String.format("%s%03d", siguienteSerie, siguienteNumero);

        } catch (SQLException e) {
            System.err.println("PedidoDAO (Preview): Error SQL al previsualizar número de pedido: " + e.getMessage());
            e.printStackTrace();
            return null; 
        } finally {
            try {
                if (rsLocal != null) rsLocal.close();
                if (psLocal != null) psLocal.close();
                if (connLocal != null) {
                    connLocal.close(); 
                }
            } catch (SQLException e) {
                System.err.println("PedidoDAO (Preview): Error al cerrar recursos de previsualización: " + e.getMessage());
            }
        }
        return numeroPedidoFormateado;
    }


    public synchronized String obtenerYConsumirSiguienteNumeroPedido() { 
        String idContador = "PEDIDO_DELIVERY";
        String serieActual = "A";
        int numeroActual = 0;
        String numeroPedidoFormateado = null;
        Connection connLocal = null; 
        PreparedStatement psLocalSelect = null;
        PreparedStatement psLocalUpdate = null;
        PreparedStatement psLocalInsert = null;
        ResultSet rsLocal = null;

        try {
            connLocal = Conexion.getConexion(); 
            if (connLocal == null) {
                System.err.println("PedidoDAO (Consume): No se pudo obtener conexión para generar número de pedido.");
                return null;
            }
            connLocal.setAutoCommit(false);

            String sqlSelect = "SELECT serie_actual, numero_actual FROM contadores_pedidos WHERE id_contador = ? FOR UPDATE";
            psLocalSelect = connLocal.prepareStatement(sqlSelect);
            psLocalSelect.setString(1, idContador);
            rsLocal = psLocalSelect.executeQuery();

            if (rsLocal.next()) {
                serieActual = rsLocal.getString("serie_actual");
                numeroActual = rsLocal.getInt("numero_actual");
            } else {

                System.out.println("PedidoDAO (Consume): Contador no encontrado para " + idContador + ". Inicializando...");
                String sqlInsertContador = "INSERT INTO contadores_pedidos (id_contador, serie_actual, numero_actual) VALUES (?, ?, ?)";
                psLocalInsert = connLocal.prepareStatement(sqlInsertContador);
                psLocalInsert.setString(1, idContador);
                psLocalInsert.setString(2, "A");
                psLocalInsert.setInt(3, 0); 
                psLocalInsert.executeUpdate();
               
                serieActual = "A";
                numeroActual = 0; 
            }
        
            numeroActual++;
            if (numeroActual > 999) { 
                numeroActual = 1; 
                char letra = serieActual.charAt(0);
                if (letra < 'Z') {
                    letra++;
                    serieActual = String.valueOf(letra);
                } else {
                    System.err.println("PedidoDAO (Consume): Se ha alcanzado el límite de la serie 'Z'. Considerar actualizar la lógica de series.");
            
                    serieActual = "A"; 
                }
            }

            String sqlUpdate = "UPDATE contadores_pedidos SET serie_actual = ?, numero_actual = ? WHERE id_contador = ?";
            psLocalUpdate = connLocal.prepareStatement(sqlUpdate);
            psLocalUpdate.setString(1, serieActual);
            psLocalUpdate.setInt(2, numeroActual);
            psLocalUpdate.setString(3, idContador);
            psLocalUpdate.executeUpdate();

            connLocal.commit(); 
            
            numeroPedidoFormateado = String.format("%s%03d", serieActual, numeroActual);

        } catch (SQLException e) {
            System.err.println("PedidoDAO (Consume): Error SQL al obtener/actualizar número de pedido: " + e.getMessage());
            e.printStackTrace();
            if (connLocal != null) {
                try {
                    connLocal.rollback();
                    System.err.println("PedidoDAO (Consume): Transacción de número de pedido revertida.");
                } catch (SQLException ex) {
                    System.err.println("PedidoDAO (Consume): Error al revertir transacción de número de pedido: " + ex.getMessage());
                }
            }
            return null; 
        } finally {
            try {
                if (rsLocal != null) rsLocal.close();
                if (psLocalSelect != null) psLocalSelect.close();
                if (psLocalUpdate != null) psLocalUpdate.close();
                if (psLocalInsert != null) psLocalInsert.close();
                if (connLocal != null) {
                    connLocal.setAutoCommit(true);
                    connLocal.close(); 
                }
            } catch (SQLException e) {
                System.err.println("PedidoDAO (Consume): Error al cerrar recursos de número de pedido: " + e.getMessage());
            }
        }
        return numeroPedidoFormateado;
    }

    public boolean guardarPedidoCompleto(Pedido pedido, List<DetallePedido> detalles) {
        String sqlPedido = "INSERT INTO pedidos (numero_pedido, nombre_cliente, dni_cliente, telefono_cliente, correo_cliente, " +
                           "direccion_envio, distrito_envio, ciudad_envio, referencia_envio, telefono_entrega, " +
                           "costo_envio, subtotal_pedido, total_pedido, metodo_pago, estado_pedido, fecha_pedido) " +
                           "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        String sqlDetalle = "INSERT INTO detalle_pedidos (id_pedido, id_producto, nombre_producto, cantidad, " +
                            "precio_unitario, subtotal_item) VALUES (?,?,?,?,?,?)";
        boolean guardadoExitoso = false;
        Connection connGuardado = null; 
        PreparedStatement psPedido = null;
        ResultSet rsPedido = null; 

        try {
            connGuardado = Conexion.getConexion(); 
            if (connGuardado == null) {
                System.err.println("PedidoDAO: No se pudo obtener conexión a la BD para guardar pedido.");
                return false; 
            }
            
            connGuardado.setAutoCommit(false);

            psPedido = connGuardado.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
            
            psPedido.setString(1, pedido.getNumeroPedido()); 
            psPedido.setString(2, pedido.getNombreCliente());
            psPedido.setString(3, pedido.getDniCliente());
            psPedido.setString(4, pedido.getTelefonoCliente());
            psPedido.setString(5, pedido.getCorreoCliente());
            psPedido.setString(6, pedido.getDireccionEnvio());
            psPedido.setString(7, pedido.getDistritoEnvio());
            psPedido.setString(8, pedido.getCiudadEnvio());
            psPedido.setString(9, pedido.getReferenciaEnvio());
            psPedido.setString(10, pedido.getTelefonoEntrega());
            psPedido.setBigDecimal(11, pedido.getCostoEnvio());
            psPedido.setBigDecimal(12, pedido.getSubtotalPedido());
            psPedido.setBigDecimal(13, pedido.getTotalPedido());
            psPedido.setString(14, pedido.getMetodoPago());
            psPedido.setString(15, pedido.getEstadoPedido()); 
            psPedido.setTimestamp(16, pedido.getFechaPedido() != null ? pedido.getFechaPedido() : new java.sql.Timestamp(System.currentTimeMillis()));

            int filasAfectadasPedido = psPedido.executeUpdate();

            if (filasAfectadasPedido > 0) {
                rsPedido = psPedido.getGeneratedKeys();
                if (rsPedido.next()) {
                    int idPedidoGenerado = rsPedido.getInt(1);
        
                    for (DetallePedido detalle : detalles) {
                        PreparedStatement psDetalle = connGuardado.prepareStatement(sqlDetalle);
                        psDetalle.setInt(1, idPedidoGenerado); 
                        psDetalle.setInt(2, detalle.getIdProducto());
                        psDetalle.setString(3, detalle.getNombreProducto());
                        psDetalle.setInt(4, detalle.getCantidad());
                        psDetalle.setBigDecimal(5, detalle.getPrecioUnitario());
                        psDetalle.setBigDecimal(6, detalle.getSubtotalItem());
                        psDetalle.executeUpdate();
                        psDetalle.close(); 
                    }
                    
                    connGuardado.commit();
                    guardadoExitoso = true;
                    System.out.println("PedidoDAO: Pedido guardado exitosamente con ID: " + idPedidoGenerado + " y Número de Pedido: " + pedido.getNumeroPedido());

                } else {
                    connGuardado.rollback();
                    System.err.println("PedidoDAO: No se pudo obtener el ID del pedido generado. Transacción revertida.");
                }
            } else {
                connGuardado.rollback();
                 System.err.println("PedidoDAO: No se insertó la cabecera del pedido. Transacción revertida.");
            }

        } catch (SQLException e) {
            System.err.println("PedidoDAO Error al guardar pedido: " + e.getMessage());
            e.printStackTrace();
            if (connGuardado != null) {
                try {
                    connGuardado.rollback(); 
                    System.err.println("PedidoDAO: Transacción revertida debido a error SQL.");
                } catch (SQLException ex) {
                    System.err.println("PedidoDAO Error al revertir transacción: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } finally {
            try {
                if (rsPedido != null) rsPedido.close();
                if (psPedido != null) psPedido.close();
                if (connGuardado != null) {
                    connGuardado.setAutoCommit(true);
                    connGuardado.close(); 
                }
            } catch (SQLException e) {
                System.err.println("PedidoDAO Error al cerrar recursos: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return guardadoExitoso;
    }
}
