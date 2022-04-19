package com.ksp.subitesv.proveedores;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ksp.subitesv.modulos.HistorialReserva;
import com.ksp.subitesv.modulos.ReservaCliente;

import java.util.HashMap;
import java.util.Map;

public class ProveedorHistorialReserva {

    private DatabaseReference mBasedeDatos;

    public ProveedorHistorialReserva() {
        mBasedeDatos = FirebaseDatabase.getInstance().getReference().child("HistorialReserva");//Creacion de nodo
    }
    public Task<Void> crear(HistorialReserva historialReserva){
        return mBasedeDatos.child(historialReserva.getIdHistorialReserva()).setValue(historialReserva);
    }

    public Task<Void> actualizarCalificacionCliente(String idHistorialReserva,float calificacionCliente){
        Map<String, Object> map = new HashMap<>();
        map.put("calificacionCliente",calificacionCliente);
        return mBasedeDatos.child(idHistorialReserva).updateChildren(map);
    }

    public Task<Void> actualizarCalificacionConductor(String idHistorialReserva,float calificacionConductor){
        Map<String, Object> map = new HashMap<>();
        map.put("calificacionConductor",calificacionConductor);
        return mBasedeDatos.child(idHistorialReserva).updateChildren(map);
    }

    public DatabaseReference obtenerHistorialReserva(String idHistorialReserva){
        return mBasedeDatos.child(idHistorialReserva);
    }

}
