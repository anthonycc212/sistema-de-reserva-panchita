package reportesreservas;

import java.util.Date;

public class Reserva {
    private int id;
    private String cliente;
    private Date fecha;
    private String hora;
    private String mesa;
    private String capacidad;
    private String sala;
    private String codigoReserva;
    private String metodoPago;
    private String estadoPago;
    private String estacionamiento;

    public Reserva(int id, String cliente, Date fecha, String hora, String mesa, String capacidad, String sala,
                   String codigoReserva, String metodoPago, String estadoPago, String estacionamiento) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.mesa = mesa;
        this.capacidad = capacidad;
        this.sala = sala;
        this.codigoReserva = codigoReserva;
        this.metodoPago = metodoPago;
        this.estadoPago = estadoPago;
        this.estacionamiento = estacionamiento;
    }

    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public Date getFecha() { return fecha; }
    public String getHora() { return hora; }
    public String getMesa() { return mesa; }
    public String getCapacidad() { return capacidad; }
    public String getSala() { return sala; }
    public String getCodigoReserva() { return codigoReserva; }
    public String getMetodoPago() { return metodoPago; }
    public String getEstadoPago() { return estadoPago; }
    public String getEstacionamiento() { return estacionamiento; }
}
