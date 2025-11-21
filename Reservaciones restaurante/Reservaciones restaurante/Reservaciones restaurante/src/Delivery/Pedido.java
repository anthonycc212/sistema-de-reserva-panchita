package Delivery;

import java.math.BigDecimal;
import java.sql.Timestamp; 

public class Pedido {
    private int id; 
    private String numeroPedido; 
    private String nombreCliente;
    private String dniCliente;
    private String telefonoCliente;
    private String correoCliente;
    private String direccionEnvio;
    private String distritoEnvio;
    private String ciudadEnvio;
    private String referenciaEnvio; 
    private String telefonoEntrega; 
    private BigDecimal costoEnvio;
    private BigDecimal subtotalPedido; 
    private BigDecimal totalPedido; 
    private String metodoPago;
    private Timestamp fechaPedido; 
    private String estadoPedido; 


    public Pedido() {
    }

  
    public Pedido(String numeroPedido, String nombreCliente, String telefonoCliente, 
                  String direccionEnvio, String distritoEnvio, BigDecimal costoEnvio, 
                  BigDecimal subtotalPedido, BigDecimal totalPedido, String metodoPago, 
                  String estadoPedido) {
        this.numeroPedido = numeroPedido;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.direccionEnvio = direccionEnvio;
        this.distritoEnvio = distritoEnvio;
        this.costoEnvio = costoEnvio;
        this.subtotalPedido = subtotalPedido;
        this.totalPedido = totalPedido;
        this.metodoPago = metodoPago;
        this.estadoPedido = estadoPedido;
      
        this.fechaPedido = new Timestamp(System.currentTimeMillis()); 
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }

    public String getCorreoCliente() {
        return correoCliente;
    }

    public void setCorreoCliente(String correoCliente) {
        this.correoCliente = correoCliente;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getDistritoEnvio() {
        return distritoEnvio;
    }

    public void setDistritoEnvio(String distritoEnvio) {
        this.distritoEnvio = distritoEnvio;
    }

    public String getCiudadEnvio() {
        return ciudadEnvio;
    }

    public void setCiudadEnvio(String ciudadEnvio) {
        this.ciudadEnvio = ciudadEnvio;
    }

    public String getReferenciaEnvio() {
        return referenciaEnvio;
    }

    public void setReferenciaEnvio(String referenciaEnvio) {
        this.referenciaEnvio = referenciaEnvio;
    }

    public String getTelefonoEntrega() {
        return telefonoEntrega;
    }

    public void setTelefonoEntrega(String telefonoEntrega) {
        this.telefonoEntrega = telefonoEntrega;
    }

    public BigDecimal getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(BigDecimal costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public BigDecimal getSubtotalPedido() {
        return subtotalPedido;
    }

    public void setSubtotalPedido(BigDecimal subtotalPedido) {
        this.subtotalPedido = subtotalPedido;
    }

    public BigDecimal getTotalPedido() {
        return totalPedido;
    }

    public void setTotalPedido(BigDecimal totalPedido) {
        this.totalPedido = totalPedido;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Timestamp getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Timestamp fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public String getEstadoPedido() {
        return estadoPedido;
    }

    public void setEstadoPedido(String estadoPedido) {
        this.estadoPedido = estadoPedido;
    }

    
    @Override
    public String toString() {
        return "Pedido{" +
               "id=" + id +
               ", numeroPedido='" + numeroPedido + '\'' +
               ", nombreCliente='" + nombreCliente + '\'' +
               ", totalPedido=" + totalPedido +
               ", fechaPedido=" + fechaPedido +
               ", estadoPedido='" + estadoPedido + '\'' +
               '}';
    }
}
