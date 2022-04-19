package com.ksp.subitesv.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.cliente.MapClienteActivity;
import com.ksp.subitesv.actividades.conductor.MapaConductorActivity;
import com.ksp.subitesv.actividades.conductor.RegistroConductorActivity;
import com.ksp.subitesv.includes.AppToolBar;

public class LoginActivity extends AppCompatActivity {

    TextInputEditText TxtCorreo, TxtContrasena;
    Button BtnIniciarSesion;
    FirebaseAuth mAuth;
    DatabaseReference mBasedeDatos;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppToolBar.mostrar(this, "Iniciar Sesion", true);

        TxtContrasena = findViewById(R.id.TxtContrasena);
        TxtCorreo = findViewById(R.id.TxtCorreo);
        BtnIniciarSesion = findViewById(R.id.BtnIniciarSesion);

        mAuth = FirebaseAuth.getInstance();
        mBasedeDatos = FirebaseDatabase.getInstance().getReference();

        mPref= getApplicationContext().getSharedPreferences("tipoUsuario", MODE_PRIVATE);
        BtnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarSesion();
            }
        });


    }

    private void iniciarSesion() {
        String correo = TxtCorreo.getText().toString();
        String contrasena = TxtContrasena.getText().toString();

        if (!correo.isEmpty() && !contrasena.isEmpty()) {
            if (contrasena.length() >= 6) {
                mAuth.signInWithEmailAndPassword(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {//Login fue realizado Con exito
                            String tipoUsuario = mPref.getString("usuario", "");
                            if (tipoUsuario.equals("cliente")){
                                Intent intent=new Intent(LoginActivity.this, MapClienteActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else {
                                Intent intent=new Intent(LoginActivity.this, MapaConductorActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                           // Toast.makeText(LoginActivity.this, getText(R.string.InicioSeccionExitoso), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, getText(R.string.CorreoContraInvalidos), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, getText(R.string.ContrasenaMayora6Digitos), Toast.LENGTH_SHORT).show();

            }
        } else {
            Toast.makeText(this, getText(R.string.CorreoContraObligatorios), Toast.LENGTH_SHORT).show();
        }
    }



}