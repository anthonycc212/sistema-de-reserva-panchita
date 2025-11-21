package Delivery;

import java.math.BigDecimal;

public class Producto {
    private int id;
    private String tipo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;

    public Producto() {
    }

    public Producto(int id, String tipo, String nombre, String descripcion, BigDecimal precio) {
        this.id = id;
        this.tipo = tipo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return nombre + " - S/" + precio; 
    }
}

