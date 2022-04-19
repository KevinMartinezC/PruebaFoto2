package com.ksp.subitesv.actividades.cliente;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ksp.subitesv.R;
import com.ksp.subitesv.modulos.FCMCuerpo;
import com.ksp.subitesv.modulos.FCMRespuesta;
import com.ksp.subitesv.modulos.ReservaCliente;
import com.ksp.subitesv.modulos.Token;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.GoogleApiProveedor;
import com.ksp.subitesv.proveedores.NotificacionProveedor;
import com.ksp.subitesv.proveedores.ProveedorGeoFire;
import com.ksp.subitesv.proveedores.ReservaClienteProveedor;
import com.ksp.subitesv.proveedores.TokenProveedor;
import com.ksp.subitesv.utils.DecodificadorPuntos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SolicitarConductorActivity extends AppCompatActivity {

    private LottieAnimationView mAnimation;
    private TextView mTextviewLookingFor;
    private Button mButtonCancelRequest;
    private ProveedorGeoFire mGeofireProvider;

    private String mExtraOrigin;
    private String mExtraDestination;
    private  double mExtraOriginLat;
    private double mExtraOriginLng;
    private  double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;
    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;
    private NotificacionProveedor mNotificacionProveedor;
    private TokenProveedor mTokenProveedor;
    private ReservaClienteProveedor mReservaClienteProveedor;
    private AuthProveedores mAuthProveedores;
    private GoogleApiProveedor mGoogleApiProvider;

    private  ValueEventListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitar_conductor);

        mAnimation = findViewById(R.id.animacion);
        mTextviewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);

        mAnimation.playAnimation();
        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat",0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng",0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destination_lat",0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destination_lng",0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng",0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");


        mOriginLatLng = new LatLng(mExtraOriginLat,mExtraOriginLng);
        mDestinationLatLng = new LatLng(mExtraDestinationLat,mExtraDestinationLng);
        mGeofireProvider = new ProveedorGeoFire("conductores_activos");
        mNotificacionProveedor = new NotificacionProveedor();
        mTokenProveedor = new TokenProveedor();
        mReservaClienteProveedor = new ReservaClienteProveedor();
        mAuthProveedores = new AuthProveedores();
        mGoogleApiProvider = new GoogleApiProveedor(SolicitarConductorActivity.this);


        mButtonCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelarSolicitud();
            }
        });


        getClosestDriver();


    }
    private void cancelarSolicitud(){
        mReservaClienteProveedor.eliminar(mAuthProveedores.obetenerId()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                enviarNotificacionCancelar();
            }
        });

    }
    private void getClosestDriver(){
        mGeofireProvider.obtenerConductoresActivos(mOriginLatLng, mRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!mDriverFound){
                    mDriverFound = true;
                    mIdDriverFound = key;
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextviewLookingFor.setText("CONDUCTOR ENCONTRADO\nESPERANDO RESPUESTA");
                    crearReservaCliente();
                    Log.d("DRIVER","ID:"+ mIdDriverFound);

                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                //iNGRESA CUANDO TERMINA LA BUSQUEDA DEL CONDUCTOR EN UN RADIO DE 0.1 KM
                if(!mDriverFound){
                    mRadius = mRadius + 0.1;

                    //No encontro ningun conductor
                    if(mRadius>5){
                        mTextviewLookingFor.setText("NO SE ENCONTRO UN CONDUCTOR");
                        Toast.makeText(SolicitarConductorActivity.this, R.string.noseEncontroConductor, Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        getClosestDriver();
                    }
                }

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    private  void crearReservaCliente(){
        mGoogleApiProvider.getDirections(mOriginLatLng, mDriverFoundLatLng).enqueue(new Callback<String>() {


            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    JSONArray legs =  route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");
                    enviarNotificacion(durationText,distanceText);


                } catch(Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

private void enviarNotificacionCancelar(){
    mTokenProveedor.obtenerToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {//contiene la informacion que esta dentro del nodo del id del usuario
            if(snapshot.exists()){
                String token = snapshot.child("token").getValue().toString();
                Map<String, String> map = new HashMap<>();
                map.put("title","VIAJE CANCELADO");
                map.put("body",
                        "El cliente cancelo la solicitud"
                );

                FCMCuerpo fcmCuerpo = new FCMCuerpo(token, "high","4500s",map);
                mNotificacionProveedor.enviarNotificacion(fcmCuerpo).enqueue(new Callback<FCMRespuesta>() {
                    @Override
                    public void onResponse(Call<FCMRespuesta> call, Response<FCMRespuesta> response) {
                        if(response.body() != null){
                            if(response.body().getSuccess() == 1){
                                Toast.makeText(SolicitarConductorActivity.this, "La solicitud se cancelo correctamente", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SolicitarConductorActivity.this,MapClienteActivity.class);
                            startActivity(intent);
                            finish();
                            //Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionEnviada, Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionNOEnviada, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionNOEnviada, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMRespuesta> call, Throwable t) {
                        Log.d("Error","Error" + t.getMessage());

                    }
                });
            }
            else {
                Toast.makeText(SolicitarConductorActivity.this, R.string.nosePudoenviarNotificacionConductorNotieneToken, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    private void enviarNotificacion(final String tiempo, final String km) {
        mTokenProveedor.obtenerToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//contiene la informacion que esta dentro del nodo del id del usuario
                if(snapshot.exists()){
                    String token = snapshot.child("token").getValue().toString();
                    Map<String, String> map = new HashMap<>();
                    map.put("title","SOLICITUD DE SERVICIO A " + tiempo + " DE TU POSICION");
                    map.put("body",
                            "Un cliente esta solicitando un servicio a una distancia de " + km + "\n" +
                                    "Recoger en: " + mExtraOrigin + "\n" +
                                    "Destino: " + mExtraDestination
                    );
                    map.put("idCliente", mAuthProveedores.obetenerId());
                    map.put("origin", mExtraOrigin);
                    map.put("destination", mExtraDestination);
                    map.put("min", tiempo);
                    map.put("distance", km);
                    FCMCuerpo fcmCuerpo = new FCMCuerpo(token, "high","4500s",map);
                    mNotificacionProveedor.enviarNotificacion(fcmCuerpo).enqueue(new Callback<FCMRespuesta>() {
                        @Override
                        public void onResponse(Call<FCMRespuesta> call, Response<FCMRespuesta> response) {
                            if(response.body() != null){
                                if(response.body().getSuccess() == 1){
                                    ReservaCliente reservaCliente = new ReservaCliente(
                                            mAuthProveedores.obetenerId(),
                                            mIdDriverFound,
                                            mExtraDestination,
                                            mExtraOrigin,
                                            tiempo,
                                            km,
                                            "create",
                                            mExtraOriginLat,
                                            mExtraOriginLng,
                                            mExtraDestinationLat,
                                            mExtraDestinationLng

                                    );
                                    mReservaClienteProveedor.crear(reservaCliente).addOnSuccessListener(new OnSuccessListener<Void>() {

                                        @Override
                                        public void onSuccess(Void unused) {
                                            revisarEstadoReservaCliente();
                                        }
                                    });
                                    //Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionEnviada, Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionNOEnviada, Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(SolicitarConductorActivity.this, R.string.notificacionNOEnviada, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<FCMRespuesta> call, Throwable t) {
                            Log.d("Error","Error" + t.getMessage());

                        }
                    });
                }
               else {
                    Toast.makeText(SolicitarConductorActivity.this, R.string.nosePudoenviarNotificacionConductorNotieneToken, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void revisarEstadoReservaCliente() {
       mListener = mReservaClienteProveedor.obtenerStado(mAuthProveedores.obetenerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String estado = snapshot.getValue().toString();
                    if(estado.equals("accept")){
                        Intent intent = new Intent(SolicitarConductorActivity.this, MapReservaClienteActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(estado.equals("cancel")){
                        Toast.makeText(SolicitarConductorActivity.this, R.string.viajeNoAceptado, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SolicitarConductorActivity.this, MapClienteActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mListener != null){
            mReservaClienteProveedor.obtenerStado(mAuthProveedores.obetenerId()).removeEventListener(mListener);
        }


    }
}