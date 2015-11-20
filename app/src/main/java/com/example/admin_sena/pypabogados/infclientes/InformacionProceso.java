package com.example.admin_sena.pypabogados.infclientes;

/**
 * Created by Admin_Sena on 05/11/2015.
 */
public class InformacionProceso
{
    String idProcesos;
    String numRadicado;
    String demandado;
    String juzgadoIncial;
    String juzgadoActual;
    String descripcion;
    String ultimaActuacion;
    String numInterno;
    String foto;
    String idEstadoProceso;
    String idTipoProceso;


    public InformacionProceso(String idProcesos, String numRadicado, String demandado, String juzgadoIncial, String juzgadoActual, String descripcion, String ultimaActuacion, String numInterno, String foto, String idEstadoProceso, String idTipoProceso) {
        this.idProcesos = idProcesos;
        this.numRadicado = numRadicado;
        this.demandado = demandado;
        this.juzgadoIncial = juzgadoIncial;
        this.juzgadoActual = juzgadoActual;
        this.descripcion = descripcion;
        this.ultimaActuacion = ultimaActuacion;
        this.numInterno = numInterno;
        this.foto = foto;
        this.idEstadoProceso = idEstadoProceso;
        this.idTipoProceso = idTipoProceso;
    }

    public String getIdProcesos() {
        return idProcesos;
    }

    public void setIdProcesos(String idProcesos) {
        this.idProcesos = idProcesos;
    }

    public String getNumRadicado() {
        return numRadicado;
    }

    public void setNumRadicado(String numRadicado) {
        this.numRadicado = numRadicado;
    }

    public String getDemandado() {
        return demandado;
    }

    public void setDemandado(String demandado) {
        this.demandado = demandado;
    }

    public String getJuzgadoIncial() {
        return juzgadoIncial;
    }

    public void setJuzgadoIncial(String juzgadoIncial) {
        this.juzgadoIncial = juzgadoIncial;
    }

    public String getJuzgadoActual() {
        return juzgadoActual;
    }

    public void setJuzgadoActual(String juzgadoActual) {
        this.juzgadoActual = juzgadoActual;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUltimaActuacion() {
        return ultimaActuacion;
    }

    public void setUltimaActuacion(String ultimaActuacion) {
        this.ultimaActuacion = ultimaActuacion;
    }

    public String getNumInterno() {
        return numInterno;
    }

    public void setNumInterno(String numInterno) {
        this.numInterno = numInterno;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getIdEstadoProceso() {
        return idEstadoProceso;
    }

    public void setIdEstadoProceso(String idEstadoProceso) {
        this.idEstadoProceso = idEstadoProceso;
    }

    public String getIdTipoProceso() {
        return idTipoProceso;
    }

    public void setIdTipoProceso(String idTipoProceso) {
        this.idTipoProceso = idTipoProceso;
    }
}
