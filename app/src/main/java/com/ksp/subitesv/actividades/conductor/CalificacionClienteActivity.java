package com.ksp.subitesv.actividades.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ksp.subitesv.R;
import com.ksp.subitesv.actividades.cliente.CalificacionConductorActivity;
import com.ksp.subitesv.actividades.cliente.MapClienteActivity;
import com.ksp.subitesv.modulos.HistorialReserva;
import com.ksp.subitesv.modulos.ReservaCliente;
import com.ksp.subitesv.proveedores.ProveedorHistorialReserva;
import com.ksp.subitesv.proveedores.ReservaClienteProveedor;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class CalificacionClienteActivity extends AppCompatActivity {

    private TextView mTextViewOrigen;
    private TextView mTextViewDestino;
    private RatingBar mBarraCalificacion;
    private Button mBotonCalificacion;

    private ReservaClienteProveedor mReservaClienteProveedor;

    private String mExtraClienteId;

    private HistorialReserva mHistorialReserva;
    private ProveedorHistorialReserva mProveedorHistorialReserva;

    private float mCalificacion = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificacion_cliente);

        mTextViewOrigen = findViewById(R.id.textViewCalificacionOrigen);
        mTextViewDestino = findViewById(R.id.textViewCalificacionDestino);
        mBarraCalificacion = findViewById(R.id.barraCalificacion);
        mBotonCalificacion = findViewById(R.id.btnCalificacion);
        mProveedorHistorialReserva = new ProveedorHistorialReserva();

        mReservaClienteProveedor = new ReservaClienteProveedor();
        mExtraClienteId = getIntent().getStringExtra("clienteId");

        mBarraCalificacion.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float calificacion, boolean b) {
                mCalificacion = calificacion;
            }
        });
        mBotonCalificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              calificar();
            }
        });

        obtenerReservaCliente();
    }

    private void obtenerReservaCliente(){
        mReservaClienteProveedor.obtenerReservaCliente(mExtraClienteId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ReservaCliente reservaCliente = snapshot.getValue(ReservaCliente.class);
                    mTextViewOrigen.setText(reservaCliente.getOrigen());
                    mTextViewDestino.setText(reservaCliente.getDestino());
                    mHistorialReserva= new HistorialReserva(
                        reservaCliente.getIdHistorialReserva(),
                            reservaCliente.getIdCliente(),
                            reservaCliente.getIdConductor(),
                            reservaCliente.getDestino(),
                            reservaCliente.getOrigen(),
                            reservaCliente.getTiempo(),
                            reservaCliente.getKm(),
                            reservaCliente.getEstado(),
                            reservaCliente.getOrigenLat(),
                            reservaCliente.getOrigenLng(),
                            reservaCliente.getDestinoLat(),
                            reservaCliente.getDestinoLng()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void calificar() {
        if (mCalificacion > 0){
            mHistorialReserva.setCalificacionCliente(mCalificacion);
            mHistorialReserva.setTimestamp(new Date().getTime());
            mProveedorHistorialReserva.obtenerHistorialReserva(mHistorialReserva.getIdHistorialReserva()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.exists()){
                       mProveedorHistorialReserva.actualizarCalificacionCliente(mHistorialReserva.getIdHistorialReserva(),mCalificacion).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(CalificacionClienteActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(CalificacionClienteActivity.this, MapaConductorActivity.class);
                               startActivity(intent);
                               finish();
                           }
                       });;
                   }
                   else {
                       mProveedorHistorialReserva.crear(mHistorialReserva).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void aVoid) {
                               Toast.makeText(CalificacionClienteActivity.this, "La calificacion se guardo correctamente", Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(CalificacionClienteActivity.this,MapaConductorActivity.class);
                               startActivity(intent);
                               finish();
                           }
                       });
                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        } else {
            Toast.makeText(this, "Debes Ingresar la Calificacion", Toast.LENGTH_SHORT).show();
        }
    }
}