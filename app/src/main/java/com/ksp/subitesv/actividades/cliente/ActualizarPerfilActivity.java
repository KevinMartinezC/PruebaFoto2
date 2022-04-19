
package com.ksp.subitesv.actividades.cliente;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ksp.subitesv.R;
import com.ksp.subitesv.includes.AppToolBar;
import com.ksp.subitesv.modulos.Cliente;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.ProveedorCliente;
import com.ksp.subitesv.utils.CompressorBitmapImage;
import com.ksp.subitesv.utils.FileUtil;

import java.io.File;

public class ActualizarPerfilActivity extends AppCompatActivity {

    private ImageView mImageViewPerfil;
    private Button mButtonActualizar;
    private TextView mTextViewNombre;

    private ProveedorCliente mProveedorCliente;
    private AuthProveedores mAuthProveedor;
    private File mImagenFile;
    private  String mImagen;
    private final int GALERIA_REQUEST = 1;
    private ProgressDialog mProgressDialog;
    private String mNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_perfil);
        AppToolBar.mostrar(this, "Actualizar Perfil", true);

        mImageViewPerfil = findViewById(R.id.imagenViewPerfil);
        mButtonActualizar = findViewById(R.id.btnActualizarPerfil);
        mTextViewNombre = findViewById(R.id.txtNombreRegistro);

        mProveedorCliente = new ProveedorCliente();
        mAuthProveedor = new AuthProveedores();
        mProgressDialog = new ProgressDialog(this);

        obtenerInformacionCliente();

        mImageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();


            }
        });


        mButtonActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actulizarPerfil();
            }
        });

    }

    private void abrirGaleria() {
        Intent galeriaIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galeriaIntent.setType("image/*");
        startActivityForResult(galeriaIntent,GALERIA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALERIA_REQUEST && resultCode == RESULT_OK){
            try {
                mImagenFile = FileUtil.from(this,data.getData());
                mImageViewPerfil.setImageBitmap(BitmapFactory.decodeFile(mImagenFile.getAbsolutePath()));
            }catch (Exception e){
                Log.d("ERROR","Mensaje: " + e.getMessage());
            }
        }
    }

    private void obtenerInformacionCliente(){
        mProveedorCliente.obtenerCLiente(mAuthProveedor.obetenerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String nombre = snapshot.child("nombre").getValue().toString();
                    mTextViewNombre.setText(nombre);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void actulizarPerfil() {
        mNombre = mTextViewNombre.getText().toString();
        if(mNombre.equals("") && mImagenFile != null){
            mProgressDialog.setMessage("Espere un momento...");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            guardarImagen();
        }else{
            Toast.makeText(this, "Ingresa la imagen y el nombre", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarImagen() {
        byte[] imagenByte = CompressorBitmapImage.getImage(this,mImagenFile.getPath(),500,500);
        StorageReference storage = FirebaseStorage.getInstance().getReference().child("cliente_imagenes").child(mAuthProveedor.obetenerId() + ".jpg");
        UploadTask uploadTask = storage.putBytes(imagenByte);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imagen = uri.toString();

                            Cliente cliente = new Cliente();
                            cliente.setImagen(imagen);
                            cliente.setNombre(mNombre);
                            cliente.setId(mAuthProveedor.obetenerId());
                            mProveedorCliente.actualizar(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(ActualizarPerfilActivity.this, "Su informacion se actualizo correctamente.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }else{
                    Toast.makeText(ActualizarPerfilActivity.this, "Hubo un error a subir la imagen", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}