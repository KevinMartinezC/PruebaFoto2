package com.ksp.subitesv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class SeleccionAutenticacion extends AppCompatActivity {

    Toolbar bToolBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_autenticacion);
        bToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(bToolBar);
        getSupportActionBar().setTitle("Seleccionar opción");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}