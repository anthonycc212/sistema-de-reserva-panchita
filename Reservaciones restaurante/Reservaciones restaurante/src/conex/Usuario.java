/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conex;

/**
 *
 * @author antho
 */
public class Usuario {
    private int id;
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private String rol;
    private String telefono;

    public Usuario(String nombreCompleto, String correo, String contrasena, String rol, String telefono) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.telefono = telefono;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo() { return correo; }
    public String getContrasena() { return contrasena; }
    public String getRol() { return rol; }
    public String getTelefono() { return telefono; }
}

