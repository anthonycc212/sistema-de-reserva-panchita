/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;

/**
 *
 * @author antho
 */
import javax.swing.*;
import java.awt.*;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MainPlato extends JFrame {

    private DefaultListModel<String> listaPlatosModel;
    private JList<String> listaPlatos;
    private JTextField campoNombre, campoDescripcion, campoPrecio;
    private PlatosDao dao;

    public MainPlato() {
        super("Gestor de Platos (Lista Enlazada + BD)");
        dao = new PlatosDao();

        setLayout(new BorderLayout());

        listaPlatosModel = new DefaultListModel<>();
        listaPlatos = new JList<>(listaPlatosModel);
        JScrollPane scrollPane = new JScrollPane(listaPlatos);
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelIngreso = new JPanel(new GridLayout(6, 2)); // Se ajusta para 6 filas
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
        JButton btnCargarBD = new JButton("Cargar desde BD");

        panelIngreso.add(btnAgregar);
        panelIngreso.add(btnEliminar);
        panelIngreso.add(btnGuardarBD);
        panelIngreso.add(btnCargarBD);

        add(panelIngreso, BorderLayout.SOUTH);

        // Acción: Agregar plato a la lista
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

            Platos plato = new Platos(dao.listarPlatos().size() + 1, nombre, descripcion, precio);
            dao.agregarPlato(plato);
          listaPlatosModel.addElement(nombre + " - " + descripcion + " - $" + precio);
            campoNombre.setText("");
            campoDescripcion.setText("");
            campoPrecio.setText("");
        });

        // Acción: Eliminar plato seleccionado
        btnEliminar.addActionListener(e -> {
            int selectedIndex = listaPlatos.getSelectedIndex();
            if (selectedIndex != -1) {
                dao.eliminarPlato(selectedIndex);
                listaPlatosModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona un plato para eliminar");
            }
        });

        // Acción: Guardar todos los platos en la base de datos
        btnGuardarBD.addActionListener(e -> {
            dao.guardarEnBD(dao.listarPlatos());
            JOptionPane.showMessageDialog(this, "Platos guardados en la base de datos.");
        });

        // Acción: Cargar platos desde la base de datos
        btnCargarBD.addActionListener(e -> {
            listaPlatosModel.clear();
            List<Platos> platos = dao.cargarDesdeBD();
            for (Platos p : platos) {
                dao.agregarPlato(p);
         listaPlatosModel.addElement(p.getNombre() + " - " + p.getDescripcion() + " - $" + p.getPrecio());
            }
            JOptionPane.showMessageDialog(this, "Datos cargados desde la base de datos.");
        });

        setSize(450, 400);
      setLocationRelativeTo(null); 
setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main());
    }
}
/**
 *
 * @author USER
 */

/**
 *
 * @author USER
 */