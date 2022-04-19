package com.ksp.subitesv.actividades.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.cliente.RegistroActivity;
import com.ksp.subitesv.includes.AppToolBar;
import com.ksp.subitesv.modulos.Cliente;
import com.ksp.subitesv.modulos.Conductor;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.ProveedorCliente;
import com.ksp.subitesv.proveedores.ProveedorConductor;

public class RegistroConductorActivity extends AppCompatActivity {

    AuthProveedores mAuthProveedores;
    ProveedorConductor mProveedorConductor;

    Button btnRegistrarse;
    TextInputEditText txtNombreRegistro, txtCorreoRegistro, txtContrasenaRegistro,txtMarcaCarro, txtPlacaCarro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_conductor);
        AppToolBar.mostrar(this, "Registro Conductor", true);

        btnRegistrarse = findViewById(R.id.btnRegistro);
        txtNombreRegistro = findViewById(R.id.txtNombreRegistro);
        txtCorreoRegistro = findViewById(R.id.txtCorreoRegistro);
        txtContrasenaRegistro = findViewById(R.id.TxtContrasenaRegistro);
        txtMarcaCarro = findViewById(R.id.TxtMarcaVehiculo);
        txtPlacaCarro = findViewById(R.id.TxtPlacaVehiculo);

        mAuthProveedores = new AuthProveedores();
        mProveedorConductor = new ProveedorConductor();

        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickregistrar();
            }
        });
    }

    void clickregistrar() {
        String nombre = txtNombreRegistro.getText().toString();
        String correo = txtCorreoRegistro.getText().toString();
        String contrasena = txtContrasenaRegistro.getText().toString();
        String marcaCarro = txtMarcaCarro.getText().toString();
        String placaCarro = txtPlacaCarro.getText().toString();

        if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty() && !marcaCarro.isEmpty() && !placaCarro.isEmpty()){
            if (contrasena.length() >= 6) {
                registrar(nombre, correo, contrasena,marcaCarro,placaCarro);
            } else {
                Toast.makeText(this, getText(R.string.ContrasenaMayora6Digitos), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getText(R.string.IngreseTodoslosCampos), Toast.LENGTH_SHORT).show();
        }
    }

    void registrar(String nombre, String correo, String contrasena, String marcaVehiculo, String placaVehiculo) {
        mAuthProveedores.registro(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();//Se obtenga el identificador del usuario que se acaba de registrar en firesebase autenticacion
                    Conductor conductor = new Conductor(id, nombre, correo, marcaVehiculo, placaVehiculo);
                    crear(conductor);
                } else {
                    Toast.makeText(RegistroConductorActivity.this, getText(R.string.NosePudoRegistrar), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void crear(Conductor conductor) {
       mProveedorConductor.crear(conductor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent intent=new Intent(RegistroConductorActivity.this, MapaConductorActivity.class);
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    //Toast.makeText(RegistroConductorActivity.this, "El registro se realizo exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistroConductorActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}