package reportesreservas;

public class NodoArbolReserva {
    public Reserva reserva;
    public NodoArbolReserva izquierda;
    public NodoArbolReserva derecha;

    public NodoArbolReserva(Reserva reserva) {
        this.reserva = reserva;
        this.izquierda = null;
        this.derecha = null;
    }
}
