package com.ksp.subitesv.actividades.cliente;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.ksp.subitesv.R;
import com.ksp.subitesv.proveedores.AuthProveedores;
import com.ksp.subitesv.proveedores.GoogleApiProveedor;
import com.ksp.subitesv.proveedores.ProveedorConductor;
import com.ksp.subitesv.proveedores.ProveedorGeoFire;
import com.ksp.subitesv.proveedores.ReservaClienteProveedor;
import com.ksp.subitesv.proveedores.TokenProveedor;
import com.ksp.subitesv.utils.DecodificadorPuntos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapReservaClienteActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private AuthProveedores mAuthProveedores;
    private ProveedorGeoFire mProveedorGeofire;
    private TokenProveedor mTokenProveedor;
    private ReservaClienteProveedor mReservaClienteProveedor;
    private ProveedorConductor mProveedorConductor;
    private Marker mMarkerConductor;

    private PlacesClient mPlaces;

    private String mOrigin;
    private LatLng mOriginLatLng;

    private String mDestination;
    private LatLng mDestinationLatLng;
    private LatLng mConductorLatLng;

    private TextView mTextViewReservaConductor;
    private TextView mTextViewEmailReservaConductor;
    private TextView mTextViewOrigenReservaCliente;
    private TextView mTextViewDestinoReservaCliente;
    private TextView mTextViewEstadoReservaCliente;

    private GoogleApiProveedor mGoogleApiProvider;
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolylineOptions;
    private ValueEventListener mListener;
    private String mconductorId;

    private boolean mPrimeraVez = true;
    private ValueEventListener mListenerEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_reserva_cliente);

        mAuthProveedores = new AuthProveedores();
        mProveedorGeofire = new ProveedorGeoFire("conductores_trabajando");
        mTokenProveedor = new TokenProveedor();
        mReservaClienteProveedor = new ReservaClienteProveedor();
        mGoogleApiProvider = new GoogleApiProveedor(MapReservaClienteActivity.this);
        mProveedorConductor = new ProveedorConductor();

        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }

        mTextViewReservaConductor = findViewById(R.id.textViewReservaConductor);
        mTextViewEmailReservaConductor = findViewById(R.id.textViewEmailReservaConductor);
        mTextViewOrigenReservaCliente= findViewById(R.id.textViewOrigenReservaConductor);
        mTextViewDestinoReservaCliente = findViewById(R.id.textViewDestinoReservaConductor);
        mTextViewEstadoReservaCliente = findViewById(R.id.textViewEstadoReserva);

        obtenerEstado();
        obtenerReservaCliente();

    }

    private void obtenerEstado(){
        mListenerEstado = mReservaClienteProveedor.obtenerStado(mAuthProveedores.obetenerId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.exists()){
                  String estado = snapshot.getValue().toString();
                  if (estado.equals("accept")){
                      mTextViewEstadoReservaCliente.setText("Estado: Aceptado");
                  }
                  if (estado.equals("Comenzar")){
                      mTextViewEstadoReservaCliente.setText("Estado: Viaje Iniciado");
                      comenzarReservacion();
                  } else if (estado.equals("Finalizar")){
                      mTextViewEstadoReservaCliente.setText("Estado: Viaje Finalizado");
                      terminarReservacion();
                  }
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void terminarReservacion() {
        Intent intent = new Intent(MapReservaClienteActivity.this,CalificacionConductorActivity.class);
        startActivity(intent);
        finish();
    }

    private void comenzarReservacion() {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLng).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_blue)));

        drawRoute(mDestinationLatLng);
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mListener != null){
            mProveedorGeofire.obtenerUbicacionConductor(mconductorId).removeEventListener(mListener);
        }
        if (mListenerEstado != null){
            mReservaClienteProveedor.obtenerStado(mAuthProveedores.obetenerId()).removeEventListener(mListenerEstado);
        }
    }

    private void obtenerReservaCliente(){
        mReservaClienteProveedor.obtenerReservaCliente(mAuthProveedores.obetenerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String destino = snapshot.child("destino").getValue().toString();
                    String origen = snapshot.child("origen").getValue().toString();
                    String conductorId = snapshot.child("idConductor").getValue().toString();
                    mconductorId = conductorId;
                    double destinoLat = Double.parseDouble(snapshot.child("destinoLat").getValue().toString());
                    double destinoLng = Double.parseDouble(snapshot.child("destinoLng").getValue().toString());

                    double origenLat = Double.parseDouble(snapshot.child("origenLat").getValue().toString());
                    double origenLng = Double.parseDouble(snapshot.child("origenLng").getValue().toString());

                    mOriginLatLng = new LatLng(origenLat,origenLng);
                    mDestinationLatLng = new LatLng(destinoLat,destinoLng);
                    mTextViewDestinoReservaCliente.setText("Destino: " + destino);
                    mTextViewOrigenReservaCliente.setText("Recoger en: " + origen);
                    mMap.addMarker(new MarkerOptions().position(mOriginLatLng).title("Recoger aqui").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_pin_red)));
                    obtenerConductor(conductorId);
                    obtenerUbicacionConductor(conductorId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerConductor(String conductorId){
        mProveedorConductor.obtenerConductor(conductorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                  String nombre = snapshot.child("nombre").getValue().toString();
                  String correo = snapshot.child("correo").getValue().toString();
                  mTextViewReservaConductor.setText(nombre);
                  mTextViewEmailReservaConductor.setText(correo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void obtenerUbicacionConductor(String conductorId){
       mListener = mProveedorGeofire.obtenerUbicacionConductor(conductorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.exists()){
                   double lat = Double.parseDouble(snapshot.child("0").getValue().toString());
                   double lon = Double.parseDouble(snapshot.child("1").getValue().toString());
                   mConductorLatLng = new LatLng(lat,lon);

                    if (mMarkerConductor != null){
                        mMarkerConductor.remove();
                    }
                   mMarkerConductor = mMap.addMarker(new MarkerOptions().position(
                           new LatLng(lat,lon)
                           )
                                   .title("Tu conductor")
                                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_conductor))
                   );

                    if (mPrimeraVez){
                        mPrimeraVez= false;
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(mConductorLatLng)
                                        .zoom(14f)
                                        .build()
                        ));

                        drawRoute(mOriginLatLng);

                    }
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void drawRoute(LatLng latLng) {
        mGoogleApiProvider.getDirections(mConductorLatLng,latLng).enqueue(new Callback<String>() {


            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes");
                    JSONObject route = jsonArray.getJSONObject(0);
                    JSONObject polylines = route.getJSONObject("overview_polyline");
                    String points = polylines.getString("points");
                    mPolylineList = DecodificadorPuntos.decodePoly(points);
                    mPolylineOptions = new PolylineOptions();
                    mPolylineOptions.color(Color.DKGRAY);
                    mPolylineOptions.width(8f);
                    mPolylineOptions.startCap(new SquareCap());
                    mPolylineOptions.jointType(JointType.ROUND);
                    mPolylineOptions.addAll(mPolylineList);
                    mMap.addPolyline(mPolylineOptions);

                    JSONArray legs =  route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");


                } catch(Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }
}