package com.example.admin_sena.pypabogados.infclientes;

/**
 * Created by Admin_Sena on 05/11/2015.
 */
public class InformacionCliente
{
    String idCliente;
    String cedula;
    String nombre;
    String apellido;
    String direccion;
    String ciudad;
    String telefono;
    String correo;
    String idProcesos;


    public InformacionCliente(String idCliente, String cedula, String nombre, String apellido, String direccion, String ciudad, String telefono, String correo, String idProcesos) {
        this.idCliente = idCliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.correo = correo;
        this.idProcesos = idProcesos;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getIdProcesos() {
        return idProcesos;
    }

    public void setIdProcesos(String idProcesos) {
        this.idProcesos = idProcesos;
    }
}
