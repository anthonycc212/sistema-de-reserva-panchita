package reportesreservas;

import java.util.*;

public class ArbolReservasPorFecha {
    private NodoArbolReserva raiz;

    public void insertar(Reserva reserva) {
        raiz = insertarRec(raiz, reserva);
    }

    private NodoArbolReserva insertarRec(NodoArbolReserva nodo, Reserva reserva) {
        if (nodo == null) {
            return new NodoArbolReserva(reserva);
        }
        if (reserva.getFecha().before(nodo.reserva.getFecha())) {
            nodo.izquierda = insertarRec(nodo.izquierda, reserva);
        } else {
            nodo.derecha = insertarRec(nodo.derecha, reserva);
        }
        return nodo;
    }

    public List<Reserva> buscarPorFecha(Date fecha) {
        List<Reserva> resultado = new ArrayList<>();
        buscarRec(raiz, fecha, resultado);
        return resultado;
    }

    private void buscarRec(NodoArbolReserva nodo, Date fecha, List<Reserva> resultado) {
        if (nodo == null) return;
        if (fecha.before(nodo.reserva.getFecha())) {
            buscarRec(nodo.izquierda, fecha, resultado);
        } else if (fecha.after(nodo.reserva.getFecha())) {
            buscarRec(nodo.derecha, fecha, resultado);
        } else {
            resultado.add(nodo.reserva);
            buscarRec(nodo.izquierda, fecha, resultado);
            buscarRec(nodo.derecha, fecha, resultado);
        }
    }

    public void inOrden(List<Reserva> lista) {
        inOrdenRec(raiz, lista);
    }

    private void inOrdenRec(NodoArbolReserva nodo, List<Reserva> lista) {
        if (nodo == null) return;
        inOrdenRec(nodo.izquierda, lista);
        lista.add(nodo.reserva);
        inOrdenRec(nodo.derecha, lista);
    }

    public NodoArbolReserva getRaiz() {
        return raiz;
    }
}
