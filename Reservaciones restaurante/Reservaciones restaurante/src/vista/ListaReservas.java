
package vista;

public class ListaReservas {
private NodoReserva nodoInicio;
    private NodoReserva nodoFin;

    public ListaReservas() {
        this.nodoInicio = null;
        this.nodoFin = null;
    }

    public void agregarInicio(Reserva reserva) {
        nodoInicio = new NodoReserva(reserva, nodoInicio);
        if (nodoFin == null) {
            nodoFin = nodoInicio;
        }
    }

    public void agregarFinal(Reserva reserva) {
        if (estaVacio()) {
            nodoInicio = new NodoReserva(reserva);
            nodoFin = nodoInicio;
        } else {
            nodoFin.setSiguienteNodo(new NodoReserva(reserva));
            nodoFin = nodoFin.getSiguienteNodo();
        }
    }

    public boolean estaVacio() {
        return (nodoInicio == null);
    }

    public void mostrarInverso() {
        mostrarInversoRecursivo(nodoInicio);
    }

    private void mostrarInversoRecursivo(NodoReserva nodo) {
        if (nodo == null) return;
        mostrarInversoRecursivo(nodo.getSiguienteNodo());
        System.out.println(nodo.getReserva());
    }

    public void eliminarPorCodigo(String codigo) {
        if (estaVacio()) return;
        NodoReserva actual = nodoInicio;
        NodoReserva anterior = null;

        while (actual != null) {
            if (actual.getReserva().getCodigoReserva().equals(codigo)) {
                if (actual == nodoInicio) {
                    borrarInicio();
                } else if (actual == nodoFin) {
                    borrarFinal();
                } else {
                    anterior.setSiguienteNodo(actual.getSiguienteNodo());
                }
                break;
            }
            anterior = actual;
            actual = actual.getSiguienteNodo();
        }
    }

    public void borrarInicio() {
        if (!estaVacio()) {
            if (nodoInicio == nodoFin) {
                nodoInicio = null;
                nodoFin = null;
            } else {
                nodoInicio = nodoInicio.getSiguienteNodo();
            }
        }
    }

    public void borrarFinal() {
        if (!estaVacio()) {
            if (nodoInicio == nodoFin) {
                nodoInicio = null;
                nodoFin = null;
            } else {
                NodoReserva temp = nodoInicio;
                while (temp.getSiguienteNodo() != nodoFin) {
                    temp = temp.getSiguienteNodo();
                }
                temp.setSiguienteNodo(null);
                nodoFin = temp;
            }
        }
    }   
}
