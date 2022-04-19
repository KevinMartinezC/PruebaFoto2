package com.ksp.subitesv.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.cliente.RegistroActivity;
import com.ksp.subitesv.actividades.conductor.RegistroConductorActivity;
import com.ksp.subitesv.includes.AppToolBar;

public class SeleccionAutenticacionActivity extends AppCompatActivity {


    Button btnIrIniciarSesion, btnRegistrarse;
    SharedPreferences mPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_autenticacion);
        AppToolBar.mostrar(this, "Seleccion Opción", true);
        mPref = getApplicationContext().getSharedPreferences("tipoUsuario", MODE_PRIVATE);

        btnIrIniciarSesion = findViewById(R.id.btnIrInicioSeccion);
        btnRegistrarse = findViewById(R.id.btnIraRegistrarse);

        btnIrIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IraInicio();
            }
        });
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IraRegistro();
            }
        });
    }

    public void IraInicio() {
        Intent intent = new Intent(SeleccionAutenticacionActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void IraRegistro() {
        String tipoUsuario = mPref.getString("usuario", "");
        if(tipoUsuario.equals("cliente")){
            Intent intent = new Intent(SeleccionAutenticacionActivity.this, RegistroActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(SeleccionAutenticacionActivity.this, RegistroConductorActivity.class);
            startActivity(intent);
        }

    }
}