package conex;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// Ahora extiende JPanel para poder insertarse en una pestaña
public class MainPlato extends JPanel {

    private DefaultListModel<String> listaPlatosModel;
    private JList<String> listaPlatos;
    private JTextField campoNombre, campoDescripcion, campoPrecio;
    private PlatosDao dao;

    // El constructor ya no es un JFrame.
    public MainPlato() {
        dao = new PlatosDao();

        // 💡 PRUEBA VISUAL: Cambia el fondo a amarillo. Si ves este color, el panel SÍ está cargando.
        setBackground(new Color(255, 255, 153));

        setLayout(new BorderLayout());

      //  listaPlatosModel = new DefaultListModel<>();
       // listaPlatos = new JList<>(listaPlatosModel);
       // JScrollPane scrollPane = new JScrollPane(listaPlatos);
        //add(scrollPane, BorderLayout.CENTER);

        JPanel panelIngreso = new JPanel(new GridLayout(6, 2));
        campoNombre = new JTextField();
        campoDescripcion = new JTextField();
        campoPrecio = new JTextField();

        panelIngreso.add(new JLabel("Nombre:"));
        panelIngreso.add(campoNombre);
        panelIngreso.add(new JLabel("Descripción:"));
        panelIngreso.add(campoDescripcion);
        panelIngreso.add(new JLabel("Precio:"));
        panelIngreso.add(campoPrecio);

        JButton btnAgregar = new JButton("Agregar Plato");
        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        JButton btnGuardarBD = new JButton("Guardar en BD");
        JButton btnCargarBD = new JButton("Cargar desde BD (Manual)");

        panelIngreso.add(btnAgregar);
        panelIngreso.add(btnEliminar);
        panelIngreso.add(btnGuardarBD);
        panelIngreso.add(btnCargarBD);

        add(panelIngreso, BorderLayout.SOUTH);

        // Acción: Agregar plato a la lista (en memoria)
        btnAgregar.addActionListener(e -> {
            String nombre = campoNombre.getText();
            String descripcion = campoDescripcion.getText();
            double precio;
            try {
                precio = Double.parseDouble(campoPrecio.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Precio inválido");
                return;
            }

            // Asumiendo que la clase Platos existe
            Platos plato = new Platos(dao.listarPlatos().size() + 1, nombre, descripcion, precio);
            dao.agregarPlato(plato);
            listaPlatosModel.addElement(nombre + " - " + descripcion + " - S/ " + String.format("%.2f", precio));
            campoNombre.setText("");
            campoDescripcion.setText("");
            campoPrecio.setText("");
        });

        // Acción: Eliminar plato seleccionado (en memoria)
        btnEliminar.addActionListener(e -> {
            int selectedIndex = listaPlatos.getSelectedIndex();
            if (selectedIndex != -1) {
                dao.eliminarPlato(selectedIndex);
                listaPlatosModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un plato para eliminar");
            }
        });

        // Acción: Guardar todos los platos de la lista en la BD
        btnGuardarBD.addActionListener(e -> {
            dao.guardarEnBD(dao.listarPlatos());
            JOptionPane.showMessageDialog(this, "Platos guardados en la base de datos.");
        });

        // Acción: Cargar platos desde la BD (Manual)
        btnCargarBD.addActionListener(e -> {
            cargarPlatos(dao);
            JOptionPane.showMessageDialog(this, "Datos cargados desde la base de datos.");
        });

        // 💡 CARGA AUTOMÁTICA: Llama al método de carga justo al terminar de configurar el panel.
        cargarPlatos(dao);
    }

    /**
     * Método central para cargar la lista y sincronizar el DAO.
     */
    private void cargarPlatos(PlatosDao dao) {
        listaPlatosModel.clear();

        // Asumiendo que PlatosDao tiene un método clearPlatos() (como en la solución anterior)
        dao.clearPlatos();

        List<Platos> platos = dao.cargarDesdeBD();

        if (platos.isEmpty()) {
            // Mensaje si no hay datos en la BD
            listaPlatosModel.addElement("--- La base de datos no tiene platos registrados ---");
        } else {
            for (Platos p : platos) {
                // Se agrega a la lista interna del DAO y al modelo visual
                dao.agregarPlato(p);
                listaPlatosModel.addElement(p.getNombre() + " - " + p.getDescripcion() + " - S/ " + String.format("%.2f", p.getPrecio()));
            }
        }
    }
}