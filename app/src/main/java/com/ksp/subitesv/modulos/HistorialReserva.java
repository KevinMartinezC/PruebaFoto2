package com.ksp.subitesv.modulos;

public class HistorialReserva {

    String idHistorialReserva;
    String idCliente;
    String idConductor;
    String destino;
    String origen;
    String tiempo;
    String km;
    String estado;
    double origenLat;
    double origenLng;
    double destinoLat;
    double destinoLng;
    double CalificacionCliente;
    double CalificacionConductor;
    long timestamp;

    public HistorialReserva(){

    }

    public HistorialReserva(String idHistorialReserva,String idCliente, String idConductor, String destino, String origen, String tiempo, String km, String estado, double origenLat, double origenLng, double destinoLat, double destinoLng) {
        this.idHistorialReserva = idHistorialReserva;
        this.idCliente = idCliente;
        this.idConductor = idConductor;
        this.destino = destino;
        this.origen = origen;
        this.tiempo = tiempo;
        this.km = km;
        this.estado = estado;
        this.origenLat = origenLat;
        this.origenLng = origenLng;
        this.destinoLat = destinoLat;
        this.destinoLng = destinoLng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdHistorialReserva() {
        return idHistorialReserva;
    }

    public void setIdHistorialReserva(String idHistorialReserva) {
        this.idHistorialReserva = idHistorialReserva;
    }

    public double getCalificacionCliente() {
        return CalificacionCliente;
    }

    public void setCalificacionCliente(double calificacionCliente) {
        CalificacionCliente = calificacionCliente;
    }

    public double getCalificacionConductor() {
        return CalificacionConductor;
    }

    public void setCalificacionConductor(double calificacionConductor) {
        CalificacionConductor = calificacionConductor;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(String idConductor) {
        this.idConductor = idConductor;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTiempo() {
        return tiempo;
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getOrigenLat() {
        return origenLat;
    }

    public void setOrigenLat(double origenLat) {
        this.origenLat = origenLat;
    }

    public double getOrigenLng() {
        return origenLng;
    }

    public void setOrigenLng(double origenLng) {
        this.origenLng = origenLng;
    }

    public double getDestinoLat() {
        return destinoLat;
    }

    public void setDestinoLat(double destinoLat) {
        this.destinoLat = destinoLat;
    }

    public double getDestinoLng() {
        return destinoLng;
    }

    public void setDestinoLng(double destinoLng) {
        this.destinoLng = destinoLng;
    }
}
