package com.ksp.subitesv.proveedores;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ksp.subitesv.modulos.Token;

import java.util.Calendar;

public class TokenProveedor {

    DatabaseReference mBasedeDatos;


    public TokenProveedor() {
        mBasedeDatos = FirebaseDatabase.getInstance().getReference().child("Tokens");//Creacion de nodo
    }

    public void crear(final String idUsuario) {

        if (idUsuario == null) return;
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Token token = new Token(s);
                mBasedeDatos.child(idUsuario).setValue(token);
            }
        });
    }
    public DatabaseReference obtenerToken(String idUsuario){
        return mBasedeDatos.child(idUsuario);
    }

}
