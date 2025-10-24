
package vista;

public class Reserva {
    private String mesa;
    private String cliente;
    private String fecha;
    private int capacidad;
    private String hora;
    private String sala;
    private String codigoReserva;
    private String metodoPago;
    private String estadoPago;
    
    public Reserva(String mesa, String cliente, String fecha, int capacidad, String hora, String sala, String codigoReserva, String metodoPago, String estadoPago) {
        this.mesa = mesa;
        this.cliente = cliente;
        this.fecha = fecha;
        this.capacidad = capacidad;
        this.hora = hora;
        this.sala = sala;
        this.codigoReserva = codigoReserva;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
    }

    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public String getSala() { return sala; }
    public void setSala(String sala) { this.sala = sala; }

    public String getCodigoReserva() { return codigoReserva; }
    public void setCodigoReserva(String codigoReserva) { this.codigoReserva = codigoReserva; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }

    @Override
    public String toString() {
        return "Reserva{" +
                "mesa='" + mesa + '\'' +
                ", cliente='" + cliente + '\'' +
                ", fecha='" + fecha + '\'' +
                ", capacidad=" + capacidad +
                ", hora='" + hora + '\'' +
                ", sala='" + sala + '\'' +
                ", codigoReserva='" + codigoReserva + '\'' +
                ", metodoPago='" + metodoPago + '\'' +
                ", estadoPago='" + estadoPago + '\'' +
                '}';
    }
}

