package vista;

import java.util.ArrayList;
import java.util.List;

public class ListaMetodosPago {
    public static class NodoMetodoPago {
        public final String nombre;
        public final String descripcion;
        public final double factorDescuento; // 1.0 = sin descuento, 0.95 = 5% descuento
        public final boolean requiereVerificacion;
        
        NodoMetodoPago siguiente;

        public NodoMetodoPago(String nombre, String descripcion, double factorDescuento, boolean requiereVerificacion) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.factorDescuento = factorDescuento;
            this.requiereVerificacion = requiereVerificacion;
        }
    }
    
    private NodoMetodoPago cabeza;
    
    public ListaMetodosPago() {
        // Métodos base con configuración mejorada
        agregarMetodo("Efectivo", "Pago en efectivo al llegar", 1.0, false);
        agregarMetodo("Yape", "Pago digital con QR (5% descuento)", 0.95, true);
        agregarMetodo("Plin", "Pago digital con Plin (2% descuento)", 0.98, true);
        agregarMetodo("Tarjeta", "Pago con tarjeta (3% recargo)", 1.03, true);
    }
    
    public void agregarMetodo(String nombre, String descripcion, double factorDescuento, boolean requiereVerificacion) {
        NodoMetodoPago nuevo = new NodoMetodoPago(nombre, descripcion, factorDescuento, requiereVerificacion);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            NodoMetodoPago actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevo;
        }
    }
    
    public String[] obtenerNombresMetodos() {
        List<String> nombres = new ArrayList<>();
        NodoMetodoPago actual = cabeza;
        while (actual != null) {
            nombres.add(actual.nombre);
            actual = actual.siguiente;
        }
        return nombres.toArray(new String[0]);
    }
    
    public NodoMetodoPago buscarMetodo(String nombre) {
        NodoMetodoPago actual = cabeza;
        while (actual != null) {
            if (actual.nombre.equalsIgnoreCase(nombre)) {
                return actual;
            }
            actual = actual.siguiente;
        }
        return null;
    }
    
    // Nuevo método para calcular precio con descuento
    public double calcularPrecioConDescuento(String metodo, double precioBase) {
        NodoMetodoPago metodoEncontrado = buscarMetodo(metodo);
        if (metodoEncontrado != null) {
            return precioBase * metodoEncontrado.factorDescuento;
        }
        return precioBase;
    }
    
    // Método para verificar si requiere confirmación
    public boolean requiereVerificacion(String metodo) {
        NodoMetodoPago nodo = buscarMetodo(metodo);
        return nodo != null && nodo.requiereVerificacion;
    }
}