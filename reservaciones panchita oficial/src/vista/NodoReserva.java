package vista;

public class NodoReserva {
    private Reserva reserva;
    private NodoReserva siguienteNodo;

    public NodoReserva(Reserva reserva) {
        this.reserva = reserva;
        this.siguienteNodo = null;
    }

    public NodoReserva(Reserva reserva, NodoReserva siguienteNodo) {
        this.reserva = reserva;
        this.siguienteNodo = siguienteNodo;
    }

    public Reserva getReserva() { return reserva; }
    public void setReserva(Reserva reserva) { this.reserva = reserva; }

    public NodoReserva getSiguienteNodo() { return siguienteNodo; }
    public void setSiguienteNodo(NodoReserva siguienteNodo) { this.siguienteNodo = siguienteNodo; }
}
