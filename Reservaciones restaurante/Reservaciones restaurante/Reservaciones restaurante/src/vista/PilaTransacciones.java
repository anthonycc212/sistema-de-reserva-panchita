/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vista;

import java.time.LocalDateTime;

/**
 *
 * @author USER
 */
public class PilaTransacciones {
    private NodoTransaccion cima;
    
    private class NodoTransaccion {
        String codigoReserva;
        String metodoPago;
        double monto;
        LocalDateTime fecha;
        NodoTransaccion siguiente;
        
        public NodoTransaccion(String codigoReserva, String metodoPago, double monto) {
            this.codigoReserva = codigoReserva;
            this.metodoPago = metodoPago;
            this.monto = monto;
            this.fecha = LocalDateTime.now();
        }
    }
    
    public void push(String codigoReserva, String metodoPago, double monto) {
        NodoTransaccion nuevo = new NodoTransaccion(codigoReserva, metodoPago, monto);
        nuevo.siguiente = cima;
        cima = nuevo;
    }
    
    public NodoTransaccion pop() {
        if (cima == null) return null;
        NodoTransaccion temp = cima;
        cima = cima.siguiente;
        return temp;
    }
}