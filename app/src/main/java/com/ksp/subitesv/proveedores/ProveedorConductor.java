package com.ksp.subitesv.proveedores;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ksp.subitesv.modulos.Cliente;
import com.ksp.subitesv.modulos.Conductor;

public class ProveedorConductor {

    DatabaseReference mBasedeDatos;

    public ProveedorConductor() {
        mBasedeDatos = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Conductores");
    }

    public Task<Void> crear(Conductor conductor) {
        return mBasedeDatos.child(conductor.getId()).setValue(conductor);
    }

    public DatabaseReference obtenerConductor(String conductorId){
        return mBasedeDatos.child(conductorId);
    }
}
