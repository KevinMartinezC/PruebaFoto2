package com.ksp.subitesv.proveedores;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ksp.subitesv.modulos.Cliente;

import java.util.HashMap;
import java.util.Map;

public class ProveedorCliente {
    DatabaseReference mBasedeDatos;

    public ProveedorCliente() {
        mBasedeDatos = FirebaseDatabase.getInstance().getReference().child("Usuarios").child("Clientes");
    }
    public Task<Void> crear(Cliente cliente){
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", cliente.getNombre());
        map.put("correo",cliente.getCorreo());
        return  mBasedeDatos.child(cliente.getId()).setValue(map);
    }
    public Task<Void> actualizar(Cliente cliente){
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", cliente.getNombre());
        map.put("imagen",cliente.getImagen());
        return  mBasedeDatos.child(cliente.getId()).setValue(map);
    }

    public DatabaseReference obtenerCLiente(String clienteId) {
        return mBasedeDatos.child(clienteId);
    }
}
