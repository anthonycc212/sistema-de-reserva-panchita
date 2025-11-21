package conex;

import javax.swing.*;
import java.sql.Connection;
import vista.SelectorRol;
import java.awt.*;
import java.awt.event.*;

public class Main {
    public static void main(String[] args) {
        // Crear una instancia de la clase Conexion
        Conexion conexion = new Conexion();

        // Intentar obtener una conexión
        Connection con = conexion.getConexion();

        if (con != null) {
            // Aquí puedes realizar operaciones con la base de datos
            System.out.println("¡Conexión exitosa!");

            

            // Inicia la interfaz gráfica SelectorRol
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SelectorRol().setVisible(true);  // Abre el SelectorRol
                }
            });
        } else {
            System.out.println("No se pudo establecer la conexión.");
        }
    }
}

  // Aquí puedes realizar operaciones con la base de datos
  // Aquí puedes realizar operaciones con la base de datos
  // Aquí puedes realizar operaciones con la base de datos
  // Aquí puedes realizar operaciones con la base de datos
  // Aquí puedes realizar operaciones con la base de datos
  // Aquí puedes realizar operaciones con la base de datos