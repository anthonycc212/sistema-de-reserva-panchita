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
    private String nombre_completo;
    private String correo;
    private String contrasena;
    private String rol;
    private String telefono;

    public Usuario() {}

    public Usuario(String nombre_completo, String correo, String contrasena, String rol, String telefono) {
        this.nombre_completo = nombre_completo;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre_completo() { return nombre_completo; }
    public void setNombre_completo(String nombre_completo) { this.nombre_completo = nombre_completo; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}