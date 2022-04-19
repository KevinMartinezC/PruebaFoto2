package com.ksp.subitesv.includes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ksp.subitesv.R;

public class AppToolBar {
    public static void mostrar(AppCompatActivity Activity, String titulo, boolean agregarBoton){
        Toolbar toolBar = Activity.findViewById(R.id.toolbar);
        Activity.setSupportActionBar(toolBar);
        Activity.getSupportActionBar().setTitle(titulo);
        Activity.getSupportActionBar().setDisplayHomeAsUpEnabled(agregarBoton);
    }
}
