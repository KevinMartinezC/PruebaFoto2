package com.ksp.subitesv.actividades.cliente;

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
import com.ksp.subitesv.R;
import com.ksp.subitesv.includes.AppToolBar;
import com.ksp.subitesv.modulos.Cliente;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.ProveedorCliente;

public class RegistroActivity extends AppCompatActivity {

    AuthProveedores mAuthProveedores;
    ProveedorCliente mProveedorCliente;

    Button btnRegistrarse;
    TextInputEditText txtNombreRegistro, txtCorreoRegistro, txtContrasenaRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        AppToolBar.mostrar(this, "Registro Usuario", true);

        btnRegistrarse = findViewById(R.id.btnRegistro);
        txtNombreRegistro = findViewById(R.id.txtNombreRegistro);
        txtCorreoRegistro = findViewById(R.id.txtCorreoRegistro);
        txtContrasenaRegistro = findViewById(R.id.TxtContrasenaRegistro);

        mAuthProveedores = new AuthProveedores();
        mProveedorCliente = new ProveedorCliente();



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

        if (!nombre.isEmpty() && !correo.isEmpty() && !contrasena.isEmpty()) {
            if (contrasena.length() >= 6) {
                registrar(nombre, correo, contrasena);
            } else {
                Toast.makeText(this, getText(R.string.ContrasenaMayora6Digitos), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getText(R.string.IngreseTodoslosCampos), Toast.LENGTH_SHORT).show();
        }
    }

    void registrar(String nombre, String correo, String contrasena) {
        mAuthProveedores.registro(correo, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();//Se obtenga el identificador del usuario que se acaba de registrar en firesebase autenticacion
                    Cliente cliente = new Cliente(id, nombre, correo);
                    crear(cliente);
                } else {
                    Toast.makeText(RegistroActivity.this, getText(R.string.NosePudoRegistrar), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void crear(Cliente cliente) {
        mProveedorCliente.crear(cliente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegistroActivity.this, "El registro se realizo exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegistroActivity.this, "No se pudo crear el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
/*
    void guardarUsuario(String id, String nombre, String correo) {
        String usuarioSeleccionado = mPref.getString("usuario", "");//para traer el tipo de usuario seleccionado desde la MainActivity
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);

        if (usuarioSeleccionado.equals("conductor")) {
            mBasedeDatos.child("Usuarios").child("Conductores").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Registro.this, "Fallo el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (usuarioSeleccionado.equals("cliente")) {
            mBasedeDatos.child("Usuarios").child("Cliente").child(id).setValue(usuario).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Registro.this, "Fallo el registro", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }*/
}