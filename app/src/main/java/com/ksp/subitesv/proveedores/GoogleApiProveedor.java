package com.ksp.subitesv.proveedores;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.ksp.subitesv.R;

import com.ksp.subitesv.retrofit.GoogleApi;
import com.ksp.subitesv.retrofit.RetrofitCliente;

import java.util.Date;

import retrofit2.Call;

public class GoogleApiProveedor {
    private Context context;


    public GoogleApiProveedor(Context context) {
        this.context = context;
    }

    public Call<String> getDirections(LatLng originLatLng, LatLng destinationLatLng) {
        String baseUrl = "https://maps.googleapis.com";
        String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
                + "origin=" + originLatLng.latitude + "," + originLatLng.longitude + "&"
                + "destination=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&"
                + "departure_time=" + (new Date().getTime() + (60*60*1000)) + "&"
                + "traffic_model=best_guess&"
                + "key=" + context.getResources().getString(R.string.google_maps_key);
        return RetrofitCliente.getClient(baseUrl).create(GoogleApi.class).getDirections(baseUrl + query);
    }
}
