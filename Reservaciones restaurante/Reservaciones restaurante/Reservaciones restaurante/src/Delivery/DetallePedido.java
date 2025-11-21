/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Delivery; // Asegúrate que este archivo esté en la carpeta src/Delivery/

import java.math.BigDecimal;

public class DetallePedido {
    private int id; 
    private int idPedido; 
    private int idProducto; 
    private String nombreProducto; 
    private int cantidad;
    private BigDecimal precioUnitario; 
    private BigDecimal subtotalItem;   

    
    public DetallePedido() {
    }

    
    public DetallePedido(int idPedido, int idProducto, String nombreProducto, int cantidad, BigDecimal precioUnitario, BigDecimal subtotalItem) {
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotalItem = subtotalItem;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getSubtotalItem() {
        return subtotalItem;
    }

    public void setSubtotalItem(BigDecimal subtotalItem) {
        this.subtotalItem = subtotalItem;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
               "id=" + id +
               ", idPedido=" + idPedido +
               ", idProducto=" + idProducto +
               ", nombreProducto='" + nombreProducto + '\'' +
               ", cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario +
               ", subtotalItem=" + subtotalItem +
               '}';
    }
}
