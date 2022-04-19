package com.ksp.subitesv.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.cliente.MapClienteActivity;
import com.ksp.subitesv.actividades.conductor.MapaConductorActivity;

public class MainActivity extends AppCompatActivity {
    Button btnCliente;
    Button btnConductor;

    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPref = getApplicationContext().getSharedPreferences("tipoUsuario", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();

        btnCliente = findViewById(R.id.BtnCliente);
        btnConductor = findViewById(R.id.BtnConductor);

        btnCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("usuario", "cliente");
                editor.apply();//Para guardar el dato y edintificar si es cliente o conductor
                irSeleccionAutenticacion();

            }
        });

        btnConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("usuario", "conductor");
                editor.apply();
                irSeleccionAutenticacion();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            String tipoUsuario = mPref.getString("usuario", "");
            if (tipoUsuario.equals("cliente")){
                Intent intent=new Intent(MainActivity.this, MapClienteActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else {
                Intent intent=new Intent(MainActivity.this, MapaConductorActivity.class);
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void irSeleccionAutenticacion() {
        Intent intent = new Intent(this, SeleccionAutenticacionActivity.class);
        startActivity(intent);
    }
}