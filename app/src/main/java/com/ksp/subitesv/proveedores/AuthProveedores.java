package com.ksp.subitesv.proveedores;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthProveedores {
    FirebaseAuth mAuth;

    public AuthProveedores(){
        mAuth = FirebaseAuth.getInstance();
    }
    public Task<AuthResult> registro(String correo, String contrasena){
        return   mAuth.createUserWithEmailAndPassword(correo, contrasena);
    }
    public Task<AuthResult> iniciarSesion(String correo, String contrasena){
        return   mAuth.signInWithEmailAndPassword(correo, contrasena);
    }

    public void cerrarSesion(){
        mAuth.signOut();
    }

    public String obetenerId(){
        return mAuth.getUid();
    }

    public boolean sesionExistente(){
        boolean Existe=false;
        if (mAuth.getCurrentUser() != null){
            Existe=true;
        }
        return Existe;
    }


}
