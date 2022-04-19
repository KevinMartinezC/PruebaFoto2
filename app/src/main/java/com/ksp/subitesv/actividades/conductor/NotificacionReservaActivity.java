package com.ksp.subitesv.actividades.conductor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.ProveedorGeoFire;
import com.ksp.subitesv.proveedores.ReservaClienteProveedor;

import com.ksp.subitesv.R;



public class NotificacionReservaActivity extends AppCompatActivity {
    private TextView mTextViewDestino;
    private TextView mTextViewOrigen;
    private TextView mTextViewMin;
    private TextView mTextViewDistancia;
    private TextView mTextViewCounter;
    private Button mbuttonAceptar;
    private Button mbuttonCancelar;

    private ReservaClienteProveedor mReservaClienteProveedor;
    private ProveedorGeoFire mProveedorGeoFire;
    private AuthProveedores mAuthProveedor;

    private String mExtraIdClient;
    private String mExtraOrigen;
    private String mExtraDestino;
    private String mExtraMin;
    private String mExtraDistancia;

    private MediaPlayer mMediaPlayer;

    private int mCounter = 10;
    private Handler mHandler;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mCounter = mCounter -1;
            mTextViewCounter.setText(String.valueOf(mCounter));
            if (mCounter > 0) {
                iniciarTimer();
            }
            else {
                cancelarNotificacion();
            }
        }
    };
    private ValueEventListener mListener;


    private void iniciarTimer() {
        mHandler = new Handler();
        mHandler.postDelayed(runnable, 1000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacion_reserva);

        mTextViewDestino = findViewById(R.id.textViewDestination);
        mTextViewOrigen = findViewById(R.id.textViewOrigin);
        mTextViewMin = findViewById(R.id.textViewMin);
        mTextViewDistancia = findViewById(R.id.textViewDistance);
        mTextViewCounter = findViewById(R.id.textViewCounter);
        mbuttonAceptar = findViewById(R.id.btnAceptarNotificacion);
        mbuttonCancelar= findViewById(R.id.btnCancelarNotificacion);

        mExtraIdClient = getIntent().getStringExtra("idCliente");
        mExtraOrigen = getIntent().getStringExtra("origin");
        mExtraDestino = getIntent().getStringExtra("destination");
        mExtraMin = getIntent().getStringExtra("min");
        mExtraDistancia = getIntent().getStringExtra("distance");

        mTextViewDestino.setText(mExtraDestino);
        mTextViewOrigen.setText(mExtraOrigen);
        mTextViewMin.setText(mExtraMin);
        mTextViewDistancia.setText(mExtraDistancia);

        mMediaPlayer = MediaPlayer.create(this, R.raw.notification);
        mMediaPlayer.setLooping(true);

        mReservaClienteProveedor = new ReservaClienteProveedor();

        getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );

        iniciarTimer();

        verificarSiClienteCanceloViaje();
        mbuttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aceptarNotificacion();
            }
        });

        mbuttonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarNotificacion();
            }
        });
    }

    private void verificarSiClienteCanceloViaje() {
        mListener = mReservaClienteProveedor.obtenerReservaCliente(mExtraIdClient).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(NotificacionReservaActivity.this, "El cliente cancelo el viaje", Toast.LENGTH_LONG).show();
                    if (mHandler != null) mHandler.removeCallbacks(runnable);
                    Intent intent = new Intent(NotificacionReservaActivity.this, MapaConductorActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void aceptarNotificacion() {
        if (mHandler != null) mHandler.removeCallbacks(runnable);
        mAuthProveedor = new AuthProveedores();
        mProveedorGeoFire = new ProveedorGeoFire("conductores_activos");
        mProveedorGeoFire.removerUbicacion(mAuthProveedor.obetenerId());


        mReservaClienteProveedor = new ReservaClienteProveedor();
        mReservaClienteProveedor.actualizarEstado(mExtraIdClient,"accept");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);

        Intent intent1 = new Intent(NotificacionReservaActivity.this, MapReservaConductorActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.setAction(Intent.ACTION_RUN);
        intent1.putExtra("clienteId", mExtraIdClient);
        startActivity(intent1);
    }





    private void cancelarNotificacion() {
        if (mHandler != null) mHandler.removeCallbacks(runnable);

        mReservaClienteProveedor = new ReservaClienteProveedor();
        mReservaClienteProveedor.actualizarEstado(mExtraIdClient,"cancel");

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(2);
        Intent intent = new Intent(NotificacionReservaActivity.this, MapaConductorActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMediaPlayer != null) {
            if (!mMediaPlayer.isPlaying()) {
                mMediaPlayer.start();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) mHandler.removeCallbacks(runnable);
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
        if (mListener != null) {
            mReservaClienteProveedor.obtenerReservaCliente(mExtraIdClient).removeEventListener(mListener);
        }
    }

}